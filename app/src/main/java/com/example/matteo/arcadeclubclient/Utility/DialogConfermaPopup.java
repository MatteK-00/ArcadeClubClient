package com.example.matteo.arcadeclubclient.Utility;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matteo.arcadeclubclient.MagazzinoSearchFragment;
import com.example.matteo.arcadeclubclient.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by matteo on 13/05/16.
 */
public class DialogConfermaPopup{
    private Dialog dialog;
    private Context context;

    public DialogConfermaPopup(Context context){
        this.context = context;
        }

    public void vendi_item(final JSONObject item){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_conferma_vendita);
        Button vendi_button = (Button) dialog.findViewById(R.id.dialog_conferma_button);
        try {
            dialog.setTitle(item.get("nome").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        vendi_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView prezzoText = (TextView) dialog.findViewById(R.id.dialog_prezzo);

                try {
                    float prezzo = Float.parseFloat(String.valueOf(prezzoText.getText().toString()));
                    if (prezzo > 0){
                        Toast.makeText(context, prezzo + " Mo ti tocca fare la put sul db", Toast.LENGTH_SHORT).show();
                        Calendar c = Calendar.getInstance();
                        int day = c.get(Calendar.DAY_OF_MONTH);
                        int month = c.get(Calendar.MONTH);
                        int year = c.get(Calendar.YEAR);


                        String query = "id_item=" + item.get("id_item").toString()
                                + "&prezzo=" + prezzo
                                + "&data=" + String.valueOf(year) + "-"
                                + String.valueOf(month) + "-"
                                + String.valueOf(day);
                        //JSONObject json_query = new JSONObject(query);
                        JSONObject request = new JSONObject();
                        request.put("method","POST");
                        request.put("table","venduti");
                        request.put("query",query);
                        RestClient RestClient = com.example.matteo.arcadeclubclient.Utility.RestClient.getIstance(context);
                        RestClient.addRequest(request);
                    }else {
                        Toast.makeText(context, prezzo + " non è un valore accettabile", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e){
                    Toast.makeText(context, String.valueOf(prezzoText.getText().toString()) +" non è un valore accettabile", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
    }

}
