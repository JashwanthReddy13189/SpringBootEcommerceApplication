import React, { useState, useRef, useEffect } from "react";
import { FaUserCircle, FaSignOutAlt, FaClipboardList, FaUserEdit } from "react-icons/fa";
import { useNavigate } from "react-router-dom";

export default function AccountDropdown({ fullName, logoutFunc }) {
    const [open, setOpen] = useState(false);
    const ref = useRef();
    const navigate = useNavigate();

    useEffect(() => {
        function handleClick(e) {
            if (ref.current && !ref.current.contains(e.target)) setOpen(false);
        }
        if (open) document.addEventListener("mousedown", handleClick);
        return () => document.removeEventListener("mousedown", handleClick);
    }, [open]);

    return (
        <div ref={ref} className="dropdown-account-root" style={{ position: "relative" }}>
            <button
                className="dropdown-account-btn"
                onClick={() => setOpen((open) => !open)}
            >
                <FaUserCircle size={24} style={{ marginRight: 8 }} />
                <span className="dropdown-account-name">{fullName}</span>
            </button>
            {open && (
                <div className="dropdown-account-menu">
                    <div
                        className="dropdown-account-item"
                        onClick={() => { setOpen(false); navigate("/profile"); }}
                    >
                        <FaUserEdit style={{ marginRight: 8 }} /> Profile Details
                    </div>
                    <div
                        className="dropdown-account-item"
                        onClick={() => { setOpen(false); navigate("/orders"); }}
                    >
                        <FaClipboardList style={{ marginRight: 8 }} /> Orders
                    </div>
                    <div
                        className="dropdown-account-item"
                        onClick={() => { setOpen(false); logoutFunc(); }}
                        style={{ color: "#e24e4e" }}
                    >
                        <FaSignOutAlt style={{ marginRight: 8 }} /> Logout
                    </div>
                </div>
            )}
        </div>
    );
}
