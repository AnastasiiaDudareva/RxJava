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
public interface CityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CityItem cityItem);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(CityItem... cityItems);

    @Delete
    void delete(CityItem cityItem);

    @Query("SELECT * FROM CityItem")
    List<CityItem> getAllPeople();

//    @Query("SELECT * FROM CityItem WHERE timeCreatedMillis > :timeInMills")
//    List<CityItem> getAllPeopleWithFavoriteColor(long timeInMills);

}
