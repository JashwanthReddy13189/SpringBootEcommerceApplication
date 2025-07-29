package mj.ecom.orderservice.service;

import lombok.RequiredArgsConstructor;
import mj.ecom.orderservice.dto.OrderCreatedEvent;
import mj.ecom.orderservice.dto.OrderItemDTO;
import mj.ecom.orderservice.dto.OrderResponse;
import mj.ecom.orderservice.model.CartItem;
import mj.ecom.orderservice.model.Order;
import mj.ecom.orderservice.model.OrderItem;
import mj.ecom.orderservice.model.OrderStatus;
import mj.ecom.orderservice.repository.OrderRepository;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final StreamBridge streamBridge;

    public Optional<OrderResponse> createOrder(String userId) {
        // Validate for cart items and also userId
        List<CartItem> cartItems = cartService.getCart(userId);
        if (cartItems.isEmpty()) {
            return Optional.empty();
        }

        // Calculate total price
        BigDecimal totalPrice = cartItems.stream()
                .map(cartItem -> cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create order
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);

        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> new OrderItem(
                        null,
                        item.getProductId(),
                        item.getQuantity(),
                        item.getPrice(),
                        order
                ))
                .toList();

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        // Clear the cart
        cartService.clearCart(userId);

        // publish order created event
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getStatus(),
                mapToOrderItemDTOs(savedOrder.getItems()),
                savedOrder.getTotalAmount(),
                savedOrder.getCreatedAt()
        );

        // sending event to rabbitmq
        /*rabbitTemplate.convertAndSend(exchangeName,
                routingKey,
                orderCreatedEvent
        );*/

        streamBridge.send("createOrder-out-0", orderCreatedEvent);

        return Optional.of(mapToOrderResponse(savedOrder));
    }

    private List<OrderItemDTO> mapToOrderItemDTOs(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> new OrderItemDTO(
                        item.getId(),
                        item.getProductId(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getPrice().multiply(new BigDecimal(item.getQuantity()))
                ))
                .collect(Collectors.toList());
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getItems().stream()
                        .map(orderItem -> new OrderItemDTO(
                                orderItem.getId(),
                                orderItem.getProductId(),
                                orderItem.getQuantity(),
                                orderItem.getPrice(),
                                orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()))
                        ))
                        .toList(),
                order.getCreatedAt()
        );
    }

    public List<OrderResponse> getAllOrdersForUserId(String userId) {
        // check if there are any orders placed with the user or not
        List<Order> orders = orderRepository.findByUserId(userId);
        if (orders.isEmpty()) {
            return new ArrayList<>();
        }

        return orders.stream()
                .map(order -> mapToOrderResponse(order))
                .collect(Collectors.toList());
    }
}
