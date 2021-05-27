package com.gazitf.etapp;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/*
 * @created 14/05/2021 - 2:25 PM
 * @project EtApp
 * @author fnkaya
 */
public class ResourceComparerTest {

    @Test
    public void testStringResourceSameAsGivenString() {
        Context context = ApplicationProvider.getApplicationContext();
        assertEquals("EtApp", context.getString(R.string.app_name));
    }

    @Test
    public void testStringResourceDifferentAsGivenString() {
        Context context = ApplicationProvider.getApplicationContext();
        assertEquals("Et-App", context.getString(R.string.app_name));
    }
}