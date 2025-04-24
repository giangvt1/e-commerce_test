import axios from 'axios';
import { AuthResponse, LoginRequest, RegisterRequest, User } from '../types/auth';

const API_URL = 'http://localhost:8080/api/auth';

export const authService = {
    async login(data: LoginRequest): Promise<AuthResponse> {
        const response = await axios.post(`${API_URL}/login`, data);
        if (response.data.token) {
            localStorage.setItem('token', response.data.token);
        }
        return response.data;
    },

    async register(data: RegisterRequest): Promise<string> {
        const response = await axios.post(`${API_URL}/register`, data);
        return response.data;
    },

    async getProfile(): Promise<User> {
        const token = localStorage.getItem('token');
        const response = await axios.get(`${API_URL}/profile`, {
            headers: { Authorization: `Bearer ${token}` }
        });
        return response.data;
    },

    logout(): void {
        localStorage.removeItem('token');
    },

    getToken(): string | null {
        return localStorage.getItem('token');
    }
};
