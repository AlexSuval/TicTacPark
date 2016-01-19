package com.alex.tictacpark.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alex.tictacpark.R;
import com.alex.tictacpark.activities.MainActivity;
import com.alex.tictacpark.adapters.HistorialAdapter;
import com.alex.tictacpark.models.Historial;
import com.alex.tictacpark.parsers.HistorialParser;

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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param SectionNumber Indica el número de la sección pulsada.
     * @return A new instance of fragment HistorialFragment.
     */
    // TODO: Rename and change types and number of parameters
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

        // Parsear JSON
        HistorialParser parser=new HistorialParser();
        ArrayList<Historial> historial_final = parser.parse(getActivity());

        // Para dar la vuelta al historial: Se muestran primero las últimas entradas
        Collections.reverse(historial_final);

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
            HistorialAdapter adapter=new HistorialAdapter(historial_final,R.layout.card_view_historial,getActivity());
            recyclerView.setAdapter(adapter);
            ProgressBar pb=(ProgressBar)rootView.findViewById(R.id.progressBar);
            pb.setVisibility(View.GONE);
        }

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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}