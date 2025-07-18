package mj.ecom.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    // Adding this filter to not expose our original api mappings
    // we are rewriting paths using custom configs
    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r.path("/api/users/**")
                        //.filters(f -> f.rewritePath("/users(?<segment>/?.*)", "/api/users${segment}")) // regex to hide api mappings
                        .filters(f -> f.circuitBreaker(config -> config
                                .setName("ecomCircuitBreaker")
                                .setFallbackUri("forward:/fallback/users")))
                        .uri("lb://USER-SERVICE"))
                .route("product-service", r -> r.path("/api/products/**")
                        //.filters(f -> f.rewritePath("/products(?<segment>/?.*)", "/api/products${segment}"))
                        .filters(f -> f.circuitBreaker(config -> config
                                .setName("ecomCircuitBreaker")
                                .setFallbackUri("forward:/fallback/products")))
                        .uri("lb://PRODUCT-SERVICE"))
                .route("order-service", r -> r.path("/api/cart/**", "/api/orders/**")
                        //.filters(f -> f.rewritePath("/(?<segment>.*)", "/api/${segment}"))
                        .filters(f -> f.circuitBreaker(config -> config
                                .setName("ecomCircuitBreaker")
                                .setFallbackUri("forward:/fallback/orders")))
                        .uri("lb://ORDER-SERVICE"))
                .route("eureka-service", r -> r.path("/eureka/main")
                        .filters(f -> f.rewritePath("/eureka/main", "/"))
                        .uri("http://localhost:8761"))
                .route("eureka-service-static", r -> r.path("/eureka/**")
                        .uri("http://localhost:8761"))
                .build();
    }
}
