package com.example.scanin.ImageDataModule;

import android.content.Context;
import android.graphics.Bitmap;
import com.squareup.picasso.Transformation;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

public class GPUFilterTransformation1 implements Transformation {

    private Context mContext;
    private GPUImageFilter mFilter;

    public GPUFilterTransformation1(Context context, GPUImageFilter filter) {
        mContext = context.getApplicationContext();
        mFilter = filter;
    }

    @Override public Bitmap transform(Bitmap source) {
        GPUImage gpuImage = new GPUImage(mContext);
        gpuImage.setImage(source);
        gpuImage.setFilter(mFilter);

        Bitmap bitmap = gpuImage.getBitmapWithFilterApplied();
//        source.recycle();

        return bitmap;
    }

    @Override public String key() {
        return getClass().getSimpleName();
    }

    @SuppressWarnings("unchecked") public <T> T getFilter() {
        return (T) mFilter;
    }
}
