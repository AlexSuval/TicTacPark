package com.alex.tictacpark.activities;

import android.app.Fragment;
import android.app.FragmentManager;
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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.alex.tictacpark.R;
import com.alex.tictacpark.fragments.EstadoFragment;
import com.alex.tictacpark.fragments.BuscarFragment;
import com.alex.tictacpark.fragments.GeocoderFragment;
import com.alex.tictacpark.fragments.ParkingFragment;
import com.alex.tictacpark.fragments.AlarmaFragment;
import com.alex.tictacpark.fragments.CocheFragment;
import com.alex.tictacpark.fragments.HistorialFragment;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GeocoderFragment.OnFragmentInteractionListener {

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

        FrameLayout view_visible = (FrameLayout) findViewById(R.id.container2);
        view_visible.setVisibility(View.VISIBLE);

        BuscarFragment bf = new BuscarFragment();
        GeocoderFragment gf = new GeocoderFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.container, bf);
        ft.replace(R.id.container2, gf);

        ft.commit();

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
        SharedPreferences sp_general = getSharedPreferences(PREFS_GENERAL, 0);
        SharedPreferences.Editor editor_general = sp_general.edit();

        if (sp_general.getBoolean("primer_acceso_app", true)){
            editor_general.putBoolean("primer_acceso_app", false);
            CrearHistorial();   // Se crea el historial
            //mostrar_ocultarMenu(true);  // Se ocultan las pestañas del menú
        }

        Intent intent = getIntent();
        boolean alarma = intent.getBooleanExtra("alarma",true); // Si está a true es que no entró desde la notificación de alarma, porque por defecto está a false
        // Si alarma=false, entramos por notificación de alarma al apagarla, entonces ponemos el
        // campo alarma=false en el archivo de preferencias, de forma que se desactivará el switch
        // en AlarmaFragment
        Log.e("Alarma=",Boolean.toString(alarma));
        if (!alarma)
        {
            /*
            // Editor de preferencias General
            SharedPreferences sp_general=this.getSharedPreferences("PREFS_GENERAL", 0);
            final SharedPreferences.Editor editor_general = sp_general.edit();
            */
            // Ponemos alarma=false en el archivo de preferencias general
            editor_general.putBoolean("alarma", false);
        }

        //Se guardan los cambios en el fichero
        editor_general.commit();

        // TODO INICIO
        // Se comprueba la conexión con la DB
        peticionServicio();
        // TODO FIN
    }

    // TODO EN PRUEBAS
    // Método que devuelve el estado de la conexión al servidor
    public void peticionServicio()
    {
        // Variable de tipo String que inicializa con la estructura principal de la URI
        // para el acceso al servicio web.
        // Nota: Para conectarnos con nuestro servidor web local (localhost), debemos usar la
        // dirección IP de nuestro equipo en vez de "localhost" o "127.0.0.1". Esto es porque
        // la dirección IP "127.0.0.1" es internamente usada por el emulador de android o por
        // nuestro dispositivo Android
        String ip = "192.168.43.192";
        // Con el emulador funciona: "10.0.2.2" (local apache server)
        // Con el emulador (red eduroam) funciona: "10.38.32.149"
        // Activando el móvil como punto de acceso y conectando el portátil con el móvil
        // es la única forma en la que funciona depurando con el móvil, saco ipconfig y
        // me conecto con: "192.168.43.192"
        String raiz = "http://" + ip + ":8080/TicTacParkDWP/rest/TicTacPark";
        String servidor = "localhost";
        String puerto = "3306";
        String baseDatos = "tictacpark";
        String usuario = "root";
        String password = "passking";

        String uri = raiz + "/estado/" + servidor + "/" + puerto + "/" + baseDatos + "/" + usuario + "/" + password;

        // Se declara e inicializa una variable de tipo RequestQueue, encargada de crear
        // una nueva petición en la cola del servicio web.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Se declara e inicializa un objeto de tipo JsonArrayRequest, que permite recuperar un
        // JSONArray a partir de la URL que recibe. El constructor de la clase JsonArrayRequest
        // recibe como argumentos de entrada el método para que el cliente realice operaciones
        // sobre el servicio web, la uri para el acceso al recurso, la interfaz Response.Listener,
        // encargada de devolver la respuesta parseada a la petición del cliente, y la interfaz
        // Response.ErrorListener encargada de entregar una respuesta errónea desde el servicio web.
        JsonArrayRequest jArray = new JsonArrayRequest(Request.Method.GET,
                uri,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response)
                    {
                        try
                        {
                            // Se comprueba mediante un condicional if, que el servicio web ha podido
                            // conectar con el servidor MySQL con los datos introducidos por el usuario.
                            if(response.get(1).toString().equals("true"))
                                Log.e(" Estado Conexión", "Fue OK");
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        try
                        {
                            Log.e("Estado Conexión", "Falló");
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
        );

        // Se definen las políticas para la petición realizada. Recibe como argumento una
        // instancia de la clase DefaultRetryPolicy, que recibe como parámetros de entrada
        // el tiempo inicial de espera para la respuesta, el número máximo de intentos,
        // y el multiplicador de retardo de envío por defecto.
        jArray.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Se añade la petición a la cola con el objeto de tipo JsonArrayRequest.
        queue.add(jArray);
    }
    // TODO FIN

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

    // El manejador para Intents recibidos, que será llamado cada vez que se emita un Intent
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
        /*if(!tag.equals(BUSCAR)){
            Fragment f = (Fragment) getFragmentManager().findFragmentById(R.id.container);
            ViewGroup.LayoutParams params = f.getView().getLayoutParams();
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            f.getView().setLayoutParams(params);
        }*/

        FrameLayout view = (FrameLayout) findViewById(R.id.container2);
        view.setVisibility(View.GONE);

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

                FrameLayout view_visible = (FrameLayout) findViewById(R.id.container2);
                view_visible.setVisibility(View.VISIBLE);

                BuscarFragment bf = new BuscarFragment();
                GeocoderFragment gf = new GeocoderFragment();

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                ft.replace(R.id.container, bf);
                ft.replace(R.id.container2, gf);

                ft.commit();

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
                // Recuperamos las coordenadas del coche aparcado
                SharedPreferences sp_mi_parking=this.getSharedPreferences("PREFS_MI_PARKING", 0);
                String latitud=Float.toString(sp_mi_parking.getFloat("latitud", 0));
                String longitud=Float.toString(sp_mi_parking.getFloat("longitud", 0));
                // Construimos el String destino con estas coordenadas
                final String destino=latitud+","+longitud;
                //Se crea el Intent, pasando la ruta de Google Maps:
                // Origen: Nuestras coordenadas actuales. Dejando el valor en blanco, toma por defecto nuestra ubicación actual.
                // Destino: El construido con las coordenadas guardadas al aparcar.
                // dirflg=w: Para pasar por defecto la ruta a pie
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + "" + "&daddr=" + destino+"&dirflg=w"));
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

    // Método para poner el nombre del Parking en la barra
    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onFragmentInteraction(LatLng coordenadas) {
        BuscarFragment bf=(BuscarFragment)getFragmentManager().findFragmentById(R.id.container);

        if(bf!=null){
            bf.moverCamara(coordenadas);
        }
    }
}