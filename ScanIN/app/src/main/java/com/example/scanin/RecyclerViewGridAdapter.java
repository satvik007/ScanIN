package com.example.scanin;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.scanin.DatabaseModule.DocumentAndImageInfo;
import com.squareup.picasso.Picasso;

public class RecyclerViewGridAdapter extends RecyclerView.Adapter<RecyclerViewGridAdapter.GridViewHolder> {
    private DocumentAndImageInfo documentAndImageInfo;
    public final GridAdapterOnClickHandler mClickHandler;

    public interface GridAdapterOnClickHandler{
        void onClick(int position);
    }

    public class GridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textView;
        public GridViewHolder(View view){
            super(view);
            imageView =view.findViewById(R.id.image_thumbnail);
            textView = view.findViewById(R.id.img_position);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(adapterPosition);
        }
    }

    public RecyclerViewGridAdapter(DocumentAndImageInfo documentAndImageInfo, GridAdapterOnClickHandler mClickHandler){
        this.documentAndImageInfo = documentAndImageInfo;
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
        Uri uri = documentAndImageInfo.getImages().get(position).getUri();
        holder.textView.setText(String.valueOf(position));
        Picasso.with(holder.imageView.getContext()).load(uri)
                .fit()
                .centerInside()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if(documentAndImageInfo == null) return 0;
        return documentAndImageInfo.getImages().size();
    }

    public void setmDataset(DocumentAndImageInfo documentAndImageInfo){
        this.documentAndImageInfo = documentAndImageInfo;
        this.notifyDataSetChanged();
    }
}
