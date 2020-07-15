package com.example.scanin.HomeModule;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.scanin.DatabaseModule.DocumentsAndFirstImage;
import com.example.scanin.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RecyclerViewDocAdapter extends RecyclerView.Adapter<RecyclerViewDocAdapter.DocViewHolder> {
    private ArrayList<DocumentsAndFirstImage> mDataset = new ArrayList<DocumentsAndFirstImage>();

    public class DocViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        public DocViewHolder(View view){
            super(view);
            imageView =view.findViewById(R.id.doc_image);
            textView = view.findViewById(R.id.doc_name);
        }
    }

    public RecyclerViewDocAdapter(ArrayList<DocumentsAndFirstImage> mDataset){
        this.mDataset = mDataset;
    }

    @NotNull
    public RecyclerViewDocAdapter.DocViewHolder onCreateViewHolder(ViewGroup parent, int viewtype){
        int layoutIdForImageAdapter =R.layout.doc_item;
        LayoutInflater inflater =LayoutInflater.from(parent.getContext());
        View view =inflater.inflate(layoutIdForImageAdapter, parent, false);

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) (parent.getWidth() * 0.45);
        layoutParams.height = (int) (parent.getWidth() * 0.3);
        view.setLayoutParams(layoutParams);
        return new DocViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewDocAdapter.DocViewHolder holder, int position) {
        DocumentsAndFirstImage documentsAndFirstImage = mDataset.get(position);

        holder.textView.setText(documentsAndFirstImage.getDocument().getDocumentName());
        Picasso.with(holder.imageView.getContext())
                .load(documentsAndFirstImage.getImageInfo().getUri())
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if(mDataset == null) return 0;
        return mDataset.size();
    }

    public void setmDataset(ArrayList<DocumentsAndFirstImage> mDataset)
    {
        this.mDataset = mDataset;
        this.notifyDataSetChanged();
    }
}
