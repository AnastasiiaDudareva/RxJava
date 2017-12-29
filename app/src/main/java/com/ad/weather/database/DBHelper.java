package com.ad.weather.database;

import android.arch.persistence.room.Room;
import android.text.format.DateUtils;

import com.ad.weather.CityItem;
import com.ad.weather.Weather;
import com.ad.weather.WeatherItem;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by anastasiia on 22.12.17.
 */

public class DBHelper {
    private static DBHelper instance = new DBHelper();

    public static DBHelper getInstance() {
        return instance;
    }

    static AppDatabase db = Room.databaseBuilder(Weather.getInstance(),
            AppDatabase.class, "populus-database").build();

    public Completable saveWeatherForCity(WeatherItem weatherItem, CityItem cityItem) {
        return Completable.create(e -> {
            try {
                weatherItem.cityItemId = cityItem.cityItemId;
                db.getCityDao().insert(cityItem);
                db.getWeatherDao().insert(weatherItem);
                e.onComplete();
            } catch (Exception ex) {
                e.onError(ex);
            }
        });
    }

    public Single<WeatherItem> getWeatherForCity(CityItem cityItem) {
        return db.getWeatherDao()
                .getWeatherForCity(cityItem.cityItemId,
                        System.currentTimeMillis() - DateUtils.HOUR_IN_MILLIS);
    }
}
