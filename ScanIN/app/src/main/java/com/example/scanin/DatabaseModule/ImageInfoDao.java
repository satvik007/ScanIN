package com.example.scanin.DatabaseModule;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

@Dao
public interface ImageInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertImageInfo(ImageInfo imageInfo);

    @Update
    public void updateImageInfo(ImageInfo imageInfo);

    @Delete
    public void deleteImageInfo(ImageInfo imageInfo);
}
