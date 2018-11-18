package io.github.andrealevy238.eyepressuremonitor;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;
import java.util.UUID;

@Entity
public class Measurement {
    @ColumnInfo(name = "Frequency")
    public double frequency;
    @ColumnInfo(name = "Pressure")
    public int pressure;
    @ColumnInfo(name = "Time")
    public Date time;
    @PrimaryKey
    @ColumnInfo(name = "mID")
    @NonNull
    String mId;

    Measurement(double frequency, int pressure, Date time) {
        this.mId = UUID.randomUUID().toString();
        this.frequency = frequency;
        this.pressure = pressure;
        this.time = time;
    }
}
