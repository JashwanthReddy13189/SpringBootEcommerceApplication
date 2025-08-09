import React, { createContext, useState, useEffect, useContext } from "react";
import { getAllProducts } from "../api/productAPI";

const ProductsContext = createContext();

export function ProductsProvider({ children }) {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        async function fetchProducts() {
            setLoading(true);
            try {
                const token = localStorage.getItem("access_token");
                const res = await getAllProducts(token);
                setProducts(res.data);
            } catch (err) {
                setProducts([]);
            } finally {
                setLoading(false);
            }
        }
        fetchProducts();
    }, []);

    return (
        <ProductsContext.Provider value={{ products, loading }}>
            {children}
        </ProductsContext.Provider>
    );
}

export function useProducts() {
    return useContext(ProductsContext);
}
