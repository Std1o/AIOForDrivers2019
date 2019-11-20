package com.stdio.aiofordrivers2019.adapter;

/**
 * Created by LordRus on 15.01.2016.
 */



import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import com.stdio.aiofordrivers2019.R;
import com.stdio.aiofordrivers2019.app.AppController;
import com.stdio.aiofordrivers2019.model.modelCity;

public class CityCustomListAdapter  extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<modelCity> cityItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CityCustomListAdapter(Activity activity, List<modelCity> cityItems) {
        this.activity = activity;
        this.cityItems = cityItems;
    }

    @Override
    public int getCount() {
        return cityItems.size();
    }

    @Override
    public Object getItem(int location) {
        return cityItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_city, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.cityName);


        // getting movie data for the row
        modelCity m = cityItems.get(position);

        // thumbnail image
        thumbNail.setImageUrl(m.getCityThumbnailUrl(), imageLoader);

        // title
        title.setText(m.getCityName());



        return convertView;
    }

}