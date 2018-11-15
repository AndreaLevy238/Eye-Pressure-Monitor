package io.github.andrealevy238.eyepressuremonitor;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("io.github.andrealevy238.eyepressuremonitor", appContext.getPackageName());
    }

    @Test
    public void testgetNum1() {
        byte[] b = {0x01, 0x10};
        assertEquals(4097, NewMeasurement.getNum(b));
    }

    @Test
    public void testGetNum2() {
        byte[] b = {0x01, (byte) 0xFF};
        assertEquals(0xFF01, NewMeasurement.getNum(b));
    }
}
