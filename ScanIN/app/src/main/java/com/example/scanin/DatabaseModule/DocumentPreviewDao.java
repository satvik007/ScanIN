package com.example.scanin.DatabaseModule;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface DocumentPreviewDao {
    @Transaction
    @Query("SELECT * FROM document")
    LiveData<List<DocumentPreview>> loadAllDocumentPreview();
}
