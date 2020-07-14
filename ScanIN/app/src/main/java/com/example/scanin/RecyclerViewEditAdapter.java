package com.example.scanin;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.scanin.ImageDataModule.ImageData;
import com.squareup.picasso.Picasso;

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

//    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
//                                                         int reqWidth, int reqHeight) {
//
//        // First decode with inJustDecodeBounds=true to check dimensions
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(res, resId, options);
//
//        // Calculate inSampleSize
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//
//        // Decode bitmap with inSampleSize set
//        options.inJustDecodeBounds = false;
//        return BitmapFactory.decodeResource(res, resId, options);
//    }

    @Override
    public void onBindViewHolder(RecyclerViewEditAdapter.EditViewHolder holder, int position) {
//        ImageData imageData = mDataset.get(position);
////        holder.imageView.setImageBitmap(
////                decodeSampledBitmapFromResource(getResources(), R.id.myimage, 100, 100));
////        holder.imageView.setImageBitmap(imageData.getOriginalBitmap());
//        Log.d("onCreateEdit", String.valueOf(imageData));
//        holder.imageView.setImageBitmap(imageData.getSmallImage());

        ImageData imageData = mDataset.get(position);
        Picasso.with(holder.imageView.getContext()).load(imageData.getFileName())
                .placeholder(R.drawable.ic_rotate)
                .resize(400, 400).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setmDataset(ArrayList<ImageData> mDataset)
    {
        this.mDataset = mDataset;
        this.notifyDataSetChanged();
    }
}
