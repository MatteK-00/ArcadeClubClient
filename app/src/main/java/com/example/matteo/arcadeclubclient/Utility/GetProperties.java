package com.example.matteo.arcadeclubclient.Utility;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Properties;

/**
 * Created by matteo on 28/12/15.
 */


public class GetProperties{

    static Properties properties = new Properties();
    static InputStream is = null;
    static FileOutputStream fos = null;
    static String path;
    static Context context;


        private static GetProperties istance = null; //riferimento all' istanza

    private GetProperties(Context context){   //costruttore

            //path = context.getResources().openRawResource(R.raw.arcadeclub);
            path = "arcadeclub.properties";
            this.context = context;

            try {
                File f = new File(path);
                is = new FileInputStream(new File(context.getFilesDir()+File.separator+"arcadeclub.properties"));
                //is = context.getResources().openRawResource(R.raw.arcadeclub);
                properties.load(is);

            } catch (FileNotFoundException e) {
                Log.i("GetProperties", "file non trovato");
                Calendar c = Calendar.getInstance();
                int date = c.get(Calendar.SECOND);

                Log.i("GetProperties", String.valueOf(date));


                String android_id = Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);

                properties.setProperty("userName", "");
                properties.setProperty("idDevice", android_id);
                properties.setProperty("logged", "false");
                properties.setProperty("lastLogin", String.valueOf(date));
                properties.setProperty("image", "false");
                properties.setProperty("localDB", "true");

                try {
                    fos =(new FileOutputStream(new File(context.getFilesDir()+File.separator+"arcadeclub.properties")));
                    properties.store(fos, "Modificato");
                } catch (FileNotFoundException e2) {
                    Log.i("GetProperties", "creazione nuove properties: file non trovato");

                } catch (IOException e2) {
                    Log.i("GetProperties", "creazione nuove properties: IO Exception");
                }

            } catch (IOException e) {
                Log.i("GetProperties", "IO Exception");
            }
        }

        public static GetProperties getIstance(Context context) {
            if(istance==null)
                istance = new GetProperties(context);
            return istance;
        }

        public static String getProp(String campo) {
            return properties.getProperty(campo);
        }

        public static void setProp(String campo,String value) {
            properties.setProperty(campo,value);
        }

        public static void save(){
            try {
                fos =(new FileOutputStream(new File(context.getFilesDir()+File.separator+"arcadeclub.properties")));
                properties.store(fos, "Modificato");
            } catch (FileNotFoundException e) {
                Log.i("GetProperties", (e.toString()));
            } catch (IOException e) {
                Log.i("GetProperties", (e.toString()));
            }

        }

    }

