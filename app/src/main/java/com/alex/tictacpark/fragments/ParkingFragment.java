package com.alex.tictacpark.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alex.tictacpark.R;
import com.alex.tictacpark.activities.MainActivity;
import com.alex.tictacpark.activities.ParkingDetalle;
import com.alex.tictacpark.models.Parking;
import com.alex.tictacpark.parsers.HistorialParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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


        Bundle b=getActivity().getIntent().getExtras();
        Parking parking_aparcado = new Parking();
        String longitud;
        String latitud;
        final String telefono;
        final String url_map;

        // Si este bundle está vacío es que se entró desde el menú (no se creó Intent)
        if(b==null)
        {
            Log.e("Activity","Menú");
            // Abrimos el fichero de preferencias
            SharedPreferences sp_mi_parking=getActivity().getSharedPreferences("PREFS_MI_PARKING", 0);
            SharedPreferences.Editor editor_mi_parking = sp_mi_parking.edit();

            // Recuperamos los campos del fichero de preferencias mi parking y los metemos en el
            // objeto parking_aparcado
            parking_aparcado.setId(sp_mi_parking.getInt("id", -1));//Segundo parámetro=Valor por defecto
            parking_aparcado.setNombre(sp_mi_parking.getString("nombre", ""));
            parking_aparcado.setDireccion(sp_mi_parking.getString("direccion", ""));
            parking_aparcado.setLocalidad(sp_mi_parking.getString("localidad", ""));
            parking_aparcado.setProvincia(sp_mi_parking.getString("provincia", ""));
            parking_aparcado.setLatitud(sp_mi_parking.getFloat("latitud", 0));
            parking_aparcado.setLongitud(sp_mi_parking.getFloat("longitud", 0));
            parking_aparcado.setTelefono(sp_mi_parking.getString("telefono", ""));
            parking_aparcado.setImagen(sp_mi_parking.getString("imagen", ""));
            parking_aparcado.setTipo(sp_mi_parking.getString("tipo", ""));
            parking_aparcado.setEstado(sp_mi_parking.getString("estado", ""));
            parking_aparcado.setPrecio(sp_mi_parking.getFloat("precio", 0));
            parking_aparcado.setHorario_Apertura(sp_mi_parking.getString("horario_apertura", ""));
            parking_aparcado.setHorario_Cierre(sp_mi_parking.getString("horario_cierre", ""));
            parking_aparcado.setTiempo_Maximo(sp_mi_parking.getFloat("tiempo_maximo", 0));
            parking_aparcado.setPlazas(sp_mi_parking.getInt("plazas", -1));
            parking_aparcado.setAltura_Minima(sp_mi_parking.getFloat("altura_minima", 0));

            boolean adaptado_discapacidad=sp_mi_parking.getBoolean("adaptado_discapacidad", false);
            parking_aparcado.setAdaptado_Discapacidad((byte) (adaptado_discapacidad ? 1 : 0)); //Convertimos de bool a byte para meterlo en el objeto

            boolean plazas_discapacidad=sp_mi_parking.getBoolean("plazas_discapacidad", false);
            parking_aparcado.setPlazas_Discapacidad((byte) (plazas_discapacidad ? 1 : 0));

            boolean motos=sp_mi_parking.getBoolean("motos", false);
            parking_aparcado.setMotos((byte) (motos ? 1 : 0));

            boolean aseos=sp_mi_parking.getBoolean("aseos", false);
            parking_aparcado.setAseos((byte) (aseos ? 1 : 0));

            boolean tarjeta=sp_mi_parking.getBoolean("tarjeta", false);
            parking_aparcado.setTarjeta((byte) (tarjeta ? 1 : 0));

            boolean seguridad=sp_mi_parking.getBoolean("seguridad", false);
            parking_aparcado.setSeguridad((byte) (seguridad ? 1 : 0));

            boolean coches_electricos=sp_mi_parking.getBoolean("coches_electricos", false);
            parking_aparcado.setCoches_Electricos((byte) (coches_electricos ? 1 : 0));

            boolean lavado=sp_mi_parking.getBoolean("lavado", false);
            parking_aparcado.setLavado((byte) (lavado ? 1 : 0));

            boolean servicio_24h=sp_mi_parking.getBoolean("servicio_24h", false);
            parking_aparcado.setServicio_24h((byte) (servicio_24h ? 1 : 0));

            parking_aparcado.setDescripcion(sp_mi_parking.getString("descripcion", ""));

            // Ponemos el nombre del parking en la barra
            ((MainActivity) getActivity()).setActionBarTitle(parking_aparcado.getNombre());
        }
        // Sino se entró pulsando un marker
        else
        {
            Log.e("Activity","Marker");
            // Reseteamos el ArrayList
            ArrayList<Parking> parking_clickado = new ArrayList<Parking>();
            // Recuperamos el ArrayList con la info del objeto Parking que hemos clickado
            Intent intent=getActivity().getIntent();
            parking_clickado=intent.getParcelableArrayListExtra("parking_clickado");
            parking_aparcado = parking_clickado.get(0);
            // Ponemos el nombre del parking en la barra
            ((ParkingDetalle) getActivity()).setActionBarTitle(parking_aparcado.getNombre());
        }

        final Parking p = parking_aparcado;

        Button bt_aparcar=(Button)view.findViewById(R.id.b_aparcar);

        SharedPreferences sp_mi_parking=getActivity().getSharedPreferences("PREFS_MI_PARKING", 0);
        int id=sp_mi_parking.getInt("id", -1);
        //String Nombre=sp_mi_parking.getString("nombre","");
        if(parking_aparcado.getId()==id)
            bt_aparcar.setText("DESAPARCAR");

        //TODO: Cambiar valores de la base de datos (nombre, tipo...). Cambiar y subrayar TextView Dirección y Teléfono

        //Asignar a variable el Botón Aparcar y asignar evento OnClick para realizar las
        //acciones correspondientes

        bt_aparcar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAparcar(v, p);
            }
        });

        // Recuperamos las coordenadas
        longitud=Double.toString(parking_aparcado.getLongitud());
        latitud=Double.toString(parking_aparcado.getLatitud());
        //Asignar a variable el TextView dirección y asignar evento OnClick para abrir
        //GoogleMaps al clickar en dirección
        url_map="http://maps.google.com/maps?q=loc:"+latitud+","+longitud;
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

        // Recuperamos el teléfono
        telefono=parking_aparcado.getTelefono();
        //Asignar a variable el TextView teléfono y asignar evento OnClick para abrir
        //la aplicación para llamar al clickar en teléfono
        TextView tv_telefono=(TextView)view.findViewById(R.id.tv_telefono_parking);
        tv_telefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se crea el Intent
                Intent intent=new Intent();
                intent.setAction("android.intent.action.DIAL");
                intent.setData(Uri.parse("tel:"+telefono));
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
    public void clickAparcar(View v, Parking parking){
        Button b = (Button) v.findViewById(R.id.b_aparcar);
        String Mensaje;

        SharedPreferences sp_mi_parking=getActivity().getSharedPreferences("PREFS_MI_PARKING", 0);
        SharedPreferences.Editor editor_mi_parking = sp_mi_parking.edit();

        if(b.getText().equals("APARCAR")){ // APARCAR
            Mensaje="MOSTRAR";
            int id=sp_mi_parking.getInt("id", -1);
            if(id==-1)  // No estamos aparcados en ningún sitio --> Podemos aparcar
            {
                // TODO Comprobar funcionamiento de float y byte
                // Rellenamos todos los campos del fichero de preferencias mi_parking con su id correspondiente
                // (que al ser distinto de -1 ya especifica que se ha aparcado)
                editor_mi_parking.putInt("id", parking.getId());
                editor_mi_parking.putString("nombre", parking.getNombre());
                editor_mi_parking.putString("direccion", parking.getDireccion());
                editor_mi_parking.putString("localidad", parking.getLocalidad());
                editor_mi_parking.putString("provincia", parking.getProvincia());
                editor_mi_parking.putFloat("latitud", (float) parking.getLatitud());
                editor_mi_parking.putFloat("longitud", (float) parking.getLongitud());
                editor_mi_parking.putString("telefono", parking.getTelefono());
                editor_mi_parking.putString("imagen", parking.getImagen());
                editor_mi_parking.putString("tipo", parking.getTipo());
                editor_mi_parking.putString("estado", parking.getEstado());
                editor_mi_parking.putFloat("precio", (float) parking.getPrecio());
                editor_mi_parking.putString("horario_apertura", parking.getHorario_Apertura());
                editor_mi_parking.putString("horario_cierre", parking.getHorario_Cierre());
                editor_mi_parking.putFloat("tiempo_maximo", (float) parking.getTiempo_Maximo());
                editor_mi_parking.putInt("plazas", parking.getPlazas());
                editor_mi_parking.putFloat("altura_minima", (float) parking.getAltura_Minima());
                editor_mi_parking.putBoolean("adaptado_discapacidad", parking.isAdaptado_Discapacidad() != 0); // Convierto de Byte a Boolean
                editor_mi_parking.putBoolean("plazas_discapacidad", parking.isPlazas_Discapacidad() != 0);
                editor_mi_parking.putBoolean("motos", parking.isMotos() != 0);
                editor_mi_parking.putBoolean("aseos", parking.isAseos() != 0);
                editor_mi_parking.putBoolean("tarjeta", parking.isTarjeta() != 0);
                editor_mi_parking.putBoolean("seguridad", parking.isSeguridad() != 0);
                editor_mi_parking.putBoolean("coches_electricos", parking.isCoches_Electricos() != 0);
                editor_mi_parking.putBoolean("lavado", parking.isLavado() != 0);
                editor_mi_parking.putBoolean("servicio_24h", parking.isServicio_24h() != 0);
                editor_mi_parking.putString("descripcion", parking.getDescripcion());

                //Se guarda la hora actual en el fichero de preferencias mi parking  hora_inicial.
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                editor_mi_parking.putString("hora_inicial", sdf.format(cal.getTime()));

                //Se cambia la apariencia del botón de APARCAR (activo) a DESAPARCAR (activo).
                b.setText(R.string.desaparcar);

            }
            else
            {
                if(id!=parking.getId()) {
                    // Diálogo que avisa al usuario de que ya está aparcado
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Ya aparcado")
                            .setMessage("Su coche se encuentra estacionado en otro parking," +
                                    "desaparque en la pestaña Mi parking antes de aparcar" +
                                    "en este estacionamiento")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create();
                    alertDialog.show();
                }
            }
        }
        else // DESAPARCAR
        {
            Mensaje="OCULTAR";
            //Limpiamos todos los campos del fichero de preferencias mi_parking y ponemos
            //id=-1(no aparcado)
            editor_mi_parking.clear();
            editor_mi_parking.putInt("id", -1);

            // Se cambia la apariencia del botón de DESAPARCAR (activo) a APARCAR (activo).
            b.setText(R.string.aparcar);

            //TODO
            // Se guarda la hora actual en el fichero de preferencias mi parking  hora_final.
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            editor_mi_parking.putString("hora_final", sdf.format(cal.getTime()));

            // Se crea el formato de la fecha en la que se abandona el aparcamiento
            SimpleDateFormat sdf_fecha = new SimpleDateFormat("dd/MM/yyyy");

            // Se añade fichero al historial generado a partir de los datos almacenados
            // en el fichero de preferencias mi parking y las operaciones correspondientes.
            HistorialParser parser = new HistorialParser();
            String JSON=parser.cargar(getActivity());
            JSONObject raiz;
            try{
                raiz = new JSONObject(JSON);
                JSONObject tarjeta=new JSONObject();
                tarjeta.put("nombre",parking.getNombre());
                tarjeta.put("fecha",sdf_fecha.format(cal.getTime())); // Se obtiene la fecha actual y se mete en el JSON
                tarjeta.put("duracion","3 horas, 20 minutos");
                tarjeta.put("precio","1.20€");
                tarjeta.put("precio_hora","("+parking.getPrecio()+"€/h)");
                JSONArray historial = raiz.getJSONArray("historial");
                historial.put(tarjeta);
                //TODO Guardar JSONObject, calcular duración y precio total
                // Pasamos la tarjeta a String
                String Historial_final=raiz.toString();
                // Se sobreescribe el historial con la nueva tarjeta al anterior
                OutputStreamWriter osw = new OutputStreamWriter(getActivity().openFileOutput("historial.json", Context.MODE_PRIVATE));
                osw.write(Historial_final);
                osw.close();
                Log.e("Guardar JSON", "OK");
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }

        //Se guardan los cambios en el fichero
        editor_mi_parking.commit();

        // Mandamos un mensaje a MainActivity para Activar/Ocultar las pestañas del menú
        // Para ello utilizamos un emisor de las notificaciones de aparcamiento
        Log.e("Emisor", "Mensaje de envío de notificación");
        Intent intent = new Intent("custom-event-name");
        // Añadimos la información al intent y lo mandamos.
        intent.putExtra("MOSTRAR_OCULTAR", Mensaje);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

        /*
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
        */
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