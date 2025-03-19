import React, { useState } from 'react';
import { Tabs, Tab, Paper, Box } from '@mui/material';
import Header from '../../layout/Header';
import Footer from '../../layout/Footer';
import CreatePlan from './CreatePlan';
import CurrentPlans from './CurrentPlans';
import TicketManagement from './TicketManagement';
 
// TabPanel Component to control visibility
const TabPanel = ({ children, value, index }) => (
    <div role="tabpanel" hidden={value !== index} id={`vertical-tabpanel-${index}`} aria-labelledby={`vertical-tab-${index}`}>
        {value === index && <Box sx={{ p: 2 }}>{children}</Box>}
    </div>
);
 
// Accessibility props for tabs
const a11yProps = (index) => ({
    id: `vertical-tab-${index}`,
    'aria-controls': `vertical-tabpanel-${index}`,
});
 
const AdminDashboard = ({ isDarkMode, toggleTheme }) => {
    const [value, setValue] = useState(0); // Default active state
 
    const handleChange = (event, newValue) => {
        setValue(newValue);
    };
 
    return (
        <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            {/* Header Component */}
            <Header isDarkMode={isDarkMode} toggleTheme={toggleTheme} />
 
            {/* Main Content with Proper Margin Below Header */}
            <Box sx={{ display: 'flex', flexGrow: 1, mt: 8, overflow: 'auto' }}>
                <Paper sx={{ display: 'flex', width: '100%', borderRadius: 0, boxShadow: 'none' }}>
                    {/* Sidebar Navigation */}
                    <Tabs
                        orientation="vertical"
                        variant="scrollable"
                        value={value}
                        onChange={handleChange}
                        aria-label="Admin Dashboard Tabs"
                        sx={{ borderRight: 1, borderColor: 'divider', minWidth: '220px' }}
                    >
                        <Tab label="Create Plan" {...a11yProps(0)} />
                        <Tab label="Current Plans" {...a11yProps(1)} />
                        <Tab label="Ticket Management" {...a11yProps(2)} />
                    </Tabs>
 
                    {/* Right Section - Corresponding Components */}
                    <Box sx={{ flexGrow: 1, p: 3 }}>
                        <TabPanel value={value} index={0}>
                            <CreatePlan />
                        </TabPanel>
                        <TabPanel value={value} index={1}>
                            <CurrentPlans />
                        </TabPanel>
                        <TabPanel value={value} index={2}>
                            <TicketManagement />
                        </TabPanel>
                    </Box>
                </Paper>
            </Box>
 
            {/* Footer Component - Ensures it always stays at the bottom */}
            <Footer />
        </Box>
    );
};
 
export default AdminDashboard;