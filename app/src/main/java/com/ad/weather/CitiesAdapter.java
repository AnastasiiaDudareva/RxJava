package com.ad.weather;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anastasiia on 21.12.17.
 */

public class CitiesAdapter extends RecyclerView.Adapter<CitiesAdapter.ViewHolder> {
    private List<CityItem> items = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setItems(List<CityItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public CitiesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new CitiesAdapter.ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_city,
                        parent,
                        false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        final TextView cityName;
        final TextView countryName;

        public ViewHolder(View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.city_name);
            countryName = itemView.findViewById(R.id.country_name);
        }

        public void bind(final CityItem item) {
            cityName.setText(item.areaName);
            countryName.setText(item.country);
            if (onItemClickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(item);
                    }
                });
            }
        }
    }

    interface OnItemClickListener {
        void onItemClick(CityItem item);
    }

}

