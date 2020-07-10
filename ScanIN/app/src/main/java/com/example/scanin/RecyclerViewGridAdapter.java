package com.example.scanin;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecylerViewGridAdapter extends RecyclerView.Adapter<RecylerViewGridAdapter.GridViewHolder> {

    private ArrayList<ImageData> mDataset = null;
    public static class GridViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public GridViewHolder(TextView v){
            super(v);
            textView = v;
        }
    }

    public RecylerViewGridAdapter(ArrayList<ImageData> mDataset){
        this.mDataset = mDataset;
    }

    public RecylerViewGridAdapter.GridViewHolder onCreateViewHolder(ViewGroup parent, int viewtype){
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_grid_item, parent, false);
        GridViewHolder gridViewHolder = new GridViewHolder(v);
        return gridViewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.setText(mDataset.get(position).getFileName());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
