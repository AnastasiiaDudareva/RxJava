package com.ad.weather;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anastasiia on 21.12.17.
 */

public class CityItem implements Parcelable {
    String areaName = "";
    String country = "";
    Location location = new Location("");

    public CityItem(String areaName, String country, Location location) {
        this.areaName = areaName;
        this.country = country;
        this.location = location;
    }

    protected CityItem(Parcel in) {
        areaName = in.readString();
        country = in.readString();
        location = in.readParcelable(Location.class.getClassLoader());
    }

    public static final Creator<CityItem> CREATOR = new Creator<CityItem>() {
        @Override
        public CityItem createFromParcel(Parcel in) {
            return new CityItem(in);
        }

        @Override
        public CityItem[] newArray(int size) {
            return new CityItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(areaName);
        dest.writeString(country);
        dest.writeParcelable(location, flags);
    }

    class AreaResult {
        Result search_api;

        public AreaResult(Result search_api) {
            this.search_api = search_api;
        }

        public List<CityItem> toCityItemList() {
            List<CityItem> list = new ArrayList<>();
            if (search_api != null) {
                for (Area area : search_api.result) {
                    list.add(new CityItem(area.getAreaName(),
                            area.getCountry(),
                            area.getLocation()));
                }
            }
            return list;
        }
    }

    class Result {
        List<Area> result;

        public Result(List<Area> result) {
            this.result = result;
        }
    }

    class Area {
        List<Value> areaName;
        List<Value> country;
        String latitude;
        String longitude;

        public Area(List<Value> areaName, List<Value> country, String latitude, String longitude) {
            this.areaName = areaName;
            this.country = country;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getAreaName() {
            if (areaName != null && !areaName.isEmpty())
                return areaName.get(0).value;
            return "";
        }

        public String getCountry() {
            if (country != null && !country.isEmpty())
                return country.get(0).value;
            return "";
        }

        public Location getLocation() {
            Location location = new Location("");
            if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
                location.setLatitude(Double.parseDouble(latitude));
                location.setLongitude(Double.parseDouble(longitude));
            }
            return location;
        }
    }


    class Value {
        String value;

        public Value(String value) {
            this.value = value;
        }
    }

}


//data class AreaResult(var search_api: Result) {
//        fun toCityItemList(): List<CityItem> {
//        var list: MutableList<CityItem> = ArrayList()
//        if (search_api == null)
//        return list
//        for (area in search_api.result) {
//        list.add(CityItem(area.areaName.get(0).value,
//        area.country.get(0).value,
//        area.region.get(0).value,
//        area.getLocation(),
//        area.weatherUrl.get(0).value))
//        }
//        return list.sortedWith(compareBy({ it.areaName }))
//        }
//
//        }
//
//        data class Value(var value: String)
//
//        data class Result (var result: List<Area>)
//
//        data class Area(var areaName: List<Value>,
//        var country: List<Value>,
//        var region: List<Value>,
//        var latitude: String,
//        var longitude: String,
//        var weatherUrl: List<Value>) {
//        fun getLocation(): Location {
//        var location: Location = Location("")
//        location.latitude = latitude.toDouble()
//        location.longitude = longitude.toDouble()
//        return location
//        }
//        }