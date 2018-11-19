package io.github.andrealevy238.eyepressuremonitor;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.Date;
import java.util.List;

public class MeasurementRepository {
    private MeasurementDao measurementDao;
    private LiveData<List<Measurement>> allMeasurements;
    private LiveData<List<Integer>> allPressures;
    private LiveData<List<Double>> allFrequencies;

    MeasurementRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        measurementDao = db.measurementDao();
        allMeasurements = measurementDao.getAll();
    }

    LiveData<List<Measurement>> getAllMeasurements() {
        return allMeasurements;
    }

    public LiveData<List<Integer>> getAllPressures() {
        return allPressures;
    }

    public void setAllPressuresAfterDate(Date date) {
        this.allPressures = measurementDao.getPressureAfterDate(date);
    }

    public void setAllFrequenciesAfterDate(Date date) {
        this.allFrequencies = measurementDao.getFrequencyAfterDate(date);
    }

    public LiveData<List<Double>> getAllFrequencies() {
        return allFrequencies;
    }

    public void insert(Measurement measurement) {
        new insertAsyncTask(measurementDao).execute(measurement);
    }

    private static class insertAsyncTask extends AsyncTask<Measurement, Void, Void> {
        private MeasurementDao mAsyncTaskDao;

        insertAsyncTask(MeasurementDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Measurement... measurements) {
            mAsyncTaskDao.insert(measurements[0]);
            return null;
        }
    }
}
