package com.alex.tictacpark.fragments;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
        String nombre;
        String tipo;
        double precio;
        String texto_descripcion;
        int plazas;
        String horario_apertura;
        String horario_cierre;
        int tiempo_maximo;
        double altura_maxima;
        String estado;
        String imagen;

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
            parking_aparcado.setTiempo_Maximo(sp_mi_parking.getInt("tiempo_maximo", 0));
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
        if(telefono.equals("0"))
            tv_telefono.setText("Sin teléfono.");
        else
        {
            tv_telefono.setTextColor(Color.parseColor("#ff33b5e5")); // "holo_blue_light" == #ff33b5e5
            tv_telefono.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Se crea el Intent
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.DIAL");
                    intent.setData(Uri.parse("tel:" + telefono));
                    startActivity(intent);
                }
            });
        }

        // Se envía evento táctil a la vista
        view.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v,MotionEvent event){
                return true;
            }
        });

        // Añadimos nombre, tipo, precio/hora y estado del parking a la vista del fragment del parking:

        // Recuperamos el nombre del parking
        nombre=parking_aparcado.getNombre();
        // Ponemos el nombre del parking en el TextView correspondiente
        TextView tv_nombre=(TextView)view.findViewById(R.id.tv_nombre_parking);
        tv_nombre.setText(nombre);

        // Recuperamos el tipo de parking
        tipo=parking_aparcado.getTipo();
        // Recuperamos el precio/hora del parking
        precio=parking_aparcado.getPrecio();
        // Ponemos el tipo de parking en el TextView correspondiente
        TextView tv_tipo=(TextView)view.findViewById(R.id.tv_tipo_parking);
        if(precio==0)
            tv_tipo.setText(tipo+": Gratuito.");
        else
            tv_tipo.setText(tipo+": "+String.format("%.2f", precio)+"€/h."); // Redondeamos el precio/hora a 2 decimales

        // Recuperamos el texto con la descripción del parking
        texto_descripcion=parking_aparcado.getDescripcion();
        // Recuperamos el número de plazas del parking
        plazas=parking_aparcado.getPlazas();
        // Recuperamos el horario de apertura del parking
        horario_apertura=parking_aparcado.getHorario_Apertura();
        // Recuperamos el horario de cierre del parking
        horario_cierre=parking_aparcado.getHorario_Cierre();
        // Recuperamos el tiempo máximo de estacionamiento en el parking
        tiempo_maximo=parking_aparcado.getTiempo_Maximo();
        // Recuperamos la altura máxima del parking
        altura_maxima=parking_aparcado.getAltura_Minima();

        // Ponemos el texto con la descripción del parking en el TextView correspondiente
        TextView tv_texto_descripcion =(TextView)view.findViewById(R.id.tv_texto_descripcion);
        if ("No".equals(texto_descripcion))
            tv_texto_descripcion.setText("");
        else
            tv_texto_descripcion.setText("- Descripción: " + texto_descripcion + "\n");
        tv_texto_descripcion.append("- Número de plazas: " + plazas + " plazas." + "\n");
        tv_texto_descripcion.append("- Horario de apertura: " + horario_apertura + " h." + "\n");
        tv_texto_descripcion.append("- Horario de cierre: " + horario_cierre + " h." + "\n");
        if (tiempo_maximo == 0)
            tv_texto_descripcion.append("- Tiempo máximo de estacionamiento: No hay tiempo máximo de ocupación de plaza." + "\n");
        else
            tv_texto_descripcion.append("- Tiempo máximo de estacionamiento: " + tiempo_maximo + " horas." + "\n");
        if (altura_maxima == 0)
            tv_texto_descripcion.append("- Altura máxima permitida: Sin restricción de altura, se trata de un parking al aire libre.");
        else
            tv_texto_descripcion.append("- Altura máxima permitida: " + String.format("%.2f", altura_maxima) + " metros.");

        // Ponemos el texto con un mensaje de advertencia en caso de que el tipo de parking sea
        // Particular, en el TextView correspondiente
        TextView tv_advertencia =(TextView)view.findViewById(R.id.tv_advertencia_particulares);
        if(tipo.equals("Particular"))
        {
            tv_advertencia.append("- Parking particular: Le recomendamos que se ponga en contacto previamente con el propietario para consultar disponibilidad y reservar su plaza.");
        }

        // Recuperamos el estado del parking
        estado=parking_aparcado.getEstado();
        // Ponemos el estado del parking en el Button correspondiente
        Button b_estado=(Button)view.findViewById(R.id.b_estado_parking);
        b_estado.setText(estado);
        if(estado.equals("Libre")) {
            // Se cambia la apariencia del botón a verde con el texto LIBRE.
            b_estado.setBackgroundColor(Color.GREEN);
        }
        else {
            // Se cambia la apariencia del botón a rojo con el texto COMPLETO.
            b_estado.setBackgroundColor(Color.RED);
        }

        // Recuperamos la imagen/fotografía del parking
        imagen=parking_aparcado.getImagen();
        // Si la hay, ponemos la imagen del parking en la ImageView correspondiente,
        // sino dejamos el logo de nuestra app
        ImageView iv_imagen=(ImageView)view.findViewById(R.id.iv_foto_parking);
        if (!"No".equals(imagen))
            new DescargarImagen(iv_imagen).execute(imagen);

        // Cargamos los iconos en un ArrayList<ImageView>
        ArrayList<ImageView> iconos = cargarIconos(p);

        LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.ll_iconos);

        for(int i=0; i<iconos.size(); i++){
            linearLayout.addView(iconos.get(i));
        }

        // Añadimos en un ArrayList<String> la descripción de los servicios del Parking
        ArrayList<String> descripcion = new ArrayList<String>();

        if(p.isAdaptado_Discapacidad()!=0) // Convierto de Byte a Boolean
            descripcion.add("Adaptado a personas con discapacidad");

        if(p.isPlazas_Discapacidad()!=0)
            descripcion.add("Plazas de aparcamiento para discapacitados");

        if(p.isMotos()!=0)
            descripcion.add("Plazas para motos");

        if(p.isAseos()!=0)
            descripcion.add("Aseos");

        if(p.isTarjeta()!=0)
            descripcion.add("Pago con tarjeta");

        if(p.isSeguridad()!=0)
            descripcion.add("Seguridad");

        if(p.isCoches_Electricos()!=0)
            descripcion.add("Carga de coches eléctricos");

        if(p.isLavado()!=0)
            descripcion.add("Lavado de vehículos");

        if(p.isServicio_24h()!=0)
            descripcion.add("Apertura 24 horas");

        // Asignamos evento OnClick para mostrar la información de los servicios
        for(int i=0; i<descripcion.size(); i++){
            final String servicio = descripcion.get(i);
            linearLayout.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), servicio, Toast.LENGTH_SHORT).show();
                }
            });
        }

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
                editor_mi_parking.putInt("tiempo_maximo", parking.getTiempo_Maximo());
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
                //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                editor_mi_parking.putString("hora_inicial",cal.getTime().toString());

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
                            .setMessage("Su coche se encuentra estacionado en otro parking, " +
                                    "desaparque en la pestaña Mi parking antes de aparcar " +
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

            // Editor de preferencias General
            SharedPreferences sp_general=getActivity().getSharedPreferences("PREFS_GENERAL", 0);
            SharedPreferences.Editor editor_general = sp_general.edit();
            // Desactivamos la alarma si hubiese alguna activa
            // para ello limpiamos todos los campos del fichero de preferencias general
            editor_general.clear();
            // Para no resetear al primer acceso de la app
            editor_general.putBoolean("primer_acceso_app", false);
            editor_general.commit();
            // TODO Cancelo todas las notificaciones, para que no me salte la de la alarma pendiente
         /* ÉSTO CAMBIA LOS VALORES QUE SE INTRODUCEN AL HISTORIAL
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        */
            // Se cambia la apariencia del botón de DESAPARCAR (activo) a APARCAR (activo).
            b.setText(R.string.aparcar);

            // Se calcula la duración del estacionamiento:
            Calendar cal = Calendar.getInstance();
            String Hora_final = cal.getTime().toString();
            String Hora_inicial = sp_mi_parking.getString("hora_inicial", "0");
            long horas=0;
            long minutos=0;
            float horas_exacto=0;
            try{
                DateFormat formatter = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
                Date date_final = formatter.parse(Hora_final);
                Date date_inicial = formatter.parse(Hora_inicial);

                long diff = date_final.getTime() - date_inicial.getTime();
                horas = diff / (1000 * 60 * 60);
                minutos = diff / (1000 * 60) - (horas*60);

                float duracion = date_final.getTime() - date_inicial.getTime();
                horas_exacto = duracion / (1000 * 60 * 60);
            }
            catch(Exception e){
                Log.e("Fallo al formatear: ", "Fallo");
            }

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
                tarjeta.put("id", parking.getId());
                tarjeta.put("nombre", parking.getNombre());
                tarjeta.put("fecha",sdf_fecha.format(cal.getTime())); // Se obtiene la fecha actual y se mete en el JSON
                tarjeta.put("duracion", horas + " horas, "+ minutos + " minutos"); // Ejemplo: "3 horas, 20 minutos"
                double precio_hora = parking.getPrecio();
                tarjeta.put("precio_hora", "(" + String.format("%.2f", precio_hora)+"€/h)"); // Redondeamos el precio/hora a 2 decimales
                double precio_total = precio_hora * horas_exacto;
                tarjeta.put("precio", String.format("%.2f", precio_total) + "€"); // Redondeamos el precio total a 2 decimales

                JSONArray historial = raiz.getJSONArray("historial");
                historial.put(tarjeta);
                //TODO Guardar JSONObject
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

    // Cargamos las imágenes en un ArrayList<Drawable>
    public ArrayList<ImageView> cargarIconos(Parking parking){
        ArrayList<ImageView> iconos = new ArrayList<ImageView>();

        if(parking.isAdaptado_Discapacidad()!=0) // Convierto de Byte a Boolean
        {
            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(R.drawable.ic_accessible_black_36dp);
            iconos.add(imageView);
        }
        if(parking.isPlazas_Discapacidad()!=0)
        {
            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(R.drawable.ic_airport_shuttle_black_36dp);
            iconos.add(imageView);
        }
        if(parking.isMotos()!=0)
        {
            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(R.drawable.ic_motorcycle_black_36dp);
            iconos.add(imageView);
        }
        if(parking.isAseos()!=0)
        {
            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(R.drawable.ic_wc_black_36dp);
            iconos.add(imageView);
        }
        if(parking.isTarjeta()!=0)
        {
            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(R.drawable.ic_credit_card_black_36dp);
            iconos.add(imageView);
        }
        if(parking.isSeguridad()!=0)
        {
            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(R.drawable.ic_enhanced_encryption_black_36dp);
            iconos.add(imageView);
        }
        if(parking.isCoches_Electricos()!=0)
        {
            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(R.drawable.ic_power_black_36dp);
            iconos.add(imageView);
        }
        if(parking.isLavado()!=0)
        {
            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(R.drawable.ic_local_car_wash_black_36dp);
            iconos.add(imageView);
        }
        if(parking.isServicio_24h()!=0)
        {
            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(R.drawable.ic_local_convenience_store_black_36dp);
            iconos.add(imageView);
        }

        return iconos;
    }

    /*
    Método para descargar la imagen a partir de la URL
    La clase AsyncTask facilita el uso del hilo de interfaz de usuario. Permite realizar
    operaciones en segundo plano y publicar los resultados sobre el hilo de interfaz de usuario
    sin tener que manipular los hilos y/o manipuladores
    */

    private class DescargarImagen extends AsyncTask<String,Void,Bitmap>
    {
        ImageView imageView;

        public DescargarImagen(ImageView imageView)
        {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String...urls)
        {
            String urlImagen = urls[0];
            Bitmap imagen = null;
            try
            {
                InputStream is = new URL(urlImagen).openStream();
                // Decodificamos el flujo de entrada is en un mapa de bits
                imagen = BitmapFactory.decodeStream(is);
            }
            // Capturamos la excepción de descarga
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return imagen;
        }

        // onPostExecute corre en el hilo de UI tras el doInBackground
        protected void onPostExecute(Bitmap result)
        {
            imageView.setImageBitmap(result);
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
        public void onFragmentInteraction(Uri uri);
    }
}