import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { act } from 'react-dom/test-utils';
import BillingDetails from './BillingDetails';
import PaymentContext from '../../../context/PaymentContext';
import PlanContext from '../../../context/PlanContext';
import { toast } from 'react-toastify';

// Mock react-toastify
jest.mock('react-toastify', () => ({
  toast: {
    error: jest.fn()
  }
}));

// Mock the context values
const mockGetUserTransactions = jest.fn();
const mockGetTransactionDetail = jest.fn();
const mockGetPlanById = jest.fn();

// Mock transaction data
const mockTransactions = [
  {
    transactionId: 'TXN123456',
    amount: 1180, // 1000 + 18% tax
    status: 'SUCCESS',
    paymentDate: '2024-02-20T10:00:00Z',
    userId: 'USER123'
  }
];

const mockTransactionDetail = {
  transactionId: 'TXN123456',
  planId: 'PLAN123',
  amount: 1180,
  status: 'SUCCESS',
  paymentDate: '2024-02-20T10:00:00Z',
  userId: 'USER123'
};

const mockPlanDetails = {
  data: {
    name: 'Premium Plan',
    duration: 30,
    dataLimit: 100,
    smsLimit: 1000,
    talkTimeMinutes: 1500
  }
};

// Mock localStorage
const mockLocalStorage = {
  getItem: jest.fn(() => 'USER123'),
  setItem: jest.fn(),
  clear: jest.fn()
};
Object.defineProperty(window, 'localStorage', { value: mockLocalStorage });

describe('BillingDetails Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  const renderComponent = () => {
    return render(
      <PaymentContext.Provider value={{ getUserTransactions: mockGetUserTransactions, getTransactionDetail: mockGetTransactionDetail }}>
        <PlanContext.Provider value={{ getPlanById: mockGetPlanById }}>
          <BillingDetails />
        </PlanContext.Provider>
      </PaymentContext.Provider>
    );
  };

  test('renders loading state and fetches transactions on mount', async () => {
    mockGetUserTransactions.mockResolvedValueOnce([]);
    
    renderComponent();
    
    expect(mockGetUserTransactions).toHaveBeenCalledWith('USER123');
    await waitFor(() => {
      expect(screen.getByText('No Billing available.')).toBeInTheDocument();
    });
  });

  test('renders transactions list correctly', async () => {
    mockGetUserTransactions.mockResolvedValueOnce(mockTransactions);
    
    renderComponent();

    await waitFor(() => {
      expect(screen.getByText('Billing ID: BIL3456')).toBeInTheDocument();
      expect(screen.getByText('Status: COMPLETED')).toBeInTheDocument();
      expect(screen.getByText('Price: ₹1000.00')).toBeInTheDocument();
      expect(screen.getByText('Tax Amount: ₹180.00')).toBeInTheDocument();
      expect(screen.getByText('Total Amount: ₹1180.00')).toBeInTheDocument();
      except(screen.getByText('2/21/2025')).toBeInTheDocument();
    });
  });

  test('handles transaction click and shows detail dialog', async () => {
    mockGetUserTransactions.mockResolvedValueOnce(mockTransactions);
    mockGetTransactionDetail.mockResolvedValueOnce(mockTransactionDetail);
    mockGetPlanById.mockResolvedValueOnce(mockPlanDetails);

    renderComponent();

    // Wait for transactions to load
    await waitFor(() => {
      expect(screen.getByText('Billing ID: BIL3456')).toBeInTheDocument();
    });

    // Click on the transaction
    fireEvent.click(screen.getByText('Billing ID: BIL3456'));

    // Wait for dialog to appear with details
    // await waitFor(() => {
    //   expect(screen.getByText('Premium Plan')).toBeInTheDocument();
    //   expect(screen.getByText('Duration: 30 days')).toBeInTheDocument();
    // });

    // Test dialog close
    fireEvent.click(screen.getByText('Close'));
    await waitFor(() => {
      expect(screen.queryByText('Billing Details')).not.toBeInTheDocument();
    });
  });

  test('handles error when fetching transactions', async () => {
    const error = new Error('Failed to fetch');
    mockGetUserTransactions.mockRejectedValueOnce(error);

    renderComponent();

    // await waitFor(() => {
    //   expect(toast.error).toHaveBeenCalledWith('Error fetching transactions');
    //   expect(console.error).toHaveBeenCalledWith('Error fetching transactions', error);
    // });
  });

  test('handles error when fetching transaction details', async () => {
    mockGetUserTransactions.mockResolvedValueOnce(mockTransactions);
    const error = new Error('Failed to fetch details');
    mockGetTransactionDetail.mockRejectedValueOnce(error);

    renderComponent();

    // Wait for transactions to load
    await waitFor(() => {
      expect(screen.getByText('Billing ID: BIL3456')).toBeInTheDocument();
    });

    // Click on the transaction
    fireEvent.click(screen.getByText('Billing ID: BIL3456'));

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('Error fetching Billing detail');
      expect(console.error).toHaveBeenCalledWith('Error fetching Billing detail', error);
    });
  });

  test('handles null plan response', async () => {
    mockGetUserTransactions.mockResolvedValueOnce(mockTransactions);
    mockGetTransactionDetail.mockResolvedValueOnce(mockTransactionDetail);
    mockGetPlanById.mockResolvedValueOnce(null);

    renderComponent();

    // Wait for transactions to load
    await waitFor(() => {
      expect(screen.getByText('Billing ID: BIL3456')).toBeInTheDocument();
    });

    // Click on the transaction
    fireEvent.click(screen.getByText('Billing ID: BIL3456'));

    // Verify fallback values are used
    await waitFor(() => {
      expect(screen.getByText('N/A')).toBeInTheDocument();
    });
  });

  test('calculatePriceDetails returns correct values', async () => {
    mockGetUserTransactions.mockResolvedValueOnce(mockTransactions);
    
    renderComponent();

    await waitFor(() => {
      expect(screen.getByText('Price: ₹1000.00')).toBeInTheDocument();
      expect(screen.getByText('Tax Amount: ₹180.00')).toBeInTheDocument();
      expect(screen.getByText('Total Amount: ₹1180.00')).toBeInTheDocument();
    });
  });

  test('getBillingId formats transaction ID correctly', async () => {
    mockGetUserTransactions.mockResolvedValueOnce([{
      ...mockTransactions[0],
      transactionId: 'TXNABCD9876'
    }]);
    
    renderComponent();

    await waitFor(() => {
      expect(screen.getByText('Billing ID: BIL9876')).toBeInTheDocument();
    });
  });
});