// import { render, screen } from '@testing-library/react';
// import { BrowserRouter } from 'react-router-dom';
// import App from './App';

// test('renders learn react link', () => {
//   <BrowserRouter>
//       <App />
//     </BrowserRouter>
  
// });
import React from 'react';
import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import App from './App';


jest.mock('./components/dashboard/user/TransactionDetails', () => () => <div>Mock TransactionDetails</div>);
jest.mock('./components/dashboard/user/UserDashboardTabs', () => () => <div>Mock UserDashboardTabs</div>);
jest.mock('./components/dashboard/Dashboard', () => () => <div>Mock Dashboard</div>);
jest.mock('react-toastify', () => ({
    ToastContainer: () => <div>Mock Toast</div>,
    toast: { success: jest.fn(), error: jest.fn() },
}));

describe('App Component', () => {
    it('renders the app without crashing', () => {
        render(
            <BrowserRouter>
                <App />
            </BrowserRouter>
        );

        // Replace "learn react" with actual content from App.js
        // expect(screen.getByRole('heading', { level: 1 })).toBeInTheDocument();
    });
});
