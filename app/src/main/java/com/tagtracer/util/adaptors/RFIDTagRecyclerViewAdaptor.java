package com.tagtracer.util.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tagtracer.R;
import com.tagtracer.models.RFIDTag;

import java.util.ArrayList;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class RFIDTagRecyclerViewAdaptor extends RecyclerView.Adapter<RFIDTagRecyclerViewAdaptor.MyViewHolder> {
    private Context context;
    private ArrayList<RFIDTag> tags;

    public RFIDTagRecyclerViewAdaptor(@ApplicationContext Context context, ArrayList<RFIDTag> tags) {
        this.context = context;
        this.tags = tags;
    }

    @NonNull
    @Override
    public RFIDTagRecyclerViewAdaptor.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new RFIDTagRecyclerViewAdaptor.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RFIDTagRecyclerViewAdaptor.MyViewHolder holder, int position) {
        holder.epc.setText(this.tags.get(position).getTid());
        holder.rssi.setText(this.tags.get(position).getRssi());
    }

    @Override
    public int getItemCount() {
        return this.tags.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView card;
        public TextView epc;
        public TextView rssi;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.row);
            epc = itemView.findViewById(R.id.epc);
            rssi = itemView.findViewById(R.id.rssi);
        }
    }
}
