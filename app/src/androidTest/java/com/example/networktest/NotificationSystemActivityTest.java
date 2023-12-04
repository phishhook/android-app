package com.example.networktest;

import android.app.Activity;
import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.networktest.LinkAnalysisActivity;
import com.example.networktest.NotificationSystemActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;


@RunWith(AndroidJUnit4.class)
public class NotificationSystemActivityTest {

    private final CountingIdlingResource countingIdlingResource = new CountingIdlingResource("LinkAnalysisActivityIdlingResource");


    @Before
    public void setUp() {
        // Initialize Espresso for intents
        Intents.init();
    }

    @After
    public void tearDown() {
        // Release Espresso resources
        Intents.release();
    }

    @Test
    public void testSafeLinkAnalysisAndDisplayResult() throws InterruptedException {
        // Mock the behavior of LinkAnalysisActivity

        // Mock a URL
        String mockUrl = "https://example.com";


        // Launch the NotificationSystemActivity with a test URL
        try (ActivityScenario<NotificationSystemActivity> scenario = ActivityScenario.launch(NotificationSystemActivity.class)) {
            // Set the mocked URL to the activity
            scenario.onActivity(activity -> {
                Intent notificationSystemIntent = new Intent(activity, LinkAnalysisActivity.class);
                notificationSystemIntent.putExtra("url", mockUrl);
                activity.startActivityForResult(notificationSystemIntent, 1);
            });

            Thread.sleep(6000);
            // For example, check that the resultTextView displays the expected message
            onView(withId(R.id.resultTextView)).check(matches(withText("We have determined this link is 99.46% safe.")));
        }
    }

    @Test
    public void testPhishingLinkAnalysisAndDisplayResult() throws InterruptedException {
        // Mock the behavior of LinkAnalysisActivity

        // Mock a URL
        String mockUrl = "http://ww1.yt118.com/?subid1=ac5e4d9f-8d27-11ee-b77b-5693fd51d38f";


        // Launch the NotificationSystemActivity with a test URL
        try (ActivityScenario<NotificationSystemActivity> scenario = ActivityScenario.launch(NotificationSystemActivity.class)) {
            // Set the mocked URL to the activity
            scenario.onActivity(activity -> {
                Intent notificationSystemIntent = new Intent(activity, LinkAnalysisActivity.class);
                notificationSystemIntent.putExtra("url", mockUrl);
                activity.startActivityForResult(notificationSystemIntent, 1);
            });

            Thread.sleep(6000);
            // For example, check that the resultTextView displays the expected message
            onView(withId(R.id.resultTextView)).check(matches(withText("We have determined this link is only 0.00% safe. " +
                    "We recommend not visiting this webpage.")));
        }
    }

    @Test
    public void testIndeterminateLinkAnalysisAndDisplayResult() throws InterruptedException {
        // Mock the behavior of LinkAnalysisActivity

        // Mock a URL
        String mockUrl = "http://4afic.mylp.kz";


        // Launch the NotificationSystemActivity with a test URL
        try (ActivityScenario<NotificationSystemActivity> scenario = ActivityScenario.launch(NotificationSystemActivity.class)) {
            // Set the mocked URL to the activity
            scenario.onActivity(activity -> {
                Intent notificationSystemIntent = new Intent(activity, LinkAnalysisActivity.class);
                notificationSystemIntent.putExtra("url", mockUrl);
                activity.startActivityForResult(notificationSystemIntent, 1);
            });

            Thread.sleep(6000);
            // For example, check that the resultTextView displays the expected message
            onView(withId(R.id.resultTextView)).check(matches(withText("We were not able to determine the safety of this link. Please " +
                    "proceed with caution.")));
        }
    }
}
