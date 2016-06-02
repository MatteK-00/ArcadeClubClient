package com.example.matteo.arcadeclubclient;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.matteo.arcadeclubclient.Utility.GetProperties;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button button = (Button) findViewById(R.id.buttonLogin);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText userEdit = (EditText) findViewById(R.id.UsrLogText);
                EditText pswEdit = (EditText) findViewById(R.id.PassLogEeditText);
                CheckBox checkSave = (CheckBox) findViewById(R.id.LogCheckBox);

                if (userEdit.getText().toString().length() != 0 && pswEdit.getText().toString().length() != 0) {
                    Log.i("LogActivity", "User e Psw non vuoti");
                    String[] param = {"http://188.166.49.29:10101/arcadeclub/", userEdit.getText().toString(),pswEdit.getText().toString(),Boolean.toString(checkSave.isChecked())};
                    new CallAPI().execute(param);

                }

                InputMethodManager input = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                input.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("LogActivity", "On Start .....");

    }


    protected void onFinish() {
        super.finish();
        Log.i("LogActivity", "On Start .....");

    }

    private class CallAPI extends AsyncTask<String, String, String> {


        private boolean login = false;
        @Override
        protected String doInBackground(String... params) {
            Log.i("LogActivity", "AsyncTask!");

            String output = "";


            try {

                URL url = new URL(params[0]+params[1]+"/"+params[2]+"/");
                Log.i("LogActivity", url.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));


                Log.i("LogActivity", "Output from Server .... ");
                while ((output = br.readLine()) != null) {
                    Log.i("LogActivity", (String) output.subSequence(1,(output.length()-1)));
                    GetProperties prop = GetProperties.getIstance(getApplicationContext());
                    if (prop.getProp("idDevice").equals(output.subSequence(1,(output.length()-1)))){
                        Log.i("GetProperties", prop.getProp("idDevice"));
                        if (params[3].equals("true")){
                            prop.setProp("logged", "true");
                            Log.i("LogActivity", "checkSave cliccato");
                            prop.save();
                        }
                        login = true;
                        LogActivity.this.finish();
                    }
                }
                conn.disconnect();

            } catch (MalformedURLException e) {
                Log.i("LogActivity", (e.getMessage()));

            } catch (IOException e) {
                Log.i("LogActivity", (e.getMessage()));
            }



            return output;
        }



        protected void onPostExecute(String result) {
            if (!login){
                Log.i("LogActivity", "password sbagliata");
                Toast.makeText(getApplicationContext(), "Password o Username errati",
                        Toast.LENGTH_LONG).show();
            }

        }

    }





}
