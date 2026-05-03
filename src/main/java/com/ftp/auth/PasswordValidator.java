package com.ftp.auth;

public class PasswordValidator {
    private final int minLength;
    private final boolean requireUppercase;
    private final boolean requireLowercase;
    private final boolean requireDigit;
    private final boolean requireSpecialChar;
    private final String specialChars;

    public PasswordValidator() {
        this(8, true, true, true, false, "!@#$%^&*()_+-=[]{}|;:,.<>?");
    }

    public PasswordValidator(int minLength, boolean requireUppercase, boolean requireLowercase,
                           boolean requireDigit, boolean requireSpecialChar, String specialChars) {
        this.minLength = minLength;
        this.requireUppercase = requireUppercase;
        this.requireLowercase = requireLowercase;
        this.requireDigit = requireDigit;
        this.requireSpecialChar = requireSpecialChar;
        this.specialChars = specialChars != null ? specialChars : "";
    }

    public boolean isValid(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return validate(password).isValid();
    }

    public ValidationResult validate(String password) {
        if (password == null) {
            return new ValidationResult(false, "密码不能为空");
        }

        if (password.length() < minLength) {
            return new ValidationResult(false, String.format("密码长度至少需要%d个字符", minLength));
        }

        if (requireUppercase && !containsUppercase(password)) {
            return new ValidationResult(false, "密码必须包含至少一个大写字母");
        }

        if (requireLowercase && !containsLowercase(password)) {
            return new ValidationResult(false, "密码必须包含至少一个小写字母");
        }

        if (requireDigit && !containsDigit(password)) {
            return new ValidationResult(false, "密码必须包含至少一个数字");
        }

        if (requireSpecialChar && !containsSpecialChar(password)) {
            return new ValidationResult(false, "密码必须包含至少一个特殊字符");
        }

        return ValidationResult.success();
    }

    public String getStrengthFeedback(String password) {
        ValidationResult result = validate(password);
        if (result.isValid()) {
            return "密码强度: 强";
        }
        return "密码强度不足: " + result.errorMessage();
    }

    public PasswordStrength estimateStrength(String password) {
        if (password == null || password.isEmpty()) {
            return PasswordStrength.WEAK;
        }

        int score = 0;

        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        if (password.length() >= 16) score++;

        if (containsUppercase(password)) score++;
        if (containsLowercase(password)) score++;
        if (containsDigit(password)) score++;
        if (containsSpecialChar(password)) score++;

        if (score <= 2) return PasswordStrength.WEAK;
        if (score <= 4) return PasswordStrength.MEDIUM;
        if (score <= 6) return PasswordStrength.STRONG;
        return PasswordStrength.VERY_STRONG;
    }

    private boolean containsUppercase(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) return true;
        }
        return false;
    }

    private boolean containsLowercase(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) return true;
        }
        return false;
    }

    private boolean containsDigit(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) return true;
        }
        return false;
    }

    private boolean containsSpecialChar(String password) {
        for (char c : password.toCharArray()) {
            if (specialChars.indexOf(c) >= 0) return true;
        }
        return false;
    }

    public enum PasswordStrength {
        WEAK,
        MEDIUM,
        STRONG,
        VERY_STRONG
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult failure(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() {
            return valid;
        }

        public String errorMessage() {
            return errorMessage;
        }
    }
}
