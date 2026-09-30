package lab2.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PasswordHasherTest {

    @Test
    void hash_SameSaltAndPassword_IsDeterministic() {
        String salt = PasswordHasher.generateSalt();
        assertEquals(PasswordHasher.hash(salt, "Secret@123"), PasswordHasher.hash(salt, "Secret@123"));
    }

    @Test
    void hash_DifferentSalt_ProducesDifferentHash() {
        assertNotEquals(PasswordHasher.hash("salt-1", "Secret@123"), PasswordHasher.hash("salt-2", "Secret@123"));
    }

    @Test
    void generateSalt_TwoCalls_AreDifferent() {
        assertNotEquals(PasswordHasher.generateSalt(), PasswordHasher.generateSalt());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Secret@123", "a", "Mật khẩu có dấu"})
    void hash_IsHex64_AndNeverEqualsRawPassword(String raw) {
        String hash = PasswordHasher.hash("salt", raw);
        assertEquals(64, hash.length());
        assertTrue(hash.matches("[0-9a-f]{64}"));
        assertNotEquals(raw, hash);
    }

    @Test
    void matches_CorrectPassword_ReturnsTrue() {
        String hash = PasswordHasher.hash("salt", "Secret@123");
        assertTrue(PasswordHasher.matches("salt", "Secret@123", hash));
    }

    @ParameterizedTest
    @ValueSource(strings = {"secret@123", "SECRET@123", "Secret@1234", "Secret@12"})
    void matches_DifferentOrWrongCase_ReturnsFalse(String attempt) {
        String hash = PasswordHasher.hash("salt", "Secret@123");
        assertFalse(PasswordHasher.matches("salt", attempt, hash));
    }

    @Test
    void matches_NullArguments_ReturnsFalse() {
        assertFalse(PasswordHasher.matches(null, "x", "y"));
        assertFalse(PasswordHasher.matches("s", null, "y"));
        assertFalse(PasswordHasher.matches("s", "x", null));
    }
}
