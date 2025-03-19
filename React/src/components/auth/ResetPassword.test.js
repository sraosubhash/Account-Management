import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import ResetPassword from './ResetPassword';
import axios from 'axios';
import { toast } from 'react-toastify';

// Mock axios to prevent real API calls
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

describe('ResetPassword Component', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    it('renders the reset password form correctly', () => {
        render(
            <BrowserRouter>
                <ResetPassword />
            </BrowserRouter>
        );

        expect(screen.getByText(/Reset Password/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Email/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Security Answer/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/New Password/i)).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /Reset Password/i })).toBeInTheDocument();
    });

    it('updates input fields correctly', async () => {
        render(
            <BrowserRouter>
                <ResetPassword />
            </BrowserRouter>
        );

        fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'test@example.com' } });
        fireEvent.change(screen.getByLabelText(/Security Answer/i), { target: { value: 'Blue' } });
        fireEvent.change(screen.getByLabelText(/New Password/i), { target: { value: 'Password@123' } });

        await waitFor(() => {
            expect(screen.getByLabelText(/Email/i)).toHaveValue('test@example.com');
            expect(screen.getByLabelText(/Security Answer/i)).toHaveValue('Blue');
            expect(screen.getByLabelText(/New Password/i)).toHaveValue('Password@123');
        });
    });

    it('validates required fields', async () => {
        render(
            <BrowserRouter>
                <ResetPassword />
            </BrowserRouter>
        );

        fireEvent.click(screen.getByRole('button', { name: /Reset Password/i }));

        await waitFor(() => {
            expect(screen.getByText(/Reset password failed. Please check your inputs./i)).toBeInTheDocument();
            expect(toast.error).toHaveBeenCalledWith('Reset password failed. Please check your inputs.');
        });
    });

    it('submits form successfully and navigates on success', async () => {
        axios.post.mockResolvedValueOnce({ data: { message: 'Password reset successful!' } });

        render(
            <BrowserRouter>
                <ResetPassword />
            </BrowserRouter>
        );

        fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'test@example.com' } });
        fireEvent.change(screen.getByLabelText(/Security Answer/i), { target: { value: 'Blue' } });
        fireEvent.change(screen.getByLabelText(/New Password/i), { target: { value: 'Password@123' } });

        fireEvent.click(screen.getByRole('button', { name: /Reset Password/i }));

        await waitFor(() => {
            expect(toast.success).toHaveBeenCalledWith('Password reset successful! Please log in with your new password.');
            expect(mockNavigate).toHaveBeenCalledWith('/login');
        });
    });

    it('handles API failure gracefully', async () => {
        axios.post.mockRejectedValueOnce(new Error('Reset failed'));

        render(
            <BrowserRouter>
                <ResetPassword />
            </BrowserRouter>
        );

        fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'test@example.com' } });
        fireEvent.change(screen.getByLabelText(/Security Answer/i), { target: { value: 'Blue' } });
        fireEvent.change(screen.getByLabelText(/New Password/i), { target: { value: 'Password@123' } });

        fireEvent.click(screen.getByRole('button', { name: /Reset Password/i }));

        await waitFor(() => {
            expect(toast.error).toHaveBeenCalledWith('Reset password failed. Please check your inputs.');
        });
    });

    it('ensures dropdown updates correctly', async () => {
        render(
            <BrowserRouter>
                <ResetPassword />
            </BrowserRouter>
        );

        fireEvent.mouseDown(screen.getByLabelText(/Security Question/i));
        fireEvent.click(screen.getByRole('option', { name: /What is your favorite color?/i }));

        await waitFor(() => {
            expect(screen.getByRole('button', { name: /What is your favorite color?/i })).toBeInTheDocument();
        });
    });
});
