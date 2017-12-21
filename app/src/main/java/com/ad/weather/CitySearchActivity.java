package com.ad.weather;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

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
        adapter.setOnItemClickListener(new CitiesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CityItem item) {
                Intent intent = new Intent();
                intent.putExtra(SELECTED_CITY_ITEM, item);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_search_area, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = searchItem.getActionView().findViewById(R.id.search_view);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        AutoCompleteTextView search_text = searchView.findViewById(R.id.search_src_text);
        search_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
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
        final PublishSubject<String> search = PublishSubject.create();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search.onNext(newText);
                return false;
            }
        });

        disposables.add(search
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        return s.replaceAll(" ","+");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {

            @Override
            public void onNext(String value) {
                Log.e("onNext", value+"");
                if(!TextUtils.isEmpty(value) && value.length() > 2) {
                    search(value);
                }
            }

            @Override
            public void onError(Throwable e) {
                progress.setVisibility(View.INVISIBLE);
                emptyView.setVisibility(View.VISIBLE);
                Log.e("onError", e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        }));

        return super.onPrepareOptionsMenu(menu);
    }

    private void search(String searchText) {
        progress.setVisibility(View.VISIBLE);
        WorldWeatherRest.weatherDataApi.search(searchText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<CityItem.AreaResult>() {
                    @Override
                    public void onNext(CityItem.AreaResult value) {
                        progress.setVisibility(View.INVISIBLE);
                        adapter.setItems(value.toCityItemList());
                        emptyView.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        progress.setVisibility(View.INVISIBLE);
                        emptyView.setVisibility(View.VISIBLE);
                        Log.e("onError", e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }
}
