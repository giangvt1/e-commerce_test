# RetailOnboardPro - Spring Boot & SQL Server

Hệ thống phê duyệt cho đào tạo nhân viên bán lẻ sử dụng Spring Boot, Thymeleaf và SQL Server.

## Yêu cầu hệ thống

- Java 17 trở lên
- Maven 3.8+
- SQL Server (bản mới nhất)
- Trình duyệt web hiện đại

## Thiết lập cơ sở dữ liệu

1. Cài đặt SQL Server Express hoặc phiên bản SQL Server khác
2. Tạo cơ sở dữ liệu mới với tên "RetailOnboardPro" 
3. Tạo tài khoản SQL Server với quyền đầy đủ trên cơ sở dữ liệu này (hoặc sử dụng tài khoản hiện có)

## Biên dịch và chạy ứng dụng

1. Clone mã nguồn:
```bash
git clone https://github.com/your-username/RetailOnboardPro.git
cd RetailOnboardPro
```

2. Cấu hình cơ sở dữ liệu trong src/main/resources/application.properties:
```properties
# Cấu hình SQL Server
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=RetailOnboardPro;encrypt=true;trustServerCertificate=true
spring.datasource.username=golden
spring.datasource.password=123
```

3. Biên dịch và chạy ứng dụng:
```bash
mvn clean install
mvn spring-boot:run
```

4. Ứng dụng sẽ tự động mở trình duyệt web và hiển thị tại: http://localhost:5000

## Tài khoản mặc định

Sau khi khởi động, ứng dụng sẽ tự động tạo các tài khoản:

- **CEO**:
  - Username: admin
  - Password: 123

- **Manager**:
  - Username: manager
  - Password: 123

- **Director**:
  - Username: director
  - Password: 123

- **Staff**:
  - Username: staff
  - Password: 123

## Chức năng chính

### Đăng nhập và Đăng ký
- Đăng nhập vào hệ thống với tài khoản hiện có
- Đăng ký tài khoản mới với các vai trò khác nhau

### Quản lý Yêu cầu
- Nhân viên (Staff) có thể tạo yêu cầu đào tạo mới
- Xem danh sách yêu cầu theo quyền hạn
- Xem chi tiết yêu cầu và lịch sử phê duyệt

### Quy trình phê duyệt
- Quản lý (Manager) xem xét và phê duyệt/từ chối yêu cầu
- Giám đốc (Director) xem xét và phê duyệt/từ chối yêu cầu đã được Manager phê duyệt
- CEO xem xét và phê duyệt/từ chối cuối cùng yêu cầu đã được Director phê duyệt

## Công nghệ sử dụng

- Spring Boot 3.x
- Spring Security + Form-based Authentication
- Spring Data JPA
- Thymeleaf (server-side templating)
- Bootstrap 5 (UI framework)
- SQL Server
- Lombok 