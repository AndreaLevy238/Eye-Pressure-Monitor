package io.github.andrealevy238.eyepressuremonitor;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class Converters {
    /**
     * @param value the time in millisecconds
     * @return the Date
     */
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    /**
     * @param date the Java Date
     * @return the time in milliseconds
     */
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
