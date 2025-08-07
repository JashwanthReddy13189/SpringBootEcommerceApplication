// src/components/auth/LoginPage.jsx

import React, {useState} from 'react';
import {useNavigate, Link} from 'react-router-dom';
import {loginUser} from './authAPI';
import {FaArrowRight, FaRegEye, FaRegEyeSlash} from "react-icons/fa";
import './LoginPage.css';

function LoginPage() {
    const [form, setForm] = useState({username: '', password: ''});
    const [error, setError] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const navigate = useNavigate();

    function handleChange(e) {
        setForm({...form, [e.target.name]: e.target.value});
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setError('');
        try {
            const data = await loginUser(form);
            localStorage.setItem('access_token', data.access_token);
            localStorage.setItem('username', form.username);
            localStorage.setItem("firstname", data.user.firstName);
            localStorage.setItem("firstname", data.user.lastName);
            localStorage.setItem("user_id", data.user.id);
            localStorage.setItem("email", data.user.email);
            localStorage.setItem("user_info", JSON.stringify(data.user));

            navigate('/homepage');
        } catch {
            setError('Invalid credentials!');
        }
    }

    return (
        <div className="page-bg">
            <div className="login-card">
                <div className="login-card-left">
                    <img src="/mj-ecom-logo-main.png" alt="logo" className="login-logo-image"/>
                    <div className="login-welcome">Welcome back</div>
                    <h1 className="login-title">Sign in</h1>
                    <form onSubmit={handleSubmit}>
                        <label className="login-label">Username</label>
                        <input
                            className="login-input-alt"
                            name="username"
                            type="text"
                            placeholder="Enter your username"
                            value={form.username}
                            onChange={handleChange}
                            required
                            autoComplete="username"
                        />

                        <label className="login-label">Password</label>
                        <div className="login-password-wrapper">
                            <input
                                className="login-input-alt"
                                name="password"
                                type={showPassword ? "text" : "password"}
                                placeholder="Password"
                                value={form.password}
                                onChange={handleChange}
                                required
                                autoComplete="current-password"
                            />
                            <span
                                className="login-pw-toggle"
                                onClick={() => setShowPassword(show => !show)}
                                tabIndex={0}
                                aria-label={showPassword ? "Hide password" : "Show password"}
                                role="button"
                            >
                                {showPassword ? <FaRegEyeSlash size={20}/> : <FaRegEye size={20}/>}
                            </span>
                        </div>
                        <div className="login-form-row">
                            <span/>
                            <Link to="/forgot-password" className="login-forgot-link">
                                Forgot Password?
                            </Link>
                        </div>
                        {error && <div className="login-error">{error}</div>}
                        <button type="submit" className="login-btn-alt">
                            SIGN IN <FaArrowRight style={{marginLeft: 8}}/>
                        </button>
                    </form>
                    <div className="login-bottom-text-alt">
                        I don't have an account?{' '}
                        <Link to="/signup" className="login-signup-link">Sign up</Link>
                    </div>
                </div>
                {/* RIGHT SIDE ILLUSTRATION */}
                <div className="login-card-right">
                    <img src="/cart-art.svg" alt="art" className="login-illustration-big"/>
                </div>
            </div>
        </div>
    );
}

export default LoginPage;
