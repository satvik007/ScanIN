package com.example.scanin.HomeModule;

import android.view.View;

import com.example.scanin.DatabaseModule.DocumentPreview;

public interface DocAdapterClickListener{
    public void onClick(View view, DocumentPreview documentPreview);

    public void onLongClick(View view, int position);
}
