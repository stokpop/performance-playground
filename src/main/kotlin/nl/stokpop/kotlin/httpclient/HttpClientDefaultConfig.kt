package nl.stokpop.kotlin.httpclient

import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients

class HttpClientDefaultConfig {

    fun createHttpClient(): CloseableHttpClient {

        val requestConfig = RequestConfig.custom()
            .setConnectTimeout(200)
            .setConnectionRequestTimeout(250)
            .setSocketTimeout(5000)
            .build()

        return HttpClients.custom()
            .setMaxConnPerRoute(20)
            .setMaxConnTotal(20)
            .setDefaultRequestConfig(requestConfig)
            //.disableConnectionState()
            .build()
    }


}