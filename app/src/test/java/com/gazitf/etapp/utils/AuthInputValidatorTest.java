package com.gazitf.etapp.utils;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;


/*
 * @created 21/03/2021 - 5:42 PM
 * @project EtApp
 * @author fnkaya
 */
public class AuthInputValidatorTest {

    @Test
    public void testValidateEmptyName() {
        boolean result = AuthInputValidator.validateName("");
        assertThat(result).isFalse();
    }

    @Test
    public void testValidPassword() {
        /*
            (?=.*[0-9]) a digit must occur at least once
            (?=.*[a-z]) a lower case letter must occur at least once
            (?=.*[A-Z]) an upper case letter must occur at least once
            (?=.*[@#$%^&+=]) a special character must occur at least once
            (?=\\S+$) no whitespace allowed in the entire string
            .{8,} at least 8 characters
         */
        // String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}";

        String password = "Qwerty123";
        boolean result = AuthInputValidator.validatePassword(password);
        assertThat(result).isTrue();
    }

    @Test
    public void testInvalidPassword() {
        String password = "123456";
        boolean result = AuthInputValidator.validatePassword(password);
        assertThat(result).isFalse();
    }
}