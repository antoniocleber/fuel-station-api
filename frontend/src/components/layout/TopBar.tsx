import React from 'react';
import {
  AppBar,
  Toolbar,
  IconButton,
  Typography,
  Box,
  useTheme,
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import LocalGasStationIcon from '@mui/icons-material/LocalGasStation';
import { useUIStore } from '../../stores/uiStore';

const DRAWER_WIDTH = 240;

interface TopBarProps {
  drawerWidth?: number;
}

const TopBar: React.FC<TopBarProps> = ({ drawerWidth = DRAWER_WIDTH }) => {
  const theme = useTheme();
  const { sidebarOpen, toggleSidebar } = useUIStore();

  return (
    <AppBar
      position="fixed"
      sx={{
        width: { sm: sidebarOpen ? `calc(100% - ${drawerWidth}px)` : '100%' },
        ml: { sm: sidebarOpen ? `${drawerWidth}px` : 0 },
        transition: theme.transitions.create(['margin', 'width'], {
          easing: theme.transitions.easing.sharp,
          duration: theme.transitions.duration.leavingScreen,
        }),
      }}
    >
      <Toolbar>
        <IconButton
          color="inherit"
          edge="start"
          onClick={toggleSidebar}
          sx={{ mr: 2 }}
          aria-label="toggle sidebar"
        >
          <MenuIcon />
        </IconButton>
        <LocalGasStationIcon sx={{ mr: 1 }} />
        <Typography variant="h6" noWrap component="div" sx={{ flexGrow: 1 }}>
          Fuel Station - Sistema de Gestão
        </Typography>
        <Box />
      </Toolbar>
    </AppBar>
  );
};

export default TopBar;
