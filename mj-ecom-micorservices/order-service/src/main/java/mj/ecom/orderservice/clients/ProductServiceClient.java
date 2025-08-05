package mj.ecom.orderservice.clients;

import mj.ecom.orderservice.dto.ProductResponse;
import mj.ecom.orderservice.dto.UserResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PutExchange;

@HttpExchange
public interface ProductServiceClient {
    @GetExchange("/api/products/{id}")
    ProductResponse getAllProductById(@PathVariable String id);

    @PatchExchange("/api/products/{productId}/reduce-stock")
    void reduceProductStock(@PathVariable String productId, @RequestParam int quantity, @RequestParam(required = false) String transaction);
}
