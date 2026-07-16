import { Typography, Paper, Button } from '@mui/material';
import { Link } from 'react-router-dom';
import './Inactive.css';

const Inactive = () => {
  return (
    <div className="inactive-container">
      <Paper elevation={3} className="inactive-paper">
        <Typography variant="h2" color="error" gutterBottom>
          Oops!
        </Typography>
        <Typography variant="h5" gutterBottom>
          Link is inactive or expired
        </Typography>
        <Typography variant="body1" color="textSecondary">
          The link you are trying to access is not active.
        </Typography>
        <div className="inactive-button-wrapper">
          <Button variant="contained" color="primary" component={Link} to="/home">
            Go to Home
          </Button>
        </div>
      </Paper>
    </div>
  );
};

export default Inactive;
