package com.example.matteo.arcadeclubclient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.matteo.arcadeclubclient.Utility.GetProperties;

/**
 * Created by matteo on 27/05/16.
 */
public class SettingsFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_settings, container, false);

        CheckBox loggedCheck = (CheckBox) view.findViewById(R.id.loginCheckox);
        CheckBox imageChek = (CheckBox) view.findViewById(R.id.ImmaginiCheckbox);
        CheckBox localDBCheck = (CheckBox) view.findViewById(R.id.DBcheckbox);

        final GetProperties prop = GetProperties.getIstance(getContext());

        loggedCheck.setChecked(Boolean.parseBoolean(prop.getProp("logged")));
        imageChek.setChecked(Boolean.parseBoolean(prop.getProp("image")));
        localDBCheck.setChecked(Boolean.parseBoolean(prop.getProp("localDB")));

        loggedCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked){
                    prop.setProp("logged","true");
                } else{
                    prop.setProp("logged","false");
                }
                prop.save();
                Log.i("Settings Fragmnet",prop.getProp("logged"));
                }
            }
        );

        imageChek.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked){
                        prop.setProp("image","true");
                } else{
                        prop.setProp("image","false");
                }
                    prop.save();
                    Log.i("Settings Fragmnet",prop.getProp("image"));
                }
            }
        );

        localDBCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    if (isChecked){
                        prop.setProp("localDB","true");
                    } else{
                        prop.setProp("localDB","false");
                    }
                    prop.save();
                    Log.i("Settings Fragmnet",prop.getProp("localDB"));
                }
            }
        );


        return view;

    }



}
