package io.github.andrealevy238.eyepressuremonitor;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MeasurementDao {
    @Query("select * from measurement ORDER BY Time")
    LiveData<List<Measurement>> getAll();

    @Insert
    void insert(Measurement measurement);
}
