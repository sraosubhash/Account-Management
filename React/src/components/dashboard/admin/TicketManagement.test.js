import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import TicketManagement from './TicketManagement';
import SupportContext from "../../../context/SupportContext";
import { toast } from 'react-toastify';

// Mock toast notifications to prevent actual notifications
jest.mock('react-toastify', () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
  },
}));

describe('TicketManagement Component', () => {
  let getAllTicketsMock, getAllEmployeesMock, assignTicketMock;

  beforeEach(() => {
    getAllTicketsMock = jest.fn().mockResolvedValue([
      {
        id: 1,
        title: 'Login Issue',
        description: 'User unable to log in',
        status: 'NEW',
        priority: 'HIGH',
        userId: 1001,
        createdAt: new Date().toISOString(),
      },
      {
        id: 2,
        title: 'Slow Internet',
        description: 'User reports slow internet speed',
        status: 'ASSIGNED',
        priority: 'MEDIUM',
        userId: 1002,
        employeeId: 201,
        createdAt: new Date().toISOString(),
      },
    ]);

    getAllEmployeesMock = jest.fn().mockResolvedValue([
      {
        id: 201,
        firstName: 'John',
        lastName: 'Doe',
        email: 'john.doe@example.com',
      },
      {
        id: 202,
        firstName: 'Jane',
        lastName: 'Smith',
        email: 'jane.smith@example.com',
      },
    ]);

    assignTicketMock = jest.fn().mockResolvedValue({
      id: 1,
      title: 'Login Issue',
      status: 'ASSIGNED',
    });

    render(
      <SupportContext.Provider
        value={{
          getAllTickets: getAllTicketsMock,
          getAllEmployees: getAllEmployeesMock,
          assignTicket: assignTicketMock,
        }}
      >
        <TicketManagement />
      </SupportContext.Provider>
    );
  });

  it('renders the Ticket Management component and displays tickets', async () => {
    await waitFor(() => {
      expect(screen.getByText(/List All New Tickets/i)).toBeInTheDocument();
      expect(screen.getByText(/List All Assigned Tickets/i)).toBeInTheDocument();
    });

    expect(screen.getByText(/Login Issue/i)).toBeInTheDocument();
    expect(screen.getByText(/Slow Internet/i)).toBeInTheDocument();
  });

  it('opens the assign ticket dialog when clicking Assign button', async () => {
    await waitFor(() => {
      fireEvent.click(screen.getByRole('button', { name: /Assign/i }));
    });

    await waitFor(() => {
      expect(screen.getByText(/Assign Ticket/i)).toBeInTheDocument();
      expect(screen.getByText(/Login Issue/i)).toBeInTheDocument();
    });
  });

  it('assigns a ticket to an employee successfully', async () => {
    await waitFor(() => {
      fireEvent.click(screen.getByRole('button', { name: /Assign/i }));
    });

    fireEvent.mouseDown(screen.getByLabelText(/Select Employee/i));
    fireEvent.click(screen.getByText(/John Doe/i));

    fireEvent.click(screen.getByRole('button', { name: /Assign Ticket/i }));

    await waitFor(() => {
      expect(assignTicketMock).toHaveBeenCalledWith(1, 201);
      expect(toast.success).toHaveBeenCalledWith('Ticket assigned successfully');
    });
  });

  it('shows error when trying to assign without selecting an employee', async () => {
    await waitFor(() => {
      fireEvent.click(screen.getByRole('button', { name: /Assign/i }));
    });

    fireEvent.click(screen.getByRole('button', { name: /Assign Ticket/i }));

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('Please select an employee to assign.');
    });
  });

  it('shows error when ticket assignment fails', async () => {
    assignTicketMock.mockRejectedValueOnce(new Error('Assignment failed'));

    await waitFor(() => {
      fireEvent.click(screen.getByRole('button', { name: /Assign/i }));
    });

    fireEvent.mouseDown(screen.getByLabelText(/Select Employee/i));
    fireEvent.click(screen.getByText(/John Doe/i));

    fireEvent.click(screen.getByRole('button', { name: /Assign Ticket/i }));

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('Error assigning ticket');
    });
  });

  it('closes the assign ticket dialog properly', async () => {
    await waitFor(() => {
      fireEvent.click(screen.getByRole('button', { name: /Assign/i }));
    });

    fireEvent.click(screen.getByRole('button', { name: /Cancel/i }));

    await waitFor(() => {
      expect(screen.queryByText(/Assign Ticket/i)).not.toBeInTheDocument();
    });
  });

  it('displays assigned tickets with employee names', async () => {
    await waitFor(() => {
      expect(screen.getByText(/John Doe/i)).toBeInTheDocument();
    });
  });

  it('handles API errors when fetching tickets', async () => {
    getAllTicketsMock.mockRejectedValueOnce(new Error('Error fetching tickets'));

    render(
      <SupportContext.Provider
        value={{
          getAllTickets: getAllTicketsMock,
          getAllEmployees: getAllEmployeesMock,
          assignTicket: assignTicketMock,
        }}
      >
        <TicketManagement />
      </SupportContext.Provider>
    );

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('Error fetching tickets');
    });
  });

  it('handles API errors when fetching employees', async () => {
    getAllEmployeesMock.mockRejectedValueOnce(new Error('Error fetching employees'));

    render(
      <SupportContext.Provider
        value={{
          getAllTickets: getAllTicketsMock,
          getAllEmployees: getAllEmployeesMock,
          assignTicket: assignTicketMock,
        }}
      >
        <TicketManagement />
      </SupportContext.Provider>
    );

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('Error fetching employees');
    });
  });
});
