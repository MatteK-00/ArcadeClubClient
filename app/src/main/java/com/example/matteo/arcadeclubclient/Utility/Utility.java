package com.example.matteo.arcadeclubclient.Utility;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.example.matteo.arcadeclubclient.MainActivity;
import com.example.matteo.arcadeclubclient.R;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Calendar;

/**
 * Created by matteo on 20/05/16.
 */
public class Utility {

    public static boolean serverReachable(){
        boolean exists = false;
        final String ip = "188.166.49.29";
        final int port = 10101;
        try {
            SocketAddress sockaddr = new InetSocketAddress(ip, port);
            // Create an unbound socket
            Socket sock = new Socket();

            // This method will block no more than timeoutMs.
            // If the timeout occurs, SocketTimeoutException is thrown.
            int timeoutMs = 2000;   // 2 seconds
            sock.connect(sockaddr, timeoutMs);
            exists = true;
        }catch(Exception e){
        }
        return exists;
    }

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

    public static void sendNotification(Context context, int comprati, int venduti){
        Intent notificationIntent = new Intent(context,
                MainActivity.class);



        notificationIntent.putExtra("clicked", "Notification Clicked");
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP); // To open only one activity

        // Invoking the default notification service
        NotificationManager mNotificationManager;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context);
        Uri uri = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setContentTitle("ArcadeClub");
        mBuilder.setContentText("La modifica al database remoto è avvenuta con successo!");
        mBuilder.setTicker("ArcadeClub Alert!");
        mBuilder.setSmallIcon(R.drawable.ic_menu_camera);
        mBuilder.setSound(uri);
        mBuilder.setAutoCancel(true);

        // Add Big View Specific Configuration
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        String[] events = new String[2];

        events[0] = new String("Hai aggiunto al magazzino " +String.valueOf(comprati) + " oggetti");
        events[1] = new String("Hai venduto " +String.valueOf(venduti) + " oggetti");



        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle("Cambiamenti recenti:");

        // Moves events into the big view
        for (int i = 0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }

        mBuilder.setStyle(inboxStyle);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context,
                MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder
                .create(context);
        stackBuilder.addParentStack(MainActivity.class);


        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);


        // notificationID allows you to update the notification later  on.
        mNotificationManager.notify(999, mBuilder.build());
    }

}
