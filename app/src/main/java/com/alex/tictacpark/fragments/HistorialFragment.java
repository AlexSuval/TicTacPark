package com.alex.tictacpark.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.tictacpark.R;
import com.alex.tictacpark.activities.MainActivity;
import com.alex.tictacpark.activities.ParkingDetalle;
import com.alex.tictacpark.adapters.HistorialAdapter;
import com.alex.tictacpark.models.Historial;
import com.alex.tictacpark.models.Parking;
import com.alex.tictacpark.parsers.HistorialParser;
import com.alex.tictacpark.parsers.ParkingsParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HistorialFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistorialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistorialFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";
    private RecyclerView recyclerView;
    private ProgressBar pb;

    // Adapter global para ser usado en todas las clases. Se requiere su uso en onContextItemSelected
    HistorialAdapter adapter;

    // ArrayList<Historial> global para ser usado en todas las clases
    ArrayList<Historial> historial_final;

    // Declaramos e inicializamos la ArrayList list_parking, que contendrá todos los objetos Parking
    private ArrayList<Parking> list_parking=new ArrayList<Parking>();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param SectionNumber Indica el número de la sección pulsada.
     * @return A new instance of fragment HistorialFragment.
     */
    public static HistorialFragment newInstance(int SectionNumber) {
        HistorialFragment fragment = new HistorialFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, SectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public HistorialFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        // Asignamos un layout
        rootView=inflater.inflate(R.layout.recycler_view, container, false); //layout

        // Ponemos el nombre "Historial" en la barra
        ((MainActivity) getActivity()).setActionBarTitle("Historial");

        // Configuraciones del recyclerView
        recyclerView=(RecyclerView)rootView.findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Se muestra el historial
        mostrarHistorial(rootView);

        // Carga el menú de borrar historial
        setHasOptionsMenu(true);

        return rootView;
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
        public void onFragmentInteraction(Uri uri);
    }

    // Cargamos el menú borrar historial
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_borrar_historial, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.borrar:
                // Se crea un cuadro de diálogo
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Borrar historial");
                builder.setMessage("¿Desea borrar todo su historial definitivamente?");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Se borra el historial
                        BorrarHistorial();
                        // Se carga de nuevo el adapter, para actualizar la vista
                        ArrayList<Historial> historial_vacio = new ArrayList<>();
                        HistorialAdapter adapter = new HistorialAdapter(historial_vacio, R.layout.card_view_historial, getActivity());
                        recyclerView.setAdapter(adapter);
                        TextView tv_historial_vacio = (TextView) getView().findViewById(R.id.tv_historial_vacio);
                        tv_historial_vacio.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Método para sobreescribir un fichero del historial vacío --> BorrarHistorial
    private void BorrarHistorial(){
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
            fos=getActivity().openFileOutput("historial.json", Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        Log.e("JSON", string);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final int index=adapter.getPosicion();

        switch (item.getItemId()) {
            case R.id.acceder_parking:
                accederParking(index);
                break;
            case R.id.borrar_entrada:
                // Se crea un cuadro de diálogo
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Borrar entrada historial");
                builder.setMessage("¿Desea borrar esta entrada definitivamente?");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        borrarEntrada(index);
                        // Se carga de nuevo el adapter, para actualizar la vista del historial
                        View rootView = getView();
                        // Se notifica al adapter el id borrado
                        adapter.notifyItemRemoved(index);
                        mostrarHistorial(rootView);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    // Método para acceder a la información del parking correspondiente a la entrada del historial seleccionada.
    private void accederParking(int posicion){
        Parking parking_clickado = buscarParking(posicion);
        if (parking_clickado!=null){
            // Generamos el intent
            Intent intent=new Intent(getActivity(),ParkingDetalle.class);
            // Metemos el parking clickado en un ArrayList, para no tener que pasarle a la actividad
            // el ArrayList con todos los objetos parking
            ArrayList<Parking> parking_clickado_list = new ArrayList<Parking>();
            parking_clickado_list.add(parking_clickado);
            // Pasamos el parking_clickado a la actividad
            intent.putParcelableArrayListExtra("parking_clickado", parking_clickado_list);
            // Empieza la actividad a través del intent
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getActivity(), "El parking seleccionado ya no existe en TicTacPark.", Toast.LENGTH_LONG).show();
        }
    }

    private Parking buscarParking(int posicion){
        Historial h = historial_final.get(posicion);
        int id=h.getId();
        // Como los parkings se habrán actualizado (cargado de la DB) al acceder a la app,
        // no repetimos un nuevo acceso, sino que cargamos los almacenados en el JSON
        ParkingsParser parser = new ParkingsParser(); // Parsear JSON
        list_parking = parser.parse(getActivity());

        for (Parking p : list_parking)
        {
            if (id == p.getId())
                return p;
        }
        return null;
    }

    // Método para borrar la entrada del historial seleccionada.
    private void borrarEntrada(int posicion){
        HistorialParser parser = new HistorialParser();
        String JSON=parser.cargar(getActivity());
        JSONObject raiz;
        try{
            raiz = new JSONObject(JSON);
            JSONArray historial = raiz.getJSONArray("historial");

            // Eliminamos la posición tamaño-posición-1 por haber dado la vuelta al array para mostrar las entradas más recientes primero
            historial.remove(historial.length() - posicion - 1);

            // Pasamos a String
            String Historial_final = raiz.toString();
            // Se sobreescribe el historial sin la tarjeta eliminada al anterior
            OutputStreamWriter osw = new OutputStreamWriter(getActivity().openFileOutput("historial.json", Context.MODE_PRIVATE));
            osw.write(Historial_final);
            osw.close();
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private void mostrarHistorial(View rootView){
        // Parsear JSON
        HistorialParser parser=new HistorialParser();
        historial_final = parser.parse(getActivity());

        // Para dar la vuelta al historial: Se muestran primero las últimas entradas
        Collections.reverse(historial_final);

        // Se carga el adapter
        adapter=new HistorialAdapter(historial_final,R.layout.card_view_historial,getActivity());
        recyclerView.setAdapter(adapter);

        // Si está vacío, se pone visible el mensaje de que no existen entradas para mostrar y
        // se oculta la progress bar
        if(historial_final.size()==0)
        {
            TextView tv_historial_vacio=(TextView)rootView.findViewById(R.id.tv_historial_vacio);
            tv_historial_vacio.setVisibility(View.VISIBLE);

            ProgressBar pb=(ProgressBar)rootView.findViewById(R.id.progressBar);
            pb.setVisibility(View.GONE);
        }
        // Sino, se muestra el historial y se oculta la progress bar
        else
        {
            ProgressBar pb=(ProgressBar)rootView.findViewById(R.id.progressBar);
            pb.setVisibility(View.GONE);
        }
    }
}