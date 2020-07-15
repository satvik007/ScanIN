package com.example.scanin.DatabaseModule;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface DocumentAndImageDao {
    @Transaction
    @Query("SELECT * FROM document WHERE document_id = :id")
    DocumentAndImageInfo loadDocumentAllImageInfo(long id);
}
