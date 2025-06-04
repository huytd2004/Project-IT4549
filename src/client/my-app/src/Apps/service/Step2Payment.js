import { Box, Button, Typography, Divider, RadioGroup, FormControlLabel, Radio, Snackbar, Alert } from "@mui/material";
import { useState } from "react";

const Step2Payment = ({ formData, setFormData, onNext }) => {
  const [paymentMethod, setPaymentMethod] = useState("momo");
  const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });

  const handlePayNow = () => {
    setFormData({
      ...formData,
      paymentMethod,
      paymentStatus: "Đã thanh toán"
    });

    setSnackbar({ open: true, message: "Thanh toán thành công!", severity: "success" });

    setTimeout(() => {
      onNext();
    }, 1500);
  };

  const handleNextWithoutPayment = () => {
    setFormData({
      ...formData,
      paymentMethod,
      paymentStatus: "Chưa thanh toán"
    });

    onNext(); // không cần delay nếu không thanh toán
  };

  // Format số tiền thành định dạng tiền tệ VND
  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND'
    }).format(amount).replace(/\s/g, '');
  };

  // Xác định tên dịch vụ hiển thị dễ hiểu
  const getServiceDisplayName = () => {
    switch(formData.petService) {
      case "spa": return "Dịch vụ spa thú cưng";
      case "exam": return "Dịch vụ khám sức khỏe";
      case "hotel": return "Dịch vụ khách sạn lưu trú";
      default: return formData.petService;
    }
  };

  return (
    <Box 
      maxWidth={550}
      mx="auto"
      p={3}
      display="flex"
      flexDirection="column"
      gap={2}
      border="1px solid #ccc"
      borderRadius={2}
      boxShadow={2}
    >
      <Typography variant="h4" gutterBottom>
        Thanh toán dịch vụ
      </Typography>
      <Divider sx={{ my: 2 }} />

      <Typography variant="body1"><strong>Tên dịch vụ:</strong> {getServiceDisplayName()}</Typography>
      <Typography variant="body1"><strong>Tên thú cưng:</strong> {formData.petName}</Typography>
      
      {formData.petService === "hotel" && (
        <>
          <Typography variant="body1">
            <strong>Loại phòng:</strong> {formData.roomType === "vip" ? "VIP" : "Thường"}
          </Typography>
          <Typography variant="body1">
            <strong>Thời gian:</strong> {formData.startDate ? new Date(formData.startDate).toLocaleString() : ''} - 
            {formData.endDate ? new Date(formData.endDate).toLocaleString() : ''}
          </Typography>
        </>
      )}
      
      {formData.petService !== "hotel" && formData.date && (
        <Typography variant="body1">
          <strong>Thời gian:</strong> {new Date(formData.date).toLocaleString()}
        </Typography>
      )}

      <Typography variant="body1">
        <strong>Số tiền:</strong> {formatCurrency(formData.total)}
      </Typography>
      
      {formData.priceDescription && (
        <Typography variant="body2" color="text.secondary">
          {formData.priceDescription}
          {formData.roomType === "vip" && " (Phòng VIP x2)"}
        </Typography>
      )}

      <Box mt={3}>
        <Typography variant="subtitle1" gutterBottom>Chọn phương thức thanh toán:</Typography>
        <RadioGroup
          value={paymentMethod}
          onChange={(e) => setPaymentMethod(e.target.value)}
        >
          <FormControlLabel value="momo" control={<Radio />} label="Ví MoMo" />
          <FormControlLabel value="bank" control={<Radio />} label="Chuyển khoản ngân hàng" />
          <FormControlLabel value="cash" control={<Radio />} label="Tiền mặt khi đến" />
        </RadioGroup>
      </Box>

      <Box mt={4} display="flex" justifyContent="space-between" gap={2}>
        <Button variant="outlined" color="secondary" onClick={handlePayNow}>
          Thanh toán ngay
        </Button>
        <Button variant="contained" color="primary" onClick={handleNextWithoutPayment}>
          Tiếp tục
        </Button>
        
      </Box>

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

export default Step2Payment;