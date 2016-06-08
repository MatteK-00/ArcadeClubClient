package com.example.matteo.arcadeclubclient;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matteo.arcadeclubclient.Utility.Utility;
import com.example.matteo.arcadeclubclient.Utility.RestClient;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by matteo on 20/05/16.
 */
public class InserisciNuovoFragment extends ListFragment {

    View view;
    TextView nuovo_nome = null;
    TextView nuovo_upc;
    TextView nuovo_console;
    TextView nuovo_anno;
    final String[] comboBox_nuovo_stato = new String[1];
    final String[] comboBox_nuovo_quality = new String[1];
    TextView nuovo_prezzo_acquisto;
    TextView nuovo_data_acquisto;
    TextView nuovo_note;

    @Override
    public Context getContext() {
        return super.getContext();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("Aggiungi_Fragment", "onCreateView");
        view = inflater.inflate(R.layout.fragment_inserisci_nuovo, container, false);

        final Spinner spinner_stato = (Spinner) view.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter_stato = ArrayAdapter.createFromResource(getContext(),
                R.array.spinner1_field, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter_stato.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_stato.setAdapter(adapter_stato);

        spinner_stato.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                comboBox_nuovo_stato[0] = parent.getItemAtPosition(position).toString();
                spinner_stato.setBackgroundColor(Color.WHITE);
                Log.i("Magazzino_Fragment", comboBox_nuovo_stato[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Spinner spinner_quality = (Spinner) view.findViewById(R.id.spinner2);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter_quality = ArrayAdapter.createFromResource(getContext(),
                R.array.spinner2_field, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter_quality.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_quality.setAdapter(adapter_quality);

        spinner_quality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                comboBox_nuovo_quality[0] = parent.getItemAtPosition(position).toString();
                spinner_quality.setBackgroundColor(Color.WHITE);
                Log.i("Magazzino_Fragment", comboBox_nuovo_quality[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        nuovo_nome = (TextView) view.findViewById(R.id.nuovo_nome);
        nuovo_upc = (TextView) view.findViewById(R.id.nuovo_upc);
        nuovo_console = (TextView) view.findViewById(R.id.nuovo_console);
        nuovo_anno = (TextView) view.findViewById(R.id.nuovo_anno);
        nuovo_prezzo_acquisto = (TextView) view.findViewById(R.id.nuovo_prezzo_acquisto);
        nuovo_data_acquisto = (TextView) view.findViewById(R.id.nuovo_data_acquisto);
        nuovo_note = (TextView) view.findViewById(R.id.nuovo_note);

        Bundle bundle = this.getArguments();
        if(bundle!=null) {
            String item = bundle.getString("item");
            Log.i("Aggiungi_Fragment", item);
            try {
                JSONObject item_JSON = new JSONObject(item);
                nuovo_nome.setText(item_JSON.get("nome").toString());
                nuovo_upc.setText(item_JSON.get("upc").toString());
                nuovo_console.setText(item_JSON.get("console").toString());
                nuovo_anno.setText(item_JSON.get("anno").toString());
                nuovo_data_acquisto.setText(Utility.getDate());
                nuovo_prezzo_acquisto.setHintTextColor(Color.RED);
                String nuovo_stato = item_JSON.get("stato").toString();
                if (!nuovo_stato.equals(null)) {
                    int spinnerPosition = adapter_stato.getPosition(nuovo_stato);
                    spinner_stato.setSelection(spinnerPosition);
                }
                int nuovo_quality = Integer.valueOf(item_JSON.get("quality").toString());
                spinner_quality.setSelection(nuovo_quality);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        Button button_aggiungi = (Button) view.findViewById(R.id.nuovo_button);
        button_aggiungi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject request = new JSONObject();
                try {
                    String query = "";
                    boolean ok = true;
                    if (nuovo_nome.getText().toString().equals("")){
                        ok = false;
                        nuovo_nome.setHintTextColor(Color.RED);
                    } else {query+="nome=" + nuovo_nome.getText() + "&";}
                    if (nuovo_upc.getText().toString().equals("")){
                        ok = false;
                        nuovo_upc.setHintTextColor(Color.RED);
                    } else {query+="upc=" + nuovo_upc.getText() + "&";}
                    if (nuovo_console.getText().toString().equals("")){
                        ok = false;
                        nuovo_console.setHintTextColor(Color.RED);
                    } else {query+="console=" + nuovo_console.getText() + "&";}
                    if (nuovo_anno.getText().toString().equals("")){
                        ok = false;
                        nuovo_anno.setHintTextColor(Color.RED);
                    } else {query+="anno=" + nuovo_anno.getText() + "&";}
                    if (comboBox_nuovo_stato[0].equals("All")){
                        ok = false;
                        spinner_stato.setBackgroundColor(Color.RED);
                    } else {query+="stato=" + comboBox_nuovo_stato[0] + "&";}
                    if (comboBox_nuovo_quality[0].equals("All")){
                        ok = false;
                        spinner_quality.setBackgroundColor(Color.RED);
                    } else {query+="quality=" + comboBox_nuovo_quality[0] + "&";}
                    if (nuovo_prezzo_acquisto.getText().toString().equals("")){
                        ok = false;
                        nuovo_prezzo_acquisto.setHintTextColor(Color.RED);
                    } else {query+="prezzo_acquisto=" + nuovo_prezzo_acquisto.getText() + "&";}
                    if (nuovo_data_acquisto.getText().toString().equals("")){
                        ok = false;
                        nuovo_data_acquisto.setHintTextColor(Color.RED);
                    } else {query+="data_acquisto=" + nuovo_data_acquisto.getText();}

                    Log.i("Aggiungi_Fragment", "query = " + query);

                    if (ok) {
                        ProgressDialog progressBar;
                        progressBar = new ProgressDialog(getContext());
                        progressBar.setCancelable(false);
                        progressBar.setMessage("Invio in corso ...");
                        progressBar.show();

                        request.put("method", "POST");
                        request.put("table", "magazzino");
                        request.put("query", query);
                        RestClient RestClient = com.example.matteo.arcadeclubclient.Utility.RestClient.getIstance(getContext());
                        RestClient.addRequest(request);

                        Thread.sleep(2000);

                        progressBar.dismiss();


                    }else {
                        Toast.makeText(getContext(), "Alcuni campi sono vuoti!",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });


        return view;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
