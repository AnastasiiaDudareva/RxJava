package com.ad.weather.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ad.weather.CityItem;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;


/**
 * Created by anastasiia on 22.12.17.
 */

@Dao
public interface CityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CityItem cityItem);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(CityItem... cityItems);

    @Delete
    int delete(CityItem cityItem);

    @Query("SELECT * FROM CityItem")
    Maybe<List<CityItem>> getAllCities();
}
