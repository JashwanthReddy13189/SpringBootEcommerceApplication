import React from "react";
import { useCart } from "../context/CartContext";
import { FaShoppingCart } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import "./Homepage.css";
import AccountDropdown from "./AccountDropdown";
import { useProducts } from "../context/ProductsContext"; // <-- NEW

function Homepage() {
    const { products, loading } = useProducts(); // <-- use context now!
    const [error, setError] = React.useState("");
    const { cartCount, addToCart, error: cartError } = useCart();
    const navigate = useNavigate();

    const userInfo = JSON.parse(localStorage.getItem("user_info")) || {};
    const fullName = `${userInfo.firstName || ""} ${userInfo.lastName || ""}`.trim();

    const logoutFunc = () => {
        localStorage.removeItem('access_token');
        localStorage.removeItem('username');
        navigate('/login');
    };

    return (
        <div className="homepage-bg">
            {/* Header bar with logo, title, and cart */}
            <div className="header-bar">
                <div style={{ display: "flex", alignItems: "center" }}>
                    <img src="/mj-ecom-logo-1.png" alt="logo" className="header-logo" />
                    <span className="header-title">Product Catalog</span>
                </div>
                <div className="header-actions">
                    <button className="cart-btn" onClick={() => navigate('/cart')}>
                        <FaShoppingCart size={26} />
                        {cartCount > 0 && <span className="cart-badge">{cartCount}</span>}
                    </button>
                    <AccountDropdown fullName={fullName} logoutFunc={logoutFunc} />
                </div>
            </div>
            {error && <div className="homepage-error">{error}</div>}
            {cartError && <div className="homepage-error">{cartError}</div>}

            <div className="product-grid">
                {loading ? (
                    <div>Loading products...</div>
                ) : products.length === 0 ? (
                    <div>No products found.</div>
                ) : (
                    products.map((prod) => (
                        <div className="product-card" key={prod.id}>
                            <div className="product-img-wrap">
                                <img
                                    src={prod.imageUrl || "/default-product.png"}
                                    alt={prod.name}
                                    className="product-img"
                                />
                            </div>
                            <div className="product-info">
                                <div className="product-title">{prod.name}</div>
                                <div className="product-category">{prod.category}</div>
                                <div className="product-desc">{prod.description}</div>
                                <div className="product-price">&#8377;{prod.price}</div>
                                <div
                                    className={`stock ${
                                        prod.stockQuantity > 0 ? "avail" : "out"
                                    }`}
                                >
                                    {prod.stockQuantity > 0
                                        ? `In stock: ${prod.stockQuantity}`
                                        : "Out of stock"}
                                </div>
                                <button
                                    className="product-add-btn"
                                    onClick={() => addToCart(prod)}
                                    disabled={prod.stockQuantity === 0 || prod.active === false}
                                >
                                    {prod.active ? "Add to Cart" : "Unavailable"}
                                </button>
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}

export default Homepage;
