package com.stdio.aiofordrivers2019.adapter;

/**
 * Created by LordRus on 24.01.2016.
 */

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;



import com.stdio.aiofordrivers2019.R;
import com.stdio.aiofordrivers2019.app.AppController;
import com.stdio.aiofordrivers2019.model.modelTakenOrders;


public class takenOrdersCustomListAdapter extends BaseAdapter

{
    private Activity activity;
    private LayoutInflater inflater;
    private List<modelTakenOrders> ordersItems;


    public takenOrdersCustomListAdapter(Activity activity, List < modelTakenOrders > cityItems) {
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

        /*TextView id = (TextView) convertView.findViewById(R.id.tv_order_id);
        TextView orderTime = (TextView) convertView.findViewById(R.id.tv_order_time);
        TextView orderTarif = (TextView) convertView.findViewById(R.id.tv_order_tarif);
        TextView clientPlace = (TextView) convertView.findViewById(R.id.tv_clent_point);
        TextView clientRoute = (TextView) convertView.findViewById(R.id.tv_client_route);
        TextView orderInfo = (TextView) convertView.findViewById(R.id.tv_order_info);

        TextView clientPhone = (TextView) convertView.findViewById(R.id.tv_client_phone);
        TextView orderStatus = (TextView) convertView.findViewById(R.id.tv_order_status);

        TextView Status = (TextView) convertView.findViewById(R.id.tv_status_desc);



        modelTakenOrders m = ordersItems.get(position);

        id.setText(m.getorderId());
        orderInfo.setText(m.getorderInfo());
        orderTime.setText(m.getorderTime());
        orderTarif.setText(m.getorderTarif());
        clientPlace.setText(m.getclientPlace());
        clientRoute.setText( m.getclientRoute());

        clientPhone.setText(m.getclientPhone());

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
*/


        return convertView;
    }


}
