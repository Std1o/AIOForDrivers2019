package com.beerdelivery.driver.adapter;

/**
 * Created by BeerDelivery on 01.12.2019.
 */

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



import com.beerdelivery.driver.R;
import com.beerdelivery.driver.model.ModelTakenOrders;


public class takenOrdersCustomListAdapter extends BaseAdapter

{
    private Activity activity;
    private LayoutInflater inflater;
    private List<ModelTakenOrders> ordersItems;


    public takenOrdersCustomListAdapter(Activity activity, List <ModelTakenOrders> cityItems) {
        this.activity = activity;
        this.ordersItems = cityItems;
    }

    @Override
    public int getCount () {
        return ordersItems.size();
    }

    @Override
    public Object getItem ( int location){
        return ordersItems.get(location);
    }

    @Override
    public long getItemId ( int position){
        return position;
    }

    @Override
    public View getView ( int position, View convertView, ViewGroup parent){

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_orders, null);

        TextView id = (TextView) convertView.findViewById(R.id.orderIdText);
        TextView orderTime = (TextView) convertView.findViewById(R.id.textDateTime);
        TextView orderTarif = (TextView) convertView.findViewById(R.id.priceValue);
        TextView clientPlace = (TextView) convertView.findViewById(R.id.textFrom);
        TextView clientRoute = (TextView) convertView.findViewById(R.id.textTo);
        //TextView orderInfo = (TextView) convertView.findViewById(R.id.tv_order_info);

        //TextView clientPhone = (TextView) convertView.findViewById(R.id.tv_client_phone);
        TextView orderStatus = (TextView) convertView.findViewById(R.id.tv_order_status);

        TextView Status = (TextView) convertView.findViewById(R.id.tv_status_desc);



        ModelTakenOrders m = ordersItems.get(position);

        id.setText(m.getorderId());
        //orderInfo.setText(m.getorderInfo());
        orderTime.setText(m.getorderTime());
        orderTarif.setText(m.getorderTarif());
        clientPlace.setText(m.getclientPlace());
        clientRoute.setText( m.getclientRoute());

        //clientPhone.setText(m.getclientPhone());

        orderStatus.setText(m.getorderStatus());




        if(orderStatus.getText().toString().equals("20")){
            Status.setText("Еду за товаром");
            Status.setTextColor(Color.GREEN);
        }

        if(orderStatus.getText().toString().equals("30")){
            Status.setText("Еду к клиенту");
            Status.setTextColor(Color.BLUE);
        }

        if(orderStatus.getText().toString().equals("40")){
            Status.setText("Прибыл к клиенту");
            Status.setTextColor(Color.BLACK);
        }

        if(orderStatus.getText().toString().equals("80")){
            Status.setText("Заказ удален");
            Status.setTextColor(Color.RED);
        }


        return convertView;
    }


}
