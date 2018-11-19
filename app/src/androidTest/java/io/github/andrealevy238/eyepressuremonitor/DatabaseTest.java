package io.github.andrealevy238.eyepressuremonitor;

import android.arch.persistence.room.Room;
import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;

import static org.junit.Assert.assertEquals;

public class DatabaseTest {
    private MeasurementDao dao;
    private AppDatabase mDb;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        mDb = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        dao = mDb.measurementDao();
    }

    @After
    public void closeDb() {
        mDb.close();
    }

    @Test
    public void readWrite1() {
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(1900, 1, 1, 10, 10, 10);
        Date date = gc.getTime();
        Measurement measurement = new Measurement(0.00, 0, date);
        dao.insert(measurement);
        List<Measurement> mList = dao.getAll().getValue();
        assert mList != null;
        assertEquals(date, mList.get(0).time);
        assertEquals(0.00, mList.get(0).frequency, 0.001);
        assertEquals(0, mList.get(0).pressure);
    }

    @Test
    public void testPressure() {
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(1952, 1, 1, 10, 10, 10);
        Date date = gc.getTime();
        gc.set(1950, 1, 1);
        Date dateAfter = gc.getTime();
        Measurement measurement = new Measurement(0.00, 20, date);
        dao.insert(measurement);
        List<Integer> mList = dao.getPressureAfterDate(dateAfter).getValue();
        assert mList != null;
        Integer first = mList.get(0);
        Integer twenty = 20;
        assertEquals(twenty, first);
    }

    @Test
    public void testFrequency() {
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(2002, 1, 1, 10, 10, 10);
        Date date = gc.getTime();
        gc.set(2000, 1, 1);
        Date dateAfter = gc.getTime();
        Measurement measurement = new Measurement(3.14, 20, date);
        dao.insert(measurement);
        List<Double> mList = dao.getFrequencyAfterDate(dateAfter).getValue();
        assert mList != null;
        Double first = mList.get(0);
        assertEquals(3.14, first, 0.001);
    }

}
