import { createContext, useState, useContext } from "react";
import axios from "axios";

const CartContext = createContext();

export function CartProvider({ children }) {
    const [cartItems, setCartItems] = useState([]);
    const [error, setError] = useState('');

    // Helper to get current userId safely from localStorage
    function getUserId() {
        // Use 'user_info' (JSON string) if available, fallback to 'user_id'
        const userObj = JSON.parse(localStorage.getItem("user_info") || "{}");
        return userObj.id || localStorage.getItem("user_id");
    }

    // Backend API call to add to cart, with user id header
    const addToCartAPI = async (productId, quantity, token, userId) => {
        return axios.post(
            "http://localhost:8085/api/cart",
            { productId, quantity },
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                    "x-user-id": userId, // <-- use lowercase per REST best practice
                },
            }
        );
    };

    const addToCart = async (product) => {
        const token = localStorage.getItem("access_token");
        const userId = getUserId();
        try {
            await addToCartAPI(product.id, 1, token, userId);
            setCartItems((curr) =>
                curr.find((item) => item.id === product.id)
                    ? curr.map((item) =>
                        item.id === product.id
                            ? { ...item, quantity: item.quantity + 1 }
                            : item
                    )
                    : [...curr, { ...product, quantity: 1 }]
            );
        } catch (err) {
            setError("Failed to add to cart");
            setTimeout(() => setError(''), 2000);
        }
    };

    // For quantity changes and removal: these update only local cart UI unless API is added later
    const changeQuantity = (productId, delta) => {
        setCartItems(curr =>
            curr.map(item =>
                item.id === productId
                    ? { ...item, quantity: Math.max(1, item.quantity + delta) }
                    : item
            )
        );
    };

    const removeFromCart = (productId) => {
        setCartItems(curr => curr.filter(item => item.id !== productId));
    };

    const cartCount = cartItems.reduce((sum, it) => sum + it.quantity, 0);

    return (
        <CartContext.Provider
            value={{
                cartItems,
                addToCart,
                cartCount,
                error,
                changeQuantity,
                removeFromCart,
                setCartItems,
            }}
        >
            {children}
        </CartContext.Provider>
    );
}

export function useCart() {
    return useContext(CartContext);
}
