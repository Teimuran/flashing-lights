package org.example.client.config

import org.example.client.FlatPromoClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class FlatClientConfig {

    @Bean
    fun flatPromoClient(
        @Value("\${flat.api.base-url}") baseUrl: String,
        @Value("\${flat.api.token}") token: String
    ): FlatPromoClient {
        val restClient = RestClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader("Authorization", "Bearer $token")
            .defaultHeader("Content-Type", "application/json")
            .build()

        val adapter = RestClientAdapter.create(restClient)
        val factory = HttpServiceProxyFactory.builderFor(adapter).build()

        return factory.createClient(FlatPromoClient::class.java)
    }
}