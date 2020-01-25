package com.beerdelivery.driver.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.beerdelivery.driver.R;
import com.beerdelivery.driver.model.ModelOrders;

import java.util.List;

public class OrdersRVAdapter extends RecyclerView.Adapter<OrdersRVAdapter.DataViewHolder> {

    public static class DataViewHolder extends RecyclerView.ViewHolder {

        TextView status, id, orderTime, orderTarif, clientPlace, clientRoute, orderPrice;

        DataViewHolder(View itemView) {
            super(itemView);
            status = (TextView) itemView.findViewById(R.id.tv_status_desc);
            id = (TextView) itemView.findViewById(R.id.orderIdText);
            orderTime = (TextView) itemView.findViewById(R.id.textDateTime);
            orderTarif = (TextView) itemView.findViewById(R.id.priceValue);
            clientPlace = (TextView) itemView.findViewById(R.id.textFrom);
            clientRoute = (TextView) itemView.findViewById(R.id.textTo);
            orderPrice = (TextView) itemView.findViewById(R.id.orderPrice);
        }
    }

    public Activity activity;
    public static List<ModelOrders> ordersItems;

    public OrdersRVAdapter(List<ModelOrders> dataList, Activity activity){
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
        dataViewHolder.orderPrice.setText( modelOrders.getOrderPrice());
    }

    @Override
    public int getItemCount() {
        return ordersItems.size();
    }
}