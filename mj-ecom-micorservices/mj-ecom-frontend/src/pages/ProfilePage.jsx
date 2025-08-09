import React, {useState} from "react";
import axios from "axios";
import "./ProfilePage.css";

function ProfilePage() {
    const userData = JSON.parse(localStorage.getItem("user_info") || "{}");
    // Ensure address object exists
    const [form, setForm] = useState({
        ...userData,
        address: userData.address || {
            street: "",
            city: "",
            state: "",
            country: "",
            zipcode: ""
        }
    });
    const [success, setSuccess] = useState("");
    const [error, setError] = useState("");

    function handleChange(e) {
        const {name, value} = e.target;
        // Address fields
        if (
            ["street", "city", "state", "country", "zipcode"].includes(name)
        ) {
            setForm((f) => ({
                ...f,
                address: {...f.address, [name]: value}
            }));
        } else {
            setForm((f) => ({...f, [name]: value}));
        }
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");
        setSuccess("");
        try {
            const token = localStorage.getItem("access_token");
            const userId = form.id;
            const {data} = await axios.put(
                `http://localhost:8085/api/users/${userId}`,
                {
                    firstName: form.firstName,
                    lastName: form.lastName,
                    email: form.email,
                    phone: form.phone,
                    address: {
                        street: form.address.street,
                        city: form.address.city,
                        state: form.address.state,
                        country: form.address.country,
                        zipcode: form.address.zipcode
                    }
                },
                {headers: {Authorization: `Bearer ${token}`}}
            );
            // Since your backend returns a string, you'll likely want
            // to separately refresh user details from backend after update.
            setSuccess("Profile updated successfully!");
            // Optionally, trigger a user info refresh here or after update
        } catch (err) {
            setError("Failed to update profile.");
        }
    }

    return (
        <div className="profile-bg">
            <div className="profile-card">
                <h2>Your Profile Details</h2>
                <form onSubmit={handleSubmit} className="profile-form">
                    <div className="form-row">
                        <div className="form-field">
                            <label>First Name</label>
                            <input name="firstName" value={form.firstName || ""} onChange={handleChange} required/>
                        </div>
                        <div className="form-field">
                            <label>Last Name</label>
                            <input name="lastName" value={form.lastName || ""} onChange={handleChange} required/>
                        </div>
                    </div>
                    <div className="form-row">
                        <div className="form-field">
                            <label>Email</label>
                            <input name="email" value={form.email || ""} onChange={handleChange} required/>
                        </div>
                        <div className="form-field">
                            <label>Phone</label>
                            <input name="phone" value={form.phone || ""} onChange={handleChange} required/>
                        </div>
                    </div>

                    <fieldset className="address-fieldset">
                        <legend>Address</legend>
                        <div className="form-row">
                            <div className="form-field">
                                <label>Street</label>
                                <input name="street" value={form.address?.street || ""} onChange={handleChange}/>
                            </div>
                            <div className="form-field">
                                <label>City</label>
                                <input name="city" value={form.address?.city || ""} onChange={handleChange}/>
                            </div>
                        </div>
                        <div className="form-row">
                            <div className="form-field">
                                <label>State</label>
                                <input name="state" value={form.address?.state || ""} onChange={handleChange}/>
                            </div>
                            <div className="form-field">
                                <label>Country</label>
                                <input name="country" value={form.address?.country || ""} onChange={handleChange}/>
                            </div>
                        </div>
                        <div className="form-row">
                            <div className="form-field">
                                <label>Zipcode</label>
                                <input name="zipcode" value={form.address?.zipcode || ""} onChange={handleChange}/>
                            </div>
                        </div>
                    </fieldset>
                    <button type="submit">Update Profile</button>
                </form>
                {success && <div className="profile-success">{success}</div>}
                {error && <div className="profile-error">{error}</div>}
            </div>
        </div>
    );
}

export default ProfilePage;
