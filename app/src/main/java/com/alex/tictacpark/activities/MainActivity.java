package com.alex.tictacpark.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String ESTADO = "ESTADO";
    private static final String BUSCAR = "BUSCAR";
    private static final String PARKING = "PARKING";
    private static final String ALARMA = "ALARMA";
    private static final String COCHE = "COCHE";
    private static final String HISTORIAL = "HISTORIAL";

    //Se usarán para los ficheros de preferencias
    private static final String PREFS_GENERAL = "PREFS_GENERAL";
    private static final String PREFS_MI_PARKING = "PREFS_MI_PARKING";

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

        // Comprobamos mediante el fichero de preferencias si el id es -1 para ocultar/mostrar las pestañas,
        // pues si es el primer acceso a la app lo pondría a -1 y las ocultaría
        SharedPreferences sp = getSharedPreferences(PREFS_MI_PARKING, 0);
        int id=sp.getInt("id",-1);
        if (id==-1){
            mostrar_ocultarMenu(true);
        }

        // Se oculta/activa el menú en función del estado correspondiente
        // Para ello utilizamos un receptor de las notificaciones de aparcamiento de ParkingFragment
        // Registramos un observador (mMessageReceiver) para recibir Intents
        // con acciones nombradas "custom-event-name".
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));

        // Comprobamos mediante el fichero de preferencias si es el primer acceso a la app,
        // en caso afirmativo, crearemos el fichero del historial
        SharedPreferences settings = getSharedPreferences(PREFS_GENERAL, 0);
        if (settings.getBoolean("primer_acceso_app", true)){
            settings.edit().putBoolean("primer_acceso_app", false).commit();
            CrearHistorial();   // Se crea el historial
            //mostrar_ocultarMenu(true);  // Se ocultan las pestañas del menú
        }
    }

    // Método para crear el fichero del historial
    private void CrearHistorial(){
        String string; // String para pasar en formato string el JSON
        FileOutputStream fos;

        // Creamos el objeto y String JSON
        try{
            JSONObject objeto = new JSONObject();
            JSONArray array = new JSONArray();
            objeto.put("historial", array);
            string=objeto.toString();
        }
        catch(JSONException e){
            e.printStackTrace();
            string="";
        }

        // Creamos el fichero JSON del historial
        try{
            fos=openFileOutput("historial.json", Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        Log.e("JSON", string);
    }

    // El manejador para Intents recibidos, que será llamdo cada vez que se emita un Intent
    // con una acción "custom-event-name"
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extraemos el mensaje obtenido del intent, que nos dará la información
            // sobre si las pestañas deben ocultarse o mostrarse
            String message = intent.getStringExtra("MOSTRAR_OCULTAR");
            boolean ocultar;
            if(message=="OCULTAR")
                ocultar=true;
            else
                ocultar=false;
            mostrar_ocultarMenu(ocultar);
            Log.e("Receptor", "Mensaje obtenido: " + message);
        }
    };

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    public void mostrar_ocultarMenu(boolean ocultar)
    {
        //Desactiva/Activa las pestañas de Mi parking, alarma y gasto y volver al coche, del menú
        navigationView.getMenu().findItem(R.id.nav_parking).setEnabled(!ocultar);
        navigationView.getMenu().findItem(R.id.nav_alarma).setEnabled(!ocultar);
        navigationView.getMenu().findItem(R.id.nav_coche).setEnabled(!ocultar);
    }

    @Override
    // Se ejecuta al volver de otra actividad, ejecutándose desde el principio
    protected void onResume(){
        super.onResume();
        Log.e("Estado", "onResume");
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
    }

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
                clickVolverCoche();
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

    // Método para mostrar el diálogo de "Volver al coche"
    public void clickVolverCoche(){
        // Recuperamos las coordenadas del coche aparcado
        SharedPreferences sp_mi_parking=this.getSharedPreferences("PREFS_MI_PARKING", 0);
        String latitud=Float.toString(sp_mi_parking.getFloat("latitud", 0));
        String longitud=Float.toString(sp_mi_parking.getFloat("longitud", 0));
        // Construimos el String destino con estas coordenadas
        final String destino=latitud+","+longitud;

        // TODO Calculamos el tiempo estimado en volver al coche

        // Diálogo que avisa al usuario de que ya está aparcado
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Volver al coche")
                .setMessage("El tiempo estimado de vuelta al coche es de XX minutos, " +
                        "¿desea recibir las indicaciones para volver?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Se crea el Intent, pasando la ruta de Google Maps:
                        // Origen: Nuestras coordenadas actuales. Dejando el valor en blanco, toma por defecto nuestra ubicación actual
                        // Destino: El construido con las coordenadas guardadas al aparcar.
                        // dirflg=w: Para pasar por defecto la ruta a pie
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr=" + "" + "&daddr=" + destino+"&dirflg=w"));
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        alertDialog.show();

    }

    // Método para poner el nombre del Parking en la barra
    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }
}