package io.github.andrealevy238.eyepressuremonitor;


import org.junit.Test;

import java.util.Date;
import java.util.GregorianCalendar;

import static io.github.andrealevy238.eyepressuremonitor.DataConverter.getNum;
import static io.github.andrealevy238.eyepressuremonitor.DataConverter.toHex;
import static io.github.andrealevy238.eyepressuremonitor.NewMeasurement.getFrequency;
import static junit.framework.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testgetNum1() {
        byte[] b = {0x01, 0x10};
        assertEquals(4097, getNum(b));
    }

    @Test
    public void testGetNum2() {
        byte[] b = {0x01, (byte) 0xFF};
        assertEquals(0xFF01, getNum(b));
    }

    @Test
    public void testToHex() {
        byte[] b = {0x01, (byte) 0xFF};
        assertEquals("0x01FF", toHex(b));
    }

    @Test
    public void testFrequency() {
        int val = 998;
        assertEquals(16.0, getFrequency(val));
    }

    @Test
    public void TestConverter() {
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(2018, 11, 17, 17, 40, 0);
        Long time = gc.getTime().getTime();
        Date date = gc.getTime();
        assertEquals(time, Converters.dateToTimestamp(date));
        assertEquals(0, date.compareTo(Converters.fromTimestamp(time)));
    }
}