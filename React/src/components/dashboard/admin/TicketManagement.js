import React, { useState, useEffect, useContext } from 'react';
import {
    Box,
    Typography,
    Paper,
    TableContainer,
    Table,
    TableHead,
    TableRow,
    TableCell,
    TableBody,
    Button,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    FormControl,
    InputLabel,
    Select,
    MenuItem
} from '@mui/material';
import { toast } from 'react-toastify';
import SupportContext from "../../../context/SupportContext";
 
const TicketManagement = () => {
    const { getAllTickets, getAllEmployees, assignTicket } = useContext(SupportContext);
    const [tickets, setTickets] = useState([]);
    const [newTickets, setNewTickets] = useState([]);
    const [assignedTickets, setAssignedTickets] = useState([]);
    const [employees, setEmployees] = useState([]);
    const [assignDialogOpen, setAssignDialogOpen] = useState(false);
    const [selectedTicket, setSelectedTicket] = useState(null);
    const [selectedEmployee, setSelectedEmployee] = useState("");
    const [descriptionDialogOpen, setDescriptionDialogOpen] = useState(false);
    const [fullDescription, setFullDescription] = useState("");
 
    const fetchTickets = async () => {
        try {
            const res = await getAllTickets();
            setTickets(res);
            setNewTickets(res.filter((ticket) => ticket.status === "NEW"));
            setAssignedTickets(res.filter((ticket) => ticket.status === "ASSIGNED"));
        } catch (error) {
            console.error("Error fetching tickets", error);
            toast.error("Error fetching tickets");
        }
    };
 
    const fetchEmployees = async () => {
        try {
            const res = await getAllEmployees();
            setEmployees(res);
        } catch (error) {
            console.error("Error fetching employees", error);
            toast.error("Error fetching employees");
        }
    };
 
    useEffect(() => {
        fetchTickets();
        fetchEmployees();
    }, []);
 
    const handleOpenAssignDialog = (ticket) => {
        setSelectedTicket(ticket);
        setAssignDialogOpen(true);
    };
 
    const handleAssign = async () => {
        if (!selectedEmployee) {
            toast.error("Please select an employee to assign.");
            return;
        }
        try {
            const res = await assignTicket(selectedTicket.id, selectedEmployee);
            if (res && res.status === "ASSIGNED") {
                toast.success("Ticket assigned successfully");
                setAssignDialogOpen(false);
                fetchTickets();
            } else {
                toast.error("Assignment failed");
            }
        } catch (error) {
            console.error("Error assigning ticket", error);
            toast.error("Error assigning ticket");
        }
    };
 
    const handleViewDescription = (description) => {
        setFullDescription(description);
        setDescriptionDialogOpen(true);
    };
 
    return (
        <Paper sx={{ p: 2, mt: 4 }}>
            <Typography variant="h6" gutterBottom>
                List All New Tickets
            </Typography>
            {newTickets.length > 0 ? (
                <TableContainer component={Paper}>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>Ticket ID</TableCell>
                                <TableCell>Title</TableCell>
                                <TableCell>Description</TableCell>
                                <TableCell>Status</TableCell>
                                <TableCell>Priority</TableCell>
                                <TableCell>User ID</TableCell>
                                <TableCell>Created At</TableCell>
                                <TableCell>Assign</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {newTickets.map((ticket) => (
                                <TableRow key={ticket.id}>
                                    <TableCell>{ticket.id}</TableCell>
                                    <TableCell>{ticket.title}</TableCell>
                                    <TableCell>
                                        {ticket.description.length > 10 ? (
                                            <Button
                                                variant="text"
                                                color="primary"
                                                onClick={() => handleViewDescription(ticket.description)}
                                            >
                                                {ticket.description.substring(0, 10)}...
                                            </Button>
                                        ) : (
                                            ticket.description
                                        )}
                                    </TableCell>
                                    <TableCell>{ticket.status}</TableCell>
                                    <TableCell>{ticket.priority}</TableCell>
                                    <TableCell>{ticket.userId}</TableCell>
                                    <TableCell>{new Date(ticket.createdAt).toLocaleString()}</TableCell>
                                    <TableCell>
                                        <Button variant="contained" color="primary" size="small" onClick={() => handleOpenAssignDialog(ticket)}>
                                            Assign
                                        </Button>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            ) : (
                <Typography>No new tickets available.</Typography>
            )}
 
            <Box sx={{ mt: 4 }}>
                <Typography variant="h6" gutterBottom>
                    List All Assigned Tickets
                </Typography>
                {assignedTickets.length > 0 ? (
                    <TableContainer component={Paper}>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell>Ticket ID</TableCell>
                                    <TableCell>Title</TableCell>
                                    <TableCell>Description</TableCell>
                                    <TableCell>Status</TableCell>
                                    <TableCell>Priority</TableCell>
                                    <TableCell>User ID</TableCell>
                                    <TableCell>Assigned Employee</TableCell>
                                    <TableCell>Created At</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {assignedTickets.map((ticket) => (
                                    <TableRow key={ticket.id}>
                                        <TableCell>{ticket.id}</TableCell>
                                        <TableCell>{ticket.title}</TableCell>
                                        <TableCell>
                                            {ticket.description.length > 10 ? (
                                                <Button
                                                    variant="text"
                                                    color="primary"
                                                    onClick={() => handleViewDescription(ticket.description)}
                                                >
                                                    {ticket.description.substring(0, 10)}...
                                                </Button>
                                            ) : (
                                                ticket.description
                                            )}
                                        </TableCell>
                                        <TableCell>{ticket.status}</TableCell>
                                        <TableCell>{ticket.priority}</TableCell>
                                        <TableCell>{ticket.userId}</TableCell>
                                        <TableCell>
                                            {(() => {
                                                const employee = employees.find(emp => emp.id === ticket.employeeId);
                                                return employee ? `${employee.firstName} ${employee.lastName}` : ticket.employeeId;
                                            })()}
                                        </TableCell>
                                        <TableCell>{new Date(ticket.createdAt).toLocaleString()}</TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                ) : (
                    <Typography>No assigned tickets available.</Typography>
                )}
            </Box>
 
            {/* Assign Ticket Dialog */}
            <Dialog open={assignDialogOpen} onClose={() => setAssignDialogOpen(false)} fullWidth>
                <DialogTitle>Assign Ticket</DialogTitle>
                <DialogContent>
                    <Typography variant="body1" gutterBottom>
                        Ticket: {selectedTicket && selectedTicket.title}
                    </Typography>
                    <FormControl fullWidth>
                        <InputLabel id="employee-select-label">Select Employee</InputLabel>
                        <Select
                            labelId="employee-select-label"
                            value={selectedEmployee}
                            label="Select Employee"
                            onChange={(e) => setSelectedEmployee(e.target.value)}
                        >
                            {employees.map((emp) => (
                                <MenuItem key={emp.id} value={emp.id}>
                                    {emp.firstName} {emp.lastName} ({emp.email})
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setAssignDialogOpen(false)}>Cancel</Button>
                    <Button variant="contained" onClick={handleAssign}>
                        Assign Ticket
                    </Button>
                </DialogActions>
            </Dialog>
 
            {/* Description Dialog */}
            <Dialog open={descriptionDialogOpen} onClose={() => setDescriptionDialogOpen(false)} fullWidth>
                <DialogTitle>Full Ticket Description</DialogTitle>
                <DialogContent>
                    <Typography variant="body1">{fullDescription}</Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setDescriptionDialogOpen(false)}>Close</Button>
                </DialogActions>
            </Dialog>
        </Paper>
    );
};
 
export default TicketManagement;