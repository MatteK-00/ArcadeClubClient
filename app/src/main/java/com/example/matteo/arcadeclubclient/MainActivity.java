package com.example.matteo.arcadeclubclient;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.matteo.arcadeclubclient.SQLiteDB.DataBaseManager;
import com.example.matteo.arcadeclubclient.Utility.GetContent;
import com.example.matteo.arcadeclubclient.Utility.GetProperties;

public class MainActivity extends AppCompatActivity {


    private DrawerLayout mDrawer;
    private Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i("id_DEVICE", Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        GetProperties prop = GetProperties.getIstance(getApplicationContext());
        //Log.i("GetProperties", prop.getProp("idDevice"));
        //prop.setProp("logged", "no");
        //prop.save();

        if (!Boolean.valueOf(prop.getProp("logged"))){
            Log.i("Main_Activity", "Esegui Login");
            Intent myIntent = new Intent(MainActivity.this,LogActivity.class);
            MainActivity.this.startActivity(myIntent);
        }


        NavigationView nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);
        mDrawer.openDrawer(GravityCompat.START);

        PreferenceManager.setDefaultValues(this, R.xml.preference, false);
        Log.i("id_device",prop.getProp("idDevice"));
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the planet to show based on
        // position
        ListFragment Listfragment = null;
        Fragment fragment = null;

        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.bar_code_search:
                fragmentClass = BarCodeSearchFragment.class;
                break;
            case R.id.magazzino_search:
                fragmentClass = MagazzinoSearchFragment.class;
                break;
            case R.id.aggiungi_nuovo:
                fragmentClass = InserisciNuovoFragment.class;
                break;
            case R.id.settings:
                fragmentClass = SettingsFragment.class;
                break;
            default:
                fragmentClass = MagazzinoSearchFragment.class;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (Listfragment!=null){
            try {
                Listfragment = (ListFragment) fragmentClass.newInstance();
                fragmentManager.beginTransaction().replace(R.id.flContent, Listfragment).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        //FragmentManager fragmentManager = getSupportFragmentManager();
        //fragmentManager.beginTransaction().replace(R.id.flContent, Listfragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.help:
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, new SettingsFragment()).commit();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Make sure this is the method with just `Bundle` as the signature
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }



}


