package io.github.andrealevy238.eyepressuremonitor;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "measurement")
public class Measurement {
    @ColumnInfo(name = "Frequency")
    public double frequency;
    @ColumnInfo(name = "Pressure")
    public int pressure;

    @ColumnInfo(name = "Time")
    public Date time;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "mID")
    public int mId;

    Measurement(double frequency, int pressure, Date time) {
        this.frequency = frequency;
        this.pressure = pressure;
        this.time = time;
    }
}
