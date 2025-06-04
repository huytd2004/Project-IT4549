

# 🐾 HỆ THỐNG QUẢN LÝ TRUNG TÂM CHĂM SÓC THÚ CƯNG

Một hệ thống quản lý toàn diện giúp kết nối giữa trung tâm chăm sóc thú cưng, bác sĩ thú y, nhân viên, chủ cửa hàng và chủ nuôi. Hệ thống hỗ trợ các chức năng đặt lịch khám, làm đẹp, lưu trú, quản lý sức khỏe thú cưng, bán hàng và thống kê hoạt động.

---

## 🔧 Công nghệ sử dụng

### 👨‍💻 Frontend
- **ReactJS**: Framework xây dựng giao diện người dùng hiện đại và tương tác.
- **MUI (Material UI)**: Bộ thư viện UI component hỗ trợ thiết kế giao diện nhanh, đẹp, và nhất quán.
- **ECharts**, **ApexCharts**: Thư viện biểu đồ cho phần thống kê trực quan và tương tác.

### 🧠 Backend
- **Spring Boot**: Framework Java mạnh mẽ cho việc xây dựng RESTful API và xử lý nghiệp vụ backend.

### 🗃️ Cơ sở dữ liệu
- **PostgreSQL**: Hệ quản trị cơ sở dữ liệu quan hệ mạnh mẽ, ổn định và mã nguồn mở.

---

## 📦 Cài đặt dự án

### 1. Backend – Spring Boot


# Di chuyển vào thư mục backend
```
cd backend
```
# Biên dịch và chạy ứng dụng
./mvnw spring-boot:run

Cấu hình PostgreSQL nằm trong `application.properties` hoặc `application.yml`:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/petcare_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 2. Frontend – ReactJS


# Di chuyển vào thư mục frontend
```
cd frontend
```
# Cài đặt thư viện
```
npm install
```
# Chạy ứng dụng
```
npm start
```

## 📁 Cấu trúc chính

```
├── backend/
│   ├── src/main/java/com/petcare/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── model/
│   │   └── PetCareApplication.java
│   └── resources/
│       └── application.properties
│
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   ├── App.js
│   │   └── index.js
│
└── README.md
```

---

## 🚀 Tính năng chính

* Quản lý hồ sơ thú cưng, lịch sử khám bệnh và làm đẹp
* Đặt lịch hẹn khám, làm đẹp, lưu trú
* Gửi thông báo nhắc lịch, kết quả khám
* Quản lý dịch vụ, phòng lưu trú và nhân viên
* Quản lý cửa hàng sản phẩm cho thú cưng
* Thống kê báo cáo bằng biểu đồ (ApexCharts, ECharts)
* Phân quyền người dùng (Chủ nuôi, Bác sĩ, Nhân viên, Quản trị viên)

---

## 🧪 Tài khoản mẫu (Demo)

| Vai trò       | Tài khoản                                 | Mật khẩu |
| ------------- | ----------------------------------------- | -------- |
| Chủ nuôi      | [user@demo.com](mailto:user@demo.com)     | 123456   |
| Bác sĩ thú y  | [doctor@demo.com](mailto:doctor@demo.com) | 123456   |
| Nhân viên     | [staff@demo.com](mailto:staff@demo.com)   | 123456   |
| Quản trị viên | [admin@demo.com](mailto:admin@demo.com)   | admin123 |

---

## 📬 Liên hệ

> 📧 Email: hungnguyen.170704@gmail.com

> 💼 LinkedIn: https://www.facebook.com/NguyenVanHung.1707/

> 🚀 Dự án được phát triển bởi nhóm 11 – Trường Đại học Bách khoa Hà Nội


