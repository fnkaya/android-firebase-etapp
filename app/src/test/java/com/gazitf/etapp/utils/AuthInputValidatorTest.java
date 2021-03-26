package com.gazitf.etapp.utils;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;


/*
 * @created 21/03/2021 - 5:42 PM
 * @project EtApp
 * @author fnkaya
 */
public class AuthInputValidatorTest {

    /*
     * Empty name returns false
     */
    @Test
    public void testValidateEmptyName() {
        boolean result = AuthInputValidator.validateName("");
        assertThat(result).isFalse();
    }

    /*
     * Null name returns false
     */
    @Test
    public void testValidateNullName() {
        boolean result = AuthInputValidator.validateName(null);
        assertThat(result).isFalse();
    }
}