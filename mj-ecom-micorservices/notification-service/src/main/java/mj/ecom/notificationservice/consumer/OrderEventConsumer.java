package mj.ecom.notificationservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mj.ecom.notificationservice.dto.EmailDetails;
import mj.ecom.notificationservice.dto.OrderCreatedEvent;
import mj.ecom.notificationservice.dto.OrderItemDTO;
import mj.ecom.notificationservice.email.EmailService;
import mj.ecom.notificationservice.email.EmailTemplateService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final EmailService emailService;
    private final EmailTemplateService emailTemplateService;

    @Bean
    public Consumer<OrderCreatedEvent> orderCreated() {
        return event -> {
            log.info("Received OrderCreatedEvent with orderId : {}", event.getOrderId());
            log.info("Received OrderCreatedEvent with userId : {}", event.getUserId());
            log.info("Received OrderCreatedEvent for username : {}", event.getUserFullName());

            if (event.getOrderItems() != null && !event.getOrderItems().isEmpty()) {
                log.info("Received OrderCreatedEvent for productNames : {}",
                        event.getOrderItems().get(0).getProductName());
            }

            try {
                // Create rich HTML email using template service
                String htmlBody = emailTemplateService.buildModernOrderConfirmationEmail(event);

                EmailDetails emailDetails = new EmailDetails();
                emailDetails.setRecipient(event.getEmail());
                emailDetails.setSubject("✅ Order Confirmation #" + event.getOrderId() + " - Thank you " + event.getUserFullName() + "!");
                emailDetails.setMsgBody(htmlBody);
                emailDetails.setHtml(true); // Enable HTML rendering

                emailService.sendSimpleMail(emailDetails);
                log.info("Order confirmation email sent successfully for order {}", event.getOrderId());

            } catch (Exception exception) {
                log.error("Failed to send confirmation email for order {}", event.getOrderId(), exception);

                // Fallback to plain text email
                try {
                    sendFallbackTextEmail(event);
                    log.info("Fallback text email sent for order {}", event.getOrderId());
                } catch (Exception fallbackException) {
                    log.error("Failed to send fallback email for order {}", event.getOrderId(), fallbackException);
                }
            }
        };
    }

    // Fallback method for plain text email
    private void sendFallbackTextEmail(OrderCreatedEvent event) {
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
                event.getUserFullName(),
                event.getOrderId(),
                event.getStatus(),
                event.getTotalAmount(),
                event.getCreatedAt(),
                formatOrderItems(event.getOrderItems())
        );

        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient("jassureddy79@gmail.com");
        emailDetails.setSubject("Order Confirmation: " + event.getOrderId());
        emailDetails.setMsgBody(body);
        emailDetails.setHtml(false); // Plain text

        emailService.sendSimpleMail(emailDetails);
    }

    // Plain text formatting (keeping your original method as fallback)
    private String formatOrderItems(List<OrderItemDTO> items) {
        if (items == null || items.isEmpty()) return "No items found.";
        return items.stream()
                .map(item -> String.format("- %s x %d - ₹%.2f",
                        item.getProductName(),
                        item.getQuantity(),
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))))
                .collect(Collectors.joining("\n"));
    }
}
