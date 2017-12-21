package com.ad.weather;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by anastasiia on 21.12.17.
 */

public class WorldWeatherRest {
    static WeatherDataApi weatherDataApi;

    static {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("http://api.worldweatheronline.com/")
                .build();
        weatherDataApi = retrofit.create(WeatherDataApi.class);

    }

    interface WeatherDataApi {
        @GET("premium/v1/search.ashx?num_of_results=10&format=json&key=8397a5e259a84d4e92880143171912")
        Observable<CityItem.AreaResult> search(@Query("query") String query);
        @GET("premium/v1/weather.ashx?key=8397a5e259a84d4e92880143171912&num_of_days=7&tp=1&format=json")
        Observable<WeatherItem.WeatherResult> getWeather(@Query("query")String query );
    }
}