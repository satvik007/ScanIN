package com.example.scanin.DatabaseModule;

import androidx.room.Embedded;
import androidx.room.Relation;

public class DocumentPreview {
    @Embedded
    public Document document;

//    @ColumnInfo(name = "position")
//    public long position;

//    @Embedded
//    List<ImageInfo> imageInfo;

    @Relation(
            parentColumn = "document_id",
            entityColumn = "img_document_id"
    )
     public ImageInfo imageInfo;

    public Document getDocument() {
        return document;
    }

    public ImageInfo getImageInfo() {
        return imageInfo;
    }

    //    public ImageInfo getFirstImage(){
//        if(imageInfos.size() <= 0){
//            Log.e("DocAndFirst", "imageInfo0");
//        }
//        return imageInfos.get(0);
//    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public void setImageInfo(ImageInfo imageInfo) {
        this.imageInfo = imageInfo;
    }

    public void DocumentAndFirstImage(){

    }
}
