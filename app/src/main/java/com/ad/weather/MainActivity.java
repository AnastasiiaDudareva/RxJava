package com.ad.weather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CITY_CODE = 1000;

    private ProgressBar progressBar;
    private TextView lastObserved;
    private TextView cityName;
    private ImageView weatherIcn;
    private TextView temperature;
    private TextView feelsLike;
    private TextView description;

    private CityItem currentCity;
    private WeatherItem currentWeather;
    private DisplayImageOptions.Builder mOptionsBuilder = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .displayer(new RoundedBitmapDisplayer(15));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initImageLoader();
        progressBar = findViewById(R.id.progress);
        lastObserved = findViewById(R.id.last_observed);
        cityName = findViewById(R.id.city_name);
        weatherIcn = findViewById(R.id.weather_icn);
        temperature = findViewById(R.id.temperature);
        feelsLike = findViewById(R.id.feels_like);
        description = findViewById(R.id.description);

        startActivityForResult(new Intent(this, CitySearchActivity.class),
                REQUEST_CITY_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            startActivityForResult(new Intent(this, CitySearchActivity.class),
                    REQUEST_CITY_CODE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CITY_CODE && data != null) {
            CityItem received = data.getParcelableExtra(CitySearchActivity.SELECTED_CITY_ITEM);
            if (received != null)
                currentCity = received;
            updateCurrentWeather();
        }
    }

    private void updateCurrentWeather() {
        progressBar.setVisibility(View.VISIBLE);
        if (currentCity != null && currentCity.location != null) {
            StringBuilder sb = new StringBuilder(currentCity.location.getLatitude() + "")
                    .append(",")
                    .append(currentCity.location.getLongitude());
            WorldWeatherRest.weatherDataApi.getWeather(sb.toString())
                    .subscribeOn(Schedulers.io())                           //в каком потоке выполнять Schedulers.ij - пул потоков
                    .observeOn(AndroidSchedulers.mainThread())              //в каком потоке просматривать
                    .subscribe(weatherResult -> {
                        currentWeather = weatherResult.toWeatherItem();
                        displayWeather();
                    }, throwable -> {
                        progressBar.setVisibility(View.INVISIBLE);
                    }, () -> {
                        progressBar.setVisibility(View.INVISIBLE);
                    });
        }
    }

    private void displayWeather() {
        if (currentCity != null && currentWeather != null) {
            StringBuilder cityNameBuilder = new StringBuilder(currentCity.areaName);
            if (!TextUtils.isEmpty(currentCity.country)) {
                cityNameBuilder
                        .append(", ")
                        .append(currentCity.country);
            }
            cityName.setText(cityNameBuilder.toString());
            ImageLoader.getInstance().displayImage(currentWeather.weatherIconUrl,
                    weatherIcn,
                    mOptionsBuilder.build());
            lastObserved.setText(String.format(getResources().getString(R.string.last_observed),
                    currentWeather.lastObservedUtc));
            temperature.setText(String.format(getResources().getString(R.string.temperature),
                    currentWeather.tempt + ""));
            feelsLike.setText(String.format(getResources().getString(R.string.feels_like),
                    currentWeather.feelsLike + ""));
            description.setText(currentWeather.weatherDesc);
        }
    }

    private void initImageLoader() {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = null;
        try {
            config = new ImageLoaderConfiguration.Builder(this)
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                    .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        } catch (IllegalArgumentException argumentException) {
            try {
                config = new ImageLoaderConfiguration.Builder(this).build();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
        if (config != null) {
            // Initialize ImageLoader with configuration.
            ImageLoader.getInstance().init(config);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
