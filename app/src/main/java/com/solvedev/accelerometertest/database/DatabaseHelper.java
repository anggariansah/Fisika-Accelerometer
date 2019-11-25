package com.solvedev.accelerometertest.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // nama database
    public static String DATABASE_NAME = "dbfisika";

    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TABLE = String.format("CREATE TABLE %s"
                    + " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT," +
                    " %s TEXT," +
                    " %$ TEXT)",
            DatabaseContract.TABLE_DATA,
            DatabaseContract.NoteColumns._ID,
            DatabaseContract.NoteColumns.TITLE,
            DatabaseContract.NoteColumns.TIME,
            DatabaseContract.NoteColumns.PERCEPATAN,
            DatabaseContract.NoteColumns.KECEPATAN,
            DatabaseContract.NoteColumns.POSISI
    );

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // menjalankan method untuk membuat table database
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_DATA);
        onCreate(db);
    }

}
