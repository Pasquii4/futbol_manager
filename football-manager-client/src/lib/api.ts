import axios from 'axios';

// Create axios instance with base URL
const api = axios.create({
    baseURL: 'http://127.0.0.1:8080/api', // Spring Boot Backend URL (IPv4 forced)
    headers: {
        'Content-Type': 'application/json',
    },
});

// Response interceptor for error handling
api.interceptors.response.use(
    (response) => response,
    (error) => {
        console.error('API Error:', error.response?.data || error.message);
        return Promise.reject(error);
    }
);

export default api;
