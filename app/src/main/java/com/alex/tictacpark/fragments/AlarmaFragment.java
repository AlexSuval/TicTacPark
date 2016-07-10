package com.alex.tictacpark.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alex.tictacpark.R;
import com.alex.tictacpark.activities.MainActivity;
import com.alex.tictacpark.helpers.AlarmaReceiver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmaFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";
    private  SwipeRefreshLayout srl;
    View v;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param SectionNumber Indica el número de la sección pulsada.
     * @return A new instance of fragment AlarmaFragment.
     */
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

    AlarmManager manager;
    private PendingIntent pendingIntent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_alarma, container, false);

        // Ponemos el nombre "Alarma y gasto" en la barra
        ((MainActivity) getActivity()).setActionBarTitle("Alarma y gasto");

        //Se configura el TimePicker en formato 24h
        final TimePicker tp =(TimePicker)v.findViewById(R.id.timePicker);
        tp.setIs24HourView(true);

        // Asignar a variable al switch Activada/Desactivada y asignar evento onCheckedChangeListener
        // para realizar las acciones correspondientes
        manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlarmaReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);

        // Editor de preferencias General
        SharedPreferences sp_general=getActivity().getSharedPreferences("PREFS_GENERAL", 0);
        final SharedPreferences.Editor editor_general = sp_general.edit();

        Switch sw = (Switch)v.findViewById(R.id.switch1);

        // Comprobamos si la alarma está activada o no
        boolean estado_alarma = sp_general.getBoolean("alarma",false);
        if (estado_alarma)  // Si está activada
        {
            // Ponemos el switch Activado
            sw.setChecked(true);
        }
        else    // Si no está activada
        {
            // Ponemos el switch Desactivado
            sw.setChecked(false);

            // Para que en el caso de que desaparqué y había una alarma pendiente se cancele la alarma
            if (manager != null) {
                manager.cancel(pendingIntent);
            }
        }

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) // Switch ON --> Se activa la alarma
                {
                    // Ponemos alarma=true en el archivo de preferencias general
                    editor_general.putBoolean("alarma", true);
                    Log.e("Alarma: ", "ON");

                    // Se calcula el tiempo hasta la hora de alarma escogida:
                    Calendar current = Calendar.getInstance();
                    Calendar cal = Calendar.getInstance();

                    // Guardamos en el fichero de preferencias general los valores introducidos en el TimePicker
                    editor_general.putInt("hora_alarma", tp.getCurrentHour());
                    editor_general.putInt("minutos_alarma", tp.getCurrentMinute());
                    Log.e("hora en fichero", Integer.toString(tp.getCurrentHour()));
                    Log.e("minutos en fichero", Integer.toString(tp.getCurrentMinute()));

                    // Le pasamos al Calendar los datos introducidos en el TimePicker
                    cal.set(Calendar.HOUR_OF_DAY, tp.getCurrentHour()); //alarmHour from TimePicker
                    cal.set(Calendar.MINUTE, tp.getCurrentMinute()); //alarmMinute from TimePicker
                    cal.set(Calendar.SECOND, 0);    // Segundos y milisegundos a 0
                    cal.set(Calendar.MILLISECOND, 0);

                    // Se comprueba si la hora introducida en el TimePicker es anterior a la hora actual
                    if(cal.compareTo(current) <= 0)
                    {
                        // La hora introducida ya ha pasado
                        Toast.makeText(getActivity(),
                                "La hora introducida es incorrecta",
                                Toast.LENGTH_LONG).show();
                        // Ponemos alarma=false en el archivo de preferencias general
                        editor_general.putBoolean("alarma", false);
                        Log.e("Alarma: ", "OFF");
                    }
                    else // Se activa la alarma
                    {
                        Log.e("Tiempo de alarma: ", Long.toString(cal.getTimeInMillis()));
                        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                    }
                }
                else // Switch OFF --> Se desactiva la alarma
                {
                    // Ponemos alarma=false en el archivo de preferencias general
                    editor_general.putBoolean("alarma", false);
                    Log.e("Alarma: ", "OFF");
                    if (manager!= null)
                    {
                        manager.cancel(pendingIntent);
                    }
                }
                //Se guardan los cambios en el fichero
                editor_general.commit();
            }

        });

        // Se rellenan los TextView
        RellenarTextView(v);

        // Carga el menú de actualización (refresco)
        setHasOptionsMenu(true);

        return v;
    }

    // Método para rellenar los TextView
    private void RellenarTextView(View v) {
        TextView tiempo = (TextView) v.findViewById(R.id.tv_respuesta_tiempo);
        TextView gasto = (TextView) v.findViewById(R.id.tv_respuesta_gasto);
        TextView restante = (TextView) v.findViewById(R.id.tv_respuesta_restante);
        TextView actualizacion = (TextView) v.findViewById(R.id.tv_respuesta_actualizacion);

        // Obtener hora actual para completar el TextView actualización
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        actualizacion.setText(sdf.format(cal.getTime()));

        // Se calcula el tiempo consumido, el gasto acumulado y el tiempo restante para que se active la alarma:
        SharedPreferences sp_mi_parking=getActivity().getSharedPreferences("PREFS_MI_PARKING", 0);
        String Hora_final = cal.getTime().toString();
        String Hora_inicial = sp_mi_parking.getString("hora_inicial", "0");
        long horas=0;
        long minutos=0;

        float precio_hora = sp_mi_parking.getFloat("precio", 0);
        float horas_float=0;
        float gasto_acumulado=0;

        SharedPreferences sp_general=getActivity().getSharedPreferences("PREFS_GENERAL", 0);
        int hora_alarma = sp_general.getInt("hora_alarma", 0);
        Log.e("hora alarma", Integer.toString(hora_alarma));
        int minutos_alarma = sp_general.getInt("minutos_alarma", 0);
        Log.e("minutos alarma", Integer.toString(minutos_alarma));
        long horas_activacion=0;
        long minutos_activacion=0;
        int hora_actual = cal.get( Calendar.HOUR_OF_DAY );
        int minutos_actual = cal.get( Calendar.MINUTE );

        try{
            DateFormat formatter = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
            Date date_final = formatter.parse(Hora_final);
            Date date_inicial = formatter.parse(Hora_inicial);

            // Tiempo consumido:
            long diff = date_final.getTime() - date_inicial.getTime();
            horas = diff / (1000 * 60 * 60);
            minutos = diff / (1000 * 60) - (horas*60);
            if (horas>0)
                tiempo.setText(horas + " horas, "+ minutos + " minutos"); // Ejemplo: "3 horas, 20 minutos"
            else
                tiempo.setText(minutos + " minutos"); // Ejemplo: "20 minutos"

            // Gasto acumulado:
            float diferencia = date_final.getTime() - date_inicial.getTime();
            horas_float = diferencia / (1000 * 60 * 60);
            gasto_acumulado = precio_hora * horas_float;
            gasto.setText(String.format("%.2f", gasto_acumulado) + " €"); // Redondeamos el gasto acumulado a 2 decimales
        }
        catch(Exception e){
            Log.e("Fallo al formatear: ", "Fallo");
        }

        // Tiempo restante para que se active la alarma
        double activacion_alarma = (double) (hora_alarma * 60 + minutos_alarma);
        double tiempo_actual = (double) (hora_actual * 60 + minutos_actual);
        double tiempo_restante = activacion_alarma - tiempo_actual;

        boolean estado_alarma = sp_general.getBoolean("alarma",false);
        if (estado_alarma && tiempo_restante>0) // Si la alarma está activada (para que funcione en caso de desactivar la alarma y salir y entrar de nuevo en el fragment)
        {
            horas_activacion = (long) (tiempo_restante / 60);
            minutos_activacion = (long) (tiempo_restante - horas_activacion * 60);
            if(hora_alarma>hora_actual)
                restante.setText(horas_activacion + " horas, " + minutos_activacion + " minutos"); // Ejemplo: "3 horas, 20 minutos"
            else
                restante.setText(minutos_activacion + " minutos"); // Ejemplo: "20 minutos"
        }
        else
            restante.setText("Alarma desactivada");
    }

    // Cargamos el menú actualizar alarma
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_actualizar_alarma, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.actualizar:
                // Se refrescan los datos
                RellenarTextView(v);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}