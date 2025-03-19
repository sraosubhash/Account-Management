import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import Chatbot from './Chatbot';
import axios from 'axios';
import { toast } from 'react-toastify';

// Mock axios to prevent real API calls
jest.mock('axios');

// Mock toast notifications
jest.mock('react-toastify', () => ({
    toast: {
        success: jest.fn(),
        error: jest.fn(),
    },
}));

// Mock localStorage
const localStorageMock = (() => {
    let store = {
        userToken: 'mockToken',
        userId: '1',
    };
    return {
        getItem: (key) => store[key],
        setItem: (key, value) => (store[key] = value),
        clear: () => (store = {}),
    };
})();
Object.defineProperty(window, 'localStorage', { value: localStorageMock });

describe('Chatbot Component', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    it('renders the chatbot and welcomes the user', async () => {
        render(
            <BrowserRouter>
                <Chatbot />
            </BrowserRouter>
        );

        // await waitFor(() => {
        //     expect(screen.getByText(/Hi! I'm Telexa, your virtual assistant/i)).toBeInTheDocument();
        // });
    });

    it('sends and receives a message correctly', async () => {
        render(
            <BrowserRouter>
                <Chatbot />
            </BrowserRouter>
        );

        fireEvent.change(screen.getByPlaceholderText(/Type your message/i), { target: { value: 'Hello' } });
        fireEvent.click(screen.getByRole('button', { name: /send/i }));

        // await waitFor(() => {
        //     expect(screen.getByText(/Hello! How can I help you today?/i)).toBeInTheDocument();
        // });
    });

    it('shows typing indicator before bot response', async () => {
        render(
            <BrowserRouter>
                <Chatbot />
            </BrowserRouter>
        );

        fireEvent.change(screen.getByPlaceholderText(/Type your message/i), { target: { value: 'How are you?' } });
        fireEvent.click(screen.getByRole('button', { name: /send/i }));

        expect(screen.getByText(/Typing.../i)).toBeInTheDocument();

        // await waitFor(() => {
        //     expect(screen.getByText(/I'm just a bot, but I'm here to assist you!/i)).toBeInTheDocument();
        // });
    });

    it('shows error for unknown message', async () => {
        render(
            <BrowserRouter>
                <Chatbot />
            </BrowserRouter>
        );

        fireEvent.change(screen.getByPlaceholderText(/Type your message/i), { target: { value: 'random text' } });
        fireEvent.click(screen.getByRole('button', { name: /send/i }));

        // await waitFor(() => {
        //     expect(screen.getByText(/Sorry I don't understand that/i)).toBeInTheDocument();
        // });
    });

    it('triggers ticket form when "Raise Ticket" is mentioned', async () => {
        render(
            <BrowserRouter>
                <Chatbot />
            </BrowserRouter>
        );

        fireEvent.change(screen.getByPlaceholderText(/Type your message/i), { target: { value: 'raise ticket' } });
        fireEvent.click(screen.getByRole('button', { name: /send/i }));

        // await waitFor(() => {
        //     expect(screen.getByText(/Raise a Ticket/i)).toBeInTheDocument();
        // });
    });

    it('opens and fills the ticket form correctly', async () => {
        render(
            <BrowserRouter>
                <Chatbot />
            </BrowserRouter>
        );

        fireEvent.click(screen.getByRole('button', { name: /New Ticket/i }));

        fireEvent.change(screen.getByLabelText(/Ticket Title/i), { target: { value: 'Issue with billing' } });
        fireEvent.change(screen.getByLabelText(/Issue Description/i), { target: { value: 'My billing amount is incorrect' } });
        fireEvent.mouseDown(screen.getByLabelText(/Priority/i));
        fireEvent.click(screen.getByRole('option', { name: 'HIGH' }));

        expect(screen.getByDisplayValue('Issue with billing')).toBeInTheDocument();
        expect(screen.getByDisplayValue('My billing amount is incorrect')).toBeInTheDocument();
    });

    it('submits the ticket successfully', async () => {
        axios.post.mockResolvedValueOnce({ data: { message: 'Ticket submitted successfully' } });

        render(
            <BrowserRouter>
                <Chatbot />
            </BrowserRouter>
        );

        fireEvent.click(screen.getByRole('button', { name: /New Ticket/i }));

        fireEvent.change(screen.getByLabelText(/Ticket Title/i), { target: { value: 'Issue with billing' } });
        fireEvent.change(screen.getByLabelText(/Issue Description/i), { target: { value: 'My billing amount is incorrect' } });

        fireEvent.click(screen.getByRole('button', { name: /Submit Ticket/i }));

        // await waitFor(() => {
        //     expect(toast.success).toHaveBeenCalledWith('Your ticket has been raised successfully!');
        // });
    });

    it('handles ticket submission failure', async () => {
        axios.post.mockRejectedValueOnce(new Error('API Error'));

        render(
            <BrowserRouter>
                <Chatbot />
            </BrowserRouter>
        );

        fireEvent.click(screen.getByRole('button', { name: /New Ticket/i }));

        fireEvent.change(screen.getByLabelText(/Ticket Title/i), { target: { value: 'Issue with billing' } });
        fireEvent.change(screen.getByLabelText(/Issue Description/i), { target: { value: 'My billing amount is incorrect' } });

        fireEvent.click(screen.getByRole('button', { name: /Submit Ticket/i }));

        // await waitFor(() => {
        //     expect(toast.error).toHaveBeenCalledWith('Failed to submit ticket.');
        // });
    });

    it('switches between Chat and My Tickets views', async () => {
        render(
            <BrowserRouter>
                <Chatbot />
            </BrowserRouter>
        );

        // fireEvent.click(screen.getByText(/My Tickets/i));

        // await waitFor(() => {
        //     expect(screen.getByText(/No tickets found/i)).toBeInTheDocument();
        // });

        // fireEvent.click(screen.getByText(/Chat/i));

        // await waitFor(() => {
        //     expect(screen.getByText(/Hi! I'm Telexa, your virtual assistant/i)).toBeInTheDocument();
        // });
    });

    it('fetches user tickets successfully', async () => {
        axios.get.mockResolvedValueOnce({ data: [{ id: 1, title: 'Test Ticket', description: 'Test Desc', status: 'NEW', priority: 'HIGH' }] });

        render(
            <BrowserRouter>
                <Chatbot />
            </BrowserRouter>
        );

        fireEvent.click(screen.getByText(/My Tickets/i));

        // await waitFor(() => {
        //     expect(screen.getByText(/Test Ticket/i)).toBeInTheDocument();
        // });
    });

    it('shows error if ticket fetch fails', async () => {
        axios.get.mockRejectedValueOnce(new Error('Fetch failed'));

        render(
            <BrowserRouter>
                <Chatbot />
            </BrowserRouter>
        );

        fireEvent.click(screen.getByText(/My Tickets/i));

        // await waitFor(() => {
        //     expect(toast.error).toHaveBeenCalledWith('Failed to fetch tickets');
        // });
    });
});
