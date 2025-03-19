// src/components/CurrentPlans.js
import React, { useState, useEffect, useContext } from 'react';
import {
    Paper,
    Typography,
    TableContainer,
    Table,
    TableHead,
    TableRow,
    TableCell,
    TableBody,
    Button,
    Box,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    IconButton,
    Tooltip,
} from '@mui/material';
import FilterAltIcon from '@mui/icons-material/FilterAlt';
import { toast } from 'react-toastify';
import PlanContext from '../../../context/PlanContext';
 
const CurrentPlans = () => {
    const { getAllPlansForUser, getPlanById, activatePlan, deactivatePlan } = useContext(PlanContext);
    const [plans, setPlans] = useState([]);
 
    // State for viewing plan details in a dialog
    const [viewDialogOpen, setViewDialogOpen] = useState(false);
    const [planDetails, setPlanDetails] = useState(null);
   
    // State for sorting plans
    const [sortConfig, setSortConfig] = useState({
        key: 'name',
        direction: 'asc'
    });
 
    // Fetch all plans (both active and deactivated)
    const fetchPlans = async () => {
        try {
            const res = await getAllPlansForUser();
            // Our API returns the plans in res.data.content
            setPlans(res.data.content);
        } catch (error) {
            console.error('Error fetching plans', error);
            toast.error('Error fetching plans');
        }
    };
 
    useEffect(() => {
        fetchPlans();
    }, []);
 

    // When a plan name is clicked, fetch its details and open a dialog.
    const handleViewPlanDetails = async (planId) => {
        try {
            const res = await getPlanById(planId);
            setPlanDetails(res.data);
            setViewDialogOpen(true);
        } catch (error) {
            console.error('Error fetching plan details', error);
            toast.error('Error fetching plan details');
        }
    };
 
    // Handle deactivation of an active plan.
    const handleDeactivatePlan = async (planId) => {
        try {
            const res = await deactivatePlan(planId);
            if (res.success) {
                toast.success(res.message || 'Plan deactivated successfully');
                fetchPlans();
            } else {
                toast.error(res.message || 'Deactivation failed');
            }
        } catch (error) {
            console.error('Error deactivating plan', error);
            toast.error('Error deactivating plan');
        }
    };
 
    // Handle activation of a deactivated plan.
    const handleActivatePlan = async (planId) => {
        try {
            const res = await activatePlan(planId);
            if (res.success) {
                toast.success(res.message || 'Plan activated successfully');
                fetchPlans();
            } else {
                toast.error(res.message || 'Activation failed');
            }
        } catch (error) {
            console.error('Error activating plan', error);
            toast.error('Error activating plan');
        }
    };
 
    // Function to handle sorting
    const requestSort = (key) => {
        let direction = 'asc';
        if (sortConfig.key === key && sortConfig.direction === 'asc') {
            direction = 'desc';
        }
        setSortConfig({ key, direction });
    };
 
    // Function to sort plans
    const sortedPlans = (plansArray) => {
        const sortablePlans = [...plansArray];
        if (sortConfig.key) {
            sortablePlans.sort((a, b) => {
                if (sortConfig.key === 'name') {
                    // Sort alphabetically for name
                    if (a.name < b.name) {
                        return sortConfig.direction === 'asc' ? -1 : 1;
                    }
                    if (a.name > b.name) {
                        return sortConfig.direction === 'asc' ? 1 : -1;
                    }
                    return 0;
                } else if (sortConfig.key === 'price') {
                    // Sort numerically for price
                    return sortConfig.direction === 'asc'
                        ? a.price - b.price
                        : b.price - a.price;
                }
                return 0;
            });
        }
        return sortablePlans;
    };
 
    // Split plans into active and deactivated arrays and sort them
    const activePlans = sortedPlans(plans.filter((plan) => plan.active));
    const deactivatedPlans = sortedPlans(plans.filter((plan) => !plan.active));
 
    return (
        <Paper sx={{ p: 2, mb: 4 }}>
            <Typography variant="h6" gutterBottom>
                All Current Active Plans
            </Typography>
            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>
                                Name
                                <Tooltip title="Sort by name">
                                    <IconButton size="small" onClick={() => requestSort('name')}>
                                        <FilterAltIcon
                                            color={sortConfig.key === 'name' ? 'primary' : 'action'}
                                            fontSize="small"
                                        />
                                    </IconButton>
                                </Tooltip>
                            </TableCell>
                            <TableCell>
                                Price
                                <Tooltip title="Sort by price">
                                    <IconButton size="small" onClick={() => requestSort('price')}>
                                        <FilterAltIcon
                                            color={sortConfig.key === 'price' ? 'primary' : 'action'}
                                            fontSize="small"
                                        />
                                    </IconButton>
                                </Tooltip>
                            </TableCell>
                            <TableCell>Duration</TableCell>
                            <TableCell>Data Limit</TableCell>
                            <TableCell>Active</TableCell>
                            <TableCell>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {activePlans.map((plan) => (
                            <TableRow key={plan.id}>
                                <TableCell>
                                    <Button variant="text" color="primary" onClick={() => handleViewPlanDetails(plan.id)}>
                                        {plan.name}
                                    </Button>
                                </TableCell>
                                <TableCell>₹{plan.price}</TableCell>
                                <TableCell>{plan.duration}</TableCell>
                                <TableCell>{plan.dataLimit}</TableCell>
                                <TableCell>{plan.active ? 'Yes' : 'No'}</TableCell>
                                <TableCell>
                                   
                                    <Button variant="outlined" color="warning" size="small" onClick={() => handleDeactivatePlan(plan.id)}>
                                        Deactivate
                                    </Button>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
 
            <Box sx={{ mt: 4 }}>
                <Typography variant="h6" gutterBottom>
                    All Deactivated Plans
                </Typography>
                <TableContainer component={Paper}>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>
                                    Name
                                    <Tooltip title="Sort by name">
                                        <IconButton size="small" onClick={() => requestSort('name')}>
                                            <FilterAltIcon
                                                color={sortConfig.key === 'name' ? 'primary' : 'action'}
                                                fontSize="small"
                                            />
                                        </IconButton>
                                    </Tooltip>
                                </TableCell>
                                <TableCell>
                                    Price
                                    <Tooltip title="Sort by price">
                                        <IconButton size="small" onClick={() => requestSort('price')}>
                                            <FilterAltIcon
                                                color={sortConfig.key === 'price' ? 'primary' : 'action'}
                                                fontSize="small"
                                            />
                                        </IconButton>
                                    </Tooltip>
                                </TableCell>
                                <TableCell>Duration</TableCell>
                                <TableCell>Data Limit</TableCell>
                                <TableCell>Active</TableCell>
                                <TableCell>Actions</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {deactivatedPlans.map((plan) => (
                                <TableRow key={plan.id}>
                                    <TableCell>
                                        <Button variant="text" color="primary" onClick={() => handleViewPlanDetails(plan.id)}>
                                            {plan.name}
                                        </Button>
                                    </TableCell>
                                    <TableCell>{plan.price}</TableCell>
                                    <TableCell>{plan.duration}</TableCell>
                                    <TableCell>{plan.dataLimit}</TableCell>
                                    <TableCell>{plan.active ? 'Yes' : 'No'}</TableCell>
                                    <TableCell>
                                       
                                        <Button variant="outlined" color="success" size="small" onClick={() => handleActivatePlan(plan.id)}>
                                            Activate
                                        </Button>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Box>
 
            {/* Dialog for Viewing Plan Details */}
            <Dialog open={viewDialogOpen} onClose={() => setViewDialogOpen(false)} fullWidth>
            <DialogTitle>Plan Details</DialogTitle>
            <DialogContent dividers>
            {planDetails ? (
            <Box>
                <Typography variant="body1">
                    <strong>Name:</strong> {planDetails.name}
                </Typography>
                <Typography variant="body1">
                    <strong>Description:</strong> {planDetails.description}
                </Typography>
                <Typography variant="body1">
                    <strong>Price:</strong> ₹{planDetails.price}
                </Typography>
                <Typography variant="body1">
                    <strong>Duration:</strong> {planDetails.duration} days
                </Typography>
                <Typography variant="body1">
                    <strong>Data Limit:</strong> {planDetails.dataLimit} GB
                </Typography>
                <Typography variant="body1">
                    <strong>Features:</strong> {planDetails.features && planDetails.features.join(', ')}
                </Typography>
                <Typography variant="body1">
                    <strong>Active:</strong> {planDetails.active ? 'Yes' : 'No'}
                </Typography>
                </Box>
                ) : (
                <Typography>Loading...</Typography>
             )}
             </DialogContent>
            <DialogActions>
        <Button onClick={() => setViewDialogOpen(false)}>Close</Button>
    </DialogActions>
</Dialog>
 
        </Paper>
    );
};
 
export default CurrentPlans;