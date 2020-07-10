package com.example.scanin;

import android.view.LayoutInflater;
import android.view.ViewGroup;
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

    public RecylerViewGridAdapter(String[] myDataset){
        mDataset = myDataset;
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
        holder.textView.setText(mDataset[position]);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
