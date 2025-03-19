import React, { useState, useEffect } from 'react';
import { Box, Typography, Paper, CircularProgress, Alert, Button } from '@mui/material';
import { toast } from 'react-toastify';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
 
const AccountDetails = () => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();
 
    useEffect(() => {
        const fetchUserDetails = async () => {
            try {
                const token = localStorage.getItem('token');
                const userId = localStorage.getItem('userId');
 
                if (!token) {
                    toast.error("Authentication token not found. Redirecting to login...");
                    navigate('/login');
                    return;
                }
 
                if (!userId) {
                    toast.error("User ID not found. Redirecting to login...");
                    navigate('/login');
                    return;
                }
 
                const response = await axios.get(
                    `${process.env.REACT_APP_API_URL || 'http://localhost:8081'}/account/find-user/${userId}`,
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                            'Content-Type': 'application/json'
                        }
                    }
                );
 
                if (response.data) {
                    setUser(response.data);
                } else {
                    throw new Error('No data received from server');
                }
            } catch (error) {
                console.error('Error details:', {
                    status: error.response?.status,
                    data: error.response?.data,
                    message: error.message
                });
 
                const errorMessage = getErrorMessage(error);
                setError(errorMessage);
                toast.error(errorMessage);
 
                if (error.response?.status === 401) {
                    localStorage.removeItem('token');
                    localStorage.removeItem('userId');
                    navigate('/login');
                }
            } finally {
                setLoading(false);
            }
        };
 
        fetchUserDetails();
    }, [navigate]);
 
    const getErrorMessage = (error) => {
        if (error.response?.status === 404) {
            return 'User not found. Please try logging in again.';
        } else if (error.response?.status === 401) {
            return 'Session expired. Please log in again.';
        } else if (error.response?.status === 500) {
            return 'Server error. Please try again later.';
        }
        return error.response?.data?.message || 'Error fetching user details';
    };
 
    const renderUserDetails = () => {
        if (!user) return null;
 
        const details = [
            { label: 'Full Name', value: `${user.firstName} ${user.lastName}` },
            { label: 'Email', value: user.email },
            { label: 'Mobile', value: user.mobile },
            { label: 'Alternate Phone', value: user.alternatePhone || 'N/A' },
            { label: 'Role', value: user.role || 'N/A', color: 'Green', bold: true },
            { label: 'Address', value: user.address || 'N/A' }
        ];
 
        return (
            <Paper sx={{ p: 2 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                <Typography variant="h6">Account Details</Typography>
            </Box>
            <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2 }}>
                {details.map((detail, index) => (
                    <Box key={index} sx={{ gridColumn: detail.fullWidth ? 'span 2' : 'span 1' }}>
 
                        <Typography variant="subtitle2" color="primary">
                            {detail.label}:
                        </Typography>
                        <Typography variant="body1" sx={{ mt: 0.5 }}>
                            {detail.value}
                        </Typography>
                    </Box>
                ))}
            </Box>
        </Paper>
        );
    };
 
    return (
        <Paper
            sx={{
                p: 4,
                borderRadius: 3,
                boxShadow: 3,
                backgroundColor: 'background.paper',
                maxWidth: 600,
                margin: 'auto',
   
            }}
        >

 
            {loading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 100 }}>
                    <CircularProgress />
                </Box>
            ) : error ? (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>
            ) : user ? (
                <>
                    {renderUserDetails()}
                    <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2, gap: 2 }}>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={() => navigate('/updatedetails')}  // Navigate to /update-details
                    >
                      Update Details
                    </Button>
                    </Box>
                </>
            ) : (
                <Typography variant="body1" color="error" align="center">
                    No user details available.
                </Typography>
            )}
        </Paper>
    );
};
 
export default AccountDetails;
