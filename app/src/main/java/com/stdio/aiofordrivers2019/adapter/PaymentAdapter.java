package com.stdio.aiofordrivers2019.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.stdio.aiofordrivers2019.R;
import com.stdio.aiofordrivers2019.model.ModelOrders;
import com.stdio.aiofordrivers2019.model.ModelPayment;

import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.DataViewHolder> {

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
    public static List<ModelPayment> ordersItems;

    public PaymentAdapter(List<ModelPayment> dataList, Activity activity){
        this.activity = activity;
        this.ordersItems = dataList;
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

        ModelPayment modelOrders = ordersItems.get(position);

        dataViewHolder.textValue.setText(modelOrders.getMoneyCount());
        dataViewHolder.textCommentary.setText(modelOrders.getOperationName());
        dataViewHolder.textDate.setText(modelOrders.getDateTime());
    }

    @Override
    public int getItemCount() {
        return ordersItems.size();
    }
}