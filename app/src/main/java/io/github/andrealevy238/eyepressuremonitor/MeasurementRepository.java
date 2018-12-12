package io.github.andrealevy238.eyepressuremonitor;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class MeasurementRepository {
    private MeasurementDao measurementDao;
    private LiveData<List<Measurement>> allMeasurements;

    /**
     * @param application the application for which the application is for
     */
    MeasurementRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        measurementDao = db.measurementDao();
        allMeasurements = measurementDao.getAll();
    }

    /**
     * @return a list of all the measurements
     */
    LiveData<List<Measurement>> getAllMeasurements() {
        return allMeasurements;
    }

    /**
     * Inserts a measurement object into the database
     * @param measurement the item that is being inserted
     */
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
