package com.alex.tictacpark.fragments;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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
import java.util.logging.Handler;

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
            // Ponemos el TimePicker con los valores introducidos para la alarma
            int hora_alarma = sp_general.getInt("hora_alarma", 0);
            int minutos_alarma = sp_general.getInt("minutos_alarma",0);
            Log.e("hora que recupero", Integer.toString(hora_alarma));
            Log.e("minutos que recupero", Integer.toString(minutos_alarma));
            // TODO Esto en principio sólo funcionaría para dispositivos con Lollipop
            //tp.setHour(hora_alarma);
            //tp.setMinute(minutos_alarma);
            Log.e("hora que pongo en el tp", Integer.toString(tp.getCurrentHour()));
            Log.e("min que pongo en el tp", Integer.toString(tp.getCurrentMinute()));
        }
        else    // Sino, ponemos el switch Desactivado
        {
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
                    //tp.clearFocus();
                    //tp.setIs24HourView(true);
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
                    if(cal.compareTo(current) <= 0){
                        //The set Date/Time already passed
                        Toast.makeText(getActivity(),
                                "La hora introducida es incorrecta",
                                Toast.LENGTH_LONG).show();
                        // TODO REFRESCAR PÁGINA PONIENDO EL SWITCH EN DESCONECTADO
                        // COMPROBAR
                    }
                    else{
                        //activarAlarma(cal);
                        //manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + diff, pendingIntent);
                        Log.e("Tiempo de alarma: ", Long.toString(cal.getTimeInMillis()));
                        // TODO COMPROBAR QUE PONE BIEN ALARMAS PARA EL DÍA SIGUIENTE
                        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                    }

                    // Método peor? para calcular la hora en la que se debe activar la alarma
                    /*
                    // Obtener hora actual como hora inicial
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

                    // Hora final: La que sacamos del timepicker.
                    long hora=tp.getCurrentHour();
                    long minuto=tp.getCurrentMinute();
                    String alarma_final=hora+":"+minuto;
                    String alarma_inicial=cal.getTime().toString();
                    long diff=0;
                    try{
                        DateFormat formatter = new SimpleDateFormat("kk:mm");
                        Date date_alarma_inicial = sdf.parse(alarma_inicial);
                        Date date_alarma_final = formatter.parse(alarma_final);
                        diff = date_alarma_final.getTime() - date_alarma_inicial.getTime(); // diff en ms
                        Log.e("Tiempo para la alarma", Long.toString(diff));
                    }
                    catch(Exception e){
                        Log.e("Fallo al formatear: ", "Fallo");
                    }
                    */
                }
                else // Switch OFF --> Se desactiva la alarma
                {
                    // Ponemos alarma=false en el archivo de preferencias general
                    editor_general.putBoolean("alarma", false);
                    Log.e("Alarma: ", "OFF");
                    if (manager!= null) {
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

        // Se calcula el tiempo consumido:
        SharedPreferences sp_mi_parking=getActivity().getSharedPreferences("PREFS_MI_PARKING", 0);
        String Hora_final = cal.getTime().toString();
        String Hora_inicial = sp_mi_parking.getString("hora_inicial", "0");
        long horas=0;
        long minutos=0;
        try{
            DateFormat formatter = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
            Date date_final = formatter.parse(Hora_final);
            Date date_inicial = formatter.parse(Hora_inicial);

            long diff = date_final.getTime() - date_inicial.getTime();
            horas = diff / (1000 * 60 * 60);
            minutos = diff / (1000 * 60) - (horas*60);

            tiempo.setText(horas + " horas, "+ minutos + " minutos"); // Ejemplo: "3 horas, 20 minutos"
        }
        catch(Exception e){
            Log.e("Fallo al formatear: ", "Fallo");
        }
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