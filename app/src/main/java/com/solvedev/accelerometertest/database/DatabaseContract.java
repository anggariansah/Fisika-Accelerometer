package com.solvedev.accelerometertest.database;

import android.provider.BaseColumns;

public class DatabaseContract {

    static String TABLE_DATA = "table_data";

    static final class NoteColumns implements BaseColumns {
        static String TITLE = "title";
        static String TIME = "time";
        static String PERCEPATAN = "percepatan";
        static String KECEPATAN = "kecepatan";
        static String POSISI = "posisi";
    }

}
