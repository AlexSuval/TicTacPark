package com.alex.tictacpark.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.alex.tictacpark.R;
import com.alex.tictacpark.activities.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Handler;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlarmaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlarmaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlarmaFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";
    private  SwipeRefreshLayout srl;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param SectionNumber Indica el número de la sección pulsada.
     * @return A new instance of fragment AlarmaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlarmaFragment newInstance(int SectionNumber) {
        AlarmaFragment fragment = new AlarmaFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, SectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public AlarmaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_alarma, container, false);

        // Ponemos el nombre "Alarma y gasto" en la barra
        ((MainActivity) getActivity()).setActionBarTitle("Alarma y gasto");

        //Se configura el TimePicker en formato 24h
        TimePicker tp =(TimePicker)v.findViewById(R.id.timePicker);
        tp.setIs24HourView(true);

        //Se rellenan los TextView
        RellenarTextView(v,false);

        //Se implementa el icono de refresco actualizando los datos
        srl = (SwipeRefreshLayout)v.findViewById(R.id.swipe_refresh_layout);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
        public void onRefresh(){
                RellenarTextView(v,true);
            }
        });
        return v;
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
        public void onFragmentInteraction(Uri uri);
    }

    private void RellenarTextView(View v, boolean actualizar) {
        TextView tiempo = (TextView) v.findViewById(R.id.tv_respuesta_tiempo);
        TextView gasto = (TextView) v.findViewById(R.id.tv_respuesta_gasto);
        TextView restante = (TextView) v.findViewById(R.id.tv_respuesta_restante);
        TextView actualizacion = (TextView) v.findViewById(R.id.tv_respuesta_actualizacion);

        /*if (actualizar) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Obtener hora actual para completar el TextView actualización
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    actualizacion.setText(sdf.format(cal.getTime()));
                }
            }
        }
*/
        // Obtener hora actual para completar el TextView actualización
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        actualizacion.setText(sdf.format(cal.getTime()));

        if(actualizar)
        {
            srl.setRefreshing(false); //Una vez actualizado se oculta el icono de refresco
        }
    }
}