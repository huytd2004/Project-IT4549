import { Box, IconButton, useTheme, Button, Stack, Badge, Popover, List, ListItem, ListItemText, ListItemAvatar, Avatar, Typography, Divider } from "@mui/material";
import { useContext, useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { ColorModeContext, tokens } from "../../theme";
import InputBase from "@mui/material/InputBase";
import LightModeOutlinedIcon from "@mui/icons-material/LightModeOutlined";
import DarkModeOutlinedIcon from "@mui/icons-material/DarkModeOutlined";
import NotificationsOutlinedIcon from "@mui/icons-material/NotificationsOutlined";
import WarningIcon from '@mui/icons-material/Warning';
import { getCookie } from "../../helper/cookies.helper";
import SearchIcon from "@mui/icons-material/Search";
import AccountMenu from "../../components/menu";

function TopBar() {
    const theme = useTheme();
    const colors = tokens(theme.palette.mode);
    const colorMode = useContext(ColorModeContext);
    const isDarkMode = theme.palette.mode === "dark";
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [anchorEl, setAnchorEl] = useState(null);
    const openNotifications = Boolean(anchorEl);

    const token = getCookie("token");
    
    // Lấy dữ liệu thông báo từ API
     useEffect(() => {
      const fetchNotifications = async () => {
        try {
          const response = await fetch('http://localhost:4000/notification');
          if (response.ok) {
            const data = await response.json();
            setNotifications(data);
            // Đếm số thông báo chưa đọc
            const unread = data.filter(notification => !notification.isRead).length;
            if( token !== 'jkskskjdksks' ) {
              unread = 0;
            }
            setUnreadCount(unread);
          }
        } catch (error) {
          console.error("Lỗi khi lấy thông báo:", error);
        }
      };

      // Gọi API khi component mounted và mỗi 30 giây
      fetchNotifications();
      const interval = setInterval(fetchNotifications, 30000);
      
      return () => clearInterval(interval);
    }, []);

    const handleNotificationClick = (event) => {
      setAnchorEl(event.currentTarget);
    };

    const handleCloseNotifications = () => {
      setAnchorEl(null);
    };

    const formatTime = (timestamp) => {
      const date = new Date(timestamp);
      return date.toLocaleString();
    };

  return (
    <>
    <Box 
       display="flex" 
       justifyContent="space-between" p={2} 
       sx={{
        borderBottom: `1px solid ${colors.grey[500]}`
       }} 
    >
      
      {/* SEARCH BAR */}
      <Box display="flex" gap="10px">
        <Box
          display="flex"
          backgroundColor={colors.primary[400]}
          borderRadius="3px"
        >
          <InputBase sx={{ ml: 2, flex: 1 }} placeholder="Search" />
          <IconButton type="button" sx={{ p: 1 }}>
            <SearchIcon />
          </IconButton>
        </Box>
      </Box>
      

      {/* ICONS */}
      <Box display="flex" justifyContent="space-between" alignItems="center" >
        <Box display="flex" gap="10px 0">
          <IconButton onClick={colorMode.toggleColorMode}>
            {isDarkMode ? (
              <DarkModeOutlinedIcon />
            ) : (
              <LightModeOutlinedIcon />
            )}
          </IconButton>
          <IconButton onClick={handleNotificationClick}>
            <Badge badgeContent={unreadCount} color="error">
              <NotificationsOutlinedIcon />
            </Badge>
          </IconButton>
          <Popover
            open={openNotifications}
            anchorEl={anchorEl}
            onClose={handleCloseNotifications}
            anchorOrigin={{
              vertical: 'bottom',
              horizontal: 'right',
            }}
            transformOrigin={{
              vertical: 'top',
              horizontal: 'right',
            }}
            PaperProps={{
              sx: {
                width: 320,
                maxHeight: 400,
                overflow: 'auto'
              }
            }}
          >
            <Typography sx={{ p: 2, fontWeight: 'bold' }}>Thông báo</Typography>
            <Divider />
            <List sx={{ p: 0 }}>
              {token === 'jkskskjdksks' && notifications.length > 0 ? (
                notifications.map((notification, index) => (
                  <Box key={notification._id || index}>
                    <ListItem 
                      alignItems="flex-start" 
                      sx={{ 
                        backgroundColor: notification.isRead ? 'transparent' : colors.primary[400] + '30',
                        '&:hover': { backgroundColor: colors.primary[400] + '15' }
                      }}
                    >
                      <ListItemAvatar>
                        <Avatar sx={{ bgcolor: 'error.main' }}>
                          <WarningIcon />
                        </Avatar>
                      </ListItemAvatar>
                      <ListItemText
                        primary={
                          <Typography variant="subtitle1" color="text.primary">
                            {notification.petName} - {notification.alertMessage} - {notification.location}
                          </Typography>
                        }
                        secondary={
                          <>
                            <Typography component="span" variant="body2" color="text.secondary">
                              {notification.userName}
                            </Typography>
                            <br />
                            <Typography component="span" variant="caption" color="text.secondary">
                              {formatTime(notification.createdAt || new Date())}
                            </Typography>
                          </>
                        }
                      />
                    </ListItem>
                    {index < notifications.length - 1 && <Divider variant="inset" component="li" />}
                  </Box>
                ))
              ) : (
                <ListItem>
                  <ListItemText primary="Không có thông báo nào" />
                </ListItem>
              )}
            </List>
          </Popover>
          <AccountMenu sx={{m:0,p:0}}/>
        </Box>

        {token ? (<></>):(
          <Box ml="10px">
              {/* Login */}
              <Stack direction="row" spacing={2}>
                <Button variant="contained" color={colors.primary[400]} component={Link} to="/login">
                  Login
                </Button>
                <Button variant="contained" color={colors.primary[400]} component={Link} to="/register">
                  Register
                </Button>
              </Stack>
          </Box>
        )}
      </Box>
      
    </Box>
    </>
    
  );
}

export default TopBar;