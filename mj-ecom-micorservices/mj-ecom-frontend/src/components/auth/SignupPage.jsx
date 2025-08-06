// src/components/auth/SignupPage.jsx

import React, {useState} from 'react';
import {useNavigate, Link} from 'react-router-dom';
import {FaRegEye, FaRegEyeSlash, FaArrowRight} from "react-icons/fa";
import './LoginPage.css';
import {registerUser} from "./authAPI";

function SignupPage() {
    const [form, setForm] = useState({
        userName: "",
        password: "",
        firstName: "",
        lastName: "",
        email: "",
        phone: "",
        street: "",
        city: "",
        state: "",
        country: "",
        zipcode: ""
    });
    const [showPassword, setShowPassword] = useState(false);
    const [error, setError] = useState('');
    const [successMsg, setSuccessMsg] = useState('');
    const navigate = useNavigate();

    function handleChange(e) {
        setForm({...form, [e.target.name]: e.target.value});
    }

    async function handleSubmit(e) {
        console.log("Submitting signup", form)
        e.preventDefault();
        setError('');
        setSuccessMsg('');

        try {
            await registerUser(form);
            setSuccessMsg('Registered user ' + form.userName + " Redirecting to login...");
            setTimeout(() => navigate('/login'), 1500);
        } catch (err) {
            setError(
                err?.response?.data?.message || "Register failed. Please check inputs and try again.",
            );
        }

    }

    return (
        <div className="page-bg">
            <div className="login-card">   {/* Keep same card as login */}
                <div className="login-card-left" style={{margin: '0 auto'}}>
                    <img src="/mj-ecom-logo-1.png" alt="logo" className="login-logo-image"/>
                    <div className="login-welcome">Get started with MJ Ecom!</div>
                    <h1 className="login-title">Sign up</h1>
                    <form onSubmit={handleSubmit}>
                        <label className="login-label">Username</label>
                        <input
                            className="login-input-alt"
                            name="userName"
                            placeholder="Choose a username"
                            value={form.userName}
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
                                placeholder="Create a password"
                                value={form.password}
                                onChange={handleChange}
                                required
                                autoComplete="new-password"
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
                        <label className="login-label">First Name</label>
                        <input
                            className="login-input-alt"
                            name="firstName"
                            placeholder="First name"
                            value={form.firstName}
                            onChange={handleChange}
                            required
                        />
                        <label className="login-label">Last Name</label>
                        <input
                            className="login-input-alt"
                            name="lastName"
                            placeholder="Last name"
                            value={form.lastName}
                            onChange={handleChange}
                            required
                        />
                        <label className="login-label">Email</label>
                        <input
                            className="login-input-alt"
                            name="email"
                            type="email"
                            placeholder="your@email.com"
                            value={form.email}
                            onChange={handleChange}
                            required
                            autoComplete="email"
                        />
                        <label className="login-label">Phone</label>
                        <input
                            className="login-input-alt"
                            name="phone"
                            placeholder="Phone"
                            value={form.phone}
                            onChange={handleChange}
                            required
                        />
                        <div style={{
                            margin: "1.7rem 0 .7rem 0",
                            fontWeight: 600,
                            color: "#ff8656",
                            fontSize: "1.1rem"
                        }}>Address
                        </div>
                        <label className="login-label">Street</label>
                        <input
                            className="login-input-alt"
                            name="street"
                            placeholder="Street address"
                            value={form.street}
                            onChange={handleChange}
                            required
                        />
                        <label className="login-label">City</label>
                        <input
                            className="login-input-alt"
                            name="city"
                            placeholder="City"
                            value={form.city}
                            onChange={handleChange}
                            required
                        />
                        <label className="login-label">State</label>
                        <input
                            className="login-input-alt"
                            name="state"
                            placeholder="State"
                            value={form.state}
                            onChange={handleChange}
                            required
                        />
                        <label className="login-label">Country</label>
                        <input
                            className="login-input-alt"
                            name="country"
                            placeholder="Country"
                            value={form.country}
                            onChange={handleChange}
                            required
                        />
                        <label className="login-label">Zipcode</label>
                        <input
                            className="login-input-alt"
                            name="zipcode"
                            placeholder="Zipcode"
                            value={form.zipcode}
                            onChange={handleChange}
                            required
                        />
                        {error && <div className="login-error">{error}</div>}
                        {successMsg && <div style={{color: "#31a354", margin: "1rem 0"}}>{successMsg}</div>}
                        <button type="submit" className="login-btn-alt" style={{marginTop: 24}}>
                            SIGN UP <FaArrowRight style={{marginLeft: 8}}/>
                        </button>
                    </form>
                    <div className="login-bottom-text-alt">
                        Already have an account?{' '}
                        <Link to="/login" className="login-signup-link">Sign in</Link>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default SignupPage;
