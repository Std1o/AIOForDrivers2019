package com.beerdelivery.driver.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.beerdelivery.driver.R;
import com.beerdelivery.driver.model.ModelOrders;
import com.beerdelivery.driver.model.ModelPayment;
import com.beerdelivery.driver.model.ServicesModel;

import java.util.List;

public class UsefulServicesAdapter extends RecyclerView.Adapter<UsefulServicesAdapter.DataViewHolder> {

    public static class DataViewHolder extends RecyclerView.ViewHolder {

        TextView textName;
        CardView cv;

        DataViewHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.textName);
            cv = itemView.findViewById(R.id.cv);
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
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.services_item, viewGroup, false);
        DataViewHolder pvh = new DataViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final DataViewHolder dataViewHolder, final int position) {

        dataViewHolder.textName.setText(dataList.get(position).name);
        dataViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dataList.get(position).phone.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_DIAL,
                            Uri.parse("tel:" + dataList.get(position).phone));
                    activity.startActivity(intent);
                }
                else if(!dataList.get(position).url.isEmpty()) {
                    openBrowser(dataList.get(position).url);
                }
                else {
                    Toast.makeText(activity, "Номер или ссылка не указаны", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void openBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.setPackage("com.android.chrome");
        try {
            activity.startActivity(intent);
        }
        catch (ActivityNotFoundException e) {
            Intent intent2 = new Intent(Intent.ACTION_VIEW);
            intent2.setData(Uri.parse(url));
            activity.startActivity(intent2);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}