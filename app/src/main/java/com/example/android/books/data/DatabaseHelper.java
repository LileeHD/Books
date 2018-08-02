package com.example.android.books.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.books.data.BookContract.BookEntry;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "inventory.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String SQL_BOOK_TABLE = "CREATE TABLE "
                + BookEntry.TABLE_NAME + " ("
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.BOOK_NAME + " TEXT NOT NULL, "
                + BookEntry.BOOK_PRICE + " INTEGER NOT NULL, "
                + BookEntry.BOOK_QUANTITY + " INTEGER NOT NULL, "
                + BookEntry.BOOK_SUPPLIER_NAME + " TEXT,"
                + BookEntry.BOOK_SUPPLIER_PHONE + " TEXT NOT NULL );";

        // Execute the SQL statement
        sqLiteDatabase.execSQL(SQL_BOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
