package io.github.andrealevy238.eyepressuremonitor;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MeasurementViewModel extends AndroidViewModel {
    private MeasurementRepository mRepository;
    private LiveData<List<Measurement>> measurements;

    /**
     * Creates a MeasuementViewModel for the past 6 months worth of data
     *
     * @param application the application for the database
     */
    public MeasurementViewModel(@NonNull Application application) {
        super(application);
        Date sixMonths = sixMonthsAgo();
        mRepository = new MeasurementRepository(application, sixMonths);
        measurements = mRepository.getAllMeasurements();
    }

    /**
     * Creates a MeasurementViewModel for a custom date
     * @param application the application for the database
     * @param date the start date for which data is being collected after
     */
    public MeasurementViewModel(@NonNull Application application, Date date) {
        super(application);
        mRepository = new MeasurementRepository(application, date);
        measurements = mRepository.getAllMeasurements();
    }

    /**
     * Gets the date that is exactly six months ago
     * @return the Java date that is exactly six months ago
     */
    public Date sixMonthsAgo() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -6);
        return c.getTime();
    }

    /**
     * Gets the list of Measurements right now
     * @return a list of measurements that is the current state of the database
     */
    public List<Measurement> getMeasurements() {
        return measurements.getValue();
    }

    /**
     * @param m a Measurement  to insert into the database
     */
    public void insert(Measurement m) {
        mRepository.insert(m);
    }
}
