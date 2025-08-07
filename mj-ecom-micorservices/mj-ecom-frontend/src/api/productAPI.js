// src/api/productAPI.js
import axios from 'axios';

const API_URL = "http://localhost:8085/api/products"; // Update to match your backend

export async function getAllProducts(token) {
    return axios.get(API_URL, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });
}
