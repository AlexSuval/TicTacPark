package com.alex.tictacpark.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.alex.tictacpark.R;
import com.alex.tictacpark.fragments.EstadoFragment;
import com.alex.tictacpark.fragments.BuscarFragment;
import com.alex.tictacpark.fragments.ParkingFragment;
import com.alex.tictacpark.fragments.AlarmaFragment;
import com.alex.tictacpark.fragments.CocheFragment;
import com.alex.tictacpark.fragments.HistorialFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String ESTADO = "ESTADO";
    private static final String BUSCAR = "BUSCAR";
    private static final String PARKING = "PARKING";
    private static final String ALARMA = "ALARMA";
    private static final String COCHE = "COCHE";
    private static final String HISTORIAL = "HISTORIAL";

    //Se usará para el fichero de preferencias
    private static final String PREFS_NAME = "PREFS";

    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.e("ID", getPackageName());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Fragment mFragment=BuscarFragment.newInstance(0);
        inflate(mFragment,BUSCAR);

        //Se oculta/activa el menú en función del estado correspondiente
        ocultarMenu(navigationView);
    }

    public void ocultarMenu(NavigationView navigationView){
        //Carga las preferencias de usuario
        SharedPreferences sp = getSharedPreferences(PREFS_NAME,0);
        boolean aparcado = sp.getBoolean("aparcado",false); //Recupera si está aparcado o no

        if(aparcado==false)
        {
            //Desactiva las pestañas de Mi parking, alarma y gasto y volver al coche, del menú
            navigationView.getMenu().findItem(R.id.nav_parking).setEnabled(false);
            navigationView.getMenu().findItem(R.id.nav_alarma).setEnabled(false);
            navigationView.getMenu().findItem(R.id.nav_coche).setEnabled(false);
        }
        else if (aparcado==true)
        {
            navigationView.getMenu().findItem(R.id.nav_parking).setEnabled(true);
            navigationView.getMenu().findItem(R.id.nav_alarma).setEnabled(true);
            navigationView.getMenu().findItem(R.id.nav_coche).setEnabled(true);
        }
    }

    @Override
    // Se ejecuta al volver de otra actividad, ejecutándose desde el principio
    protected void onResume(){
        super.onResume();
        Log.e("Estado","onResume");
        //Se oculta/activa el menú en función del estado correspondiente
        ocultarMenu(navigationView);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Infla el fragment, reemplazando el que se le pasa por el que había
    private void inflate(Fragment fragment,String tag)
    {
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container,fragment,tag);
        transaction.commit();
    }+

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment mFragment;

        switch (id){
            case R.id.nav_estado:
                Log.e("MAINACTIVITY", "Estado");
                mFragment= EstadoFragment.newInstance(0);//Creamos el fragment
                inflate(mFragment,ESTADO);//Inflamos el fragment ESTADO
                break;
            case R.id.nav_buscar:
                Log.e("MAINACTIVITY", "Buscar");
                mFragment= BuscarFragment.newInstance(1);//Creamos el fragment
                inflate(mFragment,BUSCAR);//Inflamos el fragment BUSCAR
                break;
            case R.id.nav_parking:
                Log.e("MAINACTIVITY", "Mi Parking");
                mFragment= ParkingFragment.newInstance(2);//Creamos el fragment
                inflate(mFragment,PARKING);//Inflamos el fragment PARKING
                break;
            case R.id.nav_alarma:
                Log.e("MAINACTIVITY", "Alarma y gasto");
                mFragment= AlarmaFragment.newInstance(3);//Creamos el fragment
                inflate(mFragment,ALARMA);//Inflamos el fragment ALARMA
                break;
            case R.id.nav_coche:
                Log.e("MAINACTIVITY", "Volver al coche");
                //Se crea el Intent
                Intent intent=new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps")); //?saddr=ORIGEN&daddr=DESTINO
                startActivity(intent);
                break;
            case R.id.nav_historial:
                Log.e("MAINACTIVITY", "Historial");
                mFragment= HistorialFragment.newInstance(5);//Creamos el fragment
                inflate(mFragment,HISTORIAL);//Inflamos el fragment HISTORIAL
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}