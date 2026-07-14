import { useState } from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import { Box, Typography, TextField, Button, Link, Paper, Alert } from '@mui/material';
import api from '../api';

function Register() {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setPasswordError('');

    if (password !== confirmPassword) {
      setPasswordError('Passwords do not match');
      return;
    }

    try {
      await api.Auth.register({ username, email, password });
      console.log('Registration is successful!');
      navigate('/login');
    } catch (error) {
      console.error('Registration error:', error.response?.data?.message || error.message);
    }
  };

  const handlePasswordChange = (e) => {
    setPassword(e.target.value);
    if (passwordError) setPasswordError('');
  };

  const handleConfirmPasswordChange = (e) => {
    setConfirmPassword(e.target.value);
    if (passwordError) setPasswordError('');
  };

  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '100vh',
        backgroundColor: '#f9fafb',
        padding: 3,
      }}
    >
      <Box sx={{ textAlign: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1" fontWeight="bold" color="primary">
          Shortcut Link
        </Typography>
        <Typography variant="subtitle1" color="textSecondary">
          Create a short link in one click
        </Typography>
      </Box>

      <Paper
        elevation={3}
        sx={{
          width: '100%',
          maxWidth: 400,
          padding: 4,
        }}
      >
        <Typography variant="h5" align="center" gutterBottom>
          Register
        </Typography>

        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 2 }}>
          <TextField
            fullWidth
            label="Username"
            variant="standard"
            margin="normal"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
          <TextField
            fullWidth
            label="Email"
            type="email"
            variant="standard"
            margin="normal"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <TextField
            fullWidth
            label="Password"
            type="password"
            variant="standard"
            margin="normal"
            value={password}
            onChange={handlePasswordChange}
            required
          />
          <TextField
            fullWidth
            label="Confirm Password"
            type="password"
            variant="standard"
            margin="normal"
            value={confirmPassword}
            onChange={handleConfirmPasswordChange}
            required
          />
          {passwordError && (
            <Alert severity="error" sx={{ mt: 1 }}>
              {passwordError}
            </Alert>
          )}
          <Button
            type="submit"
            fullWidth
            variant="contained"
            sx={{ mt: 3, mb: 2 }}
          >
            Register
          </Button>
          <Typography align="center">
            Already have an account?{' '}
            <Link component={RouterLink} to="/login" underline="hover">
              Log in
            </Link>
          </Typography>
        </Box>
      </Paper>
    </Box>
  );
}

export default Register;
