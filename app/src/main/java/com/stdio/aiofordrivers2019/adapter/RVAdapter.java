package com.stdio.aiofordrivers2019.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.stdio.aiofordrivers2019.R;
import com.stdio.aiofordrivers2019.BrowseFreeOrders;
import com.stdio.aiofordrivers2019.model.ModelOrders;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.DataViewHolder> {

    public static class DataViewHolder extends RecyclerView.ViewHolder {

        TextView status, id, orderTime, orderTarif, clientPlace, clientRoute;

        DataViewHolder(View itemView) {
            super(itemView);
            status = (TextView) itemView.findViewById(R.id.tv_status_desc);
            id = (TextView) itemView.findViewById(R.id.orderIdText);
            orderTime = (TextView) itemView.findViewById(R.id.textDateTime);
            orderTarif = (TextView) itemView.findViewById(R.id.priceValue);
            clientPlace = (TextView) itemView.findViewById(R.id.textFrom);
            clientRoute = (TextView) itemView.findViewById(R.id.textTo);
        }
    }

    public Activity activity;
    public static List<ModelOrders> ordersItems;

    public RVAdapter(List<ModelOrders> dataList, Activity activity){
        this.activity = activity;
        this.ordersItems = dataList;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row_orders, viewGroup, false);
        DataViewHolder pvh = new DataViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final DataViewHolder dataViewHolder, final int position) {

        ModelOrders modelOrders = ordersItems.get(position);
        dataViewHolder.status.setVisibility(View.GONE);

        dataViewHolder.id.setText(modelOrders.getorderId());
        //orderInfo.setText(m.getorderInfo());
        dataViewHolder.orderTime.setText(modelOrders.getorderTime());
        dataViewHolder.orderTarif.setText(modelOrders.getorderTarif());
        dataViewHolder.clientPlace.setText(modelOrders.getclientPlace());
        dataViewHolder.clientRoute.setText( modelOrders.getclientRoute());
    }

    @Override
    public int getItemCount() {
        return ordersItems.size();
    }
}