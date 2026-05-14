package com.trustmesh.gateway

import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.logging.Logger

@Component
class ZeroTrustFilterFactory(
    private val webClientBuilder: WebClient.Builder
) : AbstractGatewayFilterFactory<ZeroTrustFilterFactory.Config>(Config::class.java) {

    private val logger = Logger.getLogger(ZeroTrustFilterFactory::class.java.name)
    private val aiEngineUrl = System.getenv("AI_ENGINE_URL") ?: "http://localhost:8000"
    private val opaUrl = System.getenv("OPA_URL") ?: "http://localhost:8181"

    class Config

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange: ServerWebExchange, chain: GatewayFilterChain ->
            val identityHeader = exchange.request.headers.getFirst("X-User-ID")
            val isIdentityVerified = identityHeader != null && identityHeader.startsWith("did:")

            val aiPayload = mapOf(
                "latency_ms" to (Math.random() * 100),
                "request_volume_per_min" to 10.0,
                "geo_distance_km" to 50.0
            )

            val client = webClientBuilder.build()

            client.post()
                .uri("$aiEngineUrl/predict")
                .bodyValue(aiPayload)
                .retrieve()
                .bodyToMono(Map::class.java)
                .flatMap { aiResponse ->
                    val riskScore = (aiResponse["risk_score"] as Number).toDouble()
                    logger.info("[TrustMesh] AI Risk Score: $riskScore | Identity verified: $isIdentityVerified")

                    val opaPayload = mapOf(
                        "input" to mapOf(
                            "identity_verified" to isIdentityVerified,
                            "risk_score" to riskScore
                        )
                    )

                    client.post()
                        .uri("$opaUrl/v1/data/trustmesh/authz/allow")
                        .bodyValue(opaPayload)
                        .retrieve()
                        .bodyToMono(Map::class.java)
                        .flatMap { opaResponse ->
                            val allowed = opaResponse["result"] as? Boolean ?: false
                            logger.info("[TrustMesh] OPA Decision: $allowed")

                            if (allowed) {
                                chain.filter(exchange)
                            } else {
                                exchange.response.statusCode = HttpStatus.FORBIDDEN
                                exchange.response.setComplete()
                            }
                        }
                }
                .onErrorResume { e ->
                    logger.severe("[TrustMesh] Zero Trust check failed: ${e.message}")
                    exchange.response.statusCode = HttpStatus.SERVICE_UNAVAILABLE
                    exchange.response.setComplete()
                }
        }
    }
}
