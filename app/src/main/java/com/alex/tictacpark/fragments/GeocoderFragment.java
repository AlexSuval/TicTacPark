package com.alex.tictacpark.fragments;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alex.tictacpark.R;
import com.alex.tictacpark.adapters.GeoAutoCompleteAdapter;
import com.alex.tictacpark.helpers.DelayAutoCompleteTextView;
import com.alex.tictacpark.models.GeoSearchResult;
import com.alex.tictacpark.parsers.ObtenerCoordenadasParser;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GeocoderFragment extends Fragment {

    private Integer THRESHOLD = 2;
    private DelayAutoCompleteTextView geo_autocomplete;
    private ImageView geo_autocomplete_clear;
    private static final String ARG_SECTION_NUMBER = "section_number";
    String URL;
    private OnFragmentInteractionListener mListener;

    public GeocoderFragment() {
        // Required empty public constructor
    }

    public static GeocoderFragment newInstance(int SectionNumber) {
        GeocoderFragment fragment = new GeocoderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, SectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        //Asignamos un layout
        rootView = inflater.inflate(R.layout.geo_search_result_item, container, false); //layout

        final ArrayList<String> direccion_array= new ArrayList<>();

        geo_autocomplete_clear = (ImageView) rootView.findViewById(R.id.geo_autocomplete_clear);

        geo_autocomplete = (DelayAutoCompleteTextView) rootView.findViewById(R.id.geo_autocomplete);
        geo_autocomplete.setThreshold(THRESHOLD);
        geo_autocomplete.setAdapter(new GeoAutoCompleteAdapter(getActivity())); // 'this' is Activity instance

        geo_autocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                GeoSearchResult result = (GeoSearchResult) adapterView.getItemAtPosition(position);
                String direccion = result.getAddress();
                direccion_array.clear();
                direccion_array.add(direccion);
                geo_autocomplete.setText(result.getAddress());
                String DireccionURL = direccion_array.get(0).replace(" ", "+").replace("\n", "+");
                URL = "http://maps.google.com/maps/api/geocode/json?address=" + DireccionURL;
                descargarJSON dj=new descargarJSON();
                dj.execute(URL);
            }
        });

        geo_autocomplete.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    geo_autocomplete_clear.setVisibility(View.VISIBLE);
                } else {
                    geo_autocomplete_clear.setVisibility(View.GONE);
                }
            }
        });

        geo_autocomplete_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                geo_autocomplete.setText("");
            }
        });

        return rootView;
    }



    //Tarea asíncrona para descargar el JSON de la API de Google Maps Places
    private class descargarJSON extends AsyncTask<String, Void, Void>
    {
        String destino="";
        String LATITUD="";
        String LONGITUD="";
        LatLng coordenadas;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            // Inicializamos los Strings para el JSON a descargar y el tiempo que obtendremos
            LATITUD="";
            LONGITUD="";
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            mListener.onFragmentInteraction(coordenadas);
        }

        @Override
        protected Void doInBackground(String... params)
        {
            ObtenerCoordenadasParser coordenadasParser=new ObtenerCoordenadasParser();
            // Obtenemos el String con el JSON descargado
            String JSON_COORDENADAS=coordenadasParser.download(params[0]);
            coordenadas = coordenadasParser.parse(JSON_COORDENADAS);

            return null;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(LatLng coordenadas);
    }

    // Método para cuando el fragment se acopla a la actividad
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

}
