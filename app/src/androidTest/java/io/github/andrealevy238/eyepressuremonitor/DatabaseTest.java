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
        gc.set(0, 0, 0);
        Date before = gc.getTime();
        Measurement measurement = new Measurement(0.00, 0, date);
        dao.insert(measurement);
        List<Measurement> mList = dao.getAll(before).getValue();
        assert mList != null;
        assertEquals(date, mList.get(0).time);
        assertEquals(0.00, mList.get(0).frequency, 0.001);
        assertEquals(0, mList.get(0).pressure);
    }

}
