package com.example.matteo.arcadeclubclient;


import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matteo.arcadeclubclient.Utility.DialogConfermaPopup;
import com.example.matteo.arcadeclubclient.Utility.GetContent;
import com.example.matteo.arcadeclubclient.Utility.GetProperties;
import com.example.matteo.arcadeclubclient.Utility.RestClient;
import com.example.matteo.arcadeclubclient.Utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class BarCodeSearchFragment  extends ListFragment implements AdapterView.OnItemClickListener{

    EditText barcode;
    View view;
    TextView id_gioco;
    TextView nome;
    TextView upc;
    TextView anno;
    TextView console;
    TextView immagine;

    private boolean FLAG = true;


    //public BarCodeSearchFragment() {
    //}



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_bar_code_search, container, false);
        barcode = (EditText) view.findViewById(R.id.bar_code_editText);

        nome = (TextView) view.findViewById(R.id.item_nome);
        upc = (TextView) view.findViewById(R.id.item_upc);
        anno = (TextView) view.findViewById(R.id.item_anno);
        console = (TextView) view.findViewById(R.id.item_console);
        id_gioco = (TextView) view.findViewById(R.id.item_hidden_id);
        immagine = (TextView) view.findViewById(R.id.item_hidden_image);

        if (FLAG) {
            //barCodeSearch();
        }

        Button button = (Button) view.findViewById(R.id.buttonBARCODE);
        Button button_aggiungi = (Button) view.findViewById(R.id.aggiungi_nuovo_button);


        //mylist = (ListView) view.findViewById(R.id.listViewBRS);
        //mylist.setOnClickListener();

        final String request = "013388130290";
        //final String request = "720238101804";
        //final String request = "3286010000057";


        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                barcode.setText(request);
                if (Utility.isOnline(getContext())) {

                    new CallServer().execute(request);
                } else{
                    Toast.makeText(getContext(), "Connessione internet assente!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        button_aggiungi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject item_JSON = new JSONObject();
                try {
                    item_JSON.putOpt("id_gioco",id_gioco.getText());
                    item_JSON.putOpt("nome",nome.getText());
                    item_JSON.putOpt("upc",upc.getText());
                    item_JSON.putOpt("anno",anno.getText());
                    item_JSON.putOpt("console",console.getText());
                    item_JSON.putOpt("immagine",immagine.getText());


                    Fragment newFragment = new InserisciNuovoFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("item",item_JSON.toString());

                    newFragment.setArguments(bundle);

                    // consider using Java coding conventions (upper first char class names!!!)
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    // Replace whatever is in the fragment_container view with this fragment,
                    // and add the transaction to the back stack
                    transaction.replace(R.id.flContent, newFragment);
                    transaction.addToBackStack(null);

                    // Commit the transaction
                    transaction.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        return view;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("BCS_Fragment", "onViewCreated");
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        VerticalListViewArrayAdapter adapter = new VerticalListViewArrayAdapter(getContext(), new ArrayList<JSONObject>());
        setListAdapter(adapter);
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        try {
            JSONObject list_view_item_JSON = new JSONObject(parent.getItemAtPosition(position).toString());// .getString(R.id.magazzino_id_item);
            itemDialogPopup(list_view_item_JSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void itemDialogPopup(JSONObject item) {
        final JSONObject item_JSON = item;
        final Dialog dialog= new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_magazzino);

        ImageView img = (ImageView) dialog.findViewById(R.id.dialog_img);

        Button vendi_button = (Button) dialog.findViewById(R.id.dialog_button1);
        Button aggiungi_button = (Button) dialog.findViewById(R.id.dialog_button2);
        TextView note = (TextView) dialog.findViewById(R.id.dialog_text_note);

        try {
            item_JSON.putOpt("nome",nome.getText());
            item_JSON.putOpt("upc",upc.getText());
            item_JSON.putOpt("anno",anno.getText());
            item_JSON.putOpt("console",console.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (!item.isNull("prezzo_vendita")){
            vendi_button.setEnabled(false);
        }

        vendi_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogConfermaPopup vendiDialog = new DialogConfermaPopup(getContext());
                vendiDialog.vendi_item(item_JSON);
            }
        });

        aggiungi_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Magazzino_Fragment", "Aggiungi al Magazzino!");


                Fragment newFragment = new InserisciNuovoFragment();
                Bundle bundle = new Bundle();
                bundle.putString("item",item_JSON.toString());

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

        try {
            dialog.setTitle(upc.getText().toString().toString());
            String noteString = item.get("note").toString();
            if (!noteString.equals("null"))
                note.setText(item.get("note").toString());
        } catch (JSONException e) {
            e.printStackTrace();

        }

        dialog.show();
    }


    private void barCodeSearch(){
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            //intent.putExtra("SCAN_MODE", "UPC-A");
            intent.putExtra("SAVE_HISTORY", true);
            //intent.putExtra("RESULT_DISPLAY_DURATION_MS", 10);
            startActivityForResult(intent, 0);

        } catch (ActivityNotFoundException e) { //if the app is not installed
            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            startActivity(marketIntent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            //if (resultCode == RESULT_OK) {
            FLAG = false;
            String content = data.getStringExtra("SCAN_RESULT"); //this is the result
            Log.e("BarCode Scanner", data.getStringExtra("SCAN_RESULT_FORMAT"));
            Log.i("BarCode Scanner", content);
            barcode.setText(content);

        }
    }

    public class VerticalListViewArrayAdapter extends ArrayAdapter<JSONObject> {
        private final Context context;
        private final ArrayList<JSONObject> values;

        public VerticalListViewArrayAdapter(Context context, ArrayList<JSONObject>  values) {
            super(context, R.layout.rowlayout, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.rowlayout, parent, false);

            TextView stato = (TextView) rowView.findViewById(R.id.label_stato);
            TextView quality = (TextView) rowView.findViewById(R.id.label_quality);
            TextView prezzo_acquisto = (TextView) rowView.findViewById(R.id.label_prezzo_acquisto);
            TextView data_acquisto = (TextView) rowView.findViewById(R.id.label_data_acquisto);


            ImageView imageView = (ImageView) rowView.findViewById(R.id.item_immagine);
            imageView.setImageBitmap(view.findViewById(R.id.imageView).getDrawingCache());
            try {
                stato.setText(String.valueOf(values.get(position).get("stato")));
                quality.setText(String.valueOf(values.get(position).get("quality")));
                prezzo_acquisto.setText(String.valueOf(values.get(position).get("prezzo_acquisto")));
                data_acquisto.setText(String.valueOf(values.get(position).get("data_acquisto")));

            } catch (JSONException e) {
                e.printStackTrace();
            }


            return rowView;
        }

    }





    private class CallServer extends AsyncTask<String, String, String> {
        VerticalListViewArrayAdapter adapter;
        final ArrayList<String> info_String = new ArrayList<>();
        Bitmap image = null;
        boolean FLAG_IMAGE = true;
        String output = null;


        @Override
        protected String doInBackground(String... params) {
            Log.i("BCS_Fragment", "AsyncTask!");



            try {

                JSONObject REQUEST = new JSONObject();
                REQUEST.put("method", "GET");
                REQUEST.put("table", "search_upc");
                REQUEST.put("query", params[0]);
                //output = RestClient.getIstance(getContext()).executeGetRequest(REQUEST);

                output = GetContent.setRequest(getContext(),REQUEST,params[0]);

                if (!output.equals("ERRORE") && output!=null) {

                    Log.i("BCS_Fragment", output);

                    JSONObject responseJSON = new JSONObject(output);
                    JSONObject info = new JSONObject(String.valueOf(responseJSON.get("info")));

                    info_String.add(0, String.valueOf(info.get("id_gioco")));
                    info_String.add(1, String.valueOf(info.get("upc")));
                    info_String.add(2, String.valueOf(info.get("nome")));
                    info_String.add(3, String.valueOf(info.get("anno")));
                    info_String.add(4, String.valueOf(info.get("console")));
                    info_String.add(5, String.valueOf(info.get("immagine")));

                    if (Boolean.valueOf(GetProperties.getIstance(getContext()).getProp("image"))) {
                        JSONObject request = new JSONObject();
                        request.put("table", "immagine");
                        request.put("query", String.valueOf(info.get("id_gioco")) + "_"
                                + String.valueOf(info.get("immagine")));
                        image = GetContent.GetImage(getContext(),request,info_String.get(1));
                    }


                    if (!responseJSON.get("item_list").equals("[]")) {
                        String item_list = String.valueOf(responseJSON.get("item_list"));
                        //Log.i("CONTROLLO",item_list);
                        item_list = item_list.replace("[", "");
                        item_list = item_list.replace("]", "");
                        item_list = item_list.replace("},{", "}--{");

                        String[] item = item_list.split("--");
                        Context context = getContext();
                        ArrayList<JSONObject> ListViewContent = new ArrayList<>();

                        for (String str : item) {
                            try {
                                //String s = "{'anno': u'1999', 'immagine': None, 'console': u'xboxone', 'upc': u'3286010000057', 'nome': u'nomeprova'}";
                                Log.i("BCS_Fragment", str);

                                JSONObject item_JSON = new JSONObject(str);
                                ListViewContent.add(item_JSON);


                            } catch (JSONException e) {
                                StringWriter sw = new StringWriter();
                                PrintWriter pw = new PrintWriter(sw);
                                e.printStackTrace(pw);
                                Log.i("BCS_Fragment", sw.toString());
                            }

                        }

                        adapter = new VerticalListViewArrayAdapter(context, ListViewContent);
                    }
                }
                }catch(JSONException e){
                    e.printStackTrace();
                }


            return output;
        }

        protected void onPostExecute(String result) {
            Log.i("CONTROLLO",output);
            if (!output.equals("ERRORE") && output!=null) {
                TextView nome = (TextView) view.findViewById(R.id.item_nome);
                TextView upc = (TextView) view.findViewById(R.id.item_upc);
                TextView anno = (TextView) view.findViewById(R.id.item_anno);
                TextView console = (TextView) view.findViewById(R.id.item_console);
                TextView hidden_image = (TextView) view.findViewById(R.id.item_hidden_image);
                TextView id_gioco = (TextView) view.findViewById(R.id.item_hidden_id);
                ImageView image_field = (ImageView) view.findViewById(R.id.imageView);

                Button button_aggiungi = (Button) view.findViewById(R.id.aggiungi_nuovo_button);
                button_aggiungi.setVisibility(view.VISIBLE);
                button_aggiungi.setEnabled(true);

                nome.setText(info_String.get(2));
                upc.setText(info_String.get(1));
                anno.setText(info_String.get(3));
                console.setText(info_String.get(4));
                hidden_image.setText(info_String.get(5));
                id_gioco.setText(info_String.get(0));

                if (Boolean.valueOf(GetProperties.getIstance(getContext()).getProp("image"))) {
                    image_field.setImageBitmap(image);
                }

                setListAdapter(adapter);
            } else {
                Toast.makeText(getContext(), "Server Off-line",
                        Toast.LENGTH_LONG).show();
            }

        }

    }

}





