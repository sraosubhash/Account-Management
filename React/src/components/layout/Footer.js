import React from 'react';
import { Paper, Container, Grid, Typography, Box, Link } from '@mui/material';

const Footer = () => {
    return (
        <Paper
            component="footer"
            sx={{
                py: 4,
                mt: 'auto',
                backgroundColor: (theme) =>
                    theme.palette.mode === 'dark'
                        ? theme.palette.grey[800]
                        : theme.palette.grey[100],
            }}
            elevation={0}
        >
            <Container maxWidth="lg">
                <Grid container justifyContent="space-between" alignItems="center">
                    <Grid item>
                        <Typography variant="body2" color="text.secondary">
                            © 2025 FutureWave. All rights reserved.
                        </Typography>
                    </Grid>
                </Grid>
            </Container>
        </Paper>
    );
};

export default Footer;
