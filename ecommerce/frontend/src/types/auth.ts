export interface LoginRequest {
    email: string;
    password: string;
}

export interface RegisterRequest {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    shopName?: string;
}

export interface AuthResponse {
    token: string;
    type: string;
    id: number;
    email: string;
    roles: string[];
}

export interface User {
    id: number;
    email: string;
    firstName: string;
    lastName: string;
    shopName?: string;
    isActive: boolean;
    isVerified: boolean;
    roles: string[];
    createdAt: string;
    updatedAt: string;
}
