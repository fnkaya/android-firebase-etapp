package com.gazitf.etapp.utils;

public abstract class AuthInputValidator {

    public static boolean validateName(String name) {
        if (name == null)
            return false;

        String[] words = name.split(" ");
        if (words.length < 2)
            return false;

        for (String word : words) {
            if (word.length() < 3)
                return false;
        }

        return true;
    }

    public static boolean validateEmail(String email) {
        if (email == null)
            return false;

        return email.contains("@") && email.contains(".com");
    }

    public static boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null)
            return false;

        return phoneNumber.length() == 10;
    }

    public static boolean validatePassword(String password) {
        if (password == null)
            return false;

        /*
            (?=.*[0-9]) a digit must occur at least once
            (?=.*[a-z]) a lower case letter must occur at least once
            (?=.*[A-Z]) an upper case letter must occur at least once
            (?=.*[@#$%^&+=]) a special character must occur at least once
            (?=\\S+$) no whitespace allowed in the entire string
            .{8,} at least 8 characters
         */
        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}";

        return password.matches(pattern);
    }

    public static boolean validateVerificationCode(String verificationCode) {
        return verificationCode.length() == 6;
    }

    public static boolean checkPasswordsAreDifferent(String currentPassword, String newPassword) {
        return !currentPassword.equals(newPassword);
    }
}
