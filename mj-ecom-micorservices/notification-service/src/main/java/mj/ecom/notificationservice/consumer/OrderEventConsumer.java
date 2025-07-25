package mj.ecom.notificationservice.consumer;

import lombok.extern.slf4j.Slf4j;
import mj.ecom.notificationservice.dto.OrderCreatedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@Slf4j
public class OrderEventConsumer {
    /*@RabbitListener(queues = "${rabbitmq.queue.name}")
    public void handleOrderEvent(OrderCreatedEvent orderCreatedEvent) {
        System.out.println("Received Order Event: " + orderCreatedEvent);

        long orderId = orderCreatedEvent.getOrderId();
        OrderStatus orderStatus = orderCreatedEvent.getStatus();

        System.out.println("Received Order ID: " + orderId);
        System.out.println("Received Order Status: " + orderStatus);

        // update database
        // send notification
        // send emails
        // generate invoice
        // send seller notification
    }*/

    @Bean
    public Consumer<OrderCreatedEvent> orderCreated() {
        return event -> {
            log.info("Received OrderCreatedEvent with orderId : {}", event.getOrderId());
            log.info("Received OrderCreatedEvent with userId : {}", event.getUserId());
        };
    }
}
