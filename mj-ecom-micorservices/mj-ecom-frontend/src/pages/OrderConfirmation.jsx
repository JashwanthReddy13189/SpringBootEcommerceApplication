import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./OrderConfirmation.css";

function OrderConfirmation() {
    const { state } = useLocation();
    const navigate = useNavigate();

    // Defensive: state could be undefined if this page is accessed directly
    const order = state?.order;
    if (!order) {
        return (
            <div className="order-confirm-bg">
                <h2>No order found!</h2>
                <button onClick={() => navigate("/homepage")}>Back to homepage</button>
            </div>
        );
    }

    return (
        <div className="order-confirm-bg">
            <div className="order-confirm-card">
                <h2>Thank you for your order!</h2>
                <div className="order-info-row"><b>Order ID:</b> {order.id}</div>
                <div className="order-info-row"><b>Status:</b> {order.status}</div>
                <div className="order-info-row"><b>Placed At:</b> {order.createdAt}</div>
                <div className="order-info-row"><b>Total Paid:</b> &#8377;{order.totalAmount}</div>

                <h3 style={{marginTop: "20px"}}>Order Items:</h3>
                <div className="order-items-list">
                    <table style={{width: "100%"}}>
                        <thead>
                        <tr>
                            <th>Product ID</th>
                            <th>Qty</th>
                            <th>Price</th>
                            <th>Subtotal</th>
                        </tr>
                        </thead>
                        <tbody>
                        {order.items.map((item) => (
                            <tr key={item.id}>
                                <td>{item.productId}</td>
                                <td>{item.quantity}</td>
                                <td>&#8377;{item.price}</td>
                                <td>&#8377;{item.subTotal}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
                <button className="order-home-btn" onClick={() => navigate("/homepage")}>Continue Shopping</button>
            </div>
        </div>
    );
}

export default OrderConfirmation;
