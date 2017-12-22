package com.ad.weather;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;


/**
 * Created by anastasiia on 22.12.17.
 */

@Dao
public interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WeatherItem weatherItem);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(WeatherItem... weatherItems);

    @Delete
    void delete(WeatherItem weatherItem);

    @Query("SELECT * FROM WeatherItem")
    List<WeatherItem> getAllWeather();

    @Query("SELECT * FROM WeatherItem WHERE cityItemId IS :cityItemId AND timeCreatedMillis > :timeInMills")
    List<WeatherItem> getWeatherForCity(String cityItemId, long timeInMills);

}
