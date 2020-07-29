package com.example.scanin.HomeModule;

import android.view.View;

import com.example.scanin.DatabaseModule.DocumentPreview;

public interface DocAdapterClickListener{
    public void onClick(View view, DocumentPreview documentPreview);

    public void onLongClick(View view, int position);

    public void deleteDoc(View view, DocumentPreview documentPreview);

    public void renameDoc(View view, DocumentPreview documentPreview);
}
