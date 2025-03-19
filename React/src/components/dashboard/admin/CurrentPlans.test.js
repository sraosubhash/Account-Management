import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import CurrentPlans from './CurrentPlans';
import PlanContext from '../../../context/PlanContext';
import { toast } from 'react-toastify';

// Mock toast notifications to prevent actual notifications
jest.mock('react-toastify', () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
  },
}));

describe('CurrentPlans Component', () => {
  let getAllPlansForUserMock, updatePlanMock, getPlanByIdMock, activatePlanMock, deactivatePlanMock;

  beforeEach(() => {
    getAllPlansForUserMock = jest.fn().mockResolvedValue({
      data: {
        content: [
          {
            id: 1,
            name: 'Gold Plan',
            description: 'Best plan for heavy users',
            price: 30.99,
            duration: 30,
            dataLimit: 50,
            features: ['Unlimited Calls', '5G Speed'],
            active: true,
          },
          {
            id: 2,
            name: 'Silver Plan',
            description: 'Affordable plan',
            price: 19.99,
            duration: 30,
            dataLimit: 25,
            features: ['4G Speed'],
            active: false,
          },
        ],
      },
    });

    updatePlanMock = jest.fn();
    getPlanByIdMock = jest.fn().mockResolvedValue({
      data: {
        id: 1,
        name: 'Gold Plan',
        description: 'Best plan for heavy users',
        price: 30.99,
        duration: 30,
        dataLimit: 50,
        features: ['Unlimited Calls', '5G Speed'],
        active: true,
      },
    });

    activatePlanMock = jest.fn().mockResolvedValue({ success: true, message: 'Plan activated successfully' });
    deactivatePlanMock = jest.fn().mockResolvedValue({ success: true, message: 'Plan deactivated successfully' });

    render(
      <PlanContext.Provider
        value={{
          getAllPlansForUser: getAllPlansForUserMock,
          updatePlan: updatePlanMock,
          getPlanById: getPlanByIdMock,
          activatePlan: activatePlanMock,
          deactivatePlan: deactivatePlanMock,
        }}
      >
        <CurrentPlans />
      </PlanContext.Provider>
    );
  });

  it('renders the Current Plans component and displays plans', async () => {
    await waitFor(() => {
      expect(screen.getByText(/All Current Active Plans/i)).toBeInTheDocument();
      expect(screen.getByText(/Gold Plan/i)).toBeInTheDocument();
      expect(screen.getByText(/Silver Plan/i)).toBeInTheDocument();
    });
  });

  it('opens the update dialog when clicking update', async () => {
    await waitFor(() => {
      fireEvent.click(screen.getByRole('button', { name: /Update/i }));
    });

    await waitFor(() => {
      expect(screen.getByText(/Update Plan/i)).toBeInTheDocument();
      expect(screen.getByLabelText(/Plan Name/i)).toHaveValue('Gold Plan');
    });
  });

  it('updates plan details correctly', async () => {
    await waitFor(() => {
      fireEvent.click(screen.getByRole('button', { name: /Update/i }));
    });

    fireEvent.change(screen.getByLabelText(/Plan Name/i), { target: { value: 'Platinum Plan' } });

    fireEvent.click(screen.getByRole('button', { name: /Update/i }));

    await waitFor(() => {
      expect(updatePlanMock).toHaveBeenCalledWith(1, expect.objectContaining({ name: 'Platinum Plan' }));
      expect(toast.success).toHaveBeenCalledWith('Plan updated successfully');
    });
  });

  it('shows error when update fails', async () => {
    updatePlanMock.mockRejectedValueOnce(new Error('Error updating plan'));

    await waitFor(() => {
      fireEvent.click(screen.getByRole('button', { name: /Update/i }));
    });

    fireEvent.click(screen.getByRole('button', { name: /Update/i }));

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('Error updating plan');
    });
  });

  it('opens and displays plan details when clicking a plan name', async () => {
    await waitFor(() => {
      fireEvent.click(screen.getByText(/Gold Plan/i));
    });

    await waitFor(() => {
      expect(screen.getByText(/Plan Details/i)).toBeInTheDocument();
      expect(screen.getByText(/Best plan for heavy users/i)).toBeInTheDocument();
    });
  });

  it('activates a deactivated plan', async () => {
    await waitFor(() => {
      fireEvent.click(screen.getByRole('button', { name: /Activate/i }));
    });

    await waitFor(() => {
      expect(activatePlanMock).toHaveBeenCalledWith(2);
      expect(toast.success).toHaveBeenCalledWith('Plan activated successfully');
    });
  });

  it('deactivates an active plan', async () => {
    await waitFor(() => {
      fireEvent.click(screen.getByRole('button', { name: /Deactivate/i }));
    });

    await waitFor(() => {
      expect(deactivatePlanMock).toHaveBeenCalledWith(1);
      expect(toast.success).toHaveBeenCalledWith('Plan deactivated successfully');
    });
  });

  it('shows error when activation fails', async () => {
    activatePlanMock.mockRejectedValueOnce(new Error('Activation failed'));

    await waitFor(() => {
      fireEvent.click(screen.getByRole('button', { name: /Activate/i }));
    });

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('Error activating plan');
    });
  });

  it('shows error when deactivation fails', async () => {
    deactivatePlanMock.mockRejectedValueOnce(new Error('Deactivation failed'));

    await waitFor(() => {
      fireEvent.click(screen.getByRole('button', { name: /Deactivate/i }));
    });

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('Error deactivating plan');
    });
  });

  it('closes dialogs properly', async () => {
    await waitFor(() => {
      fireEvent.click(screen.getByRole('button', { name: /Update/i }));
    });

    fireEvent.click(screen.getByRole('button', { name: /Cancel/i }));

    await waitFor(() => {
      expect(screen.queryByText(/Update Plan/i)).not.toBeInTheDocument();
    });

    await waitFor(() => {
      fireEvent.click(screen.getByText(/Gold Plan/i));
    });

    fireEvent.click(screen.getByRole('button', { name: /Close/i }));

    await waitFor(() => {
      expect(screen.queryByText(/Plan Details/i)).not.toBeInTheDocument();
    });
  });
});
