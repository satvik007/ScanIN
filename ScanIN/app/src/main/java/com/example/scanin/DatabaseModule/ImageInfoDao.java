package com.example.scanin.DatabaseModule;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ImageInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertImageInfo(ImageInfo imageInfo);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> insertAllOrders(List<ImageInfo> imageInfos);

    @Update
    public void updateImageInfo(ImageInfo imageInfo);

    @Delete
    public void deleteImageInfo(ImageInfo imageInfo);

    @Query("SELECT * FROM image_info")
    public List<ImageInfo> getAllImageInfo();
}
