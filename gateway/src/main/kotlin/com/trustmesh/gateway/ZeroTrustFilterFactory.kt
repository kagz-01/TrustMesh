package com.trustmesh.gateway

import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.logging.Logger

@Component
class ZeroTrustFilterFactory(private val webClientBuilder: WebClient.Builder) : AbstractGatewayFilterFactory<ZeroTrustFilterFactory.Config>(Config::class.java) {

    private val logger = Logger.getLogger(ZeroTrustFilterFactory::class.java.name)
    private val aiEngineUrl = System.getenv("AI_ENGINE_URL") ?: "http://localhost:8000"
    private val opaUrl = System.getenv("OPA_URL") ?: "http://localhost:8181"

    class Config {
        // Configuration properties can go here
    }

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val request = exchange.request
            val identityHeader = request.headers.getFirst("X-User-ID")
            
            // Assume identity is verified if header is present and starts with 'did:'
            val isIdentityVerified = identityHeader != null && identityHeader.startsWith("did:")

            // Synthesize some metadata for the AI engine based on request
            val latencyMs = Math.random() * 100 // Simulate latency
            val volume = 10.0
            val geoDist = 50.0
            
            val aiRequestPayload = mapOf(
                "latency_ms" to latencyMs,
                "request_volume_per_min" to volume,
                "geo_distance_km" to geoDist
            )

            // 1. Call AI Engine
            val webClient = webClientBuilder.build()
            
            webClient.post()
                .uri("$aiEngineUrl/predict")
                .bodyValue(aiRequestPayload)
                .retrieve()
                .bodyToMono(Map::class.java)
                .flatMap { aiResponse ->
                    val riskScore = (aiResponse["risk_score"] as Number).toDouble()
                    logger.info("AI Risk Score: $riskScore")

                    // 2. Call OPA
                    val opaPayload = mapOf(
                        "input" to mapOf(
                            "identity_verified" to isIdentityVerified,
                            "risk_score" to riskScore
                        )
                    )

                    webClient.post()
                        .uri("$opaUrl/v1/data/trustmesh/authz/allow")
                        .bodyValue(opaPayload)
                        .retrieve()
                        .bodyToMono(Map::class.java)
                        .flatMap opaFlatMap@{ opaResponse ->
                            val result = opaResponse["result"] as? Boolean ?: false
                            logger.info("OPA Decision: $result")

                            if (result) {
                                // Request is allowed
                                return@opaFlatMap chain.filter(exchange)
                            } else {
                                // Request is denied
                                exchange.response.setStatusCode(HttpStatus.FORBIDDEN)
                                return@opaFlatMap exchange.response.setComplete()
                            }
                        }
                }
                .onErrorResume { e ->
                    logger.severe("Zero Trust verification failed: ${e.message}")
                    exchange.response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    exchange.response.setComplete()
                }
        }
    }
}
