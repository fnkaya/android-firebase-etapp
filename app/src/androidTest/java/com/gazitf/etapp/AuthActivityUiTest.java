package com.gazitf.etapp;

import android.os.Handler;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.gazitf.etapp.auth.activity.AuthActivity;

import org.junit.Rule;
import org.junit.Test;

/*
 * @created 24/05/2021 - 5:04 PM
 * @project EtApp
 * @author fnkaya
 */
public class AuthActivityUiTest {

    @Rule
    public ActivityScenarioRule<AuthActivity> getActivityScenarioRule() {
        return new ActivityScenarioRule<>(AuthActivity.class);
    }

    @Test
    public void test_is_activity_in_view() {
        Espresso.onView(ViewMatchers.withId(R.id.frame_layout_auth))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void test_visibility_login_button() {
        Espresso.onView(ViewMatchers.withId(R.id.button_login))
                .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void test_login_button_text() {
        Espresso.onView(ViewMatchers.withId(R.id.button_login))
                .check(ViewAssertions.matches(ViewMatchers.withText(R.string.login)));
    }

    @Test
    public void test_login() throws InterruptedException {
        Espresso.onView(ViewMatchers.withId(R.id.text_input_login_email))
                .perform(ViewActions.typeText("fnkayadev@gmail.com"));
        Espresso.onView(ViewMatchers.withId(R.id.text_input_login_password))
                .perform(ViewActions.typeText("Qwerty123"));
        Espresso.onView(ViewMatchers.withId(R.id.button_login))
                .perform(ViewActions.click());
        Thread.sleep(2000);
        Espresso.onView(ViewMatchers.withId(R.id.layout_content))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
