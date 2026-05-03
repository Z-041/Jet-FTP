package com.ftp.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PasswordValidator 测试")
class PasswordValidatorTest {

    private PasswordValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PasswordValidator();
    }

    @Nested
    @DisplayName("基本验证测试")
    class BasicValidationTests {

        @Test
        @DisplayName("空密码应返回无效")
        void nullPasswordShouldBeInvalid() {
            assertFalse(validator.isValid(null));
        }

        @Test
        @DisplayName("空字符串应返回无效")
        void emptyPasswordShouldBeInvalid() {
            assertFalse(validator.isValid(""));
        }

        @Test
        @DisplayName("最小长度8位应通过")
        void eightCharPasswordShouldBeValid() {
            assertTrue(validator.isValid("Abcd1234"));
        }

        @Test
        @DisplayName("短于最小长度应返回无效")
        void tooShortPasswordShouldBeInvalid() {
            assertFalse(validator.isValid("Ab1"));
        }
    }

    @Nested
    @DisplayName("字符类型验证测试")
    class CharacterTypeTests {

        @Test
        @DisplayName("无大写字母应返回无效")
        void passwordWithoutUppercaseShouldBeInvalid() {
            assertFalse(validator.isValid("abcd1234"));
        }

        @Test
        @DisplayName("无小写字母应返回无效")
        void passwordWithoutLowercaseShouldBeInvalid() {
            assertFalse(validator.isValid("ABCD1234"));
        }

        @Test
        @DisplayName("无数字应返回无效")
        void passwordWithoutDigitShouldBeInvalid() {
            assertFalse(validator.isValid("Abcdabcd"));
        }

        @Test
        @DisplayName("包含所有必需字符应通过")
        void passwordWithAllRequiredCharsShouldBeValid() {
            assertTrue(validator.isValid("Abcd1234"));
        }
    }

    @Nested
    @DisplayName("密码强度评估测试")
    class StrengthEstimationTests {

        @Test
        @DisplayName("弱密码评估")
        void weakPasswordShouldReturnWeak() {
            assertEquals(PasswordValidator.PasswordStrength.WEAK, validator.estimateStrength("abc"));
        }

        @Test
        @DisplayName("中等强度密码评估")
        void mediumPasswordShouldReturnMedium() {
            assertEquals(PasswordValidator.PasswordStrength.MEDIUM, validator.estimateStrength("Abc123"));
        }

        @Test
        @DisplayName("强密码评估")
        void strongPasswordShouldReturnStrong() {
            assertEquals(PasswordValidator.PasswordStrength.STRONG, validator.estimateStrength("Abcd1234!"));
        }

        @Test
        @DisplayName("很长且复杂的密码应返回非常强")
        void veryLongComplexPasswordShouldReturnVeryStrong() {
            assertEquals(PasswordValidator.PasswordStrength.VERY_STRONG,
                validator.estimateStrength("Abcd1234!Abcd1234!Abcd1234!"));
        }
    }

    @Nested
    @DisplayName("详细验证结果测试")
    class ValidationResultTests {

        @Test
        @DisplayName("无效密码应返回具体错误信息")
        void invalidPasswordShouldReturnErrorMessage() {
            PasswordValidator.ValidationResult result = validator.validate("abc");
            assertFalse(result.isValid());
            assertNotNull(result.errorMessage());
            assertTrue(result.errorMessage().contains("长度"));
        }

        @Test
        @DisplayName("有效密码应返回成功结果")
        void validPasswordShouldReturnSuccess() {
            PasswordValidator.ValidationResult result = validator.validate("Abcd1234");
            assertTrue(result.isValid());
            assertNull(result.errorMessage());
        }
    }

    @Nested
    @DisplayName("自定义配置测试")
    class CustomConfigTests {

        @Test
        @DisplayName("自定义最小长度配置")
        void customMinLengthConfig() {
            PasswordValidator customValidator = new PasswordValidator(
                12, true, true, true, false, "!@#$%^&*()"
            );
            assertFalse(customValidator.isValid("Abcd1234"));
            assertTrue(customValidator.isValid("Abcd12345678"));
        }

        @Test
        @DisplayName("禁用特殊字符要求")
        void specialCharNotRequired() {
            PasswordValidator customValidator = new PasswordValidator(
                8, true, true, true, false, "!@#$%^&*()"
            );
            assertTrue(customValidator.isValid("Abcd1234"));
        }
    }

    @Nested
    @DisplayName("强度反馈测试")
    class StrengthFeedbackTests {

        @Test
        @DisplayName("强密码应返回正面反馈")
        void strongPasswordShouldReturnPositiveFeedback() {
            String feedback = validator.getStrengthFeedback("Abcd1234!");
            assertTrue(feedback.contains("强"));
        }

        @Test
        @DisplayName("弱密码应返回具体不足信息")
        void weakPasswordShouldReturnSpecificFeedback() {
            String feedback = validator.getStrengthFeedback("abc");
            assertTrue(feedback.contains("不足"));
        }
    }
}
