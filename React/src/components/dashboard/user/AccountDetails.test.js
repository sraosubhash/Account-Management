import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import AccountDetails from './AccountDetails';
import { BrowserRouter } from 'react-router-dom';
import axios from 'axios';
import { toast } from 'react-toastify';

// Mock modules
jest.mock('axios');
jest.mock('react-toastify', () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
  },
}));

const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

describe('AccountDetails Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
  });

  const mockUserData = {
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@example.com',
    mobile: '1234567890',
    alternatePhone: '9876543210',
    role: 'USER',
    address: '123 Main St',
  };

  it('renders loading state initially', () => {
    render(
      <BrowserRouter>
        <AccountDetails />
      </BrowserRouter>
    );

    expect(screen.getByRole('progressbar')).toBeInTheDocument();
  });

  it('fetches and displays user details successfully', async () => {
    localStorage.setItem('token', '"mockToken"');
    localStorage.setItem('userId', '"123"');

    axios.get.mockResolvedValueOnce({ data: mockUserData });

    render(
      <BrowserRouter>
        <AccountDetails />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText(/Account Details/i)).toBeInTheDocument();
      expect(screen.getByText(/Full Name:/i)).toBeInTheDocument();
      expect(screen.getByText(/John Doe/i)).toBeInTheDocument();
      expect(screen.getByText(/Email:/i)).toBeInTheDocument();
      expect(screen.getByText(/john.doe@example.com/i)).toBeInTheDocument();
    });
  });

  it('handles missing token and redirects to login', async () => {
    render(
      <BrowserRouter>
        <AccountDetails />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('Authentication token not found. Redirecting to login...');
      expect(mockNavigate).toHaveBeenCalledWith('/login');
    });
  });

  it('handles missing userId and redirects to login', async () => {
    localStorage.setItem('token', '"mockToken"');

    render(
      <BrowserRouter>
        <AccountDetails />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('User ID not found. Redirecting to login...');
      expect(mockNavigate).toHaveBeenCalledWith('/login');
    });
  });

  it('handles 401 error and forces logout', async () => {
    localStorage.setItem('token', '"mockToken"');
    localStorage.setItem('userId', '"123"');

    axios.get.mockRejectedValueOnce({ response: { status: 401 } });

    render(
      <BrowserRouter>
        <AccountDetails />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('Session expired. Please log in again.');
      expect(localStorage.getItem('token')).toBeNull();
      expect(localStorage.getItem('userId')).toBeNull();
      expect(mockNavigate).toHaveBeenCalledWith('/login');
    });
  });

  it('handles 404 error and displays appropriate message', async () => {
    localStorage.setItem('token', '"mockToken"');
    localStorage.setItem('userId', '"123"');

    axios.get.mockRejectedValueOnce({ response: { status: 404 } });

    render(
      <BrowserRouter>
        <AccountDetails />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText(/User not found. Please try logging in again./i)).toBeInTheDocument();
      expect(toast.error).toHaveBeenCalledWith('User not found. Please try logging in again.');
    });
  });

  it('handles 500 error and displays server error message', async () => {
    localStorage.setItem('token', '"mockToken"');
    localStorage.setItem('userId', '"123"');

    axios.get.mockRejectedValueOnce({ response: { status: 500 } });

    render(
      <BrowserRouter>
        <AccountDetails />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText(/Server error. Please try again later./i)).toBeInTheDocument();
      expect(toast.error).toHaveBeenCalledWith('Server error. Please try again later.');
    });
  });

  it('handles general API failure', async () => {
    localStorage.setItem('token', '"mockToken"');
    localStorage.setItem('userId', '"123"');

    axios.get.mockRejectedValueOnce(new Error('Network error'));

    render(
      <BrowserRouter>
        <AccountDetails />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText(/Error fetching user details/i)).toBeInTheDocument();
      expect(toast.error).toHaveBeenCalledWith('Error fetching user details');
    });
  });

  it('handles logout functionality', async () => {
    localStorage.setItem('token', '"mockToken"');
    localStorage.setItem('userId', '"123"');

    axios.get.mockResolvedValueOnce({ data: mockUserData });

    render(
      <BrowserRouter>
        <AccountDetails />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText(/Logout/i)).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole('button', { name: /Logout/i }));

    await waitFor(() => {
      expect(toast.success).toHaveBeenCalledWith('Logged out successfully!');
      expect(localStorage.getItem('token')).toBeNull();
      expect(localStorage.getItem('userId')).toBeNull();
      expect(mockNavigate).toHaveBeenCalledWith('/login');
    });
  });
});
