import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import Login from './Login';
import axios from 'axios';
import { toast } from 'react-toastify';

// Mock axios to prevent real network calls
jest.mock('axios');

// Mock useNavigate from react-router-dom
const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useNavigate: () => mockNavigate,
}));

// Mock toast notifications to prevent actual notifications
jest.mock('react-toastify', () => ({
    toast: {
        success: jest.fn(),
        error: jest.fn(),
    },
}));

// Mock Header and Footer for faster rendering
jest.mock('../layout/Header', () => () => <div>Mocked Header</div>);
jest.mock('../layout/Footer', () => () => <div>Mocked Footer</div>);

describe('Login Component', () => {
    beforeEach(() => {
        jest.clearAllMocks();
        localStorage.clear();
    });

    it('renders the login form correctly', () => {
        render(
            <BrowserRouter>
                <Login />
            </BrowserRouter>
        );

        // Check form fields and buttons
        expect(screen.getByLabelText(/Mobile Number or Email/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Password/i)).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /Login/i })).toBeInTheDocument();
    });

    it('updates input fields correctly', () => {
        render(
            <BrowserRouter>
                <Login />
            </BrowserRouter>
        );

        const identifierInput = screen.getByLabelText(/Mobile Number or Email/i);
        const passwordInput = screen.getByLabelText(/Password/i);

        fireEvent.change(identifierInput, { target: { value: 'test@example.com' } });
        fireEvent.change(passwordInput, { target: { value: 'password123' } });

        expect(identifierInput.value).toBe('test@example.com');
        expect(passwordInput.value).toBe('password123');
    });

    it('shows success toast and navigates on successful login', async () => {
        axios.post.mockResolvedValueOnce({
            data: {
                token: 'mocked-token',
                user: {
                    role: 'user',
                    id: '12345',
                },
            },
        });

        render(
            <BrowserRouter>
                <Login />
            </BrowserRouter>
        );

        const identifierInput = screen.getByLabelText(/Mobile Number or Email/i);
        const passwordInput = screen.getByLabelText(/Password/i);
        const loginButton = screen.getByRole('button', { name: /Login/i });

        fireEvent.change(identifierInput, { target: { value: 'test@example.com' } });
        fireEvent.change(passwordInput, { target: { value: 'password123' } });
        fireEvent.click(loginButton);

        await waitFor(() => {
            expect(localStorage.getItem('userToken')).toBe('mocked-token');
            expect(localStorage.getItem('userRole')).toBe('user');
            expect(localStorage.getItem('userId')).toBe('12345');
            expect(toast.success).toHaveBeenCalledWith('Login successful!');
            expect(mockNavigate).toHaveBeenCalledWith('/dashboard');
        });
    });

    it('shows error toast on failed login', async () => {
        axios.post.mockRejectedValueOnce(new Error('Login failed'));

        render(
            <BrowserRouter>
                <Login />
            </BrowserRouter>
        );

        const identifierInput = screen.getByLabelText(/Mobile Number or Email/i);
        const passwordInput = screen.getByLabelText(/Password/i);
        const loginButton = screen.getByRole('button', { name: /Login/i });

        fireEvent.change(identifierInput, { target: { value: 'wrong@example.com' } });
        fireEvent.change(passwordInput, { target: { value: 'wrongpassword' } });
        fireEvent.click(loginButton);

        await waitFor(() => {
            expect(toast.error).toHaveBeenCalledWith('Wrong username or password');
        });
    });
});
