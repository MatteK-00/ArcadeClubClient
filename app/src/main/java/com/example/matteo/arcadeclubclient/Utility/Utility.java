package com.example.matteo.arcadeclubclient.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Calendar;

/**
 * Created by matteo on 20/05/16.
 */
public class Utility {

    public static String getDate(){
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        String data = String.valueOf(year) + "-"
                + String.valueOf(month) + "-"
                + String.valueOf(day);

        return data;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void saveImage(String filename){
        FileOutputStream out = null;
        Bitmap bmp = null;
        try {
            out = new FileOutputStream(filename);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap loadImage(String filename) {
        FileInputStream in = null;
        Bitmap bmp = null;

        File image = new File(filename);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmp = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

        return bmp;
    }
}
