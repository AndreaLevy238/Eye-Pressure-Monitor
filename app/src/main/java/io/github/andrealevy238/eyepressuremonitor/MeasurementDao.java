package io.github.andrealevy238.eyepressuremonitor;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface MeasurementDao {
    @Query("select * from measurement")
    List<Measurement> getAll();

    @Query("select Frequency from measurement where Time > :date")
    List<Double> getFrequencyAfterDate(Date date);

    @Query("select Pressure from measurement where Time > :date")
    List<Integer> getPressureAfterDate(Date date);

    @Insert
    void insertAll(Measurement... measurements);

    @Delete
    void delete(Measurement measurement);
}
