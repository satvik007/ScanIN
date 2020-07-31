package com.example.scanin.ImageDataModule;

import android.content.Context;

import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;

public class ContrastFilterTransformation1 extends GPUFilterTransformation1 {
    private float mContrast;

    public ContrastFilterTransformation1(Context context) {
        this(context, 1.0f);
    }

    public ContrastFilterTransformation1(Context context, float contrast) {
        super(context, new GPUImageContrastFilter());
        mContrast = contrast;
        GPUImageContrastFilter filter = getFilter();
        filter.setContrast(mContrast);
    }

    @Override public String key() {
        return "ContrastFilterTransformation(contrast=" + mContrast + ")";
    }
}
