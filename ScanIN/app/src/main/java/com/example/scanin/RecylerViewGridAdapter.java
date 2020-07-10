package com.example.scanin;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class RecylerViewGridAdapter extends RecyclerView.Adapter<RecylerViewGridAdapter.GridViewHolder> {

    private String[] mDataset;
    public static class GridViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public GridViewHolder(TextView v){
            super(v);
            textView = v;
        }
    }

    public RecylerViewGridAdapter(String[] mDataset){

    }
}
