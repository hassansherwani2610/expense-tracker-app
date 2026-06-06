package com.eta.authservice.util;

import com.eta.authservice.model.UserInfoDto;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class ValidationUtil {

    private static final List<String> BLACKLISTED_USERNAMES = Arrays.asList(
            "admin", "user", "system", "root", "guest", "superuser"
    ); // Reserved usernames

    private static final int USERNAME_MIN_LENGTH = 3;
    private static final int USERNAME_MAX_LENGTH = 30;
    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 128;

    private ValidationUtil() {} // Prevent instantiation

    public static void validateUserAttributes(UserInfoDto userInfoDto) throws UserValidationException {
        if (userInfoDto == null) {
            throw new UserValidationException("User info cannot be null");
        }

        String username = safeTrim(userInfoDto.getUsername());
        String password = safeTrim(userInfoDto.getPassword());

        validateUsername(username);
        validatePassword(password, username);
    }

    // Username validation logic (NO EMAIL ALLOWED)
    private static void validateUsername(String username) {

        if (username == null || username.isEmpty()) {
            throw new UserValidationException("Username cannot be empty");
        }

        if (username.length() < USERNAME_MIN_LENGTH || username.length() > USERNAME_MAX_LENGTH) {
            throw new UserValidationException(
                    String.format("Username must be between %d and %d characters",
                            USERNAME_MIN_LENGTH, USERNAME_MAX_LENGTH)
            );
        }

        // Allow letters, numbers, underscore, dot, hyphen
        if (!username.matches("^[A-Za-z0-9._-]+$")) {
            throw new UserValidationException(
                    "Username can only contain letters, numbers, underscores (_), dots (.), and hyphens (-)"
            );
        }

        // Prevent reserved usernames
        if (BLACKLISTED_USERNAMES.contains(username.toLowerCase(Locale.ROOT))) {
            throw new UserValidationException("This username is not allowed");
        }
    }

    // Password validation logic
    private static void validatePassword(String password, String username) {

        if (password == null || password.isEmpty()) {
            throw new UserValidationException("Password cannot be empty");
        }

        if (password.length() < PASSWORD_MIN_LENGTH) {
            throw new UserValidationException(
                    String.format("Password must be at least %d characters long", PASSWORD_MIN_LENGTH)
            );
        }

        if (password.length() > PASSWORD_MAX_LENGTH) {
            throw new UserValidationException(
                    String.format("Password cannot exceed %d characters", PASSWORD_MAX_LENGTH)
            );
        }

        // Require uppercase, lowercase, number, and special character
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\\p{Punct}]).+$")) {
            throw new UserValidationException(
                    "Password must include uppercase, lowercase, number, and special character"
            );
        }

        // Prevent password containing username
        if (username != null &&
                password.toLowerCase(Locale.ROOT).contains(username.toLowerCase(Locale.ROOT))) {

            throw new UserValidationException("Password cannot contain username");
        }
    }

    // Safe trim helper
    private static String safeTrim(String value) {
        return value == null ? null : value.trim();
    }

    // Custom validation exception
    public static class UserValidationException extends RuntimeException {
        public UserValidationException(String message) {
            super(message);
        }
    }
}