package com.alex.tictacpark.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alex.tictacpark.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Asignamos un layout
        setContentView(R.layout.fragment_buscar);
        //Decimos dónde se va a inflar el fragment
        SupportMapFragment mapFragment=(SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //Inicializamos el objeto GoogleApiClient con las propiedades que va a tener
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        //Inicializamos el mLocationManager
        mLocationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        }
*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        //Asignamos un layout
        rootView=inflater.inflate(R.layout.fragment_buscar, container, false); //layout
        mMapView=(MapView) rootView.findViewById(R.id.map); //mapa en sí
        mMapView.onCreate(savedInstanceState);

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
        LatLng latLng=new LatLng(43.52,-5.67);
        CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(latLng,14); //zoom=14
        mMap.animateCamera(cameraUpdate);
        addMarkers=new addMarkers(); //Inicializamos addMarkers
        addMarkers.execute(""); //Lanzamos addMarkers pasándole la url
    }

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

    /*@Override
    // Estado tras la conexión
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                REQUEST,
                this);
    }

    @Override
    // Estado cuando se cambia el foco de la localización
    public void onLocationChanged(Location location) {
        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
        CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(latLng,10); //zoom=10
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    // Manipula el mapa una vez que esté disponible
    // Si Google Play Services no está instalado en el dispositivo del usuario le advierte de que lo instale
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // No hacer nada
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // No hacer nada
    }

    @Override
    public boolean onMyLocationButtonClick(){
        Toast.makeText(getActivity(),"Click",Toast.LENGTH_SHORT).show();
        return false;
    }*/

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
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            LatLng Molinon = new LatLng(43.535667, -5.635787);
            LatLng Europa = new LatLng(43.5385763,-5.664812);
            LatLng Begona = new LatLng(43.5374459,-5.6623837);
            LatLng Nautico = new LatLng(43.5420452,-5.6614269);
            LatLng Fomento = new LatLng(43.5420643,-5.667993);

            showMarker(Molinon,"Parking El Molinón");
            showMarker(Europa,"Parking Plaza Europa");
            showMarker(Begona,"Parking Begoña");
            showMarker(Nautico,"Parking El Náutico");
            showMarker(Fomento, "Parking Fomento");
        }

        @Override
        protected Void doInBackground(String... params)
        {

            return null;
        }
    }
}