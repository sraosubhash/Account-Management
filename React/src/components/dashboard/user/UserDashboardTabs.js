// src/components/UserDashboardTabs.js
import React, { useState, useEffect, useContext } from 'react';
import { Box, Tabs, Tab, Typography, Button, Pagination, TextField, Select, MenuItem, FormControl, InputLabel, Grid } from '@mui/material';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';
import PlanContext from '../../../context/PlanContext';
import PlanHistory from './PlanHistory';
import ActivePlan from './ActivePlan';
import TransactionDetails from './TransactionDetails';
import UsageDetails from './UsageDetails'; 
import BillingDetails from './BillingDetails';
import AccountDetails from './AccountDetails';
 
function TabPanel(props) {
    const { children, value, index, ...other } = props;
    return (
        <div
            role="tabpanel"
            hidden={value !== index}
            id={`vertical-tabpanel-${index}`}
            aria-labelledby={`vertical-tab-${index}`}
            {...other}
        >
            {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
        </div>
    );
}
 
function a11yProps(index) {
    return {
        id: `vertical-tab-${index}`,
        'aria-controls': `vertical-tabpanel-${index}`,
    };
}
 
const UserDashboardTabs = () => {
    const navigate = useNavigate();
    const { getAllPlansForUser, getPlanHistory } = useContext(PlanContext);
    const [value, setValue] = useState(0);
    const [plans, setPlans] = useState([]);
    const [filteredPlans, setFilteredPlans] = useState([]);
    const [subscriptionCount, setSubscriptionCount] = useState(0);
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 6;
    
    // Filter states
    const [nameFilter, setNameFilter] = useState('');
    const [priceFilter, setPriceFilter] = useState('');
    const [sortOption, setSortOption] = useState('none');
 
    const userId = localStorage.getItem('userId');
 
    const fetchPlans = async () => {
        try {
            const res = await getAllPlansForUser();
            setPlans(res.data.content);
            setFilteredPlans(res.data.content);
        } catch (error) {
            console.error('Error fetching plans for user', error);
            toast.error('Error fetching plans');
        }
    };
 
    const fetchSubscriptionCount = async () => {
        try {
            const res = await getPlanHistory(userId);
            const count = res.data && res.data.planHistory ? res.data.planHistory.length : 0;
            setSubscriptionCount(count);
        } catch (error) {
            console.error('Error fetching subscription count', error);
            toast.error('Error fetching subscription count');
        }
    };
 
    useEffect(() => {
        fetchPlans();
        fetchSubscriptionCount();
    }, [getAllPlansForUser, getPlanHistory, userId]);

    // Apply filters whenever filter states change
    useEffect(() => {
        applyFilters();
    }, [nameFilter, priceFilter, sortOption, plans]);

    const applyFilters = () => {
        let result = [...plans];

        if (nameFilter) {
            result = result.filter(plan => 
                plan.name.toLowerCase().includes(nameFilter.toLowerCase())
            );
        }
        
        
        // Apply sorting
        if (sortOption === 'price-asc') {
            result.sort((a, b) => a.price - b.price);
        } else if (sortOption === 'price-desc') {
            result.sort((a, b) => b.price - a.price);
        } else if (sortOption === 'name-asc') {
            result.sort((a, b) => a.name.localeCompare(b.name));
        } else if (sortOption === 'name-desc') {
            result.sort((a, b) => b.name.localeCompare(a.name));
        }
        
        setFilteredPlans(result);
        setCurrentPage(1); 
    };

    const handleResetFilters = () => {
        setNameFilter('');
        setPriceFilter('');
        setSortOption('none');
        setFilteredPlans(plans);
        setCurrentPage(1);
    };

    const activePlans = filteredPlans.filter((plan) => plan.active);
    const totalPages = Math.ceil(activePlans.length / itemsPerPage);
    const currentPlans = activePlans.slice(
        (currentPage - 1) * itemsPerPage,
        currentPage * itemsPerPage
    );
 
    const handleChange = (event, newValue) => {
        setValue(newValue);
    };
 
    const handleBuyPlan = (plan) => {
        navigate('/payment-checkout', { state: { planId: plan.id, amount: plan.price } });
    };
 
    return (
        <Box sx={{ display: 'flex', height: '100%', backgroundColor: 'background.paper' }}>
            <Tabs
                orientation="vertical"
                variant="scrollable"
                value={value}
                onChange={handleChange}
                aria-label="User Dashboard Tabs"
                sx={{ borderRight: 1, borderColor: 'divider', minWidth: '200px' }}
            >
                <Tab label="All Plans" {...a11yProps(0)} />
                <Tab label="Active Plan" {...a11yProps(1)} />
                <Tab label="Plan History" {...a11yProps(2)} />
                <Tab label="Billing Details" {...a11yProps(3)} />
                <Tab label="Transaction Details" {...a11yProps(4)} />
                <Tab label="Usage Details" {...a11yProps(5)} />
                <Tab label="Account Details" {...a11yProps(6)} />
            </Tabs>
            <TabPanel value={value} index={0}>
                <Typography variant="h6" sx={{ mb: 2 }}>
                    All Available Plans
                </Typography>
                
                {/* Filter and Sort Controls */}
                <Grid container spacing={2} sx={{ mb: 3 }}>
                    <Grid item xs={12} sm={3}>
                        <TextField
                            fullWidth
                            label="Filter by Name"
                            variant="outlined"
                            size="small"
                            value={nameFilter}
                            onChange={(e) => setNameFilter(e.target.value)}
                        />
                    </Grid>

                    <Grid item xs={12} sm={4}>
                        <FormControl fullWidth size="small">
                            <InputLabel>Sort By</InputLabel>
                            <Select
                                value={sortOption}
                                label="Sort By"
                                onChange={(e) => setSortOption(e.target.value)}
                            >
                                <MenuItem value="none">None</MenuItem>
                                <MenuItem value="price-asc">Price: Low to High</MenuItem>
                                <MenuItem value="price-desc">Price: High to Low</MenuItem>
                                <MenuItem value="name-asc">Name: A to Z</MenuItem>
                                <MenuItem value="name-desc">Name: Z to A</MenuItem>
                            </Select>
                        </FormControl>
                    </Grid>
                    <Grid item xs={12} sm={2}>
                        <Button 
                            variant="outlined" 
                            fullWidth 
                            onClick={handleResetFilters}
                            sx={{ height: '40px' }}
                        >
                            Reset
                        </Button>
                    </Grid>
                </Grid>
                
                {subscriptionCount >= 2 && (
                    <Typography color="error" sx={{ mb: 2 }}>
                        You are not allowed to select any further plans as you have already opted for 2 plans.
                    </Typography>
                )}
                
                {activePlans && activePlans.length > 0 ? (
                    <>
                        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2 }}>
                            {currentPlans.map((plan) => (
                                <Box
                                    key={plan.id}
                                    sx={{
                                        flex: '1 1 calc(33.33% - 16px)',
                                        minWidth: '280px',
                                        p: 2,
                                        border: '1px solid #ccc',
                                        borderRadius: 2,
                                        backgroundColor: 'background.paper',
                                    }}
                                >
                                    <Typography variant="subtitle1" sx={{ fontWeight: 'bold', mb: 1 }}>
                                        {plan.name}
                                    </Typography>
                                    <Typography variant="body2">
                                        Price: ₹{plan.price} | Duration: {plan.duration} days <br/> Data: {plan.dataLimit} GB | SMS: {plan.smsLimit} <br/> Talktime Minutes: {plan.talkTimeMinutes}
                                    </Typography>
                                    <Box sx={{ mt: 2 }}>
                                        <Button
                                            variant="contained"
                                            color="primary"
                                            fullWidth
                                            disabled={subscriptionCount >= 2}
                                            sx={{
                                                textTransform: 'none',
                                                fontWeight: 'bold',
                                                borderRadius: 2,
                                                boxShadow: '0px 4px 8px rgba(0,0,0,0.1)',
                                                transition: 'background-color 0.3s, box-shadow 0.3s',
                                                '&:hover': {
                                                    backgroundColor: 'primary.dark',
                                                    boxShadow: '0px 6px 12px rgba(0,0,0,0.15)',
                                                },
                                            }}
                                            onClick={() => handleBuyPlan(plan)}
                                        >
                                            Buy Plan
                                        </Button>
                                    </Box>
                                </Box>
                            ))}
                        </Box>
                        {activePlans.length > 0 && currentPlans.length === 0 && (
                            <Typography sx={{ mt: 2, textAlign: 'center' }}>
                                No plans match your filter criteria.
                            </Typography>
                        )}
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
                    <Typography>No plans available.</Typography>
                )}
            </TabPanel>
            <TabPanel value={value} index={2}>
                <PlanHistory />
            </TabPanel>
            <TabPanel value={value} index={1}>
                <ActivePlan />
            </TabPanel>
            <TabPanel value={value} index={3}>
                <BillingDetails />
            </TabPanel>
            <TabPanel value={value} index={4}>
                <TransactionDetails />
            </TabPanel>
            <TabPanel value={value} index={5}>
                <Typography variant="h6" sx={{ mb: 2 }}>
                    Usage Details
                </Typography>
                <UsageDetails />
            </TabPanel>
            <TabPanel value={value} index={6}>
                <AccountDetails />
            </TabPanel>
        </Box>
    );
};
 
export default UserDashboardTabs;