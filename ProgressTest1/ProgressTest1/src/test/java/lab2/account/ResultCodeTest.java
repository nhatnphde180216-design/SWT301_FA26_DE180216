package lab2.account;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ResultCodeTest {

    @Test
    void isSuccess_Success_ReturnsTrue() {
        assertTrue(ResultCode.SUCCESS.isSuccess());
    }

    @ParameterizedTest(name = "{0} không phải thành công")
    @EnumSource(value = ResultCode.class, names = "SUCCESS", mode = EnumSource.Mode.EXCLUDE)
    void isSuccess_AllOtherCodes_ReturnsFalse(ResultCode code) {
        assertFalse(code.isSuccess());
    }
}
