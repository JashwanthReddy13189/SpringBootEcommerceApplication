// src/api/orderAPI.js
import axios from "axios";

export async function placeOrder(token, userId) {
    return axios.post(
        "http://localhost:8085/api/orders",
        {}, // No body required per your API
        {
            headers: {
                Authorization: `Bearer ${token}`,
                "x-user-id": userId
            }
        }
    );
}
