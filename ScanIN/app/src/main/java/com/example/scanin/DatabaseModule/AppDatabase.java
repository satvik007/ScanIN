package com.example.scanin.DatabaseModule;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Document.class, ImageInfo.class}, version = 1, exportSchema = false)
@TypeConverters({UriConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    static final String DATABASE_NAME = "documents-db";
    private static AppDatabase INSTANCE;

    public abstract DocumentDao documentDao();
    public abstract ImageInfoDao imageInfoDao();
    public abstract DocumentAndImageDao documentAndImageDao();
    public abstract DocumentPreviewDao docAndFirstImageDao();

    public static AppDatabase getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME).build();
        }
        return INSTANCE;
    }
}
