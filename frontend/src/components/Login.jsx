import { useState } from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import {
  Container,
  Typography,
  TextField,
  Button,
  Box,
  Link,
  Paper,
  Alert,
} from '@mui/material';
import api from '../api';

function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      const response = await api.Auth.login({ username, password });
      localStorage.setItem('token', response.data.token);
      navigate('/home');
    } catch (error) {
      console.error('Login error:', error.response?.data?.message || error.message);
        if (error.response?.status === 401 || error.response?.status === 400) {
        setError('Invalid username or password');
      } else {
        setError(error.response?.data?.message || 'Something went wrong. Please try again.');
      }
    }
  };

  const handleUsernameChange = (e) => {
    setUsername(e.target.value);
    if (error) setError('');
  };

  const handlePasswordChange = (e) => {
    setPassword(e.target.value);
    if (error) setError('');
  };

  return (
    <Container maxWidth="xs">
      <Box sx={{ textAlign: 'center', mt: 6, mb: 3 }}>
        <Typography variant="h4" component="h1" fontWeight="bold" color="primary">
          Shortcut Link
        </Typography>
        <Typography variant="subtitle1" color="textSecondary">
          Create a short link in one click
        </Typography>
      </Box>

      <Paper elevation={3} sx={{ p: 4 }}>
        <Typography variant="h5" component="h2" align="center" gutterBottom>
          Log in
        </Typography>

        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 2 }}>
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}
          <TextField
            fullWidth
            label="Username"
            variant="standard"
            margin="normal"
            value={username}
            onChange={handleUsernameChange}
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
          <Button
            type="submit"
            fullWidth
            variant="contained"
            sx={{ mt: 3, mb: 2 }}
          >
            Log in
          </Button>
          <Typography align="center">
            No account?{' '}
            <Link component={RouterLink} to="/register" underline="hover">
              Register
            </Link>
          </Typography>
        </Box>
      </Paper>
    </Container>
  );
}

export default Login;
