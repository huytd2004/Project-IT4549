import { Box, Button, Typography, Divider } from "@mui/material";
import { jsPDF } from "jspdf";
// Import font để hỗ trợ tiếng Việt
import 'jspdf-font';

const Step3Receipt = ({ formData }) => {
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

  // Format thời gian đẹp hơn
  const formatDateTime = (dateString) => {
    if (!dateString) return "";
    try {
      return new Date(dateString).toLocaleString();
    } catch (e) {
      return dateString;
    }
  };

  const handleDownloadInvoice = () => {
    // Tạo document với encoding UTF-8
    const doc = new jsPDF();
    
    // Thêm font hỗ trợ Unicode/tiếng Việt
    doc.addFont('helvetica', 'normal');
    
    // Xử lý tiếng Việt bằng cách encode/decode
    const encodeVietnamese = (text) => {
      // Hàm này chuyển các ký tự đặc biệt tiếng Việt thành
      // các dãy ký tự Latin có thể hiển thị trong PDF
      return text
        .replace(/à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ/g, "a")
        .replace(/è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ/g, "e")
        .replace(/ì|í|ị|ỉ|ĩ/g, "i")
        .replace(/ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ/g, "o")
        .replace(/ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ/g, "u")
        .replace(/ỳ|ý|ỵ|ỷ|ỹ/g, "y")
        .replace(/đ/g, "d")
        .replace(/À|Á|Ạ|Ả|Ã|Â|Ầ|Ấ|Ậ|Ẩ|Ẫ|Ă|Ằ|Ắ|Ặ|Ẳ|Ẵ/g, "A")
        .replace(/È|É|Ẹ|Ẻ|Ẽ|Ê|Ề|Ế|Ệ|Ể|Ễ/g, "E")
        .replace(/Ì|Í|Ị|Ỉ|Ĩ/g, "I")
        .replace(/Ò|Ó|Ọ|Ỏ|Õ|Ô|Ồ|Ố|Ộ|Ổ|Ỗ|Ơ|Ờ|Ớ|Ợ|Ở|Ỡ/g, "O")
        .replace(/Ù|Ú|Ụ|Ủ|Ũ|Ư|Ừ|Ứ|Ự|Ử|Ữ/g, "U")
        .replace(/Ỳ|Ý|Ỵ|Ỷ|Ỹ/g, "Y")
        .replace(/Đ/g, "D");
    };

    doc.setFontSize(16);
    doc.text(encodeVietnamese("PHIẾU ĐĂNG KÝ DỊCH VỤ"), 70, 20);

    doc.setFontSize(12);
    let y = 40;
    const lines = [
      `Ten dich vu: ${encodeVietnamese(getServiceDisplayName())}`,
      `Ten thu cung: ${encodeVietnamese(formData.petName)}`,
      `Email: ${formData.email}`,
      `So dien thoai: ${formData.phone}`,
      formData.petService !== "hotel" && formData.date ? `Ngay dang ky: ${formatDateTime(formData.date)}` : "",
      formData.startDate ? `Thoi gian bat dau: ${formatDateTime(formData.startDate)}` : "",
      formData.endDate ? `Thoi gian ket thuc: ${formatDateTime(formData.endDate)}` : "",
      formData.roomType ? `Loai phong: ${formData.roomType === "vip" ? "VIP" : "Thuong"}` : "",
      `Loi nhan: ${encodeVietnamese(formData.message || "Khong co")}`,
      `So tien: ${formatCurrency(formData.total)}`,
      formData.priceDescription ? `Chi tiet: ${encodeVietnamese(formData.priceDescription)}${formData.roomType === "vip" ? " (Phong VIP x2)" : ""}` : "",
      `Trang thai thanh toan: ${encodeVietnamese(formData.paymentStatus)}`,
      `Phuong thuc thanh toan: ${encodeVietnamese(
          formData.paymentMethod === "momo" ? "Vi MoMo" : 
          formData.paymentMethod === "bank" ? "Chuyen khoan ngan hang" : 
          formData.paymentMethod === "cash" ? "Tien mat khi den" : formData.paymentMethod
      )}`,
    ];

    lines.forEach((line) => {
      if (line) {
        doc.text(line, 20, y);
        y += 10;
      }
    });
    
    doc.save("phieu-dang-ky.pdf");
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
      <Typography variant="h5" gutterBottom>
        Phiếu đăng ký dịch vụ
      </Typography>
      <Divider sx={{ mb: 2 }} />

      <Typography><strong>Tên dịch vụ:</strong> {getServiceDisplayName()}</Typography>
      <Typography><strong>Tên thú cưng:</strong> {formData.petName}</Typography>
      <Typography><strong>Email:</strong> {formData.email}</Typography>
      <Typography><strong>Số điện thoại:</strong> {formData.phone}</Typography>
      
      {formData.petService !== "hotel" && formData.date && (
        <Typography><strong>Ngày đăng ký:</strong> {formatDateTime(formData.date)}</Typography>
      )}
      
      {formData.startDate && (
        <Typography><strong>Thời gian bắt đầu:</strong> {formatDateTime(formData.startDate)}</Typography>
      )}
      
      {formData.endDate && (
        <Typography><strong>Thời gian kết thúc:</strong> {formatDateTime(formData.endDate)}</Typography>
      )}
      
      {formData.roomType && (
        <Typography>
          <strong>Loại phòng:</strong> {formData.roomType === "vip" ? "VIP" : "Thường"}
        </Typography>
      )}
      
      {formData.message && (
        <Typography><strong>Lời nhắn:</strong> {formData.message}</Typography>
      )}
      
      <Typography><strong>Số tiền:</strong> {formatCurrency(formData.total)}</Typography>
      
      {formData.priceDescription && (
        <Typography variant="body2" color="text.secondary">
          <strong>Chi tiết:</strong> {formData.priceDescription}
          {formData.roomType === "vip" && " (Phòng VIP x2)"}
        </Typography>
      )}
      
      <Typography>
        <strong>Trạng thái thanh toán:</strong> {formData.paymentStatus}
      </Typography>
      
      <Typography>
        <strong>Phương thức thanh toán:</strong> {
          formData.paymentMethod === "momo" ? "Ví MoMo" : 
          formData.paymentMethod === "bank" ? "Chuyển khoản ngân hàng" : 
          formData.paymentMethod === "cash" ? "Tiền mặt khi đến" : formData.paymentMethod
        }
      </Typography>

      <Box mt={4} display="flex" justifyContent="space-between" gap={2}>
        <Button variant="outlined" color="secondary">
          Quay lại
        </Button>
        <Button variant="outlined" color="secondary" onClick={handleDownloadInvoice}>
          Tải hóa đơn
        </Button>
      </Box>
    </Box>
  );
};

export default Step3Receipt;