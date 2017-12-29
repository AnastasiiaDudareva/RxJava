package com.ad.weather.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ad.weather.WeatherItem;

import java.util.List;
import java.util.Observable;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;


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
    int delete(WeatherItem weatherItem);

    @Query("SELECT * FROM WeatherItem")
    Maybe<List<WeatherItem>> getAllWeather();

    @Query("SELECT * FROM WeatherItem WHERE cityItemId IS :cityItemId AND timeCreatedMillis > :timeInMills LIMIT 1")
    Single<WeatherItem> getWeatherForCity(String cityItemId, long timeInMills);

}
