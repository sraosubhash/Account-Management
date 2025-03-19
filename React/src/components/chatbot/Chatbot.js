// src/components/Chatbot.js
import React, { useState, useEffect, useContext } from "react";
import {
  Box,
  Typography,
  TextField,
  Button,
  Paper,
  Avatar,
  Fade,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Badge,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
} from "@mui/material";
import { toast } from "react-toastify";
import SupportContext from "../../context/SupportContext";
import SendIcon from '@mui/icons-material/Send';
import BoltIcon from '@mui/icons-material/Bolt';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import ChatIcon from '@mui/icons-material/Chat';
import ConfirmationNumberIcon from '@mui/icons-material/ConfirmationNumber';
 
const Chatbot = () => {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [showOptions, setShowOptions] = useState(false);
  const [showTicketForm, setShowTicketForm] = useState(false);
  const [isTyping, setIsTyping] = useState(false);
  const [ticket, setTicket] = useState({
    title: "",
    description: "",
    priority: "MEDIUM",
  });
  const [tickets, setTickets] = useState([]);
  const [selectedView, setSelectedView] = useState('chat'); // 'chat' or 'tickets'
  const [selectedTicket, setSelectedTicket] = useState(null);
  const [isTicketDialogOpen, setIsTicketDialogOpen] = useState(false);
 
  const { createTicket, getUserTickets } = useContext(SupportContext);
 
  const FAQ_QUESTIONS = [
    "How do I recharge my phone?",
    "What payment methods are accepted?",
    "How long does recharge activation take?",
    "Can I schedule auto-recharge?",
    "How do I check my current balance?",
    "What if my recharge fails?"
  ];
 
  const FAQ_ANSWERS = {
    "How do I recharge my phone?": "You can recharge your phone through our mobile app, website, or by visiting a nearby store. Simply enter your phone number and select the recharge amount.",
    "What payment methods are accepted?": "We accept all major payment methods including credit/debit cards, UPI, net banking, and digital wallets like PayPal, Google Pay, and Apple Pay.",
    "How long does recharge activation take?": "Recharges are usually instant. However, in rare cases, it might take up to 15 minutes. If you don't see your recharge reflected after 15 minutes, please contact support.",
    "Can I schedule auto-recharge?": "Yes! You can set up auto-recharge in your account settings. Choose your preferred recharge amount and set a minimum balance threshold to trigger automatic recharge.",
    "How do I check my current balance?": "You can check your balance by dialing *123# on your phone, through our mobile app, or by logging into your account on our website.",
    "What if my recharge fails?": "If your recharge fails, the amount will be refunded to your payment method within 24-48 hours. You can also check the status of your refund in the 'Transaction History' section."
  };
 
  const fetchTickets = async () => {
    try {
      const userTickets = await getUserTickets(userId);
      setTickets(userTickets);
    } catch (error) {
      toast.error("Failed to fetch tickets");
    }
  };
 
  const userId = localStorage.getItem("userId");
 
  useEffect(() => {
    setIsTyping(true);
    setTimeout(() => {
      setIsTyping(false);
      setMessages([{
        text: "Hi! I'm Telexa, your virtual assistant. How can I help you today?",
        sender: "bot"
      }]);
      setShowOptions(true);
    }, 1500);
 
    fetchTickets();
  }, []);
 
  const sendMessage = (text, sender = "user") => {
    if (!text.trim()) return;
 
    let newMessages = [...messages, { text, sender }];
    setMessages(newMessages);
 
    let response = "I'm here to assist you. You can type 'Raise Ticket' to submit a support request.";
 
    const lowerText = text.toLowerCase();
 
    if (lowerText === "hi" || lowerText === "hello") {
        response = "Hello! How can I help you today?";
    } else if (lowerText.includes("how are you")) {
        response = "I'm just a bot, but I'm here and ready to assist you!";
    } else if (lowerText.includes("thank you") || lowerText.includes("thanks")) {
        response = "You're welcome! Let me know if you need any further assistance.";
    } else if (lowerText.includes("bye")) {
        response = "Goodbye! Have a great day!";
    } else if (lowerText.includes("ticket") || lowerText.includes("tickets") || lowerText.includes("raise ticket")) {
      setShowTicketForm(true);
      setSelectedView('chat');
    } else if (lowerText === "faq" || lowerText.includes("help") || lowerText.includes("support")) {
      response = "Here are some frequently asked questions. Please select a number  : \n\n" +
        FAQ_QUESTIONS.map((q, i) => `${ i + 1}. ${ q}`).join("\n");
    } else if (!isNaN(text) && parseInt(text) >= 1 && parseInt(text) <= FAQ_QUESTIONS.length) {
      const selectedQuestion = FAQ_QUESTIONS[parseInt(text) - 1];
      response = `\n Q: ${selectedQuestion}\nA: ${FAQ_ANSWERS[selectedQuestion]}`;
    } else {
      response = "Sorry I don't understand that. Type FAQ || help to view frequently asked questions or type raise ticket to raise a new ticket."
    }
 
    setTimeout(() => {
        setMessages([...newMessages, { text: response, sender: "bot" }]);
    }, 1000);
  };
 
  const handleRaiseTicket = () => {
    setShowTicketForm(true);
    setSelectedView('chat');
  };
 
  const handleTicketChange = (e) => {
    const { name, value } = e.target;
    setTicket({ ...ticket, [name]: value });
  };
 
  const submitTicket = async () => {
    if (!ticket.title || !ticket.description || !ticket.priority) {
      toast.error("Please fill in all required fields.");
      return;
    }
 
    try {
      setIsTyping(true);
      const userId = localStorage.getItem("userId");
      const newTicket = {
        title: ticket.title,
        description: ticket.description,
        priority: ticket.priority,
        userId: parseInt(userId, 10),
      };
 
      await createTicket(newTicket);
      await fetchTickets(); // Refresh tickets list
      setIsTyping(false);
      toast.success("Your ticket has been raised successfully!");
      sendMessage("Your ticket has been raised successfully! I'll keep you updated on its progress.", "bot");
      setShowTicketForm(false);
      setTicket({ title: "", description: "", priority: "HIGH" });
    } catch (error) {
      setIsTyping(false);
      toast.error(error.response?.data?.message || "Failed to submit ticket.");
    }
  };
 
  const getStatusColor = (status) => {
    switch (status) {
      case 'NEW':
        return 'info.light';
      case 'IN_PROGRESS':
        return 'warning.light';
      case 'COMPLETED':
        return 'success.light';
      case 'RESOLVED':
        return 'success.light';
      default:
        return 'grey.500';
    }
  };
 
  const getPriorityColor = (priority) => {
    switch (priority) {
      case 'HIGH':
        return 'error.light';
      case 'MEDIUM':
        return 'warning.light';
      case 'LOW':
        return 'success.light';
      default:
        return 'grey.500';
    }
  };
 
  const handleTicketClick = (ticket) => {
    setSelectedTicket(ticket);
    setIsTicketDialogOpen(true);
  };
 
  const handleTicketDialogClose = () => {
    setIsTicketDialogOpen(false);
  };
 
  return (
      <Box sx={{ display: 'flex', height: '100%', backgroundColor: 'background.default', position: 'relative', top: '-35px', paddingTop: '35px' }}>
        {/* Side Panel */}
        <Box
            sx={{
              width: 250,
              borderRight: 1,
              borderColor: 'divider',
              backgroundColor: 'background.paper',
              display: 'flex',
              flexDirection: 'column',
            }}
        >
          <List sx={{ pt: 0 }}>
            <ListItem
                button
                selected={selectedView === 'chat'}
                onClick={() => setSelectedView('chat')}
                sx={{
                  py: 2,
                  backgroundColor: selectedView === 'chat' ? 'action.selected' : 'transparent',
                }}
            >
              <ListItemIcon>
                <ChatIcon color={selectedView === 'chat' ? 'primary' : 'inherit'} />
              </ListItemIcon>
              <ListItemText primary="Chat" />
            </ListItem>
            <ListItem
                button
                selected={selectedView === 'tickets'}
                onClick={() => setSelectedView('tickets')}
                sx={{
                  py: 2,
                  backgroundColor: selectedView === 'tickets' ? 'action.selected' : 'transparent',
                }}
            >
              <ListItemIcon>
                <Badge badgeContent={tickets.length} color="primary">
                  <ConfirmationNumberIcon color={selectedView === 'tickets' ? 'primary' : 'inherit'} />
                </Badge>
              </ListItemIcon>
              <ListItemText primary="My Tickets" />
            </ListItem>
          </List>
 
          <Box sx={{ p: 2, mt: 'auto' }}>
            <Button
                fullWidth
                variant="contained"
                onClick={handleRaiseTicket}
                startIcon={<BoltIcon />}
                sx={{ borderRadius: 2, textTransform: 'none' }}
            >
              New Ticket
            </Button>
          </Box>
        </Box>
 
        {/* Main Content */}
        <Box sx={{ flex: 1, display: 'flex', flexDirection: 'column', height: '100%' }}>
          {/* Header */}
          <Box
              sx={{
                backgroundColor: "primary.main",
                backgroundImage: 'linear-gradient(45deg, #1976d2, #2196f3)',
                p: 2,
                color: "primary.contrastText",
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
              }}
          >
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <Avatar sx={{ backgroundColor: 'primary.light' }}>
                {selectedView === 'chat' ? <BoltIcon /> : <ConfirmationNumberIcon />}
              </Avatar>
              <Box>
                <Typography variant="h6">
                  {selectedView === 'chat' ? 'Telexa' : 'My Tickets'}
                </Typography>
                <Typography variant="caption" sx={{ opacity: 0.8 }}>
                  {selectedView === 'chat' ? 'AI Support Assistant' : `${tickets.length} tickets`}
                </Typography>
              </Box>
            </Box>
          </Box>
 
          {/* Content Area */}
          {selectedView === 'chat' ? (
              <>
                {/* Chat Messages */}
                <Box
                    sx={{
                      flex: 1,
                      p: 2,
                      overflowY: "auto",
                      display: "flex",
                      flexDirection: "column",
                      gap: 2,
                    }}
                >
                  {messages.map((msg, index) => (
                      <Fade in key={index} timeout={500}>
                        <Box
                            sx={{
                              alignSelf: msg.sender === "user" ? "flex-end" : "flex-start",
                              display: 'flex',
                              alignItems: 'flex-start',
                              gap: 1,
                              maxWidth: "75%",
                            }}
                        >
                          {msg.sender === "bot" && (
                              <Avatar
                                  sx={{
                                    width: 32,
                                    height: 32,
                                    backgroundColor: 'primary.light',
                                  }}
                              >
                                <BoltIcon sx={{ fontSize: 18 }} />
                              </Avatar>
                          )}
                          <Box
                              sx={{
                                backgroundColor: msg.sender === "user"
                                    ? 'primary.main'
                                    : 'background.paper',
                                color: msg.sender === "user"
                                    ? 'primary.contrastText'
                                    : 'text.primary',
                                p: 2,
                                borderRadius: 3,
                                borderTopLeftRadius: msg.sender === "bot" ? 0 : 3,
                                borderTopRightRadius: msg.sender === "user" ? 0 : 3,
                                boxShadow: 1,
                                wordBreak: "break-word",
                              }}
                          >
                            <Typography variant="body2" sx={{ whiteSpace: "pre-line" }}>{msg.text}</Typography>
                          </Box>
                        </Box>
                      </Fade>
                  ))}
 
                  {isTyping && (
                      <Fade in timeout={500}>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                          <Avatar
                              sx={{
                                width: 32,
                                height: 32,
                                backgroundColor: 'primary.light',
                              }}
                          >
                            <BoltIcon sx={{ fontSize: 18 }} />
                          </Avatar>
                          <Box
                              sx={{
                                backgroundColor: 'background.paper',
                                p: 2,
                                borderRadius: 3,
                                borderTopLeftRadius: 0,
                                display: 'flex',
                                alignItems: 'center',
                                gap: 1,
                              }}
                          >
                            <AccessTimeIcon sx={{ fontSize: 16, color: 'text.secondary' }} />
                            <Typography variant="body2" color="text.secondary">
                              Typing...
                            </Typography>
                          </Box>
                        </Box>
                      </Fade>
                  )}
 
                  {showTicketForm && (
                      <Fade in timeout={500}>
                        <Box
                            sx={{
                              p: 3,
                              borderRadius: 3,
                              backgroundColor: 'background.paper',
                              display: "flex",
                              flexDirection: "column",
                              gap: 2,
                              boxShadow: 2,
                            }}
                        >
                          <Typography variant="h6" align="center" color="primary">
                            Raise a Ticket
                          </Typography>
                          <TextField
                              size="small"
                              label="Ticket Title"
                              name="title"
                              value={ticket.title}
                              onChange={(e) => {
                                  if (e.target.value.length <= 20) {
                                      setTicket({ ...ticket, title: e.target.value });
                                  }
                              }}
                              fullWidth
                              required
                              inputProps={{ maxLength: 20 }} // Prevents input beyond 10 characters
                              sx={{
                                '& .MuiOutlinedInput-root': {
                                  borderRadius: 2,
                                },
                              }}
                              helperText={`${ticket.title.length}/20 characters`} // Live character count
                          />
 
                          <TextField
                              size="small"
                              label="Issue Description"
                              name="description"
                              value={ticket.description}
                              onChange={(e) => {
                                  if (e.target.value.length <= 100) {
                                      setTicket({ ...ticket, description: e.target.value });
                                  }
                              }}
                              fullWidth
                              multiline
                              rows={3}
                              required
                              inputProps={{ maxLength: 100 }} // Prevents input beyond 100 characters
                              sx={{
                                '& .MuiOutlinedInput-root': {
                                  borderRadius: 2,
                                },
                              }}
                              helperText={`${ticket.description.length}/100 characters`} // Live character count
                          />
                          {/* <Typography
                          <TextFile 
                            size="small"
                            lable="Comment"
                            name="comment"
                            */}
                          <Button
                              variant="contained"
                              onClick={submitTicket}
                              sx={{
                                mt: 1,
                                borderRadius: 2,
                                textTransform: 'none',
                                py: 1,
                              }}
                          >
                            Submit Ticket
                          </Button>
                        </Box>
                      </Fade>
                  )}
                </Box>
 
                {/* Chat Input */}
                <Box
                    sx={{
                      p: 2,
                      borderTop: 1,
                      borderColor: 'divider',
                      backgroundColor: 'background.paper',
                    }}
                >
                  <Box sx={{ display: 'flex', gap: 1 }}>
                    <TextField
                        size="small"
                        fullWidth
                        placeholder="Type your message..."
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                        sx={{
                          '& .MuiOutlinedInput-root': {
                            borderRadius: 2,
                          },
                        }}
                    />
                    <Button
                        variant="contained"
                        onClick={() => {
                          if (input.trim()) {
                            sendMessage(input);
                            setInput("");
                          }
                        }}
                        sx={{
                          borderRadius: 2,
                          minWidth: 'auto',
                          width: 48,
                          height: 40,
                        }}
                    >
                      <SendIcon fontSize="small" />
                    </Button>
                  </Box>
                </Box>
              </>
          ) : (
              // Tickets View
              <Box sx={{ flex: 1, overflowY: 'auto', p: 2 }}>
                {tickets.length > 0 ? (
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                      {tickets.map((ticket) => (
                          <Paper
                              key={ticket.id}
                              elevation={1}
                              sx={{
                                p: 2,
                                borderRadius: 2,
                                '&:hover': {
                                  backgroundColor: 'action.hover',
                                },
                              }}
                              onClick={() => handleTicketClick(ticket)}
                          >
                            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                              <Typography variant="subtitle1" color="primary" sx={{ fontWeight: 'medium' }}>
                                #{ticket.id} - {ticket.title}
                              </Typography>
                              <Box
                                  sx={{
                                    px: 1.5,
                                    py: 0.5,
                                    borderRadius: 1,
                                    backgroundColor: getStatusColor(ticket.status),
                                    color: 'white',
                                    fontSize: '0.75rem',
                                    fontWeight: 'bold',
                                    display: 'flex',
                                    alignItems: 'center',
                                    gap: 0.5,
                                  }}
                              >
                                {ticket.status === 'NEW' && <AccessTimeIcon sx={{ fontSize: 16 }} />}
                                {ticket.status}
                              </Box>
                            </Box>
 
                            <Typography
                                variant="body2"
                                color="text.secondary"
                                sx={{
                                  mb: 2,
                                  display: '-webkit-box',
                                  WebkitLineClamp: 2,
                                  WebkitBoxOrient: 'vertical',
                                  overflow: 'hidden',
                                }}
                            >
                              {ticket.description.substring(0, 10)}{ticket.description.length > 10 ? '...' : ''}
                            </Typography>
 
                            <Box sx={{
                              display: 'flex',
                              justifyContent: 'space-between',
                              alignItems: 'center',
                              borderTop: 1,
                              borderColor: 'divider',
                              pt: 2,
                            }}>
                              <Box
                                  sx={{
                                    display: 'flex',
                                    gap: 2,
                                    alignItems: 'center',
                                  }}
                              >
                                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                  <Typography variant="caption" color="text.secondary">
                                    Priority:
                                  </Typography>
                                  <Box
                                      sx={{
                                        px: 1,
                                        py: 0.25,
                                        borderRadius: 1,
                                        backgroundColor: getPriorityColor(ticket.priority),
                                        color: 'white',
                                        fontSize: '0.75rem',
                                        fontWeight: 'medium',
                                      }}
                                  >
                                    {ticket.priority}
                                  </Box>
                                </Box>
                                {ticket.employeeId && (
                                    <Typography variant="caption" color="text.secondary">
                                      Agent ID: {ticket.employeeId}
                                    </Typography>
                                )}
                              </Box>
                              <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end' }}>
                                <Typography variant="caption" color="text.secondary">
                                  Created: {new Date(ticket.createdAt).toLocaleDateString()}
                                </Typography>
                                {ticket.updatedAt !== ticket.createdAt && (
                                    <Typography variant="caption" color="text.secondary">
                                      Updated: {new Date(ticket.updatedAt).toLocaleDateString()}
                                    </Typography>
                                )}
                              </Box>
                            </Box>
                          </Paper>
                      ))}
                    </Box>
                ) : (
                    <Box
                        sx={{
                          display: 'flex',
                          flexDirection: 'column',
                          alignItems: 'center',
                          justifyContent: 'center',
                          gap: 2,
                          height: '100%',
                        }}
                    >
                      <ConfirmationNumberIcon
                          sx={{
                            fontSize: 64,
                            color: 'text.secondary',
                            opacity: 0.5,
                          }}
                      />
                      <Typography variant="h6" color="text.secondary">
                        No tickets found
                      </Typography>
                      <Button
                          variant="contained"
                          onClick={handleRaiseTicket}
                          startIcon={<BoltIcon />}
                          sx={{
                            mt: 2,
                            borderRadius: 2,
                            textTransform: 'none',
                          }}
                      >
                        Create Your First Ticket
                      </Button>
                    </Box>
                )}
              </Box>
          )}
        </Box>
 
        {/* Ticket Dialog */}
        <Dialog
            open={isTicketDialogOpen}
            onClose={handleTicketDialogClose}
            maxWidth="sm"
            fullWidth
        >
          <DialogTitle>
            Ticket #{selectedTicket?.id} - {selectedTicket?.title}
          </DialogTitle>
          <DialogContent>
            <DialogContentText>
              {selectedTicket?.description}
            </DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleTicketDialogClose} color="primary">
              Close
            </Button>
          </DialogActions>
        </Dialog>
      </Box>
  );
};
 
export default Chatbot;