package io.github.andrealevy238.eyepressuremonitor;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities = {Measurement.class}, version = 1)
@TypeConverters({Converters.class})
abstract class AppDatabase extends RoomDatabase {
    abstract MeasurementDao measurementDao();

    private static volatile AppDatabase INSTANCE;

    /**
     * @param context the application Context
     * @return an instance of the database based on the RoomDatabase Interface
     */
    static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class,
                            "measurement_database"
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }


}
