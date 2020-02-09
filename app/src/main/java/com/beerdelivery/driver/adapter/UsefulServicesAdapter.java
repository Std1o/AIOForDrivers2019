package com.beerdelivery.driver.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.beerdelivery.driver.R;
import com.beerdelivery.driver.model.ModelOrders;
import com.beerdelivery.driver.model.ModelPayment;
import com.beerdelivery.driver.model.ServicesModel;

import java.util.List;

public class UsefulServicesAdapter extends RecyclerView.Adapter<UsefulServicesAdapter.DataViewHolder> {

    public static class DataViewHolder extends RecyclerView.ViewHolder {

        TextView textCommentary, textDate, textValue;

        DataViewHolder(View itemView) {
            super(itemView);
            textCommentary = (TextView) itemView.findViewById(R.id.textCommentary);
            textDate = (TextView) itemView.findViewById(R.id.textDate);
            textValue = (TextView) itemView.findViewById(R.id.textValue);
        }
    }

    public Activity activity;
    public static List<ServicesModel> dataList;

    public UsefulServicesAdapter(List<ServicesModel> dataList, Activity activity){
        this.activity = activity;
        this.dataList = dataList;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.transaction_item, viewGroup, false);
        DataViewHolder pvh = new DataViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final DataViewHolder dataViewHolder, final int position) {

        dataViewHolder.textValue.setText(dataList.get(position).name);
        dataViewHolder.textCommentary.setText(dataList.get(position).phone);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}