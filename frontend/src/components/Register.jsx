import { useState } from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import { Box, Typography, TextField, Button, Link, Paper, Alert } from '@mui/material';
import api from '../api';

function Register() {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [passwordErrors, setPasswordErrors] = useState('');
  const navigate = useNavigate();

  const validatePassword = (pwd) => {
    const errors = [];
    if (!pwd) {
      errors.push('Password cannot be empty');
      return errors;
    }
    if (pwd.length < 8 || pwd.length > 32) {
      errors.push('Password must be between 8 and 32 characters');
    }
    if (/\s/.test(pwd)) {
      errors.push('Password cannot contain spaces');
    }
    const allowedChars = /^[A-Za-z0-9!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]+$/;
    if (!allowedChars.test(pwd)) {
      errors.push(
        'Password contains invalid characters. Only letters, numbers, and special characters !@#$%^&*()_+-=[]{}|;:\'",.<>/? are allowed'
      );
    }
    if (!/[A-Za-z]/.test(pwd)) {
      errors.push('Password must contain at least one letter');
    }
    if (!/\d/.test(pwd)) {
      errors.push('Password must contain at least one digit');
    }
    if (!/[^A-Za-z0-9]/.test(pwd)) {
      errors.push('Password must contain at least one special character');
    }
    return errors;
  };

  const updatePasswordErrors = (pwd, confirm) => {
    const errors = [];

    if (pwd && pwd.length > 0) {
      const pwdErrors = validatePassword(pwd);
      errors.push(...pwdErrors);
    }
    if (pwd && confirm && pwd !== confirm) {
      errors.push('Passwords do not match');
    }
    setPasswordErrors(errors.join('; '));
  };

  const handlePasswordChange = (e) => {
    const newPassword = e.target.value;
    setPassword(newPassword);
    updatePasswordErrors(newPassword, confirmPassword);
  };

  const handleConfirmPasswordChange = (e) => {
    const newConfirm = e.target.value;
    setConfirmPassword(newConfirm);
    updatePasswordErrors(password, newConfirm);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errors = [];
    const pwdErrors = validatePassword(password);
    errors.push(...pwdErrors);
    if (password !== confirmPassword) {
      errors.push('Passwords do not match');
    }
    if (errors.length > 0) {
      setPasswordErrors(errors.join('; '));
      return;
    }
    setPasswordErrors('');

    try {
      await api.Auth.register({ username, email, password });
      console.log('Registration is successful!');
      navigate('/login');
    } catch (error) {
      console.error('Registration error:', error.response?.data?.message || error.message);
    }
  };

  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '100vh',
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
          {passwordErrors && (
            <Alert severity="error" sx={{ mt: 1 }}>
              {passwordErrors}
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
