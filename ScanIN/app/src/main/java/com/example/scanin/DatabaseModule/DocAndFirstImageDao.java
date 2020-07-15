package com.example.scanin.DatabaseModule;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface DocAndFirstImageDao {
    @Transaction
//    @Query("SELECT document.*, image_info.* FROM document INNER JOIN image_info ON document.documentId=image_info.document_id")
    @Query("Select * from document")
    List<DocumentsAndFirstImage> loadDocumentAllImageInfo();
}
