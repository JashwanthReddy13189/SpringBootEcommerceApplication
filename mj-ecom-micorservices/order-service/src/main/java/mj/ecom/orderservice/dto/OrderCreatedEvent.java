package mj.ecom.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mj.ecom.orderservice.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    private Long orderId;
    private String userId;
    private String userFullName;
    private String email;
    private OrderStatus status;
    private List<OrderItemDTO> orderItems;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}
