package mj.ecom.orderservice.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class MessageController {

    @Value("${message}")
    private String message;

    @GetMapping("/message")
    @RateLimiter(name = "rateLimiter", fallbackMethod = "fallBackRetryMethod")
    public String getMessage() {
        return message;
    }

    public String fallBackRetryMethod(Exception exception) {
        return "Hey FallBack!";
    }
}
