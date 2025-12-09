package nl.stokpop.kotlin.httpclient

import org.apache.hc.client5.http.config.ConnectionConfig
import org.apache.hc.client5.http.config.RequestConfig
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.core5.util.Timeout


class HttpClient5DefaultConfig {

    fun createHttpClient(): CloseableHttpClient {

        val requestConfig = RequestConfig.custom()
            .setConnectTimeout(Timeout.ofMilliseconds(200))
            //.setConnectionRequestTimeout(250)
            .setResponseTimeout(Timeout.ofMilliseconds(5000))
            .build()

        return HttpClients.custom()
            //.setMaxConnPerRoute(20)
            //.setMaxConnTotal(20)
            .setDefaultRequestConfig(requestConfig)
            //.disableConnectionState()
            .build()
    }

    fun good(): CloseableHttpClient {
        val requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(Timeout.ofMilliseconds(350))
            //.setResponseTimeout(Timeout.ofMilliseconds(1200))
            .build() // good
        val connectionConfig = ConnectionConfig.custom()
            .setConnectTimeout(Timeout.ofMilliseconds(250))
            .build()
        val connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
            .setMaxConnPerRoute(20)
            .setDefaultConnectionConfig(connectionConfig)
            .build()
        return HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(requestConfig)
            .build()
    }

}