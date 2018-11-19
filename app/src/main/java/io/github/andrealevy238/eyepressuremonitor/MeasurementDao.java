package io.github.andrealevy238.eyepressuremonitor;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface MeasurementDao {
    @Query("select * from measurement")
    LiveData<List<Measurement>> getAll();

    @Query("select Frequency from measurement where Time > :date")
    LiveData<List<Double>> getFrequencyAfterDate(Date date);

    @Query("select Pressure from measurement where Time > :date")
    LiveData<List<Integer>> getPressureAfterDate(Date date);

    @Insert
    void insert(Measurement measurement);

    @Query("DELETE from measurement")
    void deleteAll();
}
