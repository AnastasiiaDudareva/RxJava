package com.ad.weather;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by anastasiia on 21.12.17.
 */
@Entity(foreignKeys = @ForeignKey(
        entity = CityItem.class,
        parentColumns = "cityItemId",
        childColumns = "cityItemId"))
public class WeatherItem {
    //    @PrimaryKey(autoGenerate = true)
    @NonNull
    @PrimaryKey
    public String cityItemId=""; // this ID points to a CityItem
    public int weatherId = 0;
    public String lastObservedUtc;
    public int tempt;
    public int feelsLike;
    public String weatherIconUrl;
    public String weatherDesc;
    public long timeCreatedMillis;


    public WeatherItem(String lastObservedUtc, int tempt, int feelsLike, String weatherIconUrl, String weatherDesc) {
        this.lastObservedUtc = lastObservedUtc;
        this.tempt = tempt;
        this.feelsLike = feelsLike;
        this.weatherIconUrl = weatherIconUrl;
        this.weatherDesc = weatherDesc;
        this.timeCreatedMillis = System.currentTimeMillis();
    }

    class WeatherResult {
        CurrentConditionArray data;

        public WeatherResult(CurrentConditionArray data) {
            this.data = data;
        }

        public WeatherItem toWeatherItem() {
            if (data != null && data.current_condition != null
                    && !data.current_condition.isEmpty()) {
                CurrentCondition cc = data.current_condition.get(0);
                return new WeatherItem(cc.observation_time,
                        Integer.parseInt(cc.temp_C),
                        Integer.parseInt(cc.FeelsLikeC),
                        cc.getWeatherIconUrl(),
                        cc.getWeatherDesc());
            }
            return null;
        }

    }

    class CurrentConditionArray {
        List<CurrentCondition> current_condition;

        public CurrentConditionArray(List<CurrentCondition> current_condition) {
            this.current_condition = current_condition;
        }
    }

    class CurrentCondition {
        String observation_time;
        String temp_C;
        String FeelsLikeC;
        List<CityItem.Value> weatherIconUrl;
        List<CityItem.Value> weatherDesc;

        public CurrentCondition(String observation_time, String temp_C, String FeelsLikeC, List<CityItem.Value> weatherIconUrl, List<CityItem.Value> weatherDesc) {
            this.observation_time = observation_time;
            this.temp_C = temp_C;
            this.FeelsLikeC = FeelsLikeC;
            this.weatherIconUrl = weatherIconUrl;
            this.weatherDesc = weatherDesc;
        }

        public String getWeatherIconUrl() {
            if (weatherIconUrl != null && !weatherIconUrl.isEmpty())
                return weatherIconUrl.get(0).value;
            return "";
        }

        public String getWeatherDesc() {
            if (weatherDesc != null && !weatherDesc.isEmpty())
                return weatherDesc.get(0).value;
            return "";
        }
    }
}

