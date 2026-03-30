import React from 'react';
import { Alert, Box, Button } from '@mui/material';

interface ErrorAlertProps {
  message: string;
  onRetry?: () => void;
}

const ErrorAlert: React.FC<ErrorAlertProps> = ({ message, onRetry }) => {
  return (
    <Box p={2}>
      <Alert
        severity="error"
        action={
          onRetry ? (
            <Button color="inherit" size="small" onClick={onRetry}>
              Tentar novamente
            </Button>
          ) : undefined
        }
      >
        {message}
      </Alert>
    </Box>
  );
};

export default ErrorAlert;
