import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useProducts } from "../context/ProductsContext";
import axios from "axios";
import "./OrdersPage.css";

function OrdersPage() {
    const [orders, setOrders] = useState([]);
    const [error, setError] = useState("");
    const navigate = useNavigate();
    const { products } = useProducts();

    // Build ID → name map
    const productIdNameMap = {};
    products.forEach(p => productIdNameMap[p.id] = p.name);

    useEffect(() => {
        async function fetchOrders() {
            try {
                const token = localStorage.getItem("access_token");
                const userObj = JSON.parse(localStorage.getItem("user_info") || "{}");
                const userId = userObj.id || localStorage.getItem("user_id");
                const response = await axios.get("http://localhost:8085/api/orders", {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "X-User-ID": userId,
                    },
                });
                setOrders(response.data);
            } catch {
                setError("Failed to fetch orders. Please try again later.");
            }
        }
        fetchOrders();
    }, []);

    return (
        <div className="orders-bg">
            <div className="orders-card">
                <h2>Your Orders</h2>
                <button
                    className="orders-back-btn"
                    onClick={() => navigate("/homepage")}
                >
                    Continue Shopping
                </button>
                {error && <div className="orders-error">{error}</div>}
                {orders.length === 0 ? (
                    <div>You have not placed any orders yet.</div>
                ) : (
                    <table className="orders-table">
                        <thead>
                        <tr>
                            <th>Order Id</th>
                            <th>Total</th>
                            <th>Status</th>
                            <th>Placed At</th>
                            <th>Items</th>
                        </tr>
                        </thead>
                        <tbody>
                        {orders.map(order => (
                            <tr key={order.id}>
                                <td>{order.id}</td>
                                <td>&#8377;{order.totalAmount}</td>
                                <td>{order.status}</td>
                                <td>{order.createdAt}</td>
                                <td>
                                    <ul>
                                        {order.items.map(item => (
                                            <li key={item.id}>
                                                {productIdNameMap[item.productId] || item.productId} × {item.quantity}
                                            </li>
                                        ))}
                                    </ul>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
}

export default OrdersPage;
