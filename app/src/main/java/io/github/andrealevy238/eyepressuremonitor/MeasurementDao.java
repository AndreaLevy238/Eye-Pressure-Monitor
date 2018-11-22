package io.github.andrealevy238.eyepressuremonitor;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface MeasurementDao {
    @Query("select * from measurement where Time >:date")
    LiveData<List<Measurement>> getAll(Date date);

    @Insert
    void insert(Measurement measurement);
}
