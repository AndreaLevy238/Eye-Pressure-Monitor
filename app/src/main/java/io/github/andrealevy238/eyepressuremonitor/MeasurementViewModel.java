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

    public MeasurementViewModel(@NonNull Application application) {
        super(application);
        Date sixMonths = sixMonthsAgo();
        mRepository = new MeasurementRepository(application, sixMonths);
        measurements = mRepository.getAllMeasurements();
    }

    public MeasurementViewModel(@NonNull Application application, Date date) {
        super(application);
        mRepository = new MeasurementRepository(application, date);
        measurements = mRepository.getAllMeasurements();
    }

    private Date sixMonthsAgo() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -6);
        return c.getTime();
    }

    public LiveData<List<Measurement>> getMeasurements() {
        return measurements;
    }

    public void insert(Measurement m) {
        mRepository.insert(m);
    }
}
