import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import Register from './Register';
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

// Mock toast notifications
jest.mock('react-toastify', () => ({
    toast: {
        success: jest.fn(),
        error: jest.fn(),
    },
}));

// Mock Header and Footer for faster rendering
jest.mock('../layout/Header', () => () => <div>Mocked Header</div>);
jest.mock('../layout/Footer', () => () => <div>Mocked Footer</div>);

describe('Register Component', () => {
    beforeEach(() => {
        jest.clearAllMocks();
        localStorage.clear();
    });

    it('renders the registration form correctly', () => {
        render(
            <BrowserRouter>
                <Register />
            </BrowserRouter>
        );

        // Check form fields and buttons
        expect(screen.getByLabelText(/First Name/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Last Name/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Email/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Password/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Confirm Password/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Mobile/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Alternate Phone/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Address/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Security Answer/i)).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /Sign Up/i })).toBeInTheDocument();
    });

    it('updates input fields correctly', async () => {
        render(
            <BrowserRouter>
                <Register />
            </BrowserRouter>
        );

        fireEvent.change(screen.getByLabelText(/First Name/i), { target: { value: 'John' } });
        fireEvent.change(screen.getByLabelText(/Last Name/i), { target: { value: 'Doe' } });
        fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'john@example.com' } });

        await waitFor(() => {
            expect(screen.getByLabelText(/First Name/i)).toHaveValue('John');
            expect(screen.getByLabelText(/Last Name/i)).toHaveValue('Doe');
            expect(screen.getByLabelText(/Email/i)).toHaveValue('john@example.com');
        });
    });

    it('validates password complexity', async () => {
        render(
            <BrowserRouter>
                <Register />
            </BrowserRouter>
        );

        const passwordInput = screen.getByLabelText(/Password/i);
        fireEvent.change(passwordInput, { target: { value: 'weakpass' } });

        await waitFor(() => {
            expect(screen.getByText(/Weak password/i)).toBeInTheDocument();
        });
    });

    it('ensures alternate phone is different from mobile number', async () => {
        render(
            <BrowserRouter>
                <Register />
            </BrowserRouter>
        );

        fireEvent.change(screen.getByLabelText(/Mobile/i), { target: { value: '1234567890' } });
        fireEvent.change(screen.getByLabelText(/Alternate Phone/i), { target: { value: '1234567890' } });

        fireEvent.click(screen.getByRole('button', { name: /Sign Up/i }));

        await waitFor(() => {
            expect(screen.getByText(/Alternate Mobile Number should be different from Mobile Number/i)).toBeInTheDocument();
        });
    });

    it('shows validation errors for empty required fields', async () => {
        render(
            <BrowserRouter>
                <Register />
            </BrowserRouter>
        );

        fireEvent.click(screen.getByRole('button', { name: /Sign Up/i }));

        await waitFor(() => {
            expect(screen.getByText(/First Name is required/i)).toBeInTheDocument();
            expect(screen.getByText(/Last Name is required/i)).toBeInTheDocument();
            expect(screen.getByText(/Valid Email is required/i)).toBeInTheDocument();
            expect(screen.getByText(/Password is required/i)).toBeInTheDocument();
            expect(screen.getByText(/Valid Mobile number is required/i)).toBeInTheDocument();
            expect(screen.getByText(/Address is required/i)).toBeInTheDocument();
        });
    });

    it('checks the role dropdown updates correctly', async () => {
        render(
            <BrowserRouter>
                <Register />
            </BrowserRouter>
        );

        fireEvent.mouseDown(screen.getByLabelText(/Role/i));
        fireEvent.click(screen.getByRole('option', { name: 'EMPLOYEE' }));

        await waitFor(() => {
            expect(screen.getByRole('button', { name: 'EMPLOYEE' })).toBeInTheDocument();
        });
    });

    it('shows success toast and navigates on successful registration', async () => {
        axios.post.mockResolvedValueOnce({ data: { message: 'Signup Successful!' } });

        render(
            <BrowserRouter>
                <Register />
            </BrowserRouter>
        );

        fireEvent.change(screen.getByLabelText(/First Name/i), { target: { value: 'John' } });
        fireEvent.change(screen.getByLabelText(/Last Name/i), { target: { value: 'Doe' } });
        fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'john@example.com' } });
        fireEvent.change(screen.getByLabelText(/Password/i), { target: { value: 'Password@123' } });
        fireEvent.change(screen.getByLabelText(/Confirm Password/i), { target: { value: 'Password@123' } });
        fireEvent.change(screen.getByLabelText(/Mobile/i), { target: { value: '1234567890' } });
        fireEvent.change(screen.getByLabelText(/Security Answer/i), { target: { value: 'Blue' } });

        fireEvent.click(screen.getByRole('button', { name: /Sign Up/i }));

        await waitFor(() => {
            expect(toast.success).toHaveBeenCalledWith('Signup Successful! Please log in.');
            expect(mockNavigate).toHaveBeenCalledWith('/login');
        });
    });

    it('shows error toast on failed registration', async () => {
        axios.post.mockRejectedValueOnce({ response: { data: 'Registration failed' } });

        render(
            <BrowserRouter>
                <Register />
            </BrowserRouter>
        );

        fireEvent.change(screen.getByLabelText(/First Name/i), { target: { value: 'John' } });
        fireEvent.change(screen.getByLabelText(/Last Name/i), { target: { value: 'Doe' } });
        fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'john@example.com' } });
        fireEvent.change(screen.getByLabelText(/Password/i), { target: { value: 'Password@123' } });
        fireEvent.change(screen.getByLabelText(/Confirm Password/i), { target: { value: 'Password@123' } });
        fireEvent.change(screen.getByLabelText(/Mobile/i), { target: { value: '1234567890' } });
        fireEvent.change(screen.getByLabelText(/Security Answer/i), { target: { value: 'Blue' } });

        fireEvent.click(screen.getByRole('button', { name: /Sign Up/i }));

        await waitFor(() => {
            expect(toast.error).toHaveBeenCalledWith('Registration failed');
        });
    });

    it('handles unexpected registration errors', async () => {
        axios.post.mockRejectedValueOnce(new Error('Network error'));

        render(
            <BrowserRouter>
                <Register />
            </BrowserRouter>
        );

        fireEvent.click(screen.getByRole('button', { name: /Sign Up/i }));

        await waitFor(() => {
            expect(toast.error).toHaveBeenCalledWith('An error occurred. Please try again.');
        });
    });
});
