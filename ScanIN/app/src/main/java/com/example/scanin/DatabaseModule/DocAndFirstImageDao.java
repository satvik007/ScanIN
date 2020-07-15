package com.example.scanin.DatabaseModule;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface DocAndFirstImageDao {
    @Transaction
    @Query("SELECT * FROM document")
    List<DocumentsAndFirstImage> loadDocumentAllImageInfo();
}
