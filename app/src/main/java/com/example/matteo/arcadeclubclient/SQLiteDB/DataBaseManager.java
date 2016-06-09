package com.example.matteo.arcadeclubclient.SQLiteDB;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by matteo on 27/05/16.
 */


public class DataBaseManager {

    private static final String LOG_TAG = DataBaseManager.class.getSimpleName();

    private static Context context;
    private static SQLiteDatabase db;
    private static SQLiteHelperManager dbHelper;

    //nome tabelle

    //tabella giochi
    //public static final String TABLE_GIOCHI = "giochi";


    public static final String TABLE_GIOCHI = "giochi";
    public static final String TABLE_IMMAGINI = "immagini";
    public static final String TABLE_CODA = "coda";
    //tab giochi column name
    public static final String UPC = "UPC";
    public static final String NOME = "NOME";
    public static final String ANNO = "ANNO";
    public static final String CONSOLE = "CONSOLE";
    public static final String IMMAGINE = "IMMAGINE";


    public static final String ID = "ID";
    public static final String RICHIESTA = "RICHIESTA";
    public static final String ID_DEVICE = "ID_DEVICE";
    public static final String BITMAP = "BITMAP";


    //private static final String[] COLUMNS_GIOCHI = {UPC,NOME,ANNO,CONSOLE,IMMAGINE,BITMAP};

    public DataBaseManager(Context context1) {
        context = context1;
    }

    //aprire connesione DB
    private static void open() throws SQLException {
        Log.i("DB locale","open DB");
        dbHelper = new SQLiteHelperManager(context);
        db = dbHelper.getWritableDatabase();
        Log.i("DB locale","open DB" + dbHelper + db);
    }
    //chiudere connesione DB
    private static void close() {
        dbHelper.close();
    }


    //per tab website
    private static ContentValues createContentValuesGioco(JSONObject item) {
        ContentValues valuesGioco = new ContentValues();
        try {
            valuesGioco.put(UPC, String.valueOf(item.get("upc")));
            valuesGioco.put(NOME, String.valueOf(item.get("nome")));
            valuesGioco.put(ANNO, String.valueOf(item.get("anno")));
            valuesGioco.put(CONSOLE, String.valueOf(item.get("console")));
            valuesGioco.put(IMMAGINE, String.valueOf(item.get("immagine")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return valuesGioco;
    }

    private static ContentValues createContentValuesImmagini(JSONObject item) {
        ContentValues valuesGioco = new ContentValues();
        try {
            valuesGioco.put(UPC, String.valueOf(item.get("upc")));
            valuesGioco.put(IMMAGINE, String.valueOf(item.get("immagine")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return valuesGioco;
    }

    //METODI

    //add gioco row
    public static long addGioco(JSONObject item) {
        ContentValues values = createContentValuesGioco(item);
        open();
        long res=db.insertOrThrow(TABLE_GIOCHI, null, values);
        close();
        Log.i("DB_Manager",String.valueOf(res));
        return res;
    }

    //delete gioco row
    public static boolean delGiochi(String upc) {
        String[] upc_g={upc};
        open();
        boolean res = db.delete(TABLE_GIOCHI, UPC + "=?", upc_g) > 0;
        close();
        return res;
    }

    public static boolean delImmagini(String upc) {
        String[] upc_g={upc};
        open();
        boolean res = db.delete(TABLE_IMMAGINI, UPC + "=?", upc_g) > 0;
        close();
        return res;
    }


    //show giochi
    public static JSONObject getGioco(String COLUMN_NAME, String argument) {
        open();
        Cursor cursor = db.rawQuery("select * from giochi where " + COLUMN_NAME + " = ?", new String[] { argument });

        //Cursor cursor = db.rawQuery("select * from giochi where upc = ?", new String[] { "013388130290" });


        JSONObject query_result = new JSONObject();
        if(cursor.moveToFirst()) {

            //Log.i("DB",cursor.getColumnNames()[0].toString() + cursor.getColumnNames()[1].toString() + cursor.getColumnNames()[2].toString() + cursor.getColumnNames()[3].toString());

            String upc = cursor.getString(cursor.getColumnIndex(UPC));
            String nome = cursor.getString(cursor.getColumnIndex(NOME));
            String anno = cursor.getString(cursor.getColumnIndex(ANNO));
            String console = cursor.getString(cursor.getColumnIndex(CONSOLE));
            String immagine = cursor.getString(cursor.getColumnIndex(IMMAGINE));


            try {
                query_result.put("upc",upc);
                query_result.put("nome",nome);
                query_result.put("anno",anno);
                query_result.put("console",console);
                query_result.put("immagine",immagine);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        close();
        return query_result;
    }



    public static long addImage(String upc, String immagini) {
        Log.i("DB locale","aggiunta immagine");
        ContentValues values = new ContentValues();
        values.put(UPC, upc);
        values.put(IMMAGINE, immagini);
        open();
        long res=db.insertOrThrow(TABLE_IMMAGINI, null, values);
        close();
        Log.i("DB_Manager",String.valueOf(res));
        return res;
    }


    public static String getImage(String upc) {
        open();
        Cursor cursor = db.rawQuery("select * from immagini where UPC = ?", new String[] { upc });

        String Bitmap = "";
        if(cursor.moveToFirst()) {
            Bitmap = cursor.getString(cursor.getColumnIndex(IMMAGINE));
        }
        close();
        return Bitmap;
    }

    public static long addReq(String request, String id_device) {
        Log.i("DB locale","aggiunta richiesta");
        ContentValues values = new ContentValues();
        values.put(RICHIESTA, request);
        values.put(ID_DEVICE, id_device);
        open();
        long res=db.insertOrThrow(TABLE_CODA, null, values);
        close();
        Log.i("DB_Manager",String.valueOf(res));
        return res;
    }


    public static ArrayList getRequests() {
        open();
        Cursor cursor = db.rawQuery("select * from coda", new String[] {});

        ArrayList codaRichieste = new ArrayList();
        while (cursor.moveToNext()) {
            JSONObject json = new JSONObject();
            try {
                json.put("id",cursor.getString(cursor.getColumnIndex(ID)));
                json.put("request",cursor.getString(cursor.getColumnIndex(RICHIESTA)));
                json.put("id_device",cursor.getString(cursor.getColumnIndex(ID_DEVICE)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            codaRichieste.add(json);
        }
        close();
        return codaRichieste;
    }

    public static boolean deleteReq(int id) {
        String[] id_g={String.valueOf(id)};
        open();
        boolean res = db.delete(TABLE_CODA, ID + "=?", id_g) > 0;
        close();
        return res;
    }



}

