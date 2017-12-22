package com.ad.weather;

import android.arch.persistence.room.Room;
import android.text.format.DateUtils;

import java.util.List;

/**
 * Created by anastasiia on 22.12.17.
 */

public class DBHelper {
    static AppDatabase db = Room.databaseBuilder(Weather.getInstance(),
            AppDatabase.class, "populus-database").build();

    public static void saveWeatherForCity(WeatherItem weatherItem, CityItem cityItem) {
        weatherItem.cityItemId = cityItem.cityItemId;
        db.getCityDao().insert(cityItem);
        db.getWeatherDao().insert(weatherItem);
    }

    public static WeatherItem getWeatherForCity(CityItem cityItem) {
        List<WeatherItem> result = db.getWeatherDao()
                .getWeatherForCity(cityItem.cityItemId,
                        System.currentTimeMillis() - DateUtils.HOUR_IN_MILLIS);
        return result.isEmpty()?null:result.get(0);
    }
}
