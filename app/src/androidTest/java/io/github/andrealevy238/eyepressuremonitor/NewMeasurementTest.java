package io.github.andrealevy238.eyepressuremonitor;


import android.os.Bundle;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
@LargeTest
public class NewMeasurementTest {
    NewMeasurement newMeasurement;
    @Rule
    public ActivityTestRule<NewMeasurement> newMeasurementActivityTestRule = new ActivityTestRule<>(NewMeasurement.class);

    @Before
    public void setup() {
        newMeasurement = new NewMeasurement();
        newMeasurement.onCreate(new Bundle());
    }

    @Test
    public void testClick() {
        onView(withId(R.id.time)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.pressure)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.frequency)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.start)).perform(click());
        onView(withId(R.id.time)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.pressure)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.frequency)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testDisplayPressure() {

        newMeasurement.displayPressure(1000);
        String oneThousand = String.valueOf(1000);
        onView(withId(R.id.pressure)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.pressure)).check(matches(withText(oneThousand)));
    }

    @Test
    public void testDisplayFrequency() {
        newMeasurement.displayFreq(998);
        String sixteen = "16.00";
        onView(withId(R.id.frequency)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.frequency)).check(matches(withText(sixteen)));
    }

    @Test
    public void testDisplayTime() {
        NewMeasurement newMeasurement = new NewMeasurement();
        newMeasurement.displayTime();
        String s = newMeasurement.getDateString();
        onView(withId(R.id.time)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.time)).check(matches(withText(s)));
    }
}
