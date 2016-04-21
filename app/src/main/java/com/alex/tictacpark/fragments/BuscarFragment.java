package com.alex.tictacpark.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alex.tictacpark.R;
import com.alex.tictacpark.activities.MainActivity;
import com.alex.tictacpark.activities.ParkingDetalle;
import com.alex.tictacpark.models.Parking;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.location.LocationListener;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BuscarFragment extends Fragment
    /*implements OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
    GoogleMap.OnMyLocationButtonClickListener*/ {

    private GoogleMap mMap;
    private MapView mMapView;
    private String TAG = "BUSCAR";
    //private GoogleApiClient mGoogleApiClient;
    private LocationManager mLocationManager;
    private addMarkers addMarkers;

    // Variables necesarias para introducir los datos de conexión al servidor.
    String ip = "192.168.0.11";// "192.168.43.192";
    // Activando el móvil como punto de acceso y conectando el portátil con el móvil
    // es la única forma en la que funciona depurando con el móvil, saco ipconfig y
    // me conecto con: "192.168.43.192"
    String raiz = "http://" + ip + ":8080/TicTacParkDWP/rest/TicTacPark";
    String servidor = "localhost";
    String puerto = "3306";
    String baseDatos = "tictacpark";
    String usuario = "root";
    String password = "passking";

    // Declaramos e inicializamos la ArrayList list_parking, que contendrá todos los objetos Parking
    private ArrayList<Parking> list_parking=new ArrayList<Parking>();

    //Configuración del mapa
    //Establece mi posición
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)  //5000 ms --> COMPLETAR COMENTARIO en api de google maps
            .setFastestInterval(16) //16ms=60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param SectionNumber Indica el número de la sección pulsada.
     * @return A new instance of fragment BuscarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BuscarFragment newInstance(int SectionNumber) {
        BuscarFragment fragment = new BuscarFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, SectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public BuscarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        //Asignamos un layout al mapa
        rootView=inflater.inflate(R.layout.fragment_buscar, container, false); //layout
        mMapView=(MapView) rootView.findViewById(R.id.map); //mapa en sí
        mMapView.onCreate(savedInstanceState);

        // Ponemos el nombre "Buscar" en la barra
        ((MainActivity) getActivity()).setActionBarTitle("Buscar");

        /*
        //Inicializamos el objeto GoogleApiClient con las propiedades que va a tener
        mGoogleApiClient=new GoogleApiClient.Builder(getActivity()) //getActivity para sacar el contexto de la actividad
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
                */
        //Inicializamos el mLocationManager
        mLocationManager=(LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MapsInitializer.initialize(getActivity());
        startMap();
    }

    public void startMap(){
        int status= GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if(status==ConnectionResult.SUCCESS)    //Comprueba si tiene Google Play Services instalado
        {
            configurarMapa();
        }
        else {
            //TODO Gestionar qué pasa si no está instalado Google Play Services

        }
    }

    private void configurarMapa(){
        mMap=mMapView.getMap(); //Cargamos mapa en el fragment
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); //Mapa básico con carreteras
        mMap.setMyLocationEnabled(true); //Mi localización
        // Configuramos la posición inicial
        comenzarLocalizacion();
    }

    private void comenzarLocalizacion() {
        LocationManager locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        //Checkear los permisos
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            //Se obtiene la última localización GPS
           final Location loc = getLastKnownLocation(locManager);

            //Muestra la última posición o si no la encuentra, una por defecto
            mostrarPosicion(loc);

            //Nos registramos para recibir actualizaciones de la posición
            LocationListener locListener=new LocationListener(){
                public void onLocationChanged(Location location){
                    mostrarPosicion(loc); //Va actualizando posición
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            };

            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,20000, locListener); //Se actualiza la posición cada 20km

        }catch(Exception ex){
            Log.e("Error: ", ex.getMessage());
        }
    }

    private Location getLastKnownLocation(LocationManager mLocationManager) {

        //Checkear los permisos
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        mLocationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            List<String> providers = mLocationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
            return bestLocation;
        }
        else{
            showGPSDisabledAlertToUser();
            return null;
        }
    }

    private void mostrarPosicion(Location loc){

        // Configuramos la posición inicial
        LatLng latLng;
        int zoom;

        if(loc==null)
        {
            latLng=new LatLng(40.43,-3.683); // Coordenadas Madrid
            zoom=5;
        }
        else
        {
            latLng = new LatLng(loc.getLatitude(),loc.getLongitude());
            zoom=14;
        }
        CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(latLng,zoom);
        mMap.animateCamera(cameraUpdate);
        addMarkers=new addMarkers(); //Inicializamos addMarkers
        addMarkers.execute(""); //Lanzamos addMarkers pasándole la url
    }

    public void moverCamara(LatLng coordenadas){
        CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(coordenadas, 14);
        mMap.animateCamera(cameraUpdate);
    }

    //Muestra cuadro de diálogo en caso de que el GPS esté desactivado
    private void showGPSDisabledAlertToUser()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder
                .setMessage(R.string.activacion_gps)
                .setCancelable(false)
                .setPositiveButton(R.string.activar_gps,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);

                            }
                        });

        alertDialogBuilder.setNegativeButton(R.string.cancelar_gps,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }// ;

    @Override
    // Estado al que se vuelve tras cerrar la aplicación y ésta queda en segundo plano
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        //mGoogleApiClient.connect();
    }

    @Override
    // Estado al salir de la aplicación sin cerrarla (botón home)
    public void onPause() {
        super.onPause();
        //mGoogleApiClient.disconnect(); //Para ahorrar batería
    }

    @Override
    // Estado al salir de la aplicación cerrándola totalmente
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    // Estado de baja memoria
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    //Los markers se ponen a la escucha de click
    private Intent clickMarker(Marker marker)
    {
        // Coger latitud del marker
        double latitud=marker.getPosition().latitude;
        // Coger longitud del marker
        double longitud=marker.getPosition().longitude;
        // Buscar en el ArrayList el id del objeto con esas coordenadas
        int index=-1;
        for(Parking p:list_parking){
            if (p.getLatitud()==latitud && p.getLongitud()==longitud)
            {
                index=list_parking.indexOf(p);
            }
        }

        Parking parking_clickado = list_parking.get(index);

        Intent intent=new Intent(getActivity(),ParkingDetalle.class);

        // Metemos el parking clickado en un ArrayList, para no tener que pasarle a la actividad
        // el ArrayList con todos los objetos parking
        ArrayList<Parking> parking_clickado_list = new ArrayList<Parking>();
        parking_clickado_list.add(parking_clickado);

        // Pasamos el parking_clickado a la actividad
        intent.putParcelableArrayListExtra("parking_clickado", parking_clickado_list);



/*
        //Le pasamos datos (el nombre del parking) a la actividad
        intent.putExtra(ParkingDetalle.NOMBRE,parking.getNombre());
        //Sobreescribe las coordenadas (Longitud y latitud) en el fichero de preferencias general
        SharedPreferences sp_general=getActivity().getSharedPreferences("PREFS_GENERAL", 0);
        //boolean aparcado=sp_general.getBoolean("aparcado",false);//Segundo parámetro=Valor por defecto
        //if(!aparcado) {
            SharedPreferences.Editor editor = sp_general.edit();
            String longitud=Double.toString(parking.getLongitud());
            String latitud=Double.toString(parking.getLatitud());
            editor.putString("longitud", longitud);
            editor.putString("latitud", latitud);
            editor.commit(); //Se guardan los cambios en el fichero
        //}´*/
        return intent;
    }

    //Función que añade el marker al mapa
    private void showMarker(LatLng coordenadas, String nombre, String tipo, double precio, String estado)
    {
        // Creamos String con Tipo de parking y precio/hora
        String Info = tipo + " - " + precio + "€/h";

        if(estado.equals("Libre"))  // Marker en color verde
        {
            mMap.addMarker(new MarkerOptions().position(coordenadas).title(nombre).snippet(Info)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
        else
        {
            mMap.addMarker(new MarkerOptions().position(coordenadas).title(nombre).snippet(Info)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
    }

    // TODO EN PRUEBAS

    // Método que invoca a peticionServicioRead(), indicándole como argumento la URI asociada al
    // listado de parkings disponibles, y que devuelve una colección de objetos Parking.
    public List<Parking> obtenerParkings()
    {
        String uri = raiz + "/lista/" + servidor + "/" + puerto + "/" + baseDatos + "/" + usuario + "/" + password;

        return peticionServicioRead(uri);
    }

    // Método que devuelve una colección de objetos Parking, y que recibe como parámetro de entrada
    // la URI para realizar la petición al servicio web.
    public ArrayList<Parking> peticionServicioRead(String uri)
    {
        // Se declara e inicializa una variable de tipo List que almacenará objetos de tipo Parking
        //list_parking = new ArrayList<Parking>();

        // Se declara e inicializa una variable de tipo RequestQueue, encargada de crear una nueva
        // petición en la cola del servicio web.
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        // Se declara e inicializa un objeto de tipo JsonArrayRequest, que permite recuperar un
        // JSONArray a partir de la URL que recibe. El constructor de la clase JsonArrayRequest
        // recibe como argumentos de entrada el método para que el cliente realice operaciones sobre
        // el servidor web, la uri para el acceso al recurso, y la interfaz Response.Listener,
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
                            // Se vacía la lista de Parkings para actualizarla
                            list_parking.clear();
                            // Se construye un bucle for() para recorrer la respuesta parseada y
                            // construir un nuevo objeto Parking por cada registro de la base de datos MySQL.
                            for (int i = 0; i < response.length(); i++)
                            {
                                JSONObject jsObjectParking = (JSONObject) response.get(i);

                                int id = jsObjectParking.getInt("id");
                                String nombre = jsObjectParking.getString("nombre");
                                Log.e("Parking ", nombre);
                                String direccion = jsObjectParking.getString("direccion");
                                String localidad = jsObjectParking.getString("localidad");
                                String provincia = jsObjectParking.getString("provincia");
                                double latitud = jsObjectParking.getDouble("latitud");
                                double longitud = jsObjectParking.getDouble("longitud");
                                String telefono = jsObjectParking.getString("telefono");
                                String imagen = jsObjectParking.getString("imagen");
                                String tipo = jsObjectParking.getString("tipo");
                                String estado = jsObjectParking.getString("estado");
                                double precio = jsObjectParking.getDouble("precio");
                                String horario_apertura = jsObjectParking.getString("horario_apertura");
                                String horario_cierre = jsObjectParking.getString("horario_cierre");
                                int tiempo_maximo = jsObjectParking.getInt("tiempo_maximo");
                                int plazas = jsObjectParking.getInt("plazas");
                                double altura_minima = jsObjectParking.getDouble("altura_minima");

                                String adaptado_discapacidad_st = jsObjectParking.getString("adaptado_discapacidad");
                                byte adaptado_discapacidad = Byte.valueOf(adaptado_discapacidad_st);

                                String plazas_discapacidad_st = jsObjectParking.getString("plazas_discapacidad");
                                byte plazas_discapacidad = Byte.valueOf(plazas_discapacidad_st);

                                String motos_st = jsObjectParking.getString("motos");
                                byte motos = Byte.valueOf(motos_st);

                                String aseos_st = jsObjectParking.getString("aseos");
                                byte aseos = Byte.valueOf(aseos_st);

                                String tarjeta_st = jsObjectParking.getString("tarjeta");
                                byte tarjeta = Byte.valueOf(tarjeta_st);

                                String seguridad_st = jsObjectParking.getString("seguridad");
                                byte seguridad = Byte.valueOf(seguridad_st);

                                String coches_electricos_st = jsObjectParking.getString("coches_electricos");
                                byte coches_electricos = Byte.valueOf(coches_electricos_st);

                                String lavado_st = jsObjectParking.getString("lavado");
                                byte lavado = Byte.valueOf(lavado_st);

                                String servicio_24h_st = jsObjectParking.getString("servicio_24h");
                                byte servicio_24h = Byte.valueOf(servicio_24h_st);

                                String descripcion = jsObjectParking.getString("descripcion");

                                Parking nuevoParking = new Parking(id, nombre, direccion, localidad,
                                        provincia, latitud, longitud, telefono,
                                        imagen, tipo, estado, precio,
                                        horario_apertura, horario_cierre, tiempo_maximo,
                                        plazas, altura_minima, adaptado_discapacidad,
                                        plazas_discapacidad, motos, aseos,
                                        tarjeta, seguridad, coches_electricos,
                                        lavado, servicio_24h, descripcion);
                                // Se añade el objeto creado a la colección de tipo List<Parking>.
                                list_parking.add(nuevoParking);
                                Log.e("Tamaño list_parking ", String.valueOf(list_parking.size()));
                            }
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                try
                {
                    Toast.makeText(getActivity(), "Error de petición de servicio: " + error.toString(), Toast.LENGTH_LONG).show();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        );

        // Se definen las políticas para la petición realizada. Recibe como argumento una instancia
        // de la clase DefaultRetryPolicy, que recibe como parámetros de entrada el tiempo inicial
        // de espera para la respuesta, el número máximo de intentos, y el multiplicador de retardo
        // de envío por defecto.
        jArray.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Se añade la petición a la cola con el objeto de tipo JsonArrayRequest.
        queue.add(jArray);
        return list_parking;
    }


    // TODO FIN

    //Tarea asíncrona que añade los markers
    private class addMarkers extends AsyncTask<String, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
/*
            // Creamos los objetos Parking
            Parking Molinon = new Parking(0, "Parking El Molinón", 43.535667, -5.635787, "987654321", "Público", 1.23, "Libre");
            Parking Europa = new Parking(1, "Parking Plaza Europa", 43.5385763,-5.664812, "987654322", "Privado", 1.23, "Completo");
            Parking Begona = new Parking(2, "Parking Begoña", 43.5374459,-5.6623837, "987654323",  "Privado", 1.23, "Libre");
            Parking Nautico = new Parking(3, "Parking El Náutico", 43.5420452,-5.6614269, "987654324",  "Privado", 1.23, "Libre");
            Parking Fomento = new Parking(4, "Parking Fomento", 43.5420643,-5.667993, "987654325",  "Privado", 1.23, "Completo", (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1);

            // Los metemos en el ArrayList de Parkings
            list_parking.add(Molinon);
            list_parking.add(Europa);
            list_parking.add(Begona);
            list_parking.add(Nautico);
            list_parking.add(Fomento);
            */
            obtenerParkings();
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            Log.e("Entró en ", "onPost");
            Log.e("Marker ", String.valueOf(list_parking.size()));

            //for que recorre la lista de parkings y crea los markers
            for(int i=0; i<list_parking.size(); i++)
            {
                Log.e("Entró en ", "el for");
                Parking parking = list_parking.get(i);
                // Creamos coordenadas
                LatLng Coordenadas = new LatLng(parking.getLatitud(), parking.getLongitud());
                Log.e("Marker Nombre ", String.valueOf(parking.getNombre()));
                // Mostramos los markers
                showMarker(Coordenadas, parking.getNombre(), parking.getTipo(), parking.getPrecio(), parking.getEstado());
            }

            //Escucha a que le demos click a algún marker
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                //Se identifica qué marker se está pulsando
                public void onInfoWindowClick(Marker marker) {
                    //Genera el intent y empieza la actividad a través del intent
                    Intent intent = clickMarker(marker);
                    startActivity(intent);
                }
            });
        }

        @Override
        protected Void doInBackground(String... params)
        {

            return null;
        }
    }
}