package com.example.matteo.arcadeclubclient.SQLiteDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by matteo on 27/05/16.
 */


public class SQLiteHelperManager extends SQLiteOpenHelper{
    //versione db
    private static final int DATABASE_VERSION = 1;
    //nome db
    private static final String DATABASE_NAME = "arcadeclubLocalDB";

    public SQLiteHelperManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //creazione tabelle
        String CREATE_GIOCHI_TABLE = "CREATE TABLE giochi (" +
                "UPC TEXT PRIMARY KEY, " +
                "NOME TEXT, " +
                "ANNO TEXT, " +
                "CONSOLE TEXT," +
                "IMMAGINE TEXT )";

        String CREATE_GIOCHI_IMMAGINI = "CREATE TABLE immagini (" +
                "UPC TEXT PRIMARY KEY, " +
                "IMMAGINE TEXT )";

        db.execSQL(CREATE_GIOCHI_TABLE);
        db.execSQL(CREATE_GIOCHI_IMMAGINI);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS giochi");
        this.onCreate(db);
    }
}