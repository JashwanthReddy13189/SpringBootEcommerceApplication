package mj.ecom.notificationservice.consumer;

import mj.ecom.notificationservice.dto.OrderCreatedEvent;
import mj.ecom.notificationservice.dto.OrderStatus;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OrderEventConsumer {
    @RabbitListener(queues = "${rabbitmq.queue.name}")
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
    }
}
