package mj.ecom.notificationservice.email;

import lombok.extern.slf4j.Slf4j;
import mj.ecom.notificationservice.dto.OrderCreatedEvent;
import mj.ecom.notificationservice.dto.OrderItemDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmailTemplateService {

    public String buildModernOrderConfirmationEmail(OrderCreatedEvent event) {
        return String.format("""
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Order Confirmation</title>
                        </head>
                        <body style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; margin: 0; padding: 0; background-color: #f4f4f4;">
                            <div style="max-width: 600px; margin: 20px auto; background-color: white; border-radius: 10px; overflow: hidden; box-shadow: 0 0 20px rgba(0,0,0,0.1);">
                        
                                <!-- Header -->
                                <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center;">
                                    <h1 style="margin: 0; font-size: 28px; font-weight: 300;">Order Confirmed! 🎉</h1>
                                    <p style="margin: 10px 0 0 0; opacity: 0.9;">Thank you for your purchase</p>
                                </div>
                        
                                <!-- Content -->
                                <div style="padding: 30px;">
                                    <h2 style="color: #333; margin-bottom: 20px;">Hello %s,</h2>
                                    <p style="color: #666; font-size: 16px; margin-bottom: 25px;">
                                        Great news! Your order has been confirmed and is being processed. We'll keep you updated on its progress.
                                    </p>
                        
                                    <!-- Order Summary Card -->
                                    <div style="background-color: #f8f9fa; border-radius: 8px; padding: 20px; margin-bottom: 25px; border-left: 4px solid #667eea;">
                                        <h3 style="margin-top: 0; color: #333; font-size: 18px;">📋 Order Summary</h3>
                                        <table style="width: 100%%; border-collapse: collapse;">
                                            <tr>
                                                <td style="padding: 8px 0; color: #666; font-weight: 500;">Order ID:</td>
                                                <td style="padding: 8px 0; color: #333; font-weight: bold;">#%d</td>
                                            </tr>
                                            <tr>
                                                <td style="padding: 8px 0; color: #666; font-weight: 500;">Status:</td>
                                                <td style="padding: 8px 0;">
                                                    <span style="background-color: #d4edda; color: #155724; padding: 4px 8px; border-radius: 12px; font-size: 12px; font-weight: 500;">
                                                        %s
                                                    </span>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td style="padding: 8px 0; color: #666; font-weight: 500;">Total Amount:</td>
                                                <td style="padding: 8px 0; color: #28a745; font-weight: bold; font-size: 18px;">₹%s</td>
                                            </tr>
                                            <tr>
                                                <td style="padding: 8px 0; color: #666; font-weight: 500;">Order Date:</td>
                                                <td style="padding: 8px 0; color: #333;">%s</td>
                                            </tr>
                                        </table>
                                    </div>
                        
                                    <!-- Items Section -->
                                    <div style="margin-bottom: 25px;">
                                        <h3 style="color: #333; margin-bottom: 15px; font-size: 18px;">🛍️ Items Ordered</h3>
                                        <div style="background-color: white; border: 1px solid #e9ecef; border-radius: 8px;">
                                            %s
                                        </div>
                                    </div>
                        
                                    <!-- CTA Section -->
                                    <div style="background-color: #f8f9fa; border-radius: 8px; padding: 20px; text-align: center; margin: 25px 0;">
                                        <p style="margin: 0 0 15px 0; color: #666; font-size: 14px;">
                                            Need help with your order?
                                        </p>
                                        <div style="margin-top: 15px;">
                                            <a href="mailto:noreply.jashwanthreddy@gmail.com" style="display: inline-block; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; text-decoration: none; padding: 10px 25px; border-radius: 20px; font-weight: 500; margin: 5px; font-size: 14px;">
                                                Contact Support
                                            </a>
                                            <a href="#" style="display: inline-block; border: 2px solid #667eea; color: #667eea; text-decoration: none; padding: 8px 23px; border-radius: 20px; font-weight: 500; margin: 5px; font-size: 14px;">
                                                Track Order
                                            </a>
                                        </div>
                                    </div>
                                </div>
                        
                                <!-- Footer -->
                                <div style="background-color: #f8f9fa; padding: 20px; text-align: center; border-top: 1px solid #e9ecef;">
                                    <p style="margin: 0; color: #666; font-size: 14px;">
                                        Thanks for choosing <strong style="color: #667eea;">mj-ecom</strong>
                                    </p>
                                    <p style="margin: 5px 0 0 0; color: #999; font-size: 12px;">
                                        This email was sent regarding order #%d. If you have any questions, please contact our support team.
                                    </p>
                                </div>
                            </div>
                        </body>
                        </html>
                        """,
                event.getUserFullName(),
                event.getOrderId(),
                event.getStatus(),
                event.getTotalAmount(),
                event.getCreatedAt(),
                formatOrderItemsHtml(event.getOrderItems()),
                event.getOrderId()
        );
    }

    public String buildMinimalistOrderConfirmationEmail(OrderCreatedEvent event) {
        return String.format("""
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Order Confirmation</title>
                        </head>
                        <body style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; line-height: 1.6; margin: 0; padding: 20px; background-color: #ffffff;">
                            <div style="max-width: 600px; margin: 0 auto; background-color: white;">
                        
                                <!-- Header -->
                                <div style="border-bottom: 2px solid #000; padding-bottom: 20px; margin-bottom: 30px;">
                                    <h1 style="margin: 0; font-size: 24px; font-weight: 600; color: #000;">mj-ecom</h1>
                                    <p style="margin: 5px 0 0 0; color: #666; font-size: 14px;">Order Confirmation</p>
                                </div>
                        
                                <!-- Customer Info -->
                                <div style="margin-bottom: 30px;">
                                    <p style="margin: 0; font-size: 16px; color: #333;">Hello <strong>%s</strong>,</p>
                                    <p style="margin: 10px 0 0 0; color: #666;">Your order has been confirmed and is being processed.</p>
                                </div>
                        
                                <!-- Order Details Table -->
                                <table style="width: 100%%; border-collapse: collapse; margin-bottom: 30px; border: 1px solid #ddd;">
                                    <thead>
                                        <tr style="background-color: #f8f9fa;">
                                            <th style="padding: 12px; text-align: left; border-bottom: 1px solid #ddd; font-weight: 600; color: #333;">Order Details</th>
                                            <th style="padding: 12px; text-align: right; border-bottom: 1px solid #ddd; font-weight: 600; color: #333;">Value</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <td style="padding: 12px; border-bottom: 1px solid #eee; color: #666;">Order ID</td>
                                            <td style="padding: 12px; border-bottom: 1px solid #eee; text-align: right; font-weight: 500;">#%d</td>
                                        </tr>
                                        <tr>
                                            <td style="padding: 12px; border-bottom: 1px solid #eee; color: #666;">Status</td>
                                            <td style="padding: 12px; border-bottom: 1px solid #eee; text-align: right;">
                                                <span style="color: #28a745; font-weight: 500;">%s</span>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="padding: 12px; border-bottom: 1px solid #eee; color: #666;">Order Date</td>
                                            <td style="padding: 12px; border-bottom: 1px solid #eee; text-align: right;">%s</td>
                                        </tr>
                                        <tr style="background-color: #f8f9fa;">
                                            <td style="padding: 12px; font-weight: 600; color: #333;">Total Amount</td>
                                            <td style="padding: 12px; text-align: right; font-weight: 600; color: #333; font-size: 18px;">₹%s</td>
                                        </tr>
                                    </tbody>
                                </table>
                        
                                <!-- Items Table -->
                                <div style="margin-bottom: 30px;">
                                    <h3 style="margin-bottom: 15px; font-size: 16px; font-weight: 600; color: #333;">Items Ordered</h3>
                                    <table style="width: 100%%; border-collapse: collapse; border: 1px solid #ddd;">
                                        <thead>
                                            <tr style="background-color: #f8f9fa;">
                                                <th style="padding: 10px; text-align: left; border-bottom: 1px solid #ddd; font-weight: 500; color: #333;">Product</th>
                                                <th style="padding: 10px; text-align: center; border-bottom: 1px solid #ddd; font-weight: 500; color: #333;">Qty</th>
                                                <th style="padding: 10px; text-align: right; border-bottom: 1px solid #ddd; font-weight: 500; color: #333;">Subtotal</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            %s
                                        </tbody>
                                    </table>
                                </div>
                        
                                <!-- Footer -->
                                <div style="border-top: 1px solid #eee; padding-top: 20px; text-align: center;">
                                    <p style="margin: 0; color: #666; font-size: 14px;">
                                        Thank you for your business!
                                    </p>
                                </div>
                            </div>
                        </body>
                        </html>
                        """,
                event.getUserFullName(),
                event.getOrderId(),
                event.getStatus(),
                event.getCreatedAt(),
                event.getTotalAmount(),
                formatOrderItemsTable(event.getOrderItems())
        );
    }

    private String formatOrderItemsHtml(List<OrderItemDTO> items) {
        if (items == null || items.isEmpty()) {
            return "<div style='padding: 20px; text-align: center; color: #666;'>No items found.</div>";
        }

        return items.stream()
                .map(item -> String.format("""
                                <div style="padding: 15px; border-bottom: 1px solid #f0f0f0; display: flex; justify-content: space-between; align-items: center;">
                                    <div style="flex-grow: 1;">
                                        <div style="font-weight: 500; color: #333; margin-bottom: 4px; font-size: 15px;">%s </div>
                                        <div style="color: #666; font-size: 13px;">Quantity: %d × ₹%.2f</div>
                                    </div>
                                    <div style="text-align: right;">
                                        <div style="color: #28a745; font-weight: bold; font-size: 16px;">₹%.2f</div>
                                    </div>
                                </div>
                                """,
                        item.getProductName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .collect(Collectors.joining(""));
    }

    private String formatOrderItemsTable(List<OrderItemDTO> items) {
        if (items == null || items.isEmpty()) {
            return "<tr><td colspan='3' style='padding: 15px; text-align: center; color: #666;'>No items found.</td></tr>";
        }

        return items.stream()
                .map(item -> String.format("""
                                <tr>
                                    <td style="padding: 10px; border-bottom: 1px solid #eee; color: #333;">%s</td>
                                    <td style="padding: 10px; border-bottom: 1px solid #eee; text-align: center; color: #666;">%d</td>
                                    <td style="padding: 10px; border-bottom: 1px solid #eee; text-align: right; font-weight: 500;">₹%.2f</td>
                                </tr>
                                """,
                        item.getProductName(),
                        item.getQuantity(),
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .collect(Collectors.joining(""));
    }
}