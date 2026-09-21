package traltb.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {

    private AccountService service;

    @BeforeEach
    void setUp() {
        // Arrange chung cho từng test case
        service = new AccountService();
    }

    // ---------- isValidEmail ----------
    @ParameterizedTest(name = "Email hợp lệ: {0}")
    @ValueSource(strings = {
        "john@example.com",
        "alice.b@mail.co.uk",
        "carol_99@domain.io"
    })
    @DisplayName("isValidEmail trả về true với email đúng định dạng")
    void isValidEmail_ValidEmails_ReturnsTrue(String email) {
        // Arrange (email được inject bởi @ValueSource)
        // Act
        boolean result = service.isValidEmail(email);

        // Assert
        assertTrue(result, () -> email + " phải là email hợp lệ");
    }

    @ParameterizedTest(name = "Email không hợp lệ: \"{0}\"")
    @CsvSource(value = {
        "bobmail.com",       // thiếu @
        "missing@dot",       // thiếu .domain
        "'@nodomain.com'",   // thiếu local part
        "' '",               // chỉ khoảng trắng
        "NULL"               // sẽ map về null
    }, nullValues = "NULL")
    @DisplayName("isValidEmail trả về false với email sai định dạng / null")
    void isValidEmail_InvalidEmails_ReturnsFalse(String email) {
        // Arrange (email được inject bởi @CsvSource)
        // Act
        boolean result = service.isValidEmail(email);

        // Assert
        assertFalse(result, () -> (email == null ? "null" : email) + " phải không hợp lệ");
    }

    // ---------- registerAccount (CSV File Source) ----------
    @ParameterizedTest(name = "Row {index}: ({0},{1},{2}) → {3}")
    @CsvFileSource(resources = "/test-data.csv", numLinesToSkip = 1)
    @DisplayName("registerAccount với dữ liệu từ test-data.csv")
    void registerAccount_FromCsv(String username, String password,
                                 String email, boolean expected) {
        // Arrange (các tham số được inject từ file test-data.csv)
        // Act
        boolean actual = service.registerAccount(username, password, email);

        // Assert
        assertEquals(expected, actual,
            () -> String.format("(%s, %s, %s) phải trả về %s",
                username, password, email, expected));
    }

    // ---------- Các edge case bổ sung ----------
    @Test
    @DisplayName("registerAccount: password = 6 ký tự (biên dưới) → false")
    void registerAccount_PasswordExactly6_ReturnsFalse() {
        // Arrange
        String username = "bob";
        String password = "abcdef"; // đúng 6 ký tự
        String email = "bob@mail.com";

        // Act
        boolean actual = service.registerAccount(username, password, email);

        // Assert
        assertFalse(actual, "password phải > 6 ký tự");
    }

    @Test
    @DisplayName("registerAccount: password = 7 ký tự (biên trên) → true")
    void registerAccount_PasswordExactly7_ReturnsTrue() {
        // Arrange
        String username = "bob";
        String password = "abcdefg"; // đúng 7 ký tự
        String email = "bob@mail.com";

        // Act
        boolean actual = service.registerAccount(username, password, email);

        // Assert
        assertTrue(actual, "password > 6 ký tự thì hợp lệ");
    }

    @Test
    @DisplayName("registerAccount: tất cả tham số null → false")
    void registerAccount_AllNull_ReturnsFalse() {
        // Arrange
        String username = null;
        String password = null;
        String email = null;

        // Act
        boolean actual = service.registerAccount(username, password, email);

        // Assert
        assertFalse(actual, "Tất cả tham số null phải trả về false");
    }

    @Test
    @DisplayName("registerAccount: username chỉ chứa khoảng trắng → false")
    void registerAccount_WhitespaceUsername_ReturnsFalse() {
        // Arrange
        String username = "   ";
        String password = "password123";
        String email = "valid@example.com";

        // Act
        boolean actual = service.registerAccount(username, password, email);

        // Assert
        assertFalse(actual, "Username chỉ chứa khoảng trắng phải trả về false");
    }

    @Test
    @DisplayName("registerAccount: password null → false")
    void registerAccount_NullPassword_ReturnsFalse() {
        // Arrange
        String username = "validUser";
        String password = null;
        String email = "valid@example.com";

        // Act
        boolean actual = service.registerAccount(username, password, email);

        // Assert
        assertFalse(actual, "Password null phải trả về false");
    }

    @Test
    @DisplayName("registerAccount: email null → false")
    void registerAccount_NullEmail_ReturnsFalse() {
        // Arrange
        String username = "validUser";
        String password = "password123";
        String email = null;

        // Act
        boolean actual = service.registerAccount(username, password, email);

        // Assert
        assertFalse(actual, "Email null phải trả về false");
    }
}
