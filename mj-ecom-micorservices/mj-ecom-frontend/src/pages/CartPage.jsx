import React, {useState} from "react";
import { useCart } from "../context/CartContext";
import { useNavigate } from "react-router-dom";
import "./CartPage.css";
import { FaPlus, FaMinus, FaTrash } from "react-icons/fa";
import {placeOrder} from "../api/placeOrderAPI";

function CartPage() {
    const { cartItems, setCartItems, changeQuantity, removeFromCart } = useCart();
    const navigate = useNavigate();
    const [placing, setPlacing] = useState(false);
    const [orderError, setOrderError] = useState("");

    // handle place order
    const handlePlaceOrder = async () => {
        setPlacing(true);
        setOrderError('');
        const token = localStorage.getItem("access_token");
        const userObj = JSON.parse(localStorage.getItem("user_info") || "{}");
        const userId = userObj.id || localStorage.getItem("user_id");
        try {
            const response = await placeOrder(token, userId);
            // Clear local cart so the UI is empty after order
            setCartItems([]);
            // Send order data to confirmation page
            navigate("/order-confirmation", { state: { order: response.data } });
        } catch (err) {
            setOrderError("Failed to place order. Try again.");
        } finally {
            setPlacing(false);
        }
    };

    // Total calculation
    const total = cartItems.reduce(
        (sum, item) => sum + item.price * item.quantity, 0
    );

    return (
        <div className="cartpage-bg">
            <div className="cart-header">
                <h1>Your Shopping Cart</h1>
                <button className="back-btn" onClick={() => navigate('/homepage')}>
                    &larr; Continue Shopping
                </button>
            </div>
            {cartItems.length === 0 ? (
                <div className="cart-empty">Your cart is empty!</div>
            ) : (
                <div className="cart-items-list">
                    {cartItems.map(item => (
                        <div className="cart-item" key={item.id}>
                            <img className="cart-img" src={item.imageUrl || "/default-product.png"} alt={item.name} />
                            <div className="cart-details">
                                <div className="cart-item-name">{item.name}</div>
                                <div className="cart-item-meta">
                                    <span className="cart-item-price">&#8377;{item.price}</span>
                                    <span className="cart-item-category">{item.category}</span>
                                </div>
                                <div className="cart-qty-row">
                                    <button className="qty-btn" onClick={() => changeQuantity(item.id, -1)} disabled={item.quantity <= 1}>
                                        <FaMinus />
                                    </button>
                                    <span className="cart-qty">{item.quantity}</span>
                                    <button className="qty-btn" onClick={() => changeQuantity(item.id, +1)}>
                                        <FaPlus />
                                    </button>
                                    <button className="remove-btn" onClick={() => removeFromCart(item.id)}><FaTrash /></button>
                                </div>
                            </div>
                            <div className="cart-item-total">&#8377;{item.price * item.quantity}</div>
                        </div>
                    ))}
                </div>
            )}
            <div className="cart-summary">
                <span>Total to Pay:</span>
                <span className="cart-total">&#8377;{total}</span>
                <button
                    className="place-order-btn"
                    disabled={cartItems.length === 0 || placing}
                    onClick={handlePlaceOrder}
                >
                    {placing ? "Placing..." : "Place Order"}
                </button>
                {orderError && <div className="cart-error">{orderError}</div>}
            </div>
        </div>
    );
}

export default CartPage;
