package com.example.matteo.arcadeclubclient.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.example.matteo.arcadeclubclient.SQLiteDB.DataBaseManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by matteo on 28/05/16.
 */
public class ImageDownloader extends  AsyncTask<String,Void,Bitmap> {


    Bitmap image = null;

    @Override
    protected Bitmap doInBackground(String... params) {
        JSONObject request = new JSONObject();
        try {
            /*TODO: riscrivere, il download a questo punto devo farlo mentre scarico
            nell'altro AsyncTask questo dovrà solo caricare le immagini e convertirle*/
            request.put("table","immagine");
            request.put("query",params[0]);
            String encoded_image = RestClient.executeGetRequest(request);
            Log.i("PROVA",encoded_image);
            byte[] decodedString = Base64.decode(encoded_image, Base64.DEFAULT);
            image = BitmapFactory.decodeByteArray(decodedString, 0,decodedString.length);

            return image;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


}
