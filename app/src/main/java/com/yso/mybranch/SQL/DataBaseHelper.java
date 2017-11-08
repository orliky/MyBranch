package com.yso.mybranch.SQL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Admin on 19-Oct-17.
 */

public class DataBaseHelper extends SQLiteOpenHelper
{

    private static final String DATABASE_NAME = "mydbnamedb";
    private static final int DATABASE_VERSION = 1;

    public static final String CARDS_TABLE = "tbl_cards";
    public static final String POICATEGORIES_TABLE = "tbl_poicategories";
    public static final String POILANGS_TABLE = "tbl_poilangs";

    public static final String ID_COLUMN = "id";

    public static final String POI_ID = "poi_id";
    public static final String POICATEGORIES_COLUMN = "poi_categories";

    public static final String POILANGS_COLUMN = "poi_langs";

    public static final String CARDS = "cards";
    public static final String CARD_ID = "card_id";
    public static final String CARDS_PCAT_ID = "pcat_id";

    public static final String CREATE_PLANG_TABLE = "CREATE TABLE "
            + POILANGS_TABLE + "(" + ID_COLUMN + " INTEGER PRIMARY KEY,"
            + POILANGS_COLUMN + " TEXT, " + POI_ID + " TEXT)";

    public static final String CREATE_PCAT_TABLE = "CREATE TABLE "
            + POICATEGORIES_TABLE + "(" + ID_COLUMN + " INTEGER PRIMARY KEY,"
            + POICATEGORIES_COLUMN + " TEXT, " + POI_ID + " TEXT)";

    public static final String CREATE_CARDS_TABLE = "CREATE TABLE "
            + CARDS_TABLE + "(" + ID_COLUMN + " INTEGER PRIMARY KEY," + CARD_ID
            + " TEXT, " + CARDS_PCAT_ID + " TEXT, " + CARDS + " TEXT)";

    private static DataBaseHelper instance;

    public static synchronized DataBaseHelper getHelper(Context context) {
        if (instance == null)
            instance = new DataBaseHelper(context);
        return instance;
    }

    private DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            // db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PCAT_TABLE);
        db.execSQL(CREATE_PLANG_TABLE);
        db.execSQL(CREATE_CARDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}