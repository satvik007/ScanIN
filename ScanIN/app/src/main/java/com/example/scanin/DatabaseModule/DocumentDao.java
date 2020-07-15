package com.example.scanin.DatabaseModule;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

@Dao
public interface DocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertDocument(Document document);

    @Update
    public void updateDocument(Document document);

    @Delete
    public void deleteDocument(Document document);
}
