package com.stdio.aiofordrivers2019.adapter;

/**
 * Created by LordRus on 17.01.2016.
 */


import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



import com.stdio.aiofordrivers2019.R;
import com.stdio.aiofordrivers2019.model.ModelOrders;


public class OrdersCustomListAdapter extends BaseAdapter

    {
        private Activity activity;
        private LayoutInflater inflater;
        private List<ModelOrders> ordersItems;


        public OrdersCustomListAdapter(Activity activity, List <ModelOrders> cityItems) {
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
            TextView status = (TextView) convertView.findViewById(R.id.tv_status_desc);
            TextView id = (TextView) convertView.findViewById(R.id.orderIdText);
            TextView orderTime = (TextView) convertView.findViewById(R.id.textDateTime);
            TextView orderTarif = (TextView) convertView.findViewById(R.id.priceValue);
            TextView clientPlace = (TextView) convertView.findViewById(R.id.textFrom);
            TextView clientRoute = (TextView) convertView.findViewById(R.id.textTo);
            //TextView orderInfo = (TextView) convertView.findViewById(R.id.tv_order_info);


            ModelOrders m = ordersItems.get(position);


            status.setVisibility(View.GONE);
            /*if (m.getStat().equals("1")){
                status.setText("ПРЕДВАРИТЕЛЬНЫЙ");
            }
                else {
                status.setText("");
            }*/


            id.setText(m.getorderId());
            //orderInfo.setText(m.getorderInfo());
            orderTime.setText(m.getorderTime());
            orderTarif.setText(m.getorderTarif());
            clientPlace.setText(m.getclientPlace());
            clientRoute.setText( m.getclientRoute());

                 return convertView;
    }


    }
