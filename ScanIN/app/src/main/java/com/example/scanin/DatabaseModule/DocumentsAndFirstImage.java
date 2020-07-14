package com.example.scanin.DatabaseModule;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class DocumentsAndFirstImage {
    @Embedded
    public Document document;
    @Relation(
            parentColumn = "documentId",
            entityColumn = "uri"
    )
    public List<ImageInfo> firstImages;
}
