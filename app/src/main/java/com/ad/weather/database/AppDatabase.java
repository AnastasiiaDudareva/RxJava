package com.ad.weather.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.ad.weather.CityItem;
import com.ad.weather.Converters;
import com.ad.weather.WeatherItem;

@Database(entities = {WeatherItem.class, CityItem.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract WeatherDao getWeatherDao();

    public abstract CityDao getCityDao();
}