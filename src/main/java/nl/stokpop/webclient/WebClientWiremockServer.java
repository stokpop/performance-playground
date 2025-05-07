package nl.stokpop.webclient;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;

import java.io.File;
import java.net.URL;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class WebClientWiremockServer {

    public static final int HTTP_PORT = 58080;
    public static final int HTTPS_PORT = 58443;

    public static WireMockServer createWiremockServer() {

        ResponseDefinitionTransformer transformer = new ResponseDefinitionTransformer() {
            @Override
            public ResponseDefinition transform(Request request, ResponseDefinition responseDefinition, FileSource files, Parameters parameters) {
                String authToken = request.getHeader("X-Auth-Token");
                return ResponseDefinition.okForJson("{\"message\": \"Hello from WireMock!\", \"Auth-Token\": \"" + authToken + "\"}");
            }

            @Override
            public String getName() {
                return "header-transformer";
            }

            @Override
            public boolean applyGlobally() {
                return false;
            }
        };

        // Get path to resources through ClassLoader
        ClassLoader classLoader = WebClientWiremockServer.class.getClassLoader();
        URL keystoreUrl = classLoader.getResource("server.p12");
        URL truststoreUrl = classLoader.getResource("server-truststore.p12");

        System.out.println("Keystore URL: " + (keystoreUrl != null ? keystoreUrl.getPath() : "NOT FOUND"));
        System.out.println("Truststore URL: " + (truststoreUrl != null ? truststoreUrl.getPath() : "NOT FOUND"));


        if (keystoreUrl == null || truststoreUrl == null) {
            throw new RuntimeException("Could not find keystore or truststore files in resources");
        }

        File keystoreFile = new File(keystoreUrl.getFile());
        File truststoreFile = new File(truststoreUrl.getFile());

        WireMockServer wireMockServer = new WireMockServer(
                WireMockConfiguration.options()
                        .port(HTTP_PORT)
                        .httpsPort(HTTPS_PORT)
                        .keystorePath(keystoreFile.getPath())
                        .keystorePassword("changeit")
                        .keyManagerPassword("changeit")
                        .keystoreType("PKCS12")
                        .trustStorePath(truststoreFile.getPath())
                        .trustStorePassword("changeit")
                        .trustStoreType("PKCS12")
                        .needClientAuth(true)
                        .extensions(transformer)
        );

        wireMockServer.stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withTransformers("header-transformer")
                ));

        return wireMockServer;
    }
}
