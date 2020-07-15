package com.example.scanin.DatabaseModule;

import android.util.Log;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class DocumentsAndFirstImage {
    @Embedded
    public Document document;

//    @Embedded
//    List<ImageInfo> imageInfo;

    @Relation(
            parentColumn = "document_id",
            entityColumn = "img_document_id"
    )
    List<ImageInfo> imageInfos;

    public Document getDocument() {
        return document;
    }

    public List<ImageInfo> getImageInfos() {
        return imageInfos;
    }

    public ImageInfo getFirstImage(){
        if(imageInfos.size() <= 0){
            Log.e("DocAndFirst", "imageInfo0");
        }
        return imageInfos.get(0);
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public void setImageInfo(List<ImageInfo> imageInfos1) {
        this.imageInfos = imageInfos1;
    }

    public void DocumentAndFirstImage(){

    }
}
