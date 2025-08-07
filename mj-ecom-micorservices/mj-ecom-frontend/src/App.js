import React from 'react';
import {BrowserRouter as Router, Routes, Route} from 'react-router-dom';

import LoginPage from './components/auth/LoginPage';
import SignupPage from './components/auth/SignupPage';
import PrivateRoute from './routes/PrivateRoute';
import Homepage from './pages/Homepage';
import {CartProvider} from './context/CartContext';
import CartPage from "./pages/CartPage";
import OrderConfirmation from "./pages/OrderConfirmation";

function App() {
    return (
        <CartProvider>
            <Router>
                <Routes>
                    <Route path="/login" element={<LoginPage/>}/>
                    <Route path="/signup" element={<SignupPage/>}/>
                    <Route path="/homepage" element={<PrivateRoute><Homepage/></PrivateRoute>}/>
                    <Route path="/cart" element={<PrivateRoute><CartPage/></PrivateRoute>} />
                    <Route path="/order-confirmation" element={<PrivateRoute><OrderConfirmation /></PrivateRoute>} />
                    <Route path="*" element={<LoginPage/>}/>
                </Routes>
            </Router>
        </CartProvider>
    );
}

export default App;
