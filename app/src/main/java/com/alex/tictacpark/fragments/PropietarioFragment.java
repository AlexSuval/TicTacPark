package com.alex.tictacpark.fragments;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.tictacpark.R;
import com.alex.tictacpark.activities.AreaUsuario;
import com.alex.tictacpark.activities.MainActivity;
import com.alex.tictacpark.models.Parking;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
 * {@link PropietarioFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PropietarioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PropietarioFragment extends Fragment
{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";

    // Variable de tipo String que inicializa con la estructura principal de la URI
    // para el acceso al servicio web.
    // Nota: Para conectarnos con nuestro servidor web local (localhost), debemos usar la
    // dirección IP de nuestro equipo en vez de "localhost" o "127.0.0.1". Esto es porque
    // la dirección IP "127.0.0.1" es internamente usada por el emulador de android o por
    // nuestro dispositivo Android
    String ip = "192.168.0.12";
    // Con el móvil en mi casa funciona "192.168.0.11";
    // Con el móvil como punto de acceso "192.168.43.192";
    // Con el emulador funciona: "10.0.2.2" (local apache server)
    // Con el emulador (red eduroam) funciona: "10.38.32.149"

    String raiz = "http://" + ip + ":8080/TicTacParkDWP/rest/TicTacPark";
    String servidor = "localhost";
    String puerto = "3306";
    String baseDatos = "tictacpark";
    String usuario = "root";
    String password = "passking";

    // Declaramos e inicializamos la ArrayList list_parking, que contendrá todos los objetos Parking
    private ArrayList<Parking> list_mis_parkings=new ArrayList<Parking>();

    String id_usuario;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param SectionNumber Indica el número de la sección pulsada.
     * @return A new instance of fragment PropietarioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PropietarioFragment newInstance(int SectionNumber) {
        PropietarioFragment fragment = new PropietarioFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, SectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PropietarioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_propietario, container, false);

        // Abrimos el fichero de preferencias
        SharedPreferences sp_usuario=getActivity().getSharedPreferences("PREFS_USUARIO", 0);

        // Recuperamos del fichero de preferencias el usuario
        String usuario = sp_usuario.getString("usuario", "-1");

        // Ponemos el nombre del propietario en la barra
        ((MainActivity) getActivity()).setActionBarTitle(usuario);

        // Se carga la tabla con los parkings del usuario
        id_usuario = getArguments().getString("id_usuario", "-1");
        cargarTabla(id_usuario, view);

        // Se envía evento táctil a la vista
        view.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v,MotionEvent event){
                return true;
            }
        });

        // Carga el menú de la Action Bar
        setHasOptionsMenu(true);

        return view;
    }

    // Método que carga la tabla con los parkings del usuario
    public View cargarTabla(String id_usuario, View view)
    {
        final TableLayout stk = (TableLayout)view.findViewById(R.id.tableLayout);

        // URI asociada al listado de parkings disponibles
        String uri = raiz + "/miLista/" + servidor + "/" + puerto + "/" + baseDatos + "/" + usuario + "/" + password + "/" + id_usuario;

        // Se declara e inicializa una variable de tipo RequestQueue, encargada de crear una nueva
        // petición en la cola del servicio web.
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        // Se declara e inicializa un objeto de tipo JsonArrayRequest, que permite recuperar un
        // JSONArray a partir de la URL que recibe. El constructor de la clase JsonArrayRequest
        // recibe como argumentos de entrada el método para que el cliente realice operaciones sobre
        // el servidor web, la uri para el acceso al recurso, y la interfaz Response.Listener,
        // encargada de devolver la respuesta parseada a la petición del cliente, y la interfaz
        // Response.ErrorListener encargada de entregar una respuesta errónea desde el servicio web.
        JsonArrayRequest jArray = new JsonArrayRequest(Request.Method.GET,
                uri,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response)
                    {
                        try
                        {
                            // Se vacía la lista de Parkings para actualizarla
                            list_mis_parkings.clear();
                            // Se construye un bucle for() para recorrer la respuesta parseada y
                            // construir un nuevo objeto Parking por cada registro de la base de datos MySQL.
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsObjectParking = (JSONObject) response.get(i);

                                int id = jsObjectParking.getInt("id");
                                String nombre = jsObjectParking.getString("nombre");
                                Log.e("Parking ", nombre);
                                String direccion = jsObjectParking.getString("direccion");
                                String localidad = jsObjectParking.getString("localidad");
                                String provincia = jsObjectParking.getString("provincia");
                                double latitud = jsObjectParking.getDouble("latitud");
                                double longitud = jsObjectParking.getDouble("longitud");
                                String telefono = jsObjectParking.getString("telefono");
                                String imagen = jsObjectParking.getString("imagen");
                                String tipo = jsObjectParking.getString("tipo");
                                String estado = jsObjectParking.getString("estado");
                                double precio = jsObjectParking.getDouble("precio");
                                String horario_apertura = jsObjectParking.getString("horario_apertura");
                                String horario_cierre = jsObjectParking.getString("horario_cierre");
                                int tiempo_maximo = jsObjectParking.getInt("tiempo_maximo");
                                int plazas = jsObjectParking.getInt("plazas");
                                double altura_minima = jsObjectParking.getDouble("altura_minima");

                                String adaptado_discapacidad_st = jsObjectParking.getString("adaptado_discapacidad");
                                byte adaptado_discapacidad = Byte.valueOf(adaptado_discapacidad_st);

                                String plazas_discapacidad_st = jsObjectParking.getString("plazas_discapacidad");
                                byte plazas_discapacidad = Byte.valueOf(plazas_discapacidad_st);

                                String motos_st = jsObjectParking.getString("motos");
                                byte motos = Byte.valueOf(motos_st);

                                String aseos_st = jsObjectParking.getString("aseos");
                                byte aseos = Byte.valueOf(aseos_st);

                                String tarjeta_st = jsObjectParking.getString("tarjeta");
                                byte tarjeta = Byte.valueOf(tarjeta_st);

                                String seguridad_st = jsObjectParking.getString("seguridad");
                                byte seguridad = Byte.valueOf(seguridad_st);

                                String coches_electricos_st = jsObjectParking.getString("coches_electricos");
                                byte coches_electricos = Byte.valueOf(coches_electricos_st);

                                String lavado_st = jsObjectParking.getString("lavado");
                                byte lavado = Byte.valueOf(lavado_st);

                                String servicio_24h_st = jsObjectParking.getString("servicio_24h");
                                byte servicio_24h = Byte.valueOf(servicio_24h_st);

                                String descripcion = jsObjectParking.getString("descripcion");

                                int id_usuario = jsObjectParking.getInt("id_usuario");

                                Parking nuevoParking = new Parking(id, nombre, direccion, localidad,
                                        provincia, latitud, longitud, telefono,
                                        imagen, tipo, estado, precio,
                                        horario_apertura, horario_cierre, tiempo_maximo,
                                        plazas, altura_minima, adaptado_discapacidad,
                                        plazas_discapacidad, motos, aseos,
                                        tarjeta, seguridad, coches_electricos,
                                        lavado, servicio_24h, descripcion, id_usuario);

                                // Se añade el objeto creado a la colección de tipo List<Parking>.
                                list_mis_parkings.add(nuevoParking);
                                Log.e("Tamaño list_parking ", String.valueOf(list_mis_parkings.size()));

                                // Cargamos una nueva fila
                                TableRow tbrow = new TableRow(getActivity());

                                // Switch de Estado
                                Switch swEstado = new Switch(getActivity());
                                swEstado.setBackgroundResource(R.drawable.estado_toggle);
                                swEstado.setTextOn("Libre");
                                swEstado.setTextOff("Completo");
                                if (estado.equals("Libre"))
                                    swEstado.setChecked(true);
                                else
                                    swEstado.setChecked(false);
                                final String id_parking = Integer.toString(id);
                                swEstado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (isChecked) // Switch ON --> Estado = Libre
                                        {
                                            String estado = "Libre";
                                            modificarEstado(id_parking, estado);
                                        } else // Switch OFF --> Estado = Completo
                                        {
                                            String estado = "Completo";
                                            modificarEstado(id_parking, estado);
                                        }
                                    }
                                });
                                tbrow.addView(swEstado);

                                // Nombre y localidad del Parking
                                TextView t2v = new TextView(getActivity());
                                t2v.setText(" " + nombre + " \n" + " (" + localidad + ") ");
                                t2v.setTextColor(Color.BLACK);
                                t2v.setGravity(Gravity.CENTER);
                                tbrow.addView(t2v);

                                // Botón Modificar
                                Button btnModificar = new Button(getActivity());
                                btnModificar.setText("Modificar");
                                //btnModificar.setId(id);
                                btnModificar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // TODO Cargar modificar parking
                                    }
                                });
                                tbrow.addView(btnModificar);

                                // Botón Eliminar
                                final String nombre_parking = nombre;
                                Button btnEliminar = new Button(getActivity());
                                btnEliminar.setText("Eliminar");
                                //btnEliminar.setId(id);
                                btnEliminar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // TODO Cargar eliminar parking
                                        // Se crea un cuadro de diálogo
                                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                                        builder.setTitle("Eliminar parking");
                                        builder.setMessage("¿Seguro que desea eliminar el parking '" + nombre_parking + "'?");
                                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                if (eliminarParking(id_parking)) {
                                                    // Se refresca el área de propietario, para ello inflamos el fragment AccesoFragment, reemplazando al que había
                                                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                                    ft.replace(R.id.container, new AccesoFragment(), "ACCESO");
                                                    ft.commit();
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                            }
                                        });
                                        android.app.AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                });
                                tbrow.addView(btnEliminar);

                                stk.addView(tbrow);
                            }
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                try
                {
                    // Un error en la petición del servicio supone que el propietario no tiene parkings
                    TableRow tbrow = new TableRow(getActivity());

                    TextView t1v = new TextView(getActivity());
                    t1v.setText("No dispone de parkings actualmente");
                    t1v.setTextColor(Color.BLACK);
                    t1v.setGravity(Gravity.CENTER);
                    tbrow.addView(t1v);

                    stk.addView(tbrow);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        );

        // Se definen las políticas para la petición realizada. Recibe como argumento una instancia
        // de la clase DefaultRetryPolicy, que recibe como parámetros de entrada el tiempo inicial
        // de espera para la respuesta, el número máximo de intentos, y el multiplicador de retardo
        // de envío por defecto.
        jArray.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Se añade la petición a la cola con el objeto de tipo JsonArrayRequest.
        queue.add(jArray);

        return view;
    }

    // Método que invocará a peticionServicioParking(), indicándole como argumento la URI
    // asociada para cambiar el estado del parking.
    public void modificarEstado(String id, String estado)
    {
        String uri = raiz + "/modificaEstado/" + servidor + "/" + puerto + "/" + baseDatos + "/" +
                usuario + "/" + password + "/" + id + "/" + estado;

        try
        {
            // Se modifica el estado del parking
            peticionServicioParking(uri);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // Método que invocará a peticionServicioParking(), indicándole como argumento la URI
    // asociada para la eliminación de un parking
    public Boolean eliminarParking(String id)
    {
        boolean parkingBorrado = false;

        String uri = raiz + "/borra/" + servidor + "/" + puerto + "/" + baseDatos + "/" +
                usuario + "/" + password + "/" + id;

        try
        {
            // Se elimina el parking
            peticionServicioParking(uri);
            parkingBorrado = true;
        }
        catch (Exception e)
        {
            parkingBorrado = false;
        }

        return parkingBorrado;
    }

    // Método encargado de realizar peticiones de creación, modificación o eliminación de un usuario
    // y que recibe como parámetro de entrada la URI para realizar dicha petición al servicio web.
    public void peticionServicioParking(String uri)
    {
        // Se declara e inicializa una variable de tipo RequestQueue, encargada de crear
        // una nueva petición en la cola del servicio web.
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        // Se declara e inicializa un objeto de tipo StringRequest, que permite recuperar un
        // String a partir de la URL que recibe. El constructor de la clase StringRequest
        // recibe como argumentos de entrada el método para que el cliente realice operaciones
        // sobre el servicio web, la uri para el acceso al recurso, la interfaz Response.Listener,
        // encargada de devolver la respuesta parseada a la petición del cliente, y la interfaz
        // Response.ErrorListener encargada de entregar una respuesta errónea desde el servicio web.
        StringRequest sRequest = new StringRequest(Request.Method.GET,
                uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    // Nuevo usuario insertado o Usuario borrado correctamente
                    Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_LONG).show();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                try
                {
                    Toast.makeText(getActivity(), "Error en la petición de servicio de parking: " + error.toString(),Toast.LENGTH_LONG).show();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        // Se definen las políticas para la petición realizada. Recibe como argumento una
        // instancia de la clase DefaultRetryPolicy, que recibe como parámetros de entrada
        // el tiempo inicial de espera para la respuesta, el número máximo de intentos,
        // y el multiplicador de retardo de envío por defecto.
        sRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Se añade la petición a la cola con el objeto de tipo JsonArrayRequest.
        queue.add(sRequest);
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

    // Cargamos el menú cuenta usuario
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_cuenta_usuario, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.cerrar_sesion:
                // Se limpian todos los campos del fichero de preferencias usuario
                // y se infla el fragment AccesoFragment
                SharedPreferences sp_usuario = getActivity().getSharedPreferences("PREFS_USUARIO", 0);
                SharedPreferences.Editor editor_usuario = sp_usuario.edit();
                editor_usuario.clear();
                editor_usuario.commit();
                // Inflamos el fragment AccesoFragment, reemplazando al que había
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, new AccesoFragment(), "ACCESO");
                ft.commit();
                return true;

            case R.id.mi_cuenta:
                //Genera el intent y empieza la actividad a través del intent
                Intent intent=new Intent(getActivity(), AreaUsuario.class);
                // Pasamos la acción a realizar a la actividad
                intent.putExtra("accion", "Cuenta");
                intent.putExtra("id_usuario", id_usuario);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}