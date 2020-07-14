package com.example.scanin.DatabaseModule;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface DocAndFirstImageDao {
    @Transaction
    @Query("SELECT * FROM document WHERE documentId = :id")
    DocumentAndImageInfo loadDocumentAllImageInfo(long id);
}
