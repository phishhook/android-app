package com.example.networktest;

//import static android.app.PendingIntent.getActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.Assert;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.networktest.LinkAnalysisActivity;
import com.example.networktest.NotificationSystemActivity;
import com.example.networktest.authentication.LoginFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
//import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

//import static androidx.core.util.Predicate.not;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.google.android.material.internal.ContextUtils.getActivity;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.rule.ActivityTestRule;

public class UIUnitTest {

    private View decorView;

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        activityScenarioRule.getScenario().onActivity(activity -> decorView = activity.getWindow().getDecorView());
    }
    @Test
    public void testValidLogin(){
        onView(withId(R.id.editTextPhoneNumber)).perform(typeText("9203191799"), closeSoftKeyboard());
        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withText("Welcome back, Lucas")).inRoot(withDecorView(not(decorView))).check(matches(isDisplayed()));
    }
    public void testInvalidLogin(){
        onView(withId(R.id.editTextPhoneNumber)).perform(typeText("0000000000"));
        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withText("Credentials do not exist, please sign up.")).inRoot(withDecorView(not(decorView))).check(matches(isDisplayed()));
    }
    public void testLinkHistory(){
        onView(withId(R.id.navigation_link_history)).perform(click());
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
    }

    public void testUserProfile(){
        onView(withId(R.id.navigation_user_profile)).perform(click());
        onView(withId(R.id.settingsBtn)).check(matches(isDisplayed()));
    }


}
