// src/components/CreatePlan.js
import React, { useState, useContext } from 'react';
import { Box, Typography, TextField, Button, Grid, Paper } from '@mui/material';
import { toast } from 'react-toastify';
import PlanContext from '../../../context/PlanContext';
 
const CreatePlan = ({ onPlanCreated }) => {
    const { createPlan } = useContext(PlanContext);
   
    const [newPlan, setNewPlan] = useState({
        name: '',
        description: '',
        price: '',
        duration: '',
        dataLimit: '',
        smsLimit: '',
        talkTimeMinutes: '',
        features: '', // comma-separated string
        active: true,
    });
 
    const [errors, setErrors] = useState({});
 
    const validateForm = () => {
        let tempErrors = {};
 
        if (!newPlan.name.trim()) tempErrors.name = 'Plan name is required';
        if (!newPlan.price) tempErrors.price = 'Price is required';
        else if (isNaN(newPlan.price) || parseFloat(newPlan.price) <= 0)
            tempErrors.price = 'Price must be a positive number';
 
        if (!newPlan.duration) tempErrors.duration = 'Duration is required';
        else if (isNaN(newPlan.duration) || parseInt(newPlan.duration, 10) <= 0)
            tempErrors.duration = 'Duration must be a positive number';
 
        if (!newPlan.dataLimit) tempErrors.dataLimit = 'Data limit is required';
        else if (isNaN(newPlan.dataLimit) || parseInt(newPlan.dataLimit, 10) < 0)
            tempErrors.dataLimit = 'Data limit must be a non-negative number';
 
        if (!newPlan.smsLimit) tempErrors.smsLimit = 'SMS limit is required';
        else if (isNaN(newPlan.smsLimit) || parseInt(newPlan.smsLimit, 10) < 0)
            tempErrors.smsLimit = 'SMS limit must be a non-negative number';
 
        if (!newPlan.talkTimeMinutes.trim())
            tempErrors.talkTimeMinutes = 'Talktime minutes are required';
 
        if (!newPlan.features.trim()) tempErrors.features = 'At least one feature is required';
        else if (!/^[a-zA-Z0-9, ]+$/.test(newPlan.features))
            tempErrors.features = 'Features must be comma-separated words without special characters';
 
        setErrors(tempErrors);
        return Object.keys(tempErrors).length === 0;
    };
 
    const handleNewPlanChange = (e) => {
        const { name, value } = e.target;
        setNewPlan({ ...newPlan, [name]: value });
    };
 
    const handleCreatePlan = async () => {
        if (!validateForm()) return;
 
        try {
            const planData = {
                ...newPlan,
                price: parseFloat(newPlan.price),
                duration: parseInt(newPlan.duration, 10),
                dataLimit: parseInt(newPlan.dataLimit, 10),
                smsLimit: parseInt(newPlan.smsLimit, 10),
                talkTimeMinutes: newPlan.talkTimeMinutes,
                features: newPlan.features.split(',').map(f => f.trim()),
                active: newPlan.active,
            };
 
            await createPlan(planData);
            toast.success('Plan created successfully');
 
            setNewPlan({
                name: '',
                description: '',
                price: '',
                duration: '',
                dataLimit: '',
                smsLimit: '',
                talkTimeMinutes: '',
                features: '',
                active: true,
            });
            setErrors({});
 
            if (onPlanCreated) onPlanCreated();
            setTimeout(() => {
                window.location.reload();
            }, 3500);
        } catch (err) {
            const errorMsg = err.response?.data?.message || 'An error occurred while creating a plan. Please try again.';
              toast.error(errorMsg);
          }
    };
 
    return (
        <Paper sx={{ p: 2, mb: 4 }}>
            <Typography variant="h6" gutterBottom>
                Create New Plan
            </Typography>
            <Grid container spacing={2}>
                <Grid item xs={12} sm={6}>
                    <TextField
                        label="Plan Name"
                        name="name"
                        value={newPlan.name}
                        onChange={handleNewPlanChange}
                        fullWidth
                        error={!!errors.name}
                        helperText={errors.name}
                    />
                </Grid>
                <Grid item xs={12} sm={6}>
                    <TextField
                        label="Price"
                        name="price"
                        type="number"
                        value={newPlan.price}
                        onChange={handleNewPlanChange}
                        fullWidth
                        error={!!errors.price}
                        helperText={errors.price}
                    />
                </Grid>
                <Grid item xs={12}>
                    <TextField
                        label="Description"
                        name="description"
                        value={newPlan.description}
                        onChange={handleNewPlanChange}
                        fullWidth
                        multiline
                        rows={2}
                    />
                </Grid>
                <Grid item xs={12} sm={4}>
                    <TextField
                        label="Duration (days)"
                        name="duration"
                        type="number"
                        value={newPlan.duration}
                        onChange={handleNewPlanChange}
                        fullWidth
                        error={!!errors.duration}
                        helperText={errors.duration}
                    />
                </Grid>
                <Grid item xs={12} sm={4}>
                    <TextField
                        label="Data Limit (GB)"
                        name="dataLimit"
                        type="number"
                        value={newPlan.dataLimit}
                        onChange={handleNewPlanChange}
                        fullWidth
                        error={!!errors.dataLimit}
                        helperText={errors.dataLimit}
                    />
                </Grid>
                <Grid item xs={12} sm={4}>
                    <TextField
                        label="SMS Limit"
                        name="smsLimit"
                        type="number"
                        value={newPlan.smsLimit}
                        onChange={handleNewPlanChange}
                        fullWidth
                        error={!!errors.smsLimit}
                        helperText={errors.smsLimit}
                    />
                </Grid>
                <Grid item xs={12} sm={4}>
                    <TextField
                        label="Talktime Minutes"
                        name="talkTimeMinutes"
                        type="text"
                        value={newPlan.talkTimeMinutes}
                        onChange={handleNewPlanChange}
                        fullWidth
                        error={!!errors.talkTimeMinutes}
                        helperText={errors.talkTimeMinutes}
                    />
                </Grid>
                <Grid item xs={12} sm={8}>
                    <TextField
                        label="Features (comma separated)"
                        name="features"
                        value={newPlan.features}
                        onChange={handleNewPlanChange}
                        fullWidth
                        error={!!errors.features}
                        helperText={errors.features}
                    />
                </Grid>
                <Grid item xs={12}>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={handleCreatePlan}
                    >
                        Create Plan
                    </Button>
                </Grid>
            </Grid>
        </Paper>
    );
};
 
export default CreatePlan;
 
