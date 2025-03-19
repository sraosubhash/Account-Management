import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import PlanHistory from './PlanHistory';
import { PlanContext } from '../../../context/PlanContext';
import { toast } from 'react-toastify';

// Mock toast notifications
jest.mock('react-toastify', () => ({
    toast: {
        success: jest.fn(),
        error: jest.fn(),
    },
}));

// Mock localStorage
beforeEach(() => {
    Storage.prototype.getItem = jest.fn((key) => {
        if (key === 'userId') return '123'; // Mock userId
        return null;
    });
});

// Helper function to render component with context
const renderWithContext = (getPlanHistoryMock, cancelSubscriptionMock) => {
    render(
        <MemoryRouter>
            <PlanContext.Provider value={{ getPlanHistory: getPlanHistoryMock, cancelSubscription: cancelSubscriptionMock }}>
                <PlanHistory />
            </PlanContext.Provider>
        </MemoryRouter>
    );
};

describe('PlanHistory Component', () => {
    it('renders without crashing', () => {
        renderWithContext(jest.fn(), jest.fn());
        expect(screen.getByText(/Plan History/i)).toBeInTheDocument();
    });

    it('calls getPlanHistory with userId on mount', async () => {
        const getPlanHistoryMock = jest.fn().mockResolvedValue({
            data: {
                planHistory: [
                    {
                        id: 'sub123',
                        plan: { name: 'Premium Plan' },
                        status: 'ACTIVE',
                        startDate: '2024-01-01T12:00:00Z',
                        endDate: '2024-06-01T12:00:00Z',
                    },
                ],
            },
        });

        renderWithContext(getPlanHistoryMock, jest.fn());

        await waitFor(() => expect(getPlanHistoryMock).toHaveBeenCalledWith('123'));
    });

    it('displays plan history when data exists', async () => {
        const getPlanHistoryMock = jest.fn().mockResolvedValue({
            data: {
                planHistory: [
                    {
                        id: 'sub567',
                        plan: { name: 'Basic Plan' },
                        status: 'UPCOMING',
                        startDate: '2024-02-01T10:30:00Z',
                        endDate: '2024-07-01T10:30:00Z',
                    },
                ],
            },
        });

        renderWithContext(getPlanHistoryMock, jest.fn());

        await waitFor(() => {
            expect(screen.getByText(/Basic Plan/i)).toBeInTheDocument();
            expect(screen.getByText(/Status: UPCOMING/i)).toBeInTheDocument();
            expect(screen.getByText(/Start Date: 2\/1\/2024/i)).toBeInTheDocument();
            expect(screen.getByText(/End Date: 7\/1\/2024/i)).toBeInTheDocument();
            expect(screen.getByRole('button', { name: /Cancel Subscription/i })).toBeInTheDocument();
        });
    });

    it('shows "No plan history available" when API returns an empty list', async () => {
        const getPlanHistoryMock = jest.fn().mockResolvedValue({ data: { planHistory: [] } });

        renderWithContext(getPlanHistoryMock, jest.fn());

        await waitFor(() => {
            expect(screen.getByText(/No plan history available/i)).toBeInTheDocument();
        });
    });

    it('handles API errors correctly and displays toast message', async () => {
        const getPlanHistoryMock = jest.fn().mockRejectedValue(new Error('API Error'));

        renderWithContext(getPlanHistoryMock, jest.fn());

        await waitFor(() => {
            expect(toast.error).toHaveBeenCalledWith('Error fetching plan history');
        });

        expect(screen.getByText(/No plan history available/i)).toBeInTheDocument();
    });

    it('calls cancelSubscription when cancel button is clicked', async () => {
        const getPlanHistoryMock = jest.fn().mockResolvedValue({
            data: {
                planHistory: [
                    {
                        id: 'sub777',
                        plan: { name: 'Gold Plan' },
                        status: 'UPCOMING',
                        startDate: '2024-04-01T09:30:00Z',
                        endDate: '2024-10-01T09:30:00Z',
                    },
                ],
            },
        });

        const cancelSubscriptionMock = jest.fn().mockResolvedValue({
            success: true,
            message: 'Subscription cancelled successfully',
        });

        renderWithContext(getPlanHistoryMock, cancelSubscriptionMock);

        await waitFor(() => {
            fireEvent.click(screen.getByRole('button', { name: /Cancel Subscription/i }));
        });

        await waitFor(() => {
            expect(cancelSubscriptionMock).toHaveBeenCalledWith('sub777');
            expect(toast.success).toHaveBeenCalledWith('Subscription cancelled successfully');
        });
    });

    it('handles cancellation failure and displays toast error', async () => {
        const getPlanHistoryMock = jest.fn().mockResolvedValue({
            data: {
                planHistory: [
                    {
                        id: 'sub888',
                        plan: { name: 'Silver Plan' },
                        status: 'UPCOMING',
                        startDate: '2024-05-01T08:00:00Z',
                        endDate: '2024-09-01T08:00:00Z',
                    },
                ],
            },
        });

        const cancelSubscriptionMock = jest.fn().mockResolvedValue({
            success: false,
            message: 'Cancellation failed',
        });

        renderWithContext(getPlanHistoryMock, cancelSubscriptionMock);

        await waitFor(() => {
            fireEvent.click(screen.getByRole('button', { name: /Cancel Subscription/i }));
        });

        await waitFor(() => {
            expect(cancelSubscriptionMock).toHaveBeenCalledWith('sub888');
            expect(toast.error).toHaveBeenCalledWith('Cancellation failed');
        });
    });

    it('handles errors when cancelling subscription', async () => {
        const getPlanHistoryMock = jest.fn().mockResolvedValue({
            data: {
                planHistory: [
                    {
                        id: 'sub999',
                        plan: { name: 'Platinum Plan' },
                        status: 'UPCOMING',
                        startDate: '2024-06-01T10:00:00Z',
                        endDate: '2024-12-01T10:00:00Z',
                    },
                ],
            },
        });

        const cancelSubscriptionMock = jest.fn().mockRejectedValue(new Error('Server error'));

        renderWithContext(getPlanHistoryMock, cancelSubscriptionMock);

        await waitFor(() => {
            fireEvent.click(screen.getByRole('button', { name: /Cancel Subscription/i }));
        });

        await waitFor(() => {
            expect(toast.error).toHaveBeenCalledWith('Error cancelling subscription');
        });
    });
});
