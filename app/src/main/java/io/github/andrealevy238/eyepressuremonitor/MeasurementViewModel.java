package io.github.andrealevy238.eyepressuremonitor;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class MeasurementViewModel extends AndroidViewModel {
    private MeasurementRepository mRepository;
    private LiveData<List<Measurement>> measurements;

    /**
     * Creates a MeasuementViewModel for the past 6 months worth of data
     * @param application the application for the database
     */
    public MeasurementViewModel(@NonNull Application application) {
        super(application);
        mRepository = new MeasurementRepository(application);
        measurements = mRepository.getAllMeasurements();
    }


    /**
     * Gets the list of Measurements right now
     * @return a list of measurements that is the current state of the database
     */
    public LiveData<List<Measurement>> getMeasurements() {
        return measurements;
    }

    /**
     * @param m a Measurement  to insert into the database
     */
    public void insert(Measurement m) {
        mRepository.insert(m);
    }
}
