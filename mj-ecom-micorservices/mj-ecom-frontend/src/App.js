import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

import LoginPage from './components/auth/LoginPage';
import SignupPage from './components/auth/SignupPage';
import Dashboard from './pages/Dashboard';
import PrivateRoute from './routes/PrivateRoute';

function App() {
  return (
      <Router>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />
          {/* PrivateRoute is for authenticated access */}
          <Route path="/dashboard" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
          {/* Add more routes as needed */}
          <Route path="*" element={<LoginPage />} />
        </Routes>
      </Router>
  );
}

export default App;
