import { useState, useEffect } from 'react';
import {
  Typography, Paper, TextField, Button, Stack, Checkbox, FormControlLabel,
  Box, Alert, CircularProgress, Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, Switch, IconButton
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import api from '../../api';
import './Home.css';
import { useNavigate } from 'react-router-dom';

function Home() {
  const navigate = useNavigate();
  const [url, setUrl] = useState('');
  const [customAlias, setCustomAlias] = useState(false);
  const [customCode, setCustomCode] = useState('');
  const [hasExpiration, setHasExpiration] = useState(false);
  const [expirationDate, setExpirationDate] = useState('');
  const [errors, setErrors] = useState({ url: false, customCode: false });
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [errorMsg, setErrorMsg] = useState('');

  const [links, setLinks] = useState([]);
  const [loadingLinks, setLoadingLinks] = useState(false);

  const loadLinks = async () => {
    setLoadingLinks(true);
    try {
      const response = await api.Links.getAllLinks();
      setLinks(response.data);
    } catch (err) {
      console.error('Failed to load links', err);
    } finally {
      setLoadingLinks(false);
    }
  };

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login', { replace: true });
    } else {
      // eslint-disable-next-line react-hooks/set-state-in-effect
      loadLinks();
    }
  }, [navigate]);

  const handleGenerate = async () => {
    const newErrors = { url: false, customCode: false };
    if (!url.trim()) newErrors.url = true;
    if (customAlias && !customCode.trim()) newErrors.customCode = true;
    setErrors(newErrors);
    if (newErrors.url || newErrors.customCode) return;

    setLoading(true);
    setErrorMsg('');
    try {
      let expiresAt = null;
      if (hasExpiration && expirationDate) {
        let dateStr = expirationDate;
        if (dateStr.length === 16) dateStr += ':00';
        expiresAt = dateStr;
      }
      const payload = {
        originalURL: url,
        shortCode: customAlias ? customCode : null,
        expiresAt,
      };
      const response = await api.Links.createLink(payload);
      setResult({
        shortUrl: response.data.linkURL,
        message: response.data.message,
        expiresAt: response.data.link?.expiresAt || null,
      });
      loadLinks();
    } catch (error) {
      setErrorMsg(error.response?.data?.message || 'Failed to generate short link');
    } finally {
      setLoading(false);
    }
  };

  const handleToggle = async (shortCode, currentActive) => {
    try {
      await api.Links.toggleLink(shortCode, !currentActive);
      loadLinks();
    } catch (err) {
      console.error('Toggle failed', err);
    }
  };

  const handleDelete = async (shortCode) => {
    if (!window.confirm('Delete this link?')) return;
    try {
      await api.Links.deleteLink(shortCode);
      loadLinks();
    } catch (err) {
      console.error('Delete failed', err);
    }
  };

  const handleReset = () => {
    setResult(null);
    setUrl('');
    setCustomAlias(false);
    setCustomCode('');
    setHasExpiration(false);
    setExpirationDate('');
    setErrors({ url: false, customCode: false });
    setErrorMsg('');
  };

  if (result) {
    return (
      <>
        <Typography variant="h3" className="home-title">Shortcut Link</Typography>
        <Paper elevation={3} className="create-link-paper">
          <Stack spacing={3}>
            <Typography variant="h6" fontWeight="bold">Your Shortlink:</Typography>
            <Typography
              variant="body1"
              component="a"
              href={result.shortUrl}
              target="_blank"
              className="result-link"
            >
              {result.shortUrl}
            </Typography>
            {result.expiresAt && (
              <Typography variant="body2" color="textSecondary">
                Expires at: {new Date(result.expiresAt).toLocaleString()}
              </Typography>
            )}
            <Button variant="contained" color="primary" onClick={handleReset} className="create-link-button" disableElevation>
              Generate Another
            </Button>
          </Stack>
        </Paper>
        <LinkTable
          links={links}
          loading={loadingLinks}
          onToggle={handleToggle}
          onDelete={handleDelete}
        />
      </>
    );
  }

  return (
    <>
      <Typography variant="h3" className="home-title">Shortcut Link</Typography>
      <Paper elevation={3} className="create-link-paper">
        <Stack spacing={3}>
          <TextField
            label="Input Your URL"
            variant="outlined"
            fullWidth
            value={url}
            onChange={(e) => setUrl(e.target.value)}
            error={errors.url}
            helperText={errors.url && 'URL is required'}
            disabled={loading}
            className="create-link-input"
          />

          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} alignItems="center">
            <FormControlLabel
              control={
                <Checkbox
                  checked={customAlias}
                  onChange={(e) => setCustomAlias(e.target.checked)}
                  color="primary"
                  disabled={loading}
                />
              }
              label="Custom short link"
              sx={{ flexShrink: 0 }}
            />
            {customAlias && (
              <TextField
                label="Enter desired short code"
                variant="outlined"
                fullWidth
                value={customCode}
                onChange={(e) => setCustomCode(e.target.value)}
                error={errors.customCode}
                helperText={errors.customCode && 'Short code is required'}
                disabled={loading}
                className="create-link-input"
              />
            )}
          </Stack>

          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} alignItems="center">
            <FormControlLabel
              control={
                <Checkbox
                  checked={hasExpiration}
                  onChange={(e) => setHasExpiration(e.target.checked)}
                  color="primary"
                  disabled={loading}
                />
              }
              label="Set expiration date"
              sx={{ flexShrink: 0 }}
            />
            {hasExpiration && (
              <TextField
                type="datetime-local"
                variant="outlined"
                fullWidth
                value={expirationDate}
                onChange={(e) => setExpirationDate(e.target.value)}
                disabled={loading}
                InputLabelProps={{ shrink: true }}
                className="create-link-input"
              />
            )}
          </Stack>

          {errorMsg && <Alert severity="error">{errorMsg}</Alert>}

          <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
            <Button
              variant="contained"
              color="primary"
              onClick={handleGenerate}
              disabled={loading}
              className="create-link-button"
              disableElevation
            >
              {loading ? <CircularProgress size={24} color="inherit" /> : 'Generate'}
            </Button>
          </Box>
        </Stack>
      </Paper>

      <LinkTable
        links={links}
        loading={loadingLinks}
        onToggle={handleToggle}
        onDelete={handleDelete}
      />
    </>
  );
}

function LinkTable({ links, loading, onToggle, onDelete }) {
  return (
    <Box sx={{ width: '90%', maxWidth: 700, mx: 'auto', mt: 4 }}>
      <Typography variant="h5" gutterBottom>Your Short Links</Typography>
      <TableContainer component={Paper} sx={{ borderRadius: '32px' }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>#</TableCell>
              <TableCell>Original Link</TableCell>
              <TableCell>Short Link</TableCell>
              <TableCell align="center">Redirects</TableCell>
              <TableCell align="center">Active</TableCell>
              <TableCell align="center">Action</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {loading ? (
              <TableRow>
                <TableCell colSpan={6} align="center">
                  <CircularProgress size={24} />
                </TableCell>
              </TableRow>
            ) : links.length === 0 ? (
              <TableRow>
                <TableCell colSpan={6} align="center">No links yet</TableCell>
              </TableRow>
            ) : (
              links.map((link, index) => (
                <TableRow key={link.id}>
                  <TableCell>{index + 1}</TableCell>
                  <TableCell>
                    <a href={link.originalURL} target="_blank" rel="noopener noreferrer" style={{ wordBreak: 'break-all' }}>
                      {link.originalURL}
                    </a>
                  </TableCell>
                  <TableCell>
                    <a href={`http://localhost:8080/r/${link.shortCode}`} target="_blank" rel="noopener noreferrer">
                      {link.shortCode}
                    </a>
                  </TableCell>
                  <TableCell align="center">{link.clickCount}</TableCell>
                  <TableCell align="center">
                    <Switch
                      checked={link.isActive}
                      onChange={() => onToggle(link.shortCode, link.isActive)}
                      color="primary"
                    />
                  </TableCell>
                  <TableCell align="center">
                    <IconButton color="error" onClick={() => onDelete(link.shortCode)}>
                      <DeleteIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}

export default Home;
