package com.example.scanin.HomeModule;


import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.scanin.DatabaseModule.DocumentPreview;
import com.example.scanin.ImageDataModule.BitmapTransform;
import com.example.scanin.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RecyclerViewDocAdapter extends RecyclerView.Adapter<RecyclerViewDocAdapter.DocViewHolder> {
    private ArrayList<DocumentPreview> mDataset = new ArrayList<DocumentPreview>();
    private DocAdapterClickListener mListener;
    private static final int MAX_WIDTH = 1024;
    private static final int MAX_HEIGHT = 768;

    public RecyclerViewDocAdapter(DocAdapterClickListener mListener){
        this.mListener = mListener;
    }

    public class DocViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        DocAdapterClickListener listener;

        public DocViewHolder(View view, DocAdapterClickListener listener){
            super(view);
            imageView =view.findViewById(R.id.doc_image);
            textView = view.findViewById(R.id.doc_name);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onClick(view, mDataset.get(getAdapterPosition()));
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mListener.onLongClick(view, getAdapterPosition());
                    return true;
                }
            });
        }
    }

    @NotNull
    public RecyclerViewDocAdapter.DocViewHolder onCreateViewHolder(ViewGroup parent, int viewtype){
        int layoutIdForImageAdapter =R.layout.doc_item;
        LayoutInflater inflater =LayoutInflater.from(parent.getContext());
        View view =inflater.inflate(layoutIdForImageAdapter, parent, false);

//        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//        layoutParams.width = (int) (parent.getWidth() * 0.45);
//        layoutParams.height = (int) (parent.getWidth() * 0.3);
//        view.setLayoutParams(layoutParams);
        return new DocViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(RecyclerViewDocAdapter.DocViewHolder holder, int position) {
        DocumentPreview documentPreview = mDataset.get(position);
        if(documentPreview.getImageInfo() == null) {
            return;
        }

        if(position%2==0){
            holder.itemView.setPadding(0, 30, 40, 0);
        }
        else{
            holder.itemView.setPadding(40, 30, 0, 0);
        }

        int size = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));
        holder.textView.setText(documentPreview.getDocument().getDocumentName());
        Uri uri = documentPreview.getImageInfo().getUri();
        Picasso.get().load(uri)
                .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                .resize(0,200)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if(mDataset == null) return 0;
        return mDataset.size();
    }

    public void setmDataset(ArrayList<DocumentPreview> mDataset)
    {
        this.mDataset = mDataset;
        this.notifyDataSetChanged();
    }
}
