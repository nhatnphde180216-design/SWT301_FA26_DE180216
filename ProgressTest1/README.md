# SWT301 – ProgressTest 1: Unit Testing với JUnit 5

> **Sinh viên:** Nguyễn Phước Hoài Nhật – MSSV: DE180216  
> **Môn:** SWT301 – Software Testing  
> **Chủ đề:** Unit Testing module *Account Management* bằng JUnit 5 và Parameterized Test  

---

## 1. Hướng dẫn chạy kiểm thử (How to run)

```bash
# Di chuyển vào thư mục project
cd ProgressTest1

# Biên dịch và chạy toàn bộ 215 lượt test + xuất báo cáo JaCoCo
mvn clean test

# Xem báo cáo độ bao phủ mã nguồn (JaCoCo Coverage Report)
# Mở file: target/site/jacoco/index.html bằng trình duyệt
```

---

## 2. Kết quả kiểm thử (Test Results)

- **Tổng số test methods:** 64 methods
- **Tổng số lượt chạy (invocations):** **215 lượt chạy** (215/215 PASS, 0 Failures, 0 Errors, 0 Skipped)
- **Số `@ParameterizedTest`:** 39 tests (vượt xa yêu cầu tối thiểu ≥ 12)
- **Đa dạng nguồn dữ liệu:**
  - `@ValueSource`
  - `@NullAndEmptySource`
  - `@CsvSource`
  - `@MethodSource`
  - `@EnumSource`

### Độ bao phủ JaCoCo (JaCoCo Coverage)
- **Instruction Coverage:** **98%** (933 / 946)
- **Branch Coverage:** **96%** (152 / 158) *(Yêu cầu: ≥ 70%)*
- **Line Coverage:** **98%** (214 / 218) *(Yêu cầu: ≥ 80%)*
- Báo cáo HTML chi tiết sinh tại: `target/site/jacoco/index.html`

---

## 3. Bảng kiểm thử đột biến thủ công (Manual Mutation Testing)

| # | File / Vị trí | Lỗi giả lập (Mutation) | Test phát hiện và Fail | Đã hoàn tác |
|---|---|---|---|:---:|
| **M1** | `AccountService.java` (hàm `login`) | `>= MAX_FAILED_ATTEMPTS` đổi thành `> MAX_FAILED_ATTEMPTS` | `Login.login_WrongPassword5thTime_LocksAccount` | ✅ |
| **M2** | `AccountService.java` (hàm `login`) | Bỏ qua kiểm tra `if (account.isLocked())` | `Login.login_WhileLocked_RejectsWithoutIncrement` | ✅ |
| **M3** | `AccountValidator.java` (regex username) | Đổi `{4,19}` thành `{4,20}` | `Username.isValidUsername_BoundaryLength[21]` | ✅ |
| **M4** | `AccountService.java` (hàm `register`) | Đổi `< MIN_AGE` thành `<= MIN_AGE` | `Register.register_AgeBoundary[18, 0]` | ✅ |
| **M5** | `Account.java` (hàm `unlock`) | Bỏ dòng `failedAttempts = 0;` khi mở khóa | `Login.login_AfterAdminUnlock_CounterRestartsAndCanLogin` | ✅ |

---

## 4. Ma trận truy vết nghiệp vụ (Business Rules Traceability Matrix)

| Business Rule | Nội dung quy tắc | Phương thức Test bảo vệ |
|---|---|---|
| **REG-01** | Bắt buộc nhập: không null, không rỗng, dob ≤ today | `Register.register_UsernameNullEmptyBlank...`, `register_EmailNullEmptyBlank...`, `register_PasswordNullEmptyBlank...`, `invalidRegisterInputs[dob null]`, `register_AgeBoundary[0, 1]` |
| **REG-02** | Username: 5-20 ký tự, bắt đầu chữ cái, chỉ `[A-Za-z0-9_]` | `AccountValidatorTest.Username.*`, `invalidRegisterInputs[username sai]` |
| **REG-03** | Trùng username (không phân biệt hoa/thường) | `Register.register_DuplicateUsernameIgnoreCase_ReturnsDuplicateUsername` |
| **REG-04** | Email: đúng định dạng chuẩn, domain rỗng = sai, dài ≤ 100 | `AccountValidatorTest.Email.*`, `invalidRegisterInputs[email sai]` |
| **REG-05** | Trùng email (không phân biệt hoa/thường) | `Register.register_DuplicateEmailIgnoreCase_ReturnsDuplicateEmail` |
| **REG-06** | Mật khẩu: 8-32 ký tự, đủ 4 nhóm, không chứa username | `AccountValidatorTest.Password.*`, `invalidRegisterInputs[mật khẩu yếu / chứa username]` |
| **REG-07** | Xác nhận mật khẩu khớp | `Register.invalidRegisterInputs[confirm lệch]` |
| **REG-08** | Tuổi ≥ 18 tuổi tính theo ngày hiện tại | `AccountValidatorTest.calculateAge_Boundaries`, `Register.register_AgeBoundary` |
| **REG-09** | Số điện thoại tùy chọn, nếu có phải hợp lệ (10 số, đầu 03/05/07/08/09) | `AccountValidatorTest.Phone.*`, `Register.register_PhoneNullOrEmpty_Success` |
| **REG-10** | Mã hóa SHA-256 + Salt riêng, lưu email lowercase | `Register.register_ValidData_CreatesActiveAccountWithHashedPassword`, `register_UpperCaseEmail_StoredAsLowerCase`, `register_TwoAccountsSamePassword_HaveDifferentSaltAndHash` |
| **REG Priority** | Thứ tự kiểm tra lỗi: REG-01 → 02 → 04 → 06 → 07 → 08 → 09 → 03 → 05 → 10 | 6 test case kết hợp trong `invalidRegisterInputs`, `register_DuplicateUsernameButInvalidEmail_ReturnsInvalidEmailFirst` |
| **LOG-01** | Bắt buộc nhập username và password | `Login.login_UsernameNullEmptyBlank...`, `login_PasswordNullEmptyBlank...` |
| **LOG-02** | Username không phân biệt hoa thường, password phân biệt | `Login.login_UsernameIgnoreCase_Success`, `login_PasswordCaseSensitive_ReturnsInvalidCredentials` |
| **LOG-03** | Sai username hoặc mật khẩu trả cùng INVALID_CREDENTIALS | `Login.login_UnknownUserAndWrongPassword_ReturnSameCode` |
| **LOG-04** | Tài khoản bị vô hiệu hóa (DISABLED) | `Login.login_DisabledAccount_ReturnsAccountDisabled` |
| **LOG-05** | Sai liên tiếp 5 lần thì khóa vĩnh viễn (ACCOUNT_LOCKED) | `Login.login_WrongPasswordLessThan5Times_IncrementsCounter`, `login_WrongPassword5thTime_LocksAccount`, `login_CorrectPasswordAfterNFailures` |
| **LOG-06** | Khi đang khóa, nhập đúng hay sai đều báo ACCOUNT_LOCKED, không tăng bộ đếm | `Login.login_WhileLocked_RejectsWithoutIncrement` |
| **LOG-08** | Đăng nhập thành công reset failedAttempts về 0 | `Login.login_CorrectCredentials_Success`, `login_SuccessAfterFailures_ResetsCounter` |
| **ADM-01/02** | Khóa tài khoản (disable), tìm kiếm theo username | `Admin.disableAccount_*`, `Admin.findByUsername_BlankOrUnknown_ReturnsEmpty` |
| **ADM-03** | Admin mở khóa tài khoản (unlock), reset bộ đếm về 0 | `Login.login_AfterAdminUnlock_CounterRestartsAndCanLogin`, `Admin.unlockAccount_BlankOrUnknown_ReturnsUserNotFound` |
| **CHG-01..08** | Đổi mật khẩu (kiểm tra mật khẩu cũ, không trùng mật khẩu trong lịch sử 3 lần gần nhất) *(Bonus)* | `ChangePassword.*` |
| **RST-01..06** | Quên / đặt lại mật khẩu bằng Token dùng một lần, mở khóa khi reset thành công *(Bonus)* | `ResetPassword.*` |

---

## 5. Checklist tự đánh giá (100% Đạt)

### A. Mã Production
- [x] **A1** `mvn clean compile` thành công
- [x] **A2** `AccountValidator` đủ 5 hàm, null trả `false`, không ném exception
- [x] **A3** Mật khẩu băm SHA-256 + salt riêng, không lưu bản rõ
- [x] **A4** `register()` đủ BR-REG-01..10, đúng thứ tự ưu tiên
- [x] **A5** `login()`: sai 5 lần thì khóa; đang khóa không tăng bộ đếm; thành công đặt bộ đếm về 0
- [x] **A6** `unlockAccount()` mở khóa và đặt `failedAttempts = 0`
- [x] **A7** Username/email không phân biệt hoa thường (`toLowerCase(Locale.ROOT)`), mật khẩu phân biệt
- [x] **A8** Không dùng Clock; không System.out, không biến static giữ trạng thái

### B. Mã Test
- [x] **B1** 64 phương thức test (≥ 20), 39 `@ParameterizedTest` (≥ 12), 215 lượt chạy (≥ 60)
- [x] **B2** Dùng đủ 5 nguồn: `@ValueSource`, `@NullAndEmptySource`, `@CsvSource`, `@MethodSource`, `@EnumSource`
- [x] **B3** Biên đầy đủ: username 4/5/20/21, mật khẩu 7/8/32/33, email 99/100/101, tuổi 17/18
- [x] **B4** Biên số lần đăng nhập sai 3/4/5/6 và test mở khóa
- [x] **B5** Đầy đủ các test thứ tự ưu tiên trong `register()`
- [x] **B6** `@Nested` + `@BeforeEach` tạo mới service cho mỗi test độc lập
- [x] **B7** Assert chi tiết trạng thái đối tượng, AAA pattern chuẩn mực
- [x] **B8** Tên test theo quy ước `method_TinhHuong_KetQua` rõ ràng

### C. Chất lượng & Nộp bài
- [x] **C1** `mvn clean test`: 215 pass, 0 failures, 0 errors, 0 skipped
- [x] **C2** JaCoCo Line: 98% (≥ 80%), Branch: 96% (≥ 70%)
- [x] **C3** 5 lỗi giả lập mutation đã thử nghiệm và ghi lại chi tiết
- [x] **C4** Git commit theo chuẩn Conventional Commits