package mj.ecom.notificationservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mj.ecom.notificationservice.dto.EmailDetails;
import mj.ecom.notificationservice.dto.OrderCreatedEvent;
import mj.ecom.notificationservice.dto.OrderItemDTO;
import mj.ecom.notificationservice.email.EmailService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final EmailService emailService;

    @Bean
    public Consumer<OrderCreatedEvent> orderCreated() {

        return event -> {
            log.info("Received OrderCreatedEvent with orderId : {}", event.getOrderId());
            log.info("Received OrderCreatedEvent with userId : {}", event.getUserId());
            try {
                // Prepare the friendly email body
                String body = String.format("""
                                Hello %s,
                                
                                Your order (ID: %d) has been confirmed successfully.
                                
                                🛒 Status      : %s
                                💰 Total       : ₹%s
                                📅 Placed on   : %s
                                
                                Items Ordered:
                                %s
                                
                                Thank you for shopping with us!
                                mj-ecom
                                """,
                        event.getUserId(),
                        event.getOrderId(),
                        event.getStatus(),
                        event.getTotalAmount(),
                        event.getCreatedAt(),
                        formatOrderItems(event.getOrderItems())
                );

                EmailDetails emailDetails = new EmailDetails();
                emailDetails.setRecipient("{user.mail}");
                emailDetails.setSubject("Order Confirmation: " + event.getOrderId());
                emailDetails.setMsgBody(body);

                emailService.sendSimpleMail(emailDetails);
            } catch (Exception exception) {
                log.error("Failed to convert event to json or sending mail", exception);
            }
        };

    }

    private String formatOrderItems(List<OrderItemDTO> items) {
        if (items == null || items.isEmpty()) return "No items found.";

        return items.stream()
                .map(item -> String.format("- %s x%d", item.getId(), item.getQuantity()))
                .collect(Collectors.joining("\n"));
    }
}
