package mj.ecom.orderservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mj.ecom.orderservice.clients.ProductServiceClient;
import mj.ecom.orderservice.clients.UserServiceClient;
import mj.ecom.orderservice.dto.CartItemRequest;
import mj.ecom.orderservice.dto.ProductResponse;
import mj.ecom.orderservice.dto.UserResponse;
import mj.ecom.orderservice.model.CartItem;
import mj.ecom.orderservice.repository.CartItemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;

    int attemptCount = 0;

    //@CircuitBreaker(name = "productService", fallbackMethod = "addToCartFallBack")
    @Retry(name = "retryBreaker", fallbackMethod = "addToCartFallBack")
    public boolean addToCart(String userId, CartItemRequest request) {
        // retires done if any service goes down
        System.out.println(" Retries done: - " + ++attemptCount);
        // Look for product
        ProductResponse productResponse = productServiceClient.getAllProductById(String.valueOf(request.getProductId()));
        if (productResponse == null || productResponse.getStockQuantity() < request.getQuantity()) {
            return false;
        }

        // validate the user from user-service microservice
        UserResponse userResponse = userServiceClient.getUserById(String.valueOf(userId));
        if (userResponse == null) {
            return false;
        }

        // checking existing cart items
        CartItem existingCartItem = cartItemRepository.findByUserIdAndProductId(userId, request.getProductId());
        if (existingCartItem != null) {
            // Update the quantity
            existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
            existingCartItem.setPrice(productResponse.getPrice()); // we will set actual price while inter service communication
            cartItemRepository.save(existingCartItem);
        } else {
            // Create new cart item
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(request.getProductId());
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(productResponse.getPrice());
            cartItemRepository.save(cartItem);
        }
        return true;
    }

    public boolean addToCartFallBack(String userId, CartItemRequest request, Exception exception) {
        System.out.println("addToCartFallBack called:");
        exception.printStackTrace();
        return false;
    }

    public boolean deleteItemFromCart(String userId, String productId) {
        //Optional<Product> productOpt = productRepository.findById(productId);
        //Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId);

        if (cartItem != null) {
            cartItemRepository.delete(cartItem);
            return true;
        }
        return false;
    }

    public List<CartItem> getCart(String userId) {
        return cartItemRepository.findByUserId(userId);
    }

    public void clearCart(String userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}
