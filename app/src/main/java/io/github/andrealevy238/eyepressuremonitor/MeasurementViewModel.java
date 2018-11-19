package io.github.andrealevy238.eyepressuremonitor;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class MeasurementViewModel extends AndroidViewModel {
    private MeasurementRepository mRepository;
    private LiveData<List<Measurement>> allMeasurements;

    public MeasurementViewModel(@NonNull Application application) {
        super(application);
        mRepository = new MeasurementRepository(application);
        allMeasurements = mRepository.getAllMeasurements();
    }

    public void insert(Measurement m) {
        mRepository.insert(m);
    }

    public LiveData<List<Measurement>> getAllMeasurements() {
        return allMeasurements;
    }
}
