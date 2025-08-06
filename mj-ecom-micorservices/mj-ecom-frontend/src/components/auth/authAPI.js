import axios from 'axios';

const API_URL = "http://localhost:8085/api/users";

export async function loginUser({ username, password }) {
    const res = await axios.post(`${API_URL}/login`, { username, password });
    return res.data; // { access_token: "..." }
}

export async function registerUser(form) {
    console.log("received payload request");
    const payload = {
        userName: form.userName,
        password: form.password,
        firstName: form.firstName,
        lastName: form.lastName,
        email: form.email,
        phone: form.phone,
        address: {
            street: form.street,
            city: form.city,
            state: form.state,
            country: form.country,
            zipcode: form.zipcode,
        }
    };
    console.log("Sending signup payload", payload);
    return await axios.post(`${API_URL}`, payload);
}
