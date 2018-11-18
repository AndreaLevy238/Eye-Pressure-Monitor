package io.github.andrealevy238.eyepressuremonitor;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {Measurement.class}, version = 1)
@TypeConverters({Converters.class})
abstract class AppDatabase extends RoomDatabase {
    abstract MeasurementDao measurementDao();
}
