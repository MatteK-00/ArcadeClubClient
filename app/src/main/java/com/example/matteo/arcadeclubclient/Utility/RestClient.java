package com.example.matteo.arcadeclubclient.Utility;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.matteo.arcadeclubclient.SQLiteDB.DataBaseManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by matteo on 22/12/15.
 */


public class RestClient {

    static String url = "http://188.166.49.29:10101/arcadeclub/";
    static Context context;
    static int venduti = 0;
    static int comprati = 0;

    private static RestClient istance = null; //riferimento all' istanza

    public RestClient(Context context){   //costruttore
        this.context = context;
    }

    public static RestClient getIstance(Context context) {
        if (istance == null)
            istance = new RestClient(context);
        return istance;
    }

    public static void addRequest(JSONObject Element) throws JSONException {
        if (Element.get("method").toString() == "GET"){

            //return (executeGetRequest(Element));

        } else if (Element.get("method").toString() == "POST") {
            Log.i("RestClient_Service", "Prima di lanciarlo");

            DataBaseManager db = new DataBaseManager(context);
            long prova = db.addReq(Element.toString(),GetProperties.getIstance(context).getProp("idDevice"));

            Log.i("RestClient_Service", "isMyServiceRunning = "+ isMyServiceRunning(QueueService.class, context));
            if (!isMyServiceRunning(QueueService.class, context)) {
                Intent intent = new Intent(context, QueueService.class);
                context.startService(intent);
            }

            Log.i("RestClient_Service", "Dopo il lancio");


        }
    }


    public static String executeGetRequest(JSONObject query) {
        try {
            GetProperties prop = GetProperties.getIstance(context);
            URL url_request = new URL(url + prop.getProp("idDevice") + "/" +query.get("table").toString() +
                    "/"+query.get("table").toString()+"?"+query.get("query").toString());
            Log.i("RestClient_Service", url_request.toString());
            HttpURLConnection conn = (HttpURLConnection) url_request.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");


            if (conn.getResponseCode() != 200) {
                if (conn.getResponseCode() == 404) {
                    Toast.makeText(context, "Server Off-line o immagine non in archivio",
                            Toast.LENGTH_LONG).show();
                }
                Log.i("Errore http","Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            Log.i("executeGetRequest", "Output from Server .... ");
            while ((output = br.readLine()) != null) {
                Log.i("executeGetRequest", output);

                return output;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "ERRORE";
    }









    private static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }



    public static class QueueService extends IntentService {
        private ArrayList coda_locale;
        private ArrayList richieste_processate;

        /**
         * A constructor is required, and must call the super IntentService(String)
         * constructor with a name for the worker thread.
         */
        public QueueService() {
            super("HelloIntentService");
        }

        /**
         * The IntentService calls this method from the default worker thread with
         * the intent that started the service. When this method returns, IntentService
         * stops the service, as appropriate.
         */

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

            richieste_processate = new ArrayList();
            coda_locale = new ArrayList();

            DataBaseManager db = new DataBaseManager(this);
            coda_locale = db.getRequests();

            //se il servixio muore viene resuscitato
            setIntentRedelivery(true);
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            Log.i("RestClient_Service", "handleMessage");
            if (!Utility.serverReachable()) {
                try {
                    Log.i("RestClient_Service", "Nessuna connessione internet o server non raggiungibile");
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    // Restore interrupt status.
                    Log.i("RestClient_Service", "Errore nello sleep");
                    Thread.currentThread().interrupt();
                }
            } else {
                while (!coda_locale.isEmpty()) {
                    try {
                        JSONObject json_request = new JSONObject(coda_locale.remove(0).toString());
                        JSONObject query = new JSONObject(json_request.get("request").toString());
                        URL url_request = new URL(url + json_request.get("id_device").toString() + "/" + query.get("table").toString());
                        Log.i("RestClient_Service", url_request.toString());
                        HttpURLConnection conn = (HttpURLConnection) url_request.openConnection();
                        conn.setDoOutput(true);
                        conn.setRequestMethod(query.get("method").toString());
                        conn.setRequestProperty("Accept", "application/json");

                        OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                        out.write(query.get("query").toString());
                        out.close();

                        if (conn.getResponseCode() != 200) {
                            coda_locale.add(json_request.toString());
                            Thread.sleep(5000);
                            Log.i("RestClient_Service", "Failed : HTTP error code : " + conn.getResponseCode());
                            //throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                        } else {
                            richieste_processate.add(json_request.get("id"));
                            if (query.get("table").equals("magazzino"))
                                comprati++;
                            else
                                venduti++;
                        }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // Stop the service using the startId, so that we don't stop
                // the service in the middle of handling another job
                stopSelf();
            }
        }
        @Override
        public void onDestroy() {

            DataBaseManager db = new DataBaseManager(this);
            while (!richieste_processate.isEmpty()) {
                int id = Integer.parseInt(richieste_processate.remove(0).toString());
                db.deleteReq(id);
            }

            Log.i("RestClient_Service","onDestroy");
            Utility.sendNotification(this,comprati,venduti);
            comprati = 0;
            venduti = 0;
            Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        }

    }

}
