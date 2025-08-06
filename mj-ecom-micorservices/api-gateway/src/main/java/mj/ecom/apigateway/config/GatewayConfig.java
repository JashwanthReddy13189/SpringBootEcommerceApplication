package mj.ecom.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    private static final Logger log = LoggerFactory.getLogger(GatewayConfig.class);
    @Value("${gateway.eureka-url:http://localhost:8761}")
    private String eurekaUrl;

    // adding this for implementing rate limit to handle input requests traffic overload
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(10, 20, 1);
    }

    // extracting the unique hostname of the user
    @Bean
    public KeyResolver hostNamekeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
    }

    // Adding this filter to not expose our original api mappings
    // we are rewriting paths using custom configs
    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        log.debug("Eureka service url - " + eurekaUrl);
        return builder.routes()
                .route("user-service", r -> r.path("/api/users","/api/users/**")
                        //.filters(f -> f.rewritePath("/users(?<segment>/?.*)", "/api/users${segment}")) // regex to hide api mappings
                        .filters(f -> f
                                .retry(retryConfig -> retryConfig
                                        .setRetries(10)
                                        .setMethods(HttpMethod.GET)
                                )
                                .circuitBreaker(config -> config
                                        .setName("ecomCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/users")
                                )
                        )
                        .uri("lb://USER-SERVICE"))
                .route("product-service", r -> r.path("/api/products/**")
                        //.filters(f -> f.rewritePath("/products(?<segment>/?.*)", "/api/products${segment}"))
                        .filters(f -> f
                                .retry(retryConfig -> retryConfig
                                        .setRetries(10)
                                        .setMethods(HttpMethod.GET)
                                )
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(hostNamekeyResolver())
                                )
                                .circuitBreaker(config -> config
                                        .setName("ecomCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/products")
                                )
                        )
                        .uri("lb://PRODUCT-SERVICE"))
                .route("order-service", r -> r.path("/api/cart/**", "/api/orders/**")
                        //.filters(f -> f.rewritePath("/(?<segment>.*)", "/api/${segment}"))
                        /*.filters(f -> f.circuitBreaker(config -> config
                                .setName("ecomCircuitBreaker")
                                .setFallbackUri("forward:/fallback/orders")))*/
                        .uri("lb://ORDER-SERVICE"))
                .route("eureka-service", r -> r.path("/eureka/main")
                        .filters(f -> f.rewritePath("/eureka/main", "/"))
                        .uri(eurekaUrl))
                .route("eureka-service-static", r -> r.path("/eureka/**")
                        .uri(eurekaUrl))
                .build();
    }
}
