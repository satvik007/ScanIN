package com.example.scanin;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.scanin.DatabaseModule.DocumentAndImageInfo;
import com.example.scanin.ImageDataModule.BitmapTransform;
import com.squareup.picasso.Picasso;

public class RecyclerViewGridAdapter extends RecyclerView.Adapter<RecyclerViewGridAdapter.GridViewHolder> {
    private DocumentAndImageInfo documentAndImageInfo;
    public final GridAdapterOnClickHandler mClickHandler;
    private static final int MAX_WIDTH = 1024;
    private static final int MAX_HEIGHT = 768;

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
        int size = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));
//        Log.d("GridAdapt", String.valueOf(holder.itemView.getWidth()));
//        holder.imageView.getLayoutParams().width = holder.itemView.getWidth();

        if(position%2==0){
            holder.itemView.setPadding(0, 0, 20, 0);
        }
        else{
            holder.itemView.setPadding(20, 0, 0, 0);
        }
        Picasso.with(holder.imageView.getContext()).load(uri)
                .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                .resize(size, size)
                .centerCrop()
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
