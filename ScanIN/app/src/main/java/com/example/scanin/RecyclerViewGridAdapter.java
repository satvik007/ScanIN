package com.example.scanin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

public class RecyclerViewGridAdapter extends RecyclerView.Adapter<RecyclerViewGridAdapter.GridViewHolder> {
    private ArrayList<ImageData> mDataset = new ArrayList<ImageData>();
    public final GridAdapterOnClickHandler mClickHandler;

    public interface GridAdapterOnClickHandler{
        void onClick(int position);
    }
    public class GridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        public GridViewHolder(View view){
            super(view);
            imageView =view.findViewById(R.id.image_thumbnail);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(adapterPosition);
        }
    }

    public RecyclerViewGridAdapter(ArrayList<ImageData> mDataset, GridAdapterOnClickHandler mClickHandler){
        this.mDataset = mDataset;
        this.mClickHandler = mClickHandler;
    }

    public RecyclerViewGridAdapter.GridViewHolder onCreateViewHolder(ViewGroup parent, int viewtype){
        int layoutIdForImageAdapter =R.layout.image_grid_item;
        LayoutInflater inflater =LayoutInflater.from(parent.getContext());
        View view =inflater.inflate(layoutIdForImageAdapter, parent, false);
        return new GridViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {
        ImageData imageData = mDataset.get(position);
        if(imageData.getOriginalBitmap() == null){
            try {
                imageData.setOriginalBitmap(holder.imageView.getContext());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        holder.imageView.setImageBitmap(imageData.getThumbnail());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setmDataset(ArrayList<ImageData> mDataset){
        this.mDataset = mDataset;
    }
}
