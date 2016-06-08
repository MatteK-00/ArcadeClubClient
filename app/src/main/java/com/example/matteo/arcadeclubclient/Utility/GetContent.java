package com.example.matteo.arcadeclubclient.Utility;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.matteo.arcadeclubclient.SQLiteDB.DataBaseManager;
import com.example.matteo.arcadeclubclient.Utility.GetProperties;
import com.example.matteo.arcadeclubclient.Utility.RestClient;
import com.example.matteo.arcadeclubclient.Utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by matteo on 31/05/16.
 */
public class GetContent {

    public static String setRequest(Context context, JSONObject request, String upc){
        String output = "ERRORE";

        DataBaseManager db = new DataBaseManager(context);
        JSONObject result = new JSONObject();

        //db.delGiochi(1);
        //db.delImmagini("");
        try {
            result = db.getGioco("UPC", upc);
        } catch (SQLiteException e){
            e.printStackTrace();
        }

        if (result.isNull("upc")){
            if (Utility.isOnline(context)) {
                output = RestClient.getIstance(context).executeGetRequest(request);
                try {
                    JSONObject boh = new JSONObject(output);
                    JSONObject info = boh.getJSONObject("info");
                    db.addGioco(info);
                    if (Boolean.valueOf(GetProperties.getIstance(context).getProp("image"))) {
                        JSONObject image_request = new JSONObject();
                        image_request.put("table", "immagine");
                        image_request.put("query", String.valueOf(info.get("id_gioco")) + "_"
                                + String.valueOf(info.get("immagine")));

                        String immg = RestClient.executeGetRequest(image_request);


                        if (db.getImage(String.valueOf(info.get("upc"))).equals(""))
                            db.addImage(String.valueOf(info.get("upc")),immg);
                        //Utility.saveImage(info.get("id_gioco").toString() + "_" + info.get("immagine").toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "Nessuna connessione e internet ne contenuto locale", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (Utility.isOnline(context)) {
                output = RestClient.getIstance(context).executeGetRequest(request);
            } else {
                try {
                    JSONObject temp = new JSONObject();
                    temp.put("info",result.toString());
                    temp.put("item_list","[]");
                    output = temp.toString();
                    Toast.makeText(context, "Attenzione stai visualizzando solo contenuti locali, " +
                            "nessuna connessione internet o server non raggiungibile", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return output;
    }

    public static void saveImage(Context context, String upc, String image){
        DataBaseManager db = new DataBaseManager(context);
        db.addImage(upc,image);
    }

    public static Bitmap getImage(Context context, JSONObject request, String upc){
        DataBaseManager db = new DataBaseManager(context);
        String result = db.getImage(upc);
        if (result.equals("")){
            result = RestClient.executeGetRequest(request);
        }
        Log.i("res DB", result);
        byte[] decodedString = Base64.decode(result, Base64.DEFAULT);
        Bitmap ret = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return ret;
    }

}
