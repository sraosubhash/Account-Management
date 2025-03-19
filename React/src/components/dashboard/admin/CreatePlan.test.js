import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { PlanContext } from '../../../context/PlanContext';
import CreatePlan from './CreatePlan';
import { toast } from 'react-toastify';

// Mock toast notifications
jest.mock('react-toastify', () => ({
    toast: {
        success: jest.fn(),
        error: jest.fn(),
    },
}));

describe('CreatePlan Component', () => {
    let createPlanMock, onPlanCreatedMock;

    beforeEach(() => {
        createPlanMock = jest.fn().mockResolvedValue({ success: true });
        onPlanCreatedMock = jest.fn();
        jest.clearAllMocks();
    });

    const renderComponent = () => {
        return render(
            <PlanContext.Provider value={{ createPlan: createPlanMock }}>
                <CreatePlan onPlanCreated={onPlanCreatedMock} />
            </PlanContext.Provider>
        );
    };

    it('renders component correctly', () => {
        renderComponent();
        expect(screen.getByText(/Create New Plan/i)).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /Create Plan/i })).toBeInTheDocument();
    });

    it('shows validation errors when submitting empty form', async () => {
        renderComponent();
        fireEvent.click(screen.getByRole('button', { name: /Create Plan/i }));

        await waitFor(() => {
            expect(screen.getByText(/Plan name is required/i)).toBeInTheDocument();
            expect(screen.getByText(/Price is required/i)).toBeInTheDocument();
            expect(screen.getByText(/Duration is required/i)).toBeInTheDocument();
            expect(screen.getByText(/Data limit is required/i)).toBeInTheDocument();
            expect(screen.getByText(/SMS limit is required/i)).toBeInTheDocument();
            expect(screen.getByText(/Talktime minutes are required/i)).toBeInTheDocument();
            expect(screen.getByText(/At least one feature is required/i)).toBeInTheDocument();
        });
    });

    it('submits form successfully when all fields are valid', async () => {
        renderComponent();

        fireEvent.change(screen.getByLabelText(/Plan Name/i), { target: { value: 'Premium Plan' } });
        fireEvent.change(screen.getByLabelText(/Price/i), { target: { value: '199' } });
        fireEvent.change(screen.getByLabelText(/Duration/i), { target: { value: '30' } });
        fireEvent.change(screen.getByLabelText(/Data Limit/i), { target: { value: '50' } });
        fireEvent.change(screen.getByLabelText(/SMS Limit/i), { target: { value: '1000' } });
        fireEvent.change(screen.getByLabelText(/Talktime Minutes/i), { target: { value: '500' } });
        fireEvent.change(screen.getByLabelText(/Features/i), { target: { value: 'Unlimited Calls, 5G Data' } });

        fireEvent.click(screen.getByRole('button', { name: /Create Plan/i }));

        await waitFor(() => {
            expect(createPlanMock).toHaveBeenCalledWith({
                name: 'Premium Plan',
                description: '',
                price: 199,
                duration: 30,
                dataLimit: 50,
                smsLimit: 1000,
                talkTimeMinutes: '500',
                features: ['Unlimited Calls', '5G Data'],
                active: true,
            });

            expect(toast.success).toHaveBeenCalledWith('Plan created successfully');
            expect(onPlanCreatedMock).toHaveBeenCalled();
        });
    });

    it('shows error toast when API call fails', async () => {
        createPlanMock.mockRejectedValueOnce(new Error('API Error'));
        renderComponent();

        fireEvent.change(screen.getByLabelText(/Plan Name/i), { target: { value: 'Basic Plan' } });
        fireEvent.change(screen.getByLabelText(/Price/i), { target: { value: '99' } });
        fireEvent.change(screen.getByLabelText(/Duration/i), { target: { value: '15' } });
        fireEvent.change(screen.getByLabelText(/Data Limit/i), { target: { value: '10' } });
        fireEvent.change(screen.getByLabelText(/SMS Limit/i), { target: { value: '500' } });
        fireEvent.change(screen.getByLabelText(/Talktime Minutes/i), { target: { value: '300' } });
        fireEvent.change(screen.getByLabelText(/Features/i), { target: { value: 'Basic Calls, 4G Data' } });

        fireEvent.click(screen.getByRole('button', { name: /Create Plan/i }));

        await waitFor(() => {
            expect(toast.error).toHaveBeenCalledWith('Error creating plan');
        });
    });

    it('validates incorrect inputs', async () => {
        renderComponent();

        fireEvent.change(screen.getByLabelText(/Price/i), { target: { value: '-10' } });
        fireEvent.change(screen.getByLabelText(/Duration/i), { target: { value: '0' } });
        fireEvent.change(screen.getByLabelText(/Data Limit/i), { target: { value: '-5' } });
        fireEvent.change(screen.getByLabelText(/SMS Limit/i), { target: { value: '-100' } });
        fireEvent.change(screen.getByLabelText(/Features/i), { target: { value: '@#%' } });

        fireEvent.click(screen.getByRole('button', { name: /Create Plan/i }));

        await waitFor(() => {
            expect(screen.getByText(/Price must be a positive number/i)).toBeInTheDocument();
            expect(screen.getByText(/Duration must be a positive number/i)).toBeInTheDocument();
            expect(screen.getByText(/Data limit must be a non-negative number/i)).toBeInTheDocument();
            expect(screen.getByText(/SMS limit must be a non-negative number/i)).toBeInTheDocument();
            expect(screen.getByText(/Features must be comma-separated words without special characters/i)).toBeInTheDocument();
        });
    });

    it('clears form and validation errors on successful submission', async () => {
        renderComponent();

        fireEvent.change(screen.getByLabelText(/Plan Name/i), { target: { value: 'Premium Plan' } });
        fireEvent.change(screen.getByLabelText(/Price/i), { target: { value: '199' } });
        fireEvent.change(screen.getByLabelText(/Duration/i), { target: { value: '30' } });
        fireEvent.change(screen.getByLabelText(/Data Limit/i), { target: { value: '50' } });
        fireEvent.change(screen.getByLabelText(/SMS Limit/i), { target: { value: '1000' } });
        fireEvent.change(screen.getByLabelText(/Talktime Minutes/i), { target: { value: '500' } });
        fireEvent.change(screen.getByLabelText(/Features/i), { target: { value: 'Unlimited Calls, 5G Data' } });

        fireEvent.click(screen.getByRole('button', { name: /Create Plan/i }));

        await waitFor(() => {
            expect(screen.getByLabelText(/Plan Name/i)).toHaveValue('');
            expect(screen.getByLabelText(/Price/i)).toHaveValue('');
            expect(screen.getByLabelText(/Duration/i)).toHaveValue('');
            expect(screen.getByLabelText(/Data Limit/i)).toHaveValue('');
            expect(screen.getByLabelText(/SMS Limit/i)).toHaveValue('');
            expect(screen.getByLabelText(/Talktime Minutes/i)).toHaveValue('');
            expect(screen.getByLabelText(/Features/i)).toHaveValue('');
        });
    });
});
