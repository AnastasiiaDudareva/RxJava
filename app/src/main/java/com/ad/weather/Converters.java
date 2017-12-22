package com.ad.weather;

import android.arch.persistence.room.TypeConverter;
import android.location.Location;

import com.google.gson.Gson;

public class Converters {
   @TypeConverter
   public static Location fromString(String value) {
      return new Gson().fromJson(value, Location.class);
   }

   @TypeConverter
   public static String fromLocation(Location location) {
      Gson gson = new Gson();
      String json = gson.toJson(location);
      return json;
   }
}