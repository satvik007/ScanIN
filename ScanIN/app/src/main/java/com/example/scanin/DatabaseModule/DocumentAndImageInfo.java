package com.example.scanin.DatabaseModule;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class DocumentAndImageInfo {
    @Embedded public Document document;
    @Relation(
            parentColumn = "documentId",
            entityColumn = "document_id"
    )
    public List<ImageInfo> images;
}
