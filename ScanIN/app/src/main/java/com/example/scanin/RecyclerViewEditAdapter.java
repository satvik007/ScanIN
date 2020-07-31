package com.example.scanin;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.example.scanin.DatabaseModule.DocumentAndImageInfo;
import com.example.scanin.DatabaseModule.ImageInfo;
import com.example.scanin.ImageDataModule.BrightnessFilterTransformation1;
import com.example.scanin.ImageDataModule.ContrastAndBrightnessTransformation;
import com.example.scanin.ImageDataModule.ContrastFilterTransformation1;
import com.example.scanin.ImageDataModule.CropTransformation;
import com.example.scanin.ImageDataModule.FilterTransformation;
import com.example.scanin.ImageDataModule.ImageEditUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import jp.wasabeef.picasso.transformations.gpu.BrightnessFilterTransformation;

public class RecyclerViewEditAdapter extends RecyclerView.Adapter<RecyclerViewEditAdapter.EditViewHolder> {
    private DocumentAndImageInfo documentAndImageInfo;
    private ProgressBar progressBar;

    private ScanActivity context;
    public static final int MAX_WIDTH = 2000;
    public static final int MAX_HEIGHT = 2000;
    public int imgPosition;
    public class EditViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public EditViewHolder(View view){
            super(view);
            imageView =view.findViewById(R.id.image_edit_item);
        }
    }

    public RecyclerViewEditAdapter(DocumentAndImageInfo documentAndImageInfo, ScanActivity context){
        this.documentAndImageInfo = documentAndImageInfo;
        this.context = context;
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
        ImageInfo imageInfo = documentAndImageInfo.getImages().get(position);
        Uri uri = imageInfo.getUri();

        int size = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));

        int w = Resources.getSystem().getDisplayMetrics().widthPixels;
        if(position == 0) {
            holder.itemView.setPadding(40, 0, 0, 0);
            w+=40;
        }
        else if(position == getItemCount()-1){
            holder.itemView.setPadding(0, 0, 40, 0);
            w+=40;
        }
        holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(w-80, ViewGroup.LayoutParams.MATCH_PARENT));
//        int size = (int) Math.ceil(Math.sqrt(w * h));
//        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.itemView.getLayoutParams();
//        layoutParams.setMargins();

//            Picasso.with(holder.imageView.getContext()).load(uri)
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                Bitmap newBitmap = ImageData.changeContrastAndBrightness(bitmap, 1.5, imageInfo.getBeta());
                holder.imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        holder.imageView.setTag(target);
        Picasso.get().load(uri)
                .transform(new CropTransformation(imageInfo.getCropPositionMap()))
                .transform(new FilterTransformation(ImageEditUtil.getFilterName(imageInfo.getFilterId())))
//                .transform(new BrightnessFilterTransformation(context, (float)imageInfo.getBeta()))
//                .transform(new ContrastFilterTransformation1(context, (float)imageInfo.getAlpha()))
                .transform(new ContrastAndBrightnessTransformation(imageInfo.getAlpha(), (int) imageInfo.getBeta()))
                .resize(size, size)
                .centerInside()
                .into(target);

        holder.imageView.setRotation(90f*imageInfo.getRotationConfig());
        if(position != getItemCount() - 1){

//            Picasso.with()
//                    .load(documentAndImageInfo.getImages().get(position + 1).getUri())
//                    .into(new Target() {
//                        @Override
//                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                        }
//
//                        @Override
//                        public void onBitmapFailed( Drawable errorDrawable) {
//                            // Error handling
//                        }
//
//                        @Override
//                        public void onPrepareLoad(Drawable placeHolderDrawable) {
//                        }
//                    });
        }
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