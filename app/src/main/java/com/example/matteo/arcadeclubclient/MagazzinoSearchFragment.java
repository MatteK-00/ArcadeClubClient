package com.example.matteo.arcadeclubclient;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.matteo.arcadeclubclient.Utility.DialogConfermaVendi;
import com.example.matteo.arcadeclubclient.Utility.GetContent;
import com.example.matteo.arcadeclubclient.Utility.GetProperties;
import com.example.matteo.arcadeclubclient.Utility.RestClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Created by matteo on 06/05/16.
 */
public class MagazzinoSearchFragment extends ListFragment implements AdapterView.OnItemClickListener{

    View view;
    ArrayList<magazzinoObj> ListViewContent = new ArrayList<>();


    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_magazzino_search, container, false);
        Button search_button = (Button) view.findViewById(R.id.button);
        final String[] comboBox_stato = new String[1];
        final String[] comboBox_quality = new String[1];


        Spinner spinner_stato = (Spinner) view.findViewById(R.id.spinner);
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
                comboBox_stato[0] = parent.getItemAtPosition(position).toString();
                Log.i("Magazzino_Fragment", comboBox_stato[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner spinner_quality = (Spinner) view.findViewById(R.id.spinner2);
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
                comboBox_quality[0] = parent.getItemAtPosition(position).toString();
                Log.i("Magazzino_Fragment", comboBox_quality[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText search_nome = (EditText) view.findViewById(R.id.magazzino_nome);
                EditText search_upc = (EditText) view.findViewById(R.id.magazzino_upc);
                EditText search_anno = (EditText) view.findViewById(R.id.magazzino_anno);
                EditText search_console = (EditText) view.findViewById(R.id.magazzino_console);
                CheckBox search_checkBox = (CheckBox) view.findViewById(R.id.magazzino_checkBox);

                String[] request = {
                        search_nome.getText().toString(),
                        search_upc.getText().toString(),
                        search_anno.getText().toString(),
                        search_console.getText().toString(),
                        comboBox_quality[0],
                        comboBox_stato[0],
                        Boolean.toString(search_checkBox.isChecked()),
                };

                ListViewContent.clear(); //svuoto l'array cache di Bitmap
                //DialogWait wait =
                //new DialogWait(getContext()).start();
                new CallServer().execute(request);

            }
        });


        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i("Magazzino_Fragment", "OnItemClick " + String.valueOf(position));
            itemDialogPopup((magazzinoObj) parent.getItemAtPosition(position), ListViewContent.get(position).getImage());
    }


    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("Magazzino_Fragment", "onViewCreated");
        getListView().setOnItemClickListener(this);
    }

    public class ListViewArrayAdapter extends ArrayAdapter<magazzinoObj> {
        private final Context context;
        private final ArrayList<magazzinoObj> values;

        public ListViewArrayAdapter(Context context, ArrayList<magazzinoObj> values) {
            super(context, R.layout.list_view_magazzino_row_layout, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_view_magazzino_row_layout, parent, false);

            TextView id_item = (TextView) rowView.findViewById(R.id.magazzino_id_item);
            TextView note = (TextView) rowView.findViewById(R.id.magazzino_note);
            TextView nome = (TextView) rowView.findViewById(R.id.magazzino_label_nome);
            TextView upc = (TextView) rowView.findViewById(R.id.magazzino_label_upc);
            TextView anno = (TextView) rowView.findViewById(R.id.magazzino_label_anno);
            TextView console = (TextView) rowView.findViewById(R.id.magazzino_label_console);
            TextView stato = (TextView) rowView.findViewById(R.id.magazzino_label_stato);
            TextView quality = (TextView) rowView.findViewById(R.id.magazzino_label_quality);
            TextView prezzo_acquisto = (TextView) rowView.findViewById(R.id.magazzino_label_prezzo_acquisto);
            TextView data_acquisto = (TextView) rowView.findViewById(R.id.magazzino_label_data_acquisto);


            if (values.get(position).venduto) {
                Log.i("Magazzino_Fragment", "Sto riempendo la listView con il venduto");
                TextView prezzo_venduto = (TextView) rowView.findViewById(R.id.magazzino_label_prezzo_venduto);
                TextView data_venduto = (TextView) rowView.findViewById(R.id.magazzino_label_data_venduto);
                prezzo_venduto.setText(values.get(position).prezzo_vendita);
                data_venduto.setText(values.get(position).data_vendita);

                rowView.setBackgroundColor(Color.parseColor("#9900CC"));

            } else {
                Log.i("Magazzino_Fragment", "Sto riempendo la listView con il magazzino");
                //rowView.setBackgroundColor(Color.GREEN);
            }

            id_item.setText(values.get(position).id_item);
            nome.setText(values.get(position).nome);
            upc.setText(values.get(position).upc);
            console.setText(values.get(position).console);
            anno.setText(values.get(position).anno);
            stato.setText(values.get(position).stato);
            quality.setText(values.get(position).quality);
            prezzo_acquisto.setText(values.get(position).prezzo_acquisto);
            data_acquisto.setText(values.get(position).data_acquisto);
            note.setText(values.get(position).note);

            if (Boolean.valueOf(GetProperties.getProp("image"))) {
                Log.i("Magazzino_Fragment", "Carico le immagini");
                ImageView imageView = (ImageView) rowView.findViewById(R.id.magazzino_immagine);
                imageView.setImageBitmap(values.get(position).getImage());
            }

            return rowView;
        }
    }

    public void itemDialogPopup(magazzinoObj item, Bitmap img) {
        final magazzinoObj itemOb = item;
        final Dialog dialog= new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_magazzino);

        ImageView img_dialog = (ImageView) dialog.findViewById(R.id.dialog_img);
        TextView dialog_console = (TextView) dialog.findViewById(R.id.dialog_console);
        TextView dialog_anno = (TextView) dialog.findViewById(R.id.dialog_anno);
        TextView dialog_upc = (TextView) dialog.findViewById(R.id.dialog_upc);
        TextView dialog_stato = (TextView) dialog.findViewById(R.id.dialog_stato);
        TextView dialog_quality = (TextView) dialog.findViewById(R.id.dialog_quality);
        TextView dialog_data_a = (TextView) dialog.findViewById(R.id.dialog_data_a);
        TextView dialog_prezzo_a = (TextView) dialog.findViewById(R.id.dialog_prezzo_a);
        TextView dialog_data_v = (TextView) dialog.findViewById(R.id.dialog_data_v);
        TextView dialog_prezzo_v = (TextView) dialog.findViewById(R.id.dialog_prezzo_v);
        Button vendi_button = (Button) dialog.findViewById(R.id.dialog_button1);
        Button aggiungi_button = (Button) dialog.findViewById(R.id.dialog_button2);
        TextView note = (TextView) dialog.findViewById(R.id.dialog_text_note);


        dialog_anno.setText(itemOb.anno);
        dialog_upc.setText(itemOb.upc);
        dialog_data_a.setText(itemOb.data_acquisto);
        dialog_prezzo_a.setText("Acquisto: " + itemOb.prezzo_acquisto);
        dialog_console.setText(itemOb.console);
        dialog_stato.setText("Condizioni: "+ itemOb.stato);
        dialog_quality.setText(itemOb.quality);

        if (itemOb.venduto){
            vendi_button.setEnabled(false);
            dialog_data_v.setText(itemOb.data_vendita);
            dialog_prezzo_v.setText("Vendita: " +itemOb.prezzo_vendita);
        }

        vendi_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogConfermaVendi vendiDialog = new DialogConfermaVendi(getContext());
                JSONObject item = new JSONObject();
                try {
                    item.put("nome",itemOb.nome);
                    item.put("id_item",itemOb.id_item);
                    vendiDialog.vendi_item(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        aggiungi_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Magazzino_Fragment", "Aggiungi al Magazzino!");

                Fragment newFragment = new InserisciNuovoFragment();
                Bundle bundle = new Bundle();

                try {
                    JSONObject item = new JSONObject();
                    item.put("nome",itemOb.nome);
                    item.put("upc",itemOb.upc);
                    item.put("console",itemOb.console);
                    item.put("stato",itemOb.stato);
                    item.put("anno",itemOb.anno);
                    item.put("console",itemOb.console);
                    item.put("note",itemOb.note);
                    item.put("quality",itemOb.quality);
                    bundle.putString("item", item.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                newFragment.setArguments(bundle);

                // consider using Java coding conventions (upper first char class names!!!)
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.flContent, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();

                dialog.dismiss();
            }
        });

            img_dialog.setImageBitmap(img);
            dialog.setTitle(itemOb.nome);
            String noteString = itemOb.note;
            if (!noteString.equals("null"))
                note.setText(itemOb.note);


        dialog.show();
    }

    private class CallServer extends AsyncTask<String, String, String> {
        ListViewArrayAdapter adapter;
        ProgressDialog progressBar;

        protected void onPreExecute() {
            super.onPreExecute();

            progressBar = new ProgressDialog(getContext());
            progressBar.setCancelable(false);
            progressBar.setMessage("File downloading ...");
            progressBar.show();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.i("Magazzino_Fragment", "AsyncTask!");


            String output = null;

            try {
                String query = "";
                if (!params[0].equals(""))
                    query += "nome=" + params[0] + "&";
                if (!params[1].equals(""))
                    query += "upc=" + params[1] + "&";
                if (!params[2].equals(""))
                    query += "anno=" + params[2] + "&";
                if (!params[3].equals(""))
                    query += "console=" + params[3] + "&";
                if (!params[4].equals("All"))
                    query += "quality=" + params[4] + "&";
                if (!params[5].equals("All"))
                    query += "stato=" + params[5] + "&";

                query += "sold=" + params[6];
                JSONObject REQUEST = new JSONObject();
                REQUEST.put("method", "GET");
                REQUEST.put("table", "magazzino");
                REQUEST.put("query", query);
                output = RestClient.executeGetRequest(REQUEST);

                if (!output.equals("ERRORE") && output != null) {

                    Log.i("Magazzino_Fragment", output);

                    JSONObject responseJSON = new JSONObject(output);
                    String magazzino = String.valueOf(responseJSON.get("in_magazzino"));
                    String venduti = String.valueOf(responseJSON.get("venduti"));

                    Context context = getContext();

                    if (!magazzino.equals("[]")) {
                        magazzino = magazzino.replace("[", "");
                        magazzino = magazzino.replace("]", "");
                        magazzino = magazzino.replace("},{", "}--{");

                        String[] magazzino_item = magazzino.split("--");


                        for (String str : magazzino_item) {
                            try {
                                //String s = "{'anno': u'1999', 'immagine': None, 'console': u'xboxone', 'upc': u'3286010000057', 'nome': u'nomeprova'}";
                                Log.i("Magazzino_Fragment", str);
                                JSONObject item_JSON = new JSONObject(str);
                                magazzinoObj obj = new magazzinoObj(String.valueOf(item_JSON.get("id_item")), String.valueOf(item_JSON.get("note")),
                                        String.valueOf(item_JSON.get("nome")), String.valueOf(item_JSON.get("upc")), String.valueOf(item_JSON.get("anno")),
                                        String.valueOf(item_JSON.get("console")), String.valueOf(item_JSON.get("stato")), String.valueOf(item_JSON.get("quality")),
                                        String.valueOf(item_JSON.get("prezzo_acquisto")), String.valueOf(item_JSON.get("data_acquisto")));


                                if (Boolean.valueOf(GetProperties.getProp("image"))) {
                                    JSONObject request = new JSONObject();
                                    request.put("table", "immagine");
                                    request.put("query", "id="+obj.upc);
                                    obj.setImage(GetContent.getImage(context,request,obj.upc));
                                }

                                ListViewContent.add(obj);

                            } catch (JSONException e) {
                                StringWriter sw = new StringWriter();
                                PrintWriter pw = new PrintWriter(sw);
                                e.printStackTrace(pw);
                                Log.i("Magazzino_Fragment", sw.toString());
                            }
                            //adapter.setNotifyOnChange(true);
                        }
                    }

                    if (!venduti.equals("[]")) {
                        venduti = venduti.replace("[", "");
                        venduti = venduti.replace("]", "");
                        venduti = venduti.replace("},{", "}--{");


                        String[] venduti_item = venduti.split("--");

                        for (String str : venduti_item) {
                            try {
                                //String s = "{'anno': u'1999', 'immagine': None, 'console': u'xboxone', 'upc': u'3286010000057', 'nome': u'nomeprova'}";
                                Log.i("Magazzino_Fragment", str);
                                JSONObject item_JSON = new JSONObject(str);
                                magazzinoObj obj = new magazzinoObj(String.valueOf(item_JSON.get("id_item")), String.valueOf(item_JSON.get("note")),
                                        String.valueOf(item_JSON.get("nome")), String.valueOf(item_JSON.get("upc")), String.valueOf(item_JSON.get("anno")),
                                        String.valueOf(item_JSON.get("console")), String.valueOf(item_JSON.get("stato")), String.valueOf(item_JSON.get("quality")),
                                        String.valueOf(item_JSON.get("prezzo_acquisto")), String.valueOf(item_JSON.get("data_acquisto")), String.valueOf(item_JSON.get("prezzo_vendita")), String.valueOf(item_JSON.get("data_vendita")));

                                if (Boolean.valueOf(GetProperties.getProp("image"))) {
                                    JSONObject request = new JSONObject();
                                    request.put("table", "immagine");
                                    request.put("query", "id="+obj.upc);
                                    obj.setImage(GetContent.getImage(context,request,obj.upc));
                                }

                                ListViewContent.add(obj);

                            } catch (JSONException e) {
                                StringWriter sw = new StringWriter();
                                PrintWriter pw = new PrintWriter(sw);
                                e.printStackTrace(pw);
                                Log.i("Magazzino_Fragment", sw.toString());
                            }
                            //adapter.setNotifyOnChange(true);
                        }
                    }
                    adapter = new ListViewArrayAdapter(context, ListViewContent);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return output;
        }

        protected void onPostExecute(String result) {

            setListAdapter(adapter);
            progressBar.dismiss();

        }

    }


    public class magazzinoObj{

        private String id_item;
        private String note;
        private String nome;
        private String upc;
        private String anno;
        private String console;
        private String stato;
        private String quality;
        private String prezzo_acquisto;
        private String data_acquisto;
        private String prezzo_vendita;
        private String data_vendita;

        private Boolean venduto;

        private Bitmap bitmap;

        public magazzinoObj(String id_item, String note, String nome, String upc, String anno, String console, String stato, String quality, String prezzo_acquisto, String data_acquisto, String prezzo_vendita, String data_vendita){
            this.id_item = id_item;
            this.note = note;
            this.nome = nome;
            this.upc = upc;
            this.anno = anno;
            this.console = console;
            this.stato = stato;
            this.quality = quality;
            this.prezzo_acquisto = prezzo_acquisto;
            this.data_acquisto = data_acquisto;
            this.prezzo_vendita = prezzo_vendita;
            this.data_vendita = data_vendita;

            this.venduto = true;

        }

        public magazzinoObj(String id_item, String note, String nome, String upc, String anno, String console, String stato, String quality, String prezzo_acquisto, String data_acquisto){
            this.id_item = id_item;
            this.note = note;
            this.nome = nome;
            this.upc = upc;
            this.anno = anno;
            this.console = console;
            this.stato = stato;
            this.quality = quality;
            this.prezzo_acquisto = prezzo_acquisto;
            this.data_acquisto = data_acquisto;

            this.venduto = false;

        }

        public void setImage(Bitmap img){
            this.bitmap = img;
        }

        public Bitmap getImage(){
            return this.bitmap;
        }

    }


}
