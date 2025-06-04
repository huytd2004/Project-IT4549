import { Box, Button, MenuItem, TextField, useTheme } from "@mui/material";
import { useState, useEffect } from "react";
import { tokens } from "../../theme";
import { Snackbar, Alert } from "@mui/material";
import { getListPet } from "../../service/petList.service";

const Step1Register = ({ formData, setFormData, onNext }) => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);
  // ---------------phần lấy danh sách thú cưng
  const [petList, setPetList] = useState([]);
  const [total, setTotal] = useState("0đ");
  
  useEffect(() => {
    const fetchPets = async () => {
      try {
        const res = await getListPet();
        if (res) {
          setPetList(res); // giả sử `res.data` là array
        }
      } catch (error) {
        console.error("Không thể tải danh sách thú cưng", error);
      }
    };

    fetchPets();
  }, []);
  
  // Tính toán thành tiền dựa trên dịch vụ và thời gian
// 1. Sửa useEffect để tính lại khi roomType thay đổi
useEffect(() => {
  calculateTotal();
}, [formData.petService, formData.startDate, formData.endDate, formData.date, formData.roomType]);
  
const calculateTotal = () => {
  let calculatedTotal = 0;
  
  switch(formData.petService) {
    case "spa":
      calculatedTotal = 300000;
      break;
    case "exam":
      calculatedTotal = 500000;
      break;
    case "hotel":
      if (formData.startDate && formData.endDate) {
        const start = new Date(formData.startDate);
        const end = new Date(formData.endDate);
        const diffTime = Math.abs(end - start);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        
        // Đảm bảo roomType luôn có giá trị mặc định "normal" nếu chưa chọn
        const roomType = formData.roomType || "normal";
        // Hệ số nhân dựa theo loại phòng
        const roomMultiplier = roomType === "vip" ? 2 : 1;
        
        if (diffDays < 1) {
          // Nếu đặt dưới 24 giờ, tính theo giờ
          const diffHours = Math.max(1, Math.ceil(diffTime / (1000 * 60 * 60)));
          const hourlyRate = 20000 * roomMultiplier;
          calculatedTotal = diffHours * hourlyRate;
          
          // Cập nhật mô tả giá vào formData để hiển thị chi tiết
          setFormData({
            ...formData, 
            priceDescription: `${diffHours} giờ x ${hourlyRate.toLocaleString()}đ/giờ`
          });
        } else {
          // Nếu đặt từ 24 giờ trở lên, tính theo ngày
          const dailyRate = 200000 * roomMultiplier;
          calculatedTotal = diffDays * dailyRate;
          
          // Cập nhật mô tả giá vào formData để hiển thị chi tiết
          setFormData({
            ...formData, 
            priceDescription: `${diffDays} ngày x ${dailyRate.toLocaleString()}đ/ngày`
          });
        }
      }
      break;
    default:
      calculatedTotal = 0;
  }
  
  const formattedTotal = new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(calculatedTotal).replace(/\s/g, '');
  
  setTotal(formattedTotal);
  setFormData(prev => ({...prev, total: calculatedTotal})); // Sử dụng callback để tránh lập vô hạn
};

  const [errors, setErrors] = useState({});
  // phần thông báo
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: "",
    severity: "success",
  });
  // end phần thông báo
  const validate = () => {
    const newErrors = {};
    if (!formData.petService) newErrors.petService = "Vui lòng chọn dịch vụ";
    if (!formData.petName) newErrors.petName = "Vui lòng nhập tên thú cưng";
    if (!formData.email) newErrors.email = "Vui lòng nhập email";
    if (!formData.phone) newErrors.phone = "Vui lòng nhập số điện thoại";
    if (formData.petService === "hotel") {
      if (!formData.roomType) newErrors.roomType = "Vui lòng chọn loại phòng";
      if (!formData.startDate)
        newErrors.startDate = "Vui lòng chọn ngày bắt đầu";
      if (!formData.endDate) newErrors.endDate = "Vui lòng chọn ngày kết thúc";
      
      // Kiểm tra ngày kết thúc phải sau ngày bắt đầu
      if (formData.startDate && formData.endDate) {
        const start = new Date(formData.startDate);
        const end = new Date(formData.endDate);
        if (end <= start) {
          newErrors.endDate = "Ngày kết thúc phải sau ngày bắt đầu";
        }
      }
    } else {
      if (!formData.date) newErrors.date = "Vui lòng chọn thời gian";
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleNext = async () => {
    if (!validate()) return;

    try {
      const res = await fetch("http://localhost:4000/service_register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formData),
      });
      // const res = await serviceRegister(formData);
      if (res.ok) {
        const data = await res.json();
        setSnackbar({
          open: true,
          message: "Đăng ký thành công!",
          severity: "success",
        });
        onNext();
      } else {
        setSnackbar({
          open: true,
          message: "Lỗi khi đăng ký!",
          severity: "error",
        });
      }
    } catch (error) {
      setSnackbar({
        open: true,
        message: "Không thể kết nối đến máy chủ!",
        severity: "error",
      });
    }
  };

  const services = [
    { value: "hotel", label: "Dịch vụ khách sạn lưu trú" },
    { value: "spa", label: "Dịch vụ spa thú cưng" },
    { value: "exam", label: "Dịch vụ khám sức khỏe" },
  ];

  const isHotel = formData.petService === "hotel";

  // Xử lý thay đổi dịch vụ để reset các trường liên quan
  const handleServiceChange = (value) => {
    const newFormData = { ...formData, petService: value };
    
    // Reset các trường không liên quan
    if (value === 'hotel') {
      delete newFormData.date;
    } else {
      delete newFormData.roomType;
      delete newFormData.startDate;
      delete newFormData.endDate;
    }
    
    setFormData(newFormData);
  };

  return (
    <Box
      maxWidth={650}
      mx="auto"
      p={3}
      display="flex"
      flexDirection="column"
      gap={2}
      border="1px solid #ccc"
      borderRadius={2}
      boxShadow={2}
    >
      <TextField
        required
        fullWidth
        select
        label="Tên dịch vụ"
        value={formData.petService || ""}
        onChange={(e) => handleServiceChange(e.target.value)}
        error={!!errors.petService}
        helperText={errors.petService}
      >
        {services.map((s) => (
          <MenuItem key={s.value} value={s.value}>
            {s.label}
          </MenuItem>
        ))}
      </TextField>

      <TextField
        select
        label="Tên thú cưng"
        fullWidth
        value={formData.petName || ""}
        onChange={(e) => setFormData({ ...formData, petName: e.target.value })}
        error={!!errors.petName}
        helperText={errors.petName}
      >
        {petList.length === 0 ? (
          <MenuItem disabled>Không có thú cưng nào</MenuItem>
        ) : (
          petList.map((pet) => (
            <MenuItem key={pet.id} value={pet.name}>
              {pet.name}
            </MenuItem>
          ))
        )}
      </TextField>

      <TextField
        label="Email"
        fullWidth
        value={formData.email || ""}
        onChange={(e) => setFormData({ ...formData, email: e.target.value })}
        error={!!errors.email}
        helperText={errors.email}
      />

      <TextField
        label="Số điện thoại"
        fullWidth
        value={formData.phone || ""}
        onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
        error={!!errors.phone}
        helperText={errors.phone}
      />

      {isHotel && (
        <>
          <TextField
            select
            label="Loại phòng"
            fullWidth
            value={formData.roomType || ""}
            onChange={(e) =>
              setFormData({ ...formData, roomType: e.target.value })
            }
            error={!!errors.roomType}
            helperText={errors.roomType}
          >
            <MenuItem value="normal">Thường (Giá cơ bản)</MenuItem>
            <MenuItem value="vip">VIP (x2 Giá phòng thường)</MenuItem>
          </TextField>

          <Box display="flex" gap="10px">
            <TextField
              label="Ngày bắt đầu"
              type="datetime-local"
              fullWidth
              InputLabelProps={{ shrink: true }}
              value={formData.startDate || ""}
              onChange={(e) =>
                setFormData({ ...formData, startDate: e.target.value })
              }
              error={!!errors.startDate}
              helperText={errors.startDate}
            />

            <TextField
              label="Ngày kết thúc"
              type="datetime-local"
              fullWidth
              InputLabelProps={{ shrink: true }}
              value={formData.endDate || ""}
              onChange={(e) =>
                setFormData({ ...formData, endDate: e.target.value })
              }
              error={!!errors.endDate}
              helperText={errors.endDate}
            />
          </Box>
        </>
      )}

      {!isHotel && (
        <TextField
          label="Thời gian"
          type="datetime-local"
          fullWidth
          InputLabelProps={{ shrink: true }}
          value={formData.date || ""}
          onChange={(e) => setFormData({ ...formData, date: e.target.value })}
          error={!!errors.date}
          helperText={errors.date}
        />
      )}

      <TextField
        label="Lời nhắn"
        fullWidth
        multiline
        rows={3}
        inputProps={{ maxLength: 200 }}
        value={formData.message || ""}
        onChange={(e) => setFormData({ ...formData, message: e.target.value })}
      />

      <TextField
        label="Thành tiền"
        fullWidth
        disabled
        value={total}
        InputProps={{
          readOnly: true,
        }}
        helperText={formData.petService === "hotel" && formData.priceDescription ? 
          formData.priceDescription + (formData.roomType === "vip" ? " (Phòng VIP x2)" : "") : ""}
      />

      <Box textAlign="right" mt={2}>
        <Button variant="contained" onClick={handleNext}>
          Tiếp tục
        </Button>
      </Box>
      {/* phần thông báo */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={3000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
        anchorOrigin={{ vertical: "top", horizontal: "center" }}
      >
        <Alert
          onClose={() => setSnackbar({ ...snackbar, open: false })}
          severity={snackbar.severity}
          sx={{ width: "100%" }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default Step1Register;