package com.beerdelivery.driver.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.beerdelivery.driver.R;
import com.beerdelivery.driver.model.ModelTakenOrders;

import java.util.List;

public class TakenOrdersRVAdapter extends RecyclerView.Adapter<TakenOrdersRVAdapter.DataViewHolder> {

    public static class DataViewHolder extends RecyclerView.ViewHolder {

        TextView status, id, orderTime, orderTarif, clientPlace, clientRoute, clientPhone, orderStatus, orderPrice;

        DataViewHolder(View itemView) {
            super(itemView);
            status = (TextView) itemView.findViewById(R.id.tv_status_desc);
            id = (TextView) itemView.findViewById(R.id.orderIdText);
            orderTime = (TextView) itemView.findViewById(R.id.textDateTime);
            orderTarif = (TextView) itemView.findViewById(R.id.priceValue);
            clientPlace = (TextView) itemView.findViewById(R.id.textFrom);
            clientRoute = (TextView) itemView.findViewById(R.id.textTo);
            clientPhone = (TextView) itemView.findViewById(R.id.tv_client_phone);
            orderStatus = (TextView) itemView.findViewById(R.id.tv_order_status);
            orderPrice = (TextView) itemView.findViewById(R.id.orderPrice);
        }
    }

    public Activity activity;
    public static List<ModelTakenOrders> ordersItems;

    public TakenOrdersRVAdapter(List<ModelTakenOrders> dataList, Activity activity){
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
    public void onBindViewHolder(DataViewHolder dataViewHolder, final int position) {
        ModelTakenOrders modelTakenOrders = ordersItems.get(position);

        dataViewHolder.id.setText(modelTakenOrders.getorderId());
        //orderInfo.setText(m.getorderInfo());
        dataViewHolder.orderTime.setText(modelTakenOrders.getorderTime());
        dataViewHolder.orderTarif.setText(modelTakenOrders.getorderTarif());
        dataViewHolder.clientPlace.setText(modelTakenOrders.getclientPlace());
        dataViewHolder.clientRoute.setText(modelTakenOrders.getclientRoute());
        dataViewHolder.clientPhone.setText(modelTakenOrders.getClientPhone());

        dataViewHolder.orderStatus.setText(modelTakenOrders.getorderStatus());
        dataViewHolder.orderPrice.setText( modelTakenOrders.getOrderPrice());




        if(dataViewHolder.orderStatus.getText().toString().equals("20")){
            dataViewHolder.status.setText("Еду за товаром");
            dataViewHolder.status.setTextColor(Color.GREEN);
        }

        if(dataViewHolder.orderStatus.getText().toString().equals("30")){
            dataViewHolder.status.setText("Еду к клиенту");
            dataViewHolder.status.setTextColor(Color.BLUE);
        }

        if(dataViewHolder.orderStatus.getText().toString().equals("40")){
            dataViewHolder.status.setText("Прибыл к клиенту");
            dataViewHolder.status.setTextColor(Color.BLACK);
        }

        if(dataViewHolder.orderStatus.getText().toString().equals("80")){
            dataViewHolder.status.setText("Заказ удален");
            dataViewHolder.status.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return ordersItems.size();
    }
}