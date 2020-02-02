package com.beerdelivery.driver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.beerdelivery.driver.R;
import com.beerdelivery.driver.model.ChatMessageModel;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.DataViewHolder> {

    public static class DataViewHolder extends RecyclerView.ViewHolder {

        TextView tvMessage;
        TextView tvSender;

        DataViewHolder(View itemView) {
            super(itemView);
            tvMessage = (TextView)itemView.findViewById(R.id.messageTextView);
            tvSender = (TextView)itemView.findViewById(R.id.messengerTextView);
        }
    }

    List<ChatMessageModel> dataList;
    Context mContext;

    public ChatAdapter(List<ChatMessageModel> dataList, Context context){
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message, viewGroup, false);
        DataViewHolder pvh = new DataViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(DataViewHolder dataViewHolder, final int position) {
        dataViewHolder.tvMessage.setText(dataList.get(position).message);
        dataViewHolder.tvSender.setText(dataList.get(position).sender);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}