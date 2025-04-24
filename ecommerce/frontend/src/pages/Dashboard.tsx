import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import {
    AppBar,
    Box,
    CssBaseline,
    Drawer,
    IconButton,
    List,
    ListItem,
    ListItemIcon,
    ListItemText,
    Toolbar,
    Typography,
    Button,
    Container,
    Paper,
    Avatar,
    Grid as MuiGrid
} from '@mui/material';
import {
    Menu as MenuIcon,
    Dashboard as DashboardIcon,
    ShoppingCart as ShoppingCartIcon,
    People as PeopleIcon,
    Store as StoreIcon,
    ExitToApp as LogoutIcon
} from '@mui/icons-material';
import { RootState } from '../store/store';
import { logout } from '../store/slices/authSlice';

const drawerWidth = 240;

export const Dashboard: React.FC = () => {
    const [mobileOpen, setMobileOpen] = React.useState(false);
    const { user } = useSelector((state: RootState) => state.auth);
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const handleDrawerToggle = () => {
        setMobileOpen(!mobileOpen);
    };

    const handleLogout = () => {
        dispatch(logout());
        navigate('/login');
    };

    const drawer = (
        <div>
            <Toolbar>
                <Typography variant="h6" noWrap component="div">
                    E-Commerce
                </Typography>
            </Toolbar>
            <List>
                <ListItem component="div">
                    <ListItemIcon>
                        <DashboardIcon />
                    </ListItemIcon>
                    <ListItemText primary="Dashboard" />
                </ListItem>
                <ListItem component="div">
                    <ListItemIcon>
                        <ShoppingCartIcon />
                    </ListItemIcon>
                    <ListItemText primary="Orders" />
                </ListItem>
                <ListItem component="div">
                    <ListItemIcon>
                        <StoreIcon />
                    </ListItemIcon>
                    <ListItemText primary="Products" />
                </ListItem>
                <ListItem component="div">
                    <ListItemIcon>
                        <PeopleIcon />
                    </ListItemIcon>
                    <ListItemText primary="Customers" />
                </ListItem>
            </List>
        </div>
    );

    return (
        <Box sx={{ display: 'flex' }}>
            <CssBaseline />
            <AppBar
                position="fixed"
                sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}
            >
                <Toolbar>
                    <IconButton
                        color="inherit"
                        aria-label="open drawer"
                        edge="start"
                        onClick={handleDrawerToggle}
                        sx={{ mr: 2, display: { sm: 'none' } }}
                    >
                        <MenuIcon />
                    </IconButton>
                    <Typography variant="h6" noWrap component="div" sx={{ flexGrow: 1 }}>
                        Dashboard
                    </Typography>
                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <Typography variant="body1" sx={{ mr: 2 }}>
                            {user?.firstName} {user?.lastName}
                        </Typography>
                        <Button color="inherit" onClick={handleLogout} startIcon={<LogoutIcon />}>
                            Logout
                        </Button>
                    </Box>
                </Toolbar>
            </AppBar>
            <Box
                component="nav"
                sx={{ width: { sm: drawerWidth }, flexShrink: { sm: 0 } }}
            >
                <Drawer
                    variant="temporary"
                    open={mobileOpen}
                    onClose={handleDrawerToggle}
                    ModalProps={{
                        keepMounted: true,
                    }}
                    sx={{
                        display: { xs: 'block', sm: 'none' },
                        '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth },
                    }}
                >
                    {drawer}
                </Drawer>
                <Drawer
                    variant="permanent"
                    sx={{
                        display: { xs: 'none', sm: 'block' },
                        '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth },
                    }}
                    open
                >
                    {drawer}
                </Drawer>
            </Box>
            <Box
                component="main"
                sx={{
                    flexGrow: 1,
                    p: 3,
                    width: { sm: `calc(100% - ${drawerWidth}px)` },
                    mt: 8
                }}
            >
                <Container maxWidth="lg">
                    <MuiGrid container spacing={3}>
                        {/* Welcome Card */}
                        <MuiGrid item xs={12}>
                            <Paper sx={{ p: 3, display: 'flex', alignItems: 'center', gap: 2 }}>
                                <Avatar sx={{ bgcolor: 'primary.main', width: 56, height: 56 }}>
                                    {user?.firstName?.[0]}
                                </Avatar>
                                <Box>
                                    <Typography variant="h5">
                                        Welcome back, {user?.firstName}!
                                    </Typography>
                                    <Typography variant="body1" color="text.secondary">
                                        {user?.shopName ? `Shop: ${user.shopName}` : 'Customer Account'}
                                    </Typography>
                                </Box>
                            </Paper>
                        </MuiGrid>

                        {/* Statistics Cards */}
                        <MuiGrid item xs={12} md={4}>
                            <Paper sx={{ p: 3, textAlign: 'center' }}>
                                <Typography variant="h6">Total Orders</Typography>
                                <Typography variant="h4">0</Typography>
                            </Paper>
                        </MuiGrid>
                        <MuiGrid item xs={12} md={4}>
                            <Paper sx={{ p: 3, textAlign: 'center' }}>
                                <Typography variant="h6">Total Products</Typography>
                                <Typography variant="h4">0</Typography>
                            </Paper>
                        </MuiGrid>
                        <MuiGrid item xs={12} md={4}>
                            <Paper sx={{ p: 3, textAlign: 'center' }}>
                                <Typography variant="h6">Total Revenue</Typography>
                                <Typography variant="h4">$0</Typography>
                            </Paper>
                        </MuiGrid>
                    </MuiGrid>
                </Container>
            </Box>
        </Box>
    );
};
