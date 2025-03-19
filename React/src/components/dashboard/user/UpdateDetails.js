import React, { useState, useEffect } from 'react';
import { Box, Typography, Paper, CircularProgress, Alert, Button, TextField } from '@mui/material';
import { toast } from 'react-toastify';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const UpdateDetails = () => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [formErrors, setFormErrors] = useState({});
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        alternatePhone: '',
        address: ''
    });
    const navigate = useNavigate();

    useEffect(() => {
        const fetchUserDetails = async () => {
            try {
                const token = localStorage.getItem('token');
                const userId = localStorage.getItem('userId');

                if (!token || !userId) {
                    toast.error("Session expired. Redirecting to login...");
                    navigate('/login');
                    return;
                }

                const response = await axios.get(
                    `http://localhost:8081/account/find-user/${userId}`,
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                            'Content-Type': 'application/json'
                        }
                    }
                );

                if (response.data) {
                    setUser(response.data);
                    setFormData({
                        firstName: response.data.firstName,
                        lastName: response.data.lastName,
                        alternatePhone: response.data.alternatePhone || '',
                        address: response.data.address || ''
                    });
                } else {
                    throw new Error('No data received from server');
                }
            } catch (error) {
                setError(getErrorMessage(error));
                toast.error(getErrorMessage(error));

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
        if (error.response?.status === 404) return 'User not found. Please try logging in again.';
        if (error.response?.status === 401) return 'Session expired. Please log in again.';
        if (error.response?.status === 500) return 'Server error. Please try again later.';
        return error.response?.data?.message || 'Error fetching user details';
    };

    const validateForm = () => {
        const errors = {};
        const nameRegex = /^[A-Za-z]+$/; // Only alphabets
        const phoneRegex = /^[6789]\d{9}$/; // Starts with 6, 7, 8, or 9 and has 10 digits

        if (!formData.firstName.trim()) {
            errors.firstName = "First Name is required";
        } else if (!nameRegex.test(formData.firstName)) {
            errors.firstName = "First Name should contain only alphabets";
        }

        if (!formData.lastName.trim()) {
            errors.lastName = "Last Name is required";
        } else if (!nameRegex.test(formData.lastName)) {
            errors.lastName = "Last Name should contain only alphabets";
        }

        if (!formData.address.trim()) {
            errors.address = "Address is required";
        }

        if (formData.alternatePhone && !phoneRegex.test(formData.alternatePhone)) {
            errors.alternatePhone = "Alternate Phone must be 10 digits & start with 6, 7, 8, or 9";
        }

        setFormErrors(errors);
        return Object.keys(errors).length === 0;
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value
        });
    };

    const handleUpdate = async () => {
        if (!validateForm()) return;

        try {
            const token = localStorage.getItem('token');
            const userId = localStorage.getItem('userId');

            if (!token || !userId) {
                toast.error("Session expired. Redirecting to login...");
                navigate('/login');
                return;
            }

            const response = await axios.put(
                `http://localhost:8081/account/update-user/${userId}`,
                formData,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                }
            );

            if (response.data) {
                setUser(response.data);
                toast.success("User details updated successfully!");
                navigate('/dashboard');
            } else {
                throw new Error('No data received from server');
            }
        } catch (error) {
            setError(getErrorMessage(error));
            toast.error(getErrorMessage(error));

            if (error.response?.status === 401) {
                localStorage.removeItem('token');
                localStorage.removeItem('userId');
                navigate('/login');
            }
        }
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
                mt: 5
            }}
        >
            <Typography variant="h5" gutterBottom align="center" color="primary">
                Update Account Details
            </Typography>

            {loading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 100 }}>
                    <CircularProgress />
                </Box>
            ) : error ? (
                <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>
            ) : user ? (
                <>
                    <Box sx={{ display: 'grid', gridTemplateColumns: '1fr', gap: 2 }}>
                        <TextField
                            name="firstName"
                            label="First Name"
                            value={formData.firstName}
                            onChange={handleInputChange}
                            fullWidth
                            sx={{ mt: 1 }}
                            error={!!formErrors.firstName}
                            helperText={formErrors.firstName}
                        />
                        <TextField
                            name="lastName"
                            label="Last Name"
                            value={formData.lastName}
                            onChange={handleInputChange}
                            fullWidth
                            sx={{ mt: 1 }}
                            error={!!formErrors.lastName}
                            helperText={formErrors.lastName}
                        />
                        <TextField
                            name="alternatePhone"
                            label="Alternate Phone (Optional)"
                            value={formData.alternatePhone}
                            onChange={handleInputChange}
                            fullWidth
                            sx={{ mt: 1 }}
                            error={!!formErrors.alternatePhone}
                            helperText={formErrors.alternatePhone}
                        />
                        <TextField
                            name="address"
                            label="Address"
                            value={formData.address}
                            onChange={handleInputChange}
                            fullWidth
                            multiline
                            rows={4}
                            sx={{ mt: 1 }}
                            error={!!formErrors.address}
                            helperText={formErrors.address}
                        />
                    </Box>
                    <Box sx={{ display: 'flex', justifyContent: 'center', mt: 3, gap: 2 }}>
                        <Button variant="contained" color="primary" onClick={handleUpdate}>
                            Update
                        </Button>
                        <Button variant="outlined" color="secondary" onClick={() => navigate('/dashboard')}>
                            Cancel
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

export default UpdateDetails;
