package com.solvedev.accelerometertest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.solvedev.accelerometertest.model.Data;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.solvedev.accelerometertest.database.DatabaseContract.NoteColumns.KECEPATAN;
import static com.solvedev.accelerometertest.database.DatabaseContract.NoteColumns.PERCEPATAN;
import static com.solvedev.accelerometertest.database.DatabaseContract.NoteColumns.POSISI;
import static com.solvedev.accelerometertest.database.DatabaseContract.NoteColumns.TIME;
import static com.solvedev.accelerometertest.database.DatabaseContract.NoteColumns.TITLE;
import static com.solvedev.accelerometertest.database.DatabaseContract.TABLE_DATA;

public class DMLHelper {

    // Pendefinisian variabel string yang menampung nama tabel
    private static final String DATABASE_TABLE = TABLE_DATA;

    // Pendefinisian variabel
    private static DatabaseHelper dataBaseHelper;
    private static DMLHelper INSTANCE;
    private static SQLiteDatabase database;

    private DMLHelper(Context context) {
        dataBaseHelper = new DatabaseHelper(context);
    }

    // untuk memastikan bahwa hanya ada satu objek yang terpanggil
    public static DMLHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DMLHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    // untuk membuka koneksi database
    public void open() throws SQLException {
        database = dataBaseHelper.getWritableDatabase();
    }

    // untuk menutup koneksi database
    public void close() {
        dataBaseHelper.close();
        if (database.isOpen())
            database.close();
    }

    // fungsi untuk mengambil data dari database
    public ArrayList<Data> getAllData() {
        ArrayList<Data> arrayList = new ArrayList<>();
        Cursor cursor = database.query(DATABASE_TABLE, null,
                null,
                null,
                null,
                null,
                _ID + " DESC",
                null);
        cursor.moveToFirst();
        Data data;
        if (cursor.getCount() > 0) {
            do {
                data = new Data();
                data.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                data.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                data.setTime(cursor.getString(cursor.getColumnIndexOrThrow(TIME)));
                data.setPercepatan(cursor.getString(cursor.getColumnIndexOrThrow(PERCEPATAN)));
                data.setKecepatan(cursor.getString(cursor.getColumnIndexOrThrow(KECEPATAN)));
                data.setPosisi(cursor.getString(cursor.getColumnIndexOrThrow(POSISI)));
                arrayList.add(data);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }

    // fungsi untuk memasukkan data ke database
    public long insertData(Data data) {
        ContentValues args = new ContentValues();
        // method untuk menaruh data sesuai dengan nama kolom dan datanya
        args.put(TITLE, data.getTitle());
        args.put(TIME, data.getTime());
        args.put(PERCEPATAN, data.getPercepatan());
        args.put(KECEPATAN, data.getKecepatan());
        args.put(POSISI, data.getPosisi());
        return database.insert(DATABASE_TABLE, null, args);
    }

}
