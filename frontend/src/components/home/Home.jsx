import { useState } from 'react';
import { Typography, Paper, TextField, Button, Stack, Checkbox, FormControlLabel, Box, Alert, CircularProgress } from '@mui/material';
import api from '../../api';
import './Home.css';

function Home() {
  const [url, setUrl] = useState('');
  const [customAlias, setCustomAlias] = useState(false);
  const [customCode, setCustomCode] = useState('');
  const [hasExpiration, setHasExpiration] = useState(false);
  const [expirationDate, setExpirationDate] = useState('');
  const [errors, setErrors] = useState({ url: false, customCode: false });
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [errorMsg, setErrorMsg] = useState('');

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
    } catch (error) {
      setErrorMsg(error.response?.data?.message || 'Failed to generate short link');
    } finally {
      setLoading(false);
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
    </>
  );
}

export default Home;
