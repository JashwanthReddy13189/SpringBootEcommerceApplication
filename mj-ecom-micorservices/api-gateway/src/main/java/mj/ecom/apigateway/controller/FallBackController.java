package mj.ecom.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class FallBackController {
    @GetMapping("/fallback/users")
    public ResponseEntity<List<String>> userServiceFallBack() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Collections.singletonList(" User-Service unavailable please try again after sometime "));
    }
    @GetMapping("/fallback/products")
    public ResponseEntity<List<String>> productServiceFallBack() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Collections.singletonList(" Product-Service unavailable please try again after sometime "));
    }
    @GetMapping("/fallback/orders")
    public ResponseEntity<List<String>> orderServiceFallBack() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Collections.singletonList(" Order-Service unavailable please try again after sometime "));
    }
}
