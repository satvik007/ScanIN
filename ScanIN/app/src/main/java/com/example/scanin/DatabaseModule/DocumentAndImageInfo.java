package com.example.scanin.DatabaseModule;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class DocumentAndImageInfo {
    @Embedded public Document document;

    @Relation(
            parentColumn = "document_id",
            entityColumn = "img_document_id"
    )
    public List<ImageInfo> images;

    public Document getDocument() {
        return document;
    }

    public List<ImageInfo> getImages() {
        return images;
    }
}
