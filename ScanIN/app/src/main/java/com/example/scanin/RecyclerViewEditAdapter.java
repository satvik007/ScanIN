package com.example.scanin;


import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.example.scanin.DatabaseModule.DocumentAndImageInfo;
import com.squareup.picasso.Picasso;

public class RecyclerViewEditAdapter extends RecyclerView.Adapter<RecyclerViewEditAdapter.EditViewHolder> {
    private DocumentAndImageInfo documentAndImageInfo;
    private ProgressBar progressBar;

    public class EditViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public EditViewHolder(View view){
            super(view);
            imageView =view.findViewById(R.id.image_edit_item);
        }
    }

    public RecyclerViewEditAdapter(DocumentAndImageInfo documentAndImageInfo){
        this.documentAndImageInfo = documentAndImageInfo;
    }

    public RecyclerViewEditAdapter.EditViewHolder onCreateViewHolder(ViewGroup parent, int viewtype){
        int layoutIdForImageAdapter =R.layout.image_edit_item;
        LayoutInflater inflater =LayoutInflater.from(parent.getContext());
        View view =inflater.inflate(layoutIdForImageAdapter, parent, false);
        return new EditViewHolder(view);
    }

    private void setViewInteract(View view, boolean canDo) {
        view.setEnabled(canDo);
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setViewInteract(((ViewGroup) view).getChildAt(i), canDo);
            }
        }
    }

    protected void showProgressBar(View view) {
        RelativeLayout rlContainer = view.findViewById(R.id.rlContainer);
        setViewInteract(rlContainer, false);
        progressBar.setVisibility(View.VISIBLE);
    }

    protected void hideProgressBar(View view) {
        RelativeLayout rlContainer = view.findViewById(R.id.rlContainer);
        setViewInteract(rlContainer, true);
        progressBar.setVisibility(View.GONE);
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
        Uri uri = documentAndImageInfo.getImages().get(position).getUri();
        Picasso.with(holder.imageView.getContext()).load(uri)
                .fit().centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if(documentAndImageInfo == null) return 0;
        return documentAndImageInfo.getImages().size();
    }

    public void setmDataset(DocumentAndImageInfo documentAndImageInfo)
    {
        this.documentAndImageInfo = documentAndImageInfo;
        this.notifyDataSetChanged();
    }
}
