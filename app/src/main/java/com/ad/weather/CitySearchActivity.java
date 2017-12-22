package com.ad.weather;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import com.jakewharton.rxbinding.widget.RxTextView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by anastasiia on 21.12.17.
 */

public class CitySearchActivity extends AppCompatActivity {
    public static final String SELECTED_CITY_ITEM = "SELECTED_CITY_ITEM";
    private ProgressBar progress;
    private RecyclerView recyclerView;
    private View emptyView;
    private CitiesAdapter adapter;
    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_search);
        progress = findViewById(R.id.pregress);
        recyclerView = findViewById(R.id.rv_cities);
        emptyView = findViewById(R.id.empty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CitiesAdapter();
        recyclerView.setAdapter(adapter);
        adapter.getItemClickSignal().subscribe(cityItem -> {
            Intent intent = new Intent();
            intent.putExtra(SELECTED_CITY_ITEM, cityItem);
            setResult(RESULT_OK, intent);
            finish();
        });

        PermissionsHelper.requestPermissionsIfNeed(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                new int[]{PermissionsHelper.MY_PERMISSIONS_ACCESS_FINE_LOCATION,
                        PermissionsHelper.MY_PERMISSIONS_ACCESS_COARSE_LOCATION});

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_search_area, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionsHelper.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
                || requestCode == PermissionsHelper.MY_PERMISSIONS_ACCESS_FINE_LOCATION) {
            searchNearest();
        }
    }

    private void searchNearest() {
        LocationHelper locationHelper = new LocationHelper(this);
        disposables.add(locationHelper
                .getLastKnownLocation()
                .map(location -> {
                    if (location != null) {
                        return new StringBuilder(location.getLatitude() + "")
                                .append(",")
                                .append(location.getLongitude() + "").toString();
                    }
                    return "";
                })
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String value) {
                        if (!TextUtils.isEmpty(value)) {
                            search(value);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("onError", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = searchItem.getActionView().findViewById(R.id.search_view);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        final AutoCompleteTextView searchText = searchView.findViewById(R.id.search_src_text);
        searchText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
        searchView.setQueryHint(getResources().getString(R.string.search_area));
        searchView.requestFocus();
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                View currFocus = getCurrentFocus();
                if (currFocus != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(currFocus.getWindowToken(), 0);
                    currFocus.clearFocus();
                }
                return true;
            }
        });

        RxTextView.textChanges(searchText)
                .map(charSequence -> charSequence.toString().replaceAll(" ", "+"))
                .subscribe(value -> {
                    if (!TextUtils.isEmpty(value) && value.length() > 2) {
                        search(value);
                    } else {
                        searchNearest();
                    }
                }, throwable -> {
                    progress.setVisibility(View.INVISIBLE);
                    emptyView.setVisibility(View.VISIBLE);
                    Log.e("onError", throwable.getMessage());
                });

        return super.onPrepareOptionsMenu(menu);
    }

    private void search(String searchText) {
        progress.setVisibility(View.VISIBLE);
        WorldWeatherRest.weatherDataApi.search(searchText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(areaResult -> {
                            adapter.setItems(areaResult.toCityItemList());
                            emptyView.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
                        },
                        throwable -> {
                            progress.setVisibility(View.INVISIBLE);
                            emptyView.setVisibility(View.VISIBLE);
                            Log.e("onError", throwable.getMessage());
                        },
                        () -> {
                            progress.setVisibility(View.INVISIBLE);
                        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }
}
