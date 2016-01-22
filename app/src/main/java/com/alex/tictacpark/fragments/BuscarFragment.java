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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.location.LocationListener;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

    //Inicializamos el ArrayList, que contendrá todos los objetos Parking
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
                    //Muestra mensaje al usuario de fallo con el GPS
                    Toast.makeText(getActivity(),"El GPS se ha activado",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProviderDisabled(String provider) {
                    //Muestra mensaje al usuario de fallo con el GPS
                    Toast.makeText(getActivity(),"El GPS se ha desactivado",
                            Toast.LENGTH_SHORT).show();
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
                index=p.getId();
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
    private void showMarker(LatLng coordenadas, String nombre)
    {
        mMap.addMarker(new MarkerOptions().position(coordenadas).title(nombre));
    }

    //Tarea asíncrona que añade los markers
    private class addMarkers extends AsyncTask<String, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            // Creamos los objetos Parking
            Parking Molinon = new Parking(0, "Parking El Molinón", 43.535667, -5.635787, "987654321");
            Parking Europa = new Parking(1, "Parking Plaza Europa", 43.5385763,-5.664812, "987654322");
            Parking Begona = new Parking(2, "Parking Begoña", 43.5374459,-5.6623837, "987654323");
            Parking Nautico = new Parking(3, "Parking El Náutico", 43.5420452,-5.6614269, "987654324");
            Parking Fomento = new Parking(4, "Parking Fomento", 43.5420643,-5.667993, "987654325");

            // Los metemos en el ArrayList de Parkings
            list_parking.add(Molinon);
            list_parking.add(Europa);
            list_parking.add(Begona);
            list_parking.add(Nautico);
            list_parking.add(Fomento);
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            //for que recorre la lista de parkings y crea los markers
            for(int i=0; i<list_parking.size(); i++)
            {
                Parking parking = list_parking.get(i);
                // Creamos coordenadas
                LatLng Coordenadas = new LatLng(parking.getLatitud(), parking.getLongitud());
                // Mostramos los markers
                showMarker(Coordenadas, parking.getNombre());
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