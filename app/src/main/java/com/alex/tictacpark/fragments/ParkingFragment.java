package com.alex.tictacpark.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alex.tictacpark.R;
import com.alex.tictacpark.activities.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ParkingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ParkingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParkingFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param SectionNumber Indica el número de la sección pulsada.
     * @return A new instance of fragment ParkingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ParkingFragment newInstance(int SectionNumber) {
        ParkingFragment fragment = new ParkingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, SectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ParkingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_parking, container, false);

        //Comprobar actividad
        //Si es MainActivity --> cargar preferencias de mi parking
        //Si es ParkingDetalle
            //Cargar SharedPreferences general
            //Comprobar si hay un coche aparcado
                //Si lo hay, comprobar nombre
                    //Si es el mismo, se pone botón desaparcar
                    //Sino, inhabilitar botón aparcar (no se podría aparcar hasta desaparcar antes)












        //TODO: Cambiar valores de la base de datos (nombre, tipo...). Cambiar y subrayar TextView Dirección y Teléfono

        // Si entramos a través de la actividad ParkingDetalle (es decir, clickando un Marker)
        // Recuperamos las coordenadas del fichero de preferencias general
        SharedPreferences sp_general=getActivity().getSharedPreferences("PREFS_GENERAL", 0);
        final String longitud=sp_general.getString("longitud", ""); //Segundo parámetro=Valor por defecto
        final String latitud=sp_general.getString("latitud", "");

        final String url_map="http://maps.google.com/maps?q=loc:"+latitud+","+longitud;

        //Asignar a variable el Botón Aparcar y asignar evento OnClick para realizar las
        //acciones correspondientes
        Button bt_aparcar=(Button)view.findViewById(R.id.b_aparcar);
        bt_aparcar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAparcar(v,longitud,latitud);
            }
        });

        //Asignar a variable el TextView dirección y asignar evento OnClick para abrir
        //GoogleMaps al clickar en dirección
        TextView tv_direccion=(TextView)view.findViewById(R.id.tv_direccion_parking);
        tv_direccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se crea el Intent
                Intent intent=new Intent(Intent.ACTION_VIEW,
                        Uri.parse(url_map));
                startActivity(intent);
            }
        });

        //Asignar a variable el TextView teléfono y asignar evento OnClick para abrir
        //la aplicación para llamar al clickar en teléfono
        TextView tv_telefono=(TextView)view.findViewById(R.id.tv_telefono_parking);
        tv_telefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se crea el Intent
                Intent intent=new Intent();
                intent.setAction("android.intent.action.DIAL");
                intent.setData(Uri.parse("tel:"+"987654321"));
                startActivity(intent);
            }
        });

        view.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v,MotionEvent event){
                return true;
            }
        });

        return view;
    }

    // Ponemos la variable a true al dar click a APARCAR
    public void clickAparcar(View v, String longitud, String latitud){
        Button b = (Button) v.findViewById(R.id.b_aparcar);

        SharedPreferences sp_general=getActivity().getSharedPreferences("PREFS_GENERAL", 0);
        SharedPreferences.Editor editor_general = sp_general.edit();

        SharedPreferences sp_mi_parking=getActivity().getSharedPreferences("PREFS_MI_PARKING", 0);
        SharedPreferences.Editor editor_mi_parking = sp_mi_parking.edit();

        boolean aparcado = sp_general.getBoolean("aparcado",false); //Recupera si está aparcado o no

        if (!aparcado) {
            //Guardamos la hora actual en la que se ha aparcado
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            editor_mi_parking.putString("hora", sdf.format(cal.getTime()));

            //Cambiamos el estado a aparcado
            editor_general.putBoolean("aparcado", true);

            //Guardamos la longitud y latitud en el fichero de preferencias del parking aparcado
            editor_mi_parking.putString("longitud",longitud);
            editor_mi_parking.putString("latitud",longitud);

            // Se cambia la apariencia del botón de Aparcar a Desaparcar
            b.setText(R.string.desaparcar);

            // Se muestran todas las pestañas del menú
        }
        else{
            // Cambiamos el estado a desaparcado
            editor_general.putBoolean("aparcado", false);

            // Se cambia la apariencia del botón de Desaparcar a Aparcar
            b.setText(R.string.aparcar);

            //TODO: Añadir histórico al historial
        }
        editor_general.commit(); //Se guardan los cambios en el fichero
        editor_mi_parking.commit();
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