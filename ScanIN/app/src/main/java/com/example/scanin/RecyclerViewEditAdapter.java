package com.example.scanin;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewEditAdapter extends RecyclerView.Adapter<RecyclerViewEditAdapter.EditViewHolder> {
    private ArrayList<ImageData> mDataset = new ArrayList<ImageData>();

    public class EditViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public EditViewHolder(View view){
            super(view);
            imageView =view.findViewById(R.id.image_edit_item);
        }
    }

    public RecyclerViewEditAdapter(ArrayList<ImageData> mDataset){
        this.mDataset = mDataset;
    }

    public RecyclerViewEditAdapter.EditViewHolder onCreateViewHolder(ViewGroup parent, int viewtype){
        int layoutIdForImageAdapter =R.layout.image_edit_item;
        LayoutInflater inflater =LayoutInflater.from(parent.getContext());
        View view =inflater.inflate(layoutIdForImageAdapter, parent, false);
        return new EditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewEditAdapter.EditViewHolder holder, int position) {
        ImageData imageData = mDataset.get(position);
        holder.imageView.setImageBitmap(imageData.getOriginalBitmap());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setmDataset(ArrayList<ImageData> mDataset){
        this.mDataset = mDataset;
    }
}
