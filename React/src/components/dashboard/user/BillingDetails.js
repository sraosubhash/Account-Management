import React, { useState, useEffect, useContext } from 'react';
import { Box, Typography, Paper, Dialog, DialogTitle, DialogContent, DialogActions, Button, Pagination } from '@mui/material';
import { toast } from 'react-toastify';
import PaymentContext from '../../../context/PaymentContext';
import PlanContext from '../../../context/PlanContext';

const BillingDetails = () => {
    const { getUserTransactions, getTransactionDetail } = useContext(PaymentContext);
    const { getPlanById } = useContext(PlanContext);
    const [transactions, setTransactions] = useState([]);
    const [selectedTransaction, setSelectedTransaction] = useState(null);
    const [detailDialogOpen, setDetailDialogOpen] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 6;
    const userId = localStorage.getItem('userId'); // userId is stored during login

    const fetchTransactions = async () => {
        try {
            const res = await getUserTransactions(userId);

            setTransactions(res.slice().reverse());
        } catch (error) {
            console.error('Error fetching transactions', error);
            toast.error('Error fetching transactions');
        }
    };

    useEffect(() => {
        fetchTransactions();
    }, []);

    // When a blling is clicked, billing card appears with info
    const handleTransactionClick = async (transactionId) => {
        try {
            const txnDetail = await getTransactionDetail(transactionId);
            // Fetching plan details
            const planRes = await getPlanById(txnDetail.planId);
            // Extracting plan details from the API response.
            const updatedTxn = {
                ...txnDetail,
                planName: planRes?.data?.name || 'N/A',
                planDuration: planRes?.data?.duration || 'N/A',
                planDataLimit: planRes?.data?.dataLimit || 'N/A',
                planSMSLimit: planRes?.data?.smsLimit || 'N/A',
                planTalkTime: planRes?.data?.talkTimeMinutes || 'N/A',
            };
            setSelectedTransaction(updatedTxn);
            setDetailDialogOpen(true);
        } catch (error) {
            console.error('Error fetching Billing detail', error);
            toast.error('Error fetching Billing detail');
        }
    };

    const calculatePriceDetails = (amount) => {
        const price = (amount / 1.18).toFixed(2);
        const tax = ((amount * 0.18) / 1.18).toFixed(2);
        const total = amount.toFixed(2);
        return { price, tax, total };
    };

    const getBillingId = (transactionId) => {
        return 'BIL' + transactionId.slice(-4); // Triming last 4 numbers
    };

    // Pagination
    const totalPages = Math.ceil(transactions.length / itemsPerPage);
    const currentTransactions = transactions.slice(
        (currentPage - 1) * itemsPerPage,
        currentPage * itemsPerPage
    );

    return (
        <Paper sx={{ p: 2 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                <Typography variant="h6">Billing Details</Typography>
            </Box>
            {transactions && transactions.length > 0 ? (
                <>
                    <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2 }}>
                        {currentTransactions.map((txn) => {
                            const { price, tax, total } = calculatePriceDetails(txn.amount);
                            return (
                                <Box
                                    key={txn.transactionId}
                                    sx={{
                                        flex: '1 1 calc(33.33% - 16px)',
                                        minWidth: '280px',
                                        p: 2,
                                        borderRadius: 2,
                                        boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
                                        backgroundColor: 'background.paper',
                                        cursor: 'pointer',
                                        transition: 'transform 0.2s',
                                        '&:hover': {
                                            transform: 'scale(1.02)',
                                        },
                                    }}
                                    onClick={() => handleTransactionClick(txn.transactionId)}
                                >
                                    <Typography variant="subtitle1" color="primary" sx={{ textDecoration: 'underline', mb: 1 }}>
                                        Billing ID: {getBillingId(txn.transactionId)}
                                    </Typography>
                                    <Typography variant="body2" sx={{ mb: 1 }}></Typography>
                                    <Typography variant="body2">
                                        Status: <b><span style={{ color: 'green' }}>{txn.status}</span></b>
                                    </Typography>
                                    <Typography variant="body2">Price: ₹{price}</Typography>
                                    <Typography variant="body2">Tax Amount: ₹{tax}</Typography>
                                    <Typography variant="body2">
                                        <strong>Total Amount: ₹{total}</strong>
                                    </Typography>
                                    <Typography variant="body2">
                                        Payment Date: {new Date(txn.paymentDate).toLocaleDateString()}
                                    </Typography>
                                </Box>
                            );
                        })}
                    </Box>
                    {totalPages > 1 && (
                        <Box display="flex" justifyContent="center" mt={2}>
                            <Pagination
                                count={totalPages}
                                page={currentPage}
                                onChange={(event, value) => setCurrentPage(value)}
                                color="primary"
                            />
                        </Box>
                    )}
                </>
            ) : (
                <Typography>No Billing available.</Typography>
            )}

            {/* Transaction Detail Dialog */}
            <Dialog open={detailDialogOpen} onClose={() => setDetailDialogOpen(false)} fullWidth>
                <DialogTitle>Billing Details</DialogTitle>
                <DialogContent dividers>
                    {selectedTransaction ? (
                        <Box>
                            <Typography variant="body1">
                                <strong>Billing ID:</strong> {getBillingId(selectedTransaction.transactionId)}
                            </Typography>
                            <Typography variant="body1">
                                <strong>Plan Name:</strong> {selectedTransaction.planName}
                            </Typography>
                            <Typography variant="body1">
                                <strong>Duration:</strong> {selectedTransaction.planDuration} days
                            </Typography>
                            <Typography variant="body1">
                                <strong>Price:</strong> ₹{calculatePriceDetails(selectedTransaction.amount).price}
                            </Typography>
                            <Typography variant="body1">
                                <strong>Tax Amount:</strong> ₹{calculatePriceDetails(selectedTransaction.amount).tax}
                            </Typography>
                            <Typography variant="body1">
                                <strong>Total Amount:</strong> ₹{calculatePriceDetails(selectedTransaction.amount).total}
                            </Typography>
                            <Typography variant="body1">
                                <strong>Status:</strong> {selectedTransaction.status}
                            </Typography>
                            <Typography variant="body1">
                                <strong>Payment Date:</strong> {new Date(selectedTransaction.paymentDate).toLocaleString()}
                            </Typography>
                        </Box>
                    ) : (
                        <Typography>Loading...</Typography>
                    )}
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setDetailDialogOpen(false)}>Close</Button>
                </DialogActions>
            </Dialog>
        </Paper>
    );
};

export default BillingDetails;
