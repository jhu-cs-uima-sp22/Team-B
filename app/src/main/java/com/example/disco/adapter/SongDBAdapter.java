package com.example.disco.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SongDBAdapter extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "LikedSongs.db";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE LikedSongs ( " +
            "songName VARCHAR(256) NOT NULL," +
            "songArtist VARCHAR(256) NOT NULL," +
            "songAlbum VARCHAR(256)," +
            "songURL VARCHAR(256) NOT NULL UNIQUE," +
            "songAlbumURI VARCHAR(256) NOT NULL );";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS LikedSongs";

    public SongDBAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}


