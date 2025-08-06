package mj.ecom.productservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mj.ecom.productservice.dto.ProductRequest;
import mj.ecom.productservice.dto.ProductResponse;
import mj.ecom.productservice.model.Product;
import mj.ecom.productservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Slf4j
public class ProductController {

    private final ProductService productService;

    @GetMapping("/simulate")
    public ResponseEntity<String> simulateFailure(@RequestParam(defaultValue = "false") boolean fail) {
        if (fail) throw new RuntimeException("Simulated Failure");
        return ResponseEntity.ok("Product Service is OK");
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest) {
        log.info("Created Product {}", productRequest);
        return new ResponseEntity<ProductResponse>(productService.createProduct(productRequest),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {
        return productService.getAllProductById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String id,
            @RequestBody ProductRequest productRequest) {
        return productService.updateProduct(id, productRequest)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{productId}/reduce-stock")
    public ResponseEntity<String> reduceStock(@PathVariable String productId, @RequestParam int quantity, @RequestParam(required = false) String transaction) {
        log.info("Reducing Stock {} and reducing quantity request received from {}", productId, transaction);
        return productService.reduceStock(productId, quantity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok("Error while updating stock quantity of product" + productId));
    }

    @PatchMapping("/{productId}/add-stock")
    public ResponseEntity<String> addStock(@PathVariable String productId, @RequestParam int quantity) {
        return productService.addStock(productId, quantity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok("Error while updating stock quantity of product" + productId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        boolean deleted = productService.deleteProduct(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();

    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String keyword) {
        return ResponseEntity.ok(productService.searchProducts(keyword));
    }
}
