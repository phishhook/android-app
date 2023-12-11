package com.example.networktest;

import android.app.Application;
import android.content.Intent;
import android.os.Build;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@RunWith(AndroidJUnit4.class)
public class LinkAnalysisActivityTest {

    @Mock
    private RequestQueue mockRequestQueue;

    @Test
    public void testLinkAnalysisAnimation() {
        // Mock the Volley request queue
        RequestQueue mockedQueue = Mockito.mock(RequestQueue.class);
        Mockito.when(mockedQueue.add(Mockito.any(Request.class))).thenReturn(null);

        // Launch the activity with a test URL
        try (ActivityScenario<LinkAnalysisActivity> scenario = ActivityScenario.launch(LinkAnalysisActivity.class)) {
            scenario.onActivity(activity -> {
                // Set the mocked request queue to the activity
                activity.queue = mockedQueue;

                // Set a test URL for link analysis
                activity.linkAnalysis("http://example.com");
            });

            // Perform assertions or verifications as needed
            // For example, check that the animation view is visible
            onView(withId(R.id.animationView)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testWriteResultToDatabase() {

        // Mock the Volley request queue
        RequestQueue mockedQueue = Mockito.mock(RequestQueue.class);
        Mockito.when(mockedQueue.add(Mockito.any(Request.class))).thenReturn(null);
        // Launch the real activity with a test URL
        try (ActivityScenario<LinkAnalysisActivity> scenario = ActivityScenario.launch(LinkAnalysisActivity.class)) {
            scenario.onActivity(activity -> {
                // Set the mocked request queue to the activity
                activity.queue = mockedQueue;

                // Mock the result to test
                String mockResult = "75%"; // Replace with your desired mock result
                activity.writeResultToDatabase(mockResult);

                // Verify that the RequestQueue.add method is called with the correct StringRequest
                verify(mockedQueue).add(Mockito.any());
            });
        }

    }

    @Test
    public void testIsClickedWithinOneWeek() {

        // Mocked clicked_at value for testing
        LocalDateTime localDateTime = LocalDateTime.now();

        // Use InstrumentationRegistry to run on the main thread
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            LinkAnalysisActivity linkAnalysisActivity = new LinkAnalysisActivity();

            // Test case: clicked_at is within one week
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assertTrue(linkAnalysisActivity.isClickedWithinOneWeek(localDateTime));
            }

            // Test case: clicked_at is more than one week ago
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime moreThanOneWeekAgo = localDateTime.minusDays(8);
                assertFalse(linkAnalysisActivity.isClickedWithinOneWeek(moreThanOneWeekAgo));
            }
        });
    }


}
