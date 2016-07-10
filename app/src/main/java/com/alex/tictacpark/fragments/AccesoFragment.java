package com.alex.tictacpark.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alex.tictacpark.R;
import com.alex.tictacpark.activities.MainActivity;
import com.alex.tictacpark.activities.AreaUsuario;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccesoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccesoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccesoFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";

    // Declaramos las variables necesarias para introducir los datos de conexión al servidor
    private EditText edUsuario, edPassword;
    private Button btnIniciarSesion;
    private Button btnNuevoRegistro;

    // Variable de tipo String que inicializa con la estructura principal de la URI
    // para el acceso al servicio web.
    // Nota: Para conectarnos con nuestro servidor web local (localhost), debemos usar la
    // dirección IP de nuestro equipo en vez de "localhost" o "127.0.0.1". Esto es porque
    // la dirección IP "127.0.0.1" es internamente usada por el emulador de android o por
    // nuestro dispositivo Android
    String ip = "192.168.0.12";
    // Con el móvil en mi casa funciona "192.168.0.12";
    // Con el móvil como punto de acceso "192.168.43.192";
    // Con el móvil con anclaje de USB "192.168.42.173";
    // Con el emulador funciona: "10.0.2.2" (local apache server)
    // Con el emulador (red eduroam) funciona: "10.38.32.149"

    String raiz = "http://" + ip + ":8080/TicTacParkDWP/rest/TicTacPark";
    String servidor = "localhost";
    String puerto = "3306";
    String baseDatos = "tictacpark";
    String usuario = "root";
    String password = "passking";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param SectionNumber Indica el número de la sección pulsada.
     * @return A new instance of fragment AccesoFragment.
     */
    public static AccesoFragment newInstance(int SectionNumber) {
        AccesoFragment fragment = new AccesoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, SectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public AccesoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;

        // Abrimos el fichero de preferencias
        SharedPreferences sp_usuario=getActivity().getSharedPreferences("PREFS_USUARIO", 0);
        // Recuperamos del fichero de preferencias si hay una sesión iniciada
        if(sp_usuario.getBoolean("sesion_iniciada", false)) // Si es true --> Sesión iniciada
        {
            //Asignamos el layout fragment_propietario
            rootView = inflater.inflate(R.layout.fragment_propietario, container, false);

            // Recuperamos del fichero de preferencias el id_usuario
            String usuario_propietario = sp_usuario.getString("usuario", "");
            String password_propietario = sp_usuario.getString("password", "");

            // Ponemos el nombre del propietario en la barra (así aparecerá en caso de no tener conexión)
            ((MainActivity) getActivity()).setActionBarTitle(usuario_propietario);

            // Aunque haya una sesión iniciada, se comprueba que hay conexión y que las credenciales
            // almacenadas en el fichero de preferencias son correctas
            peticionServicio(usuario_propietario, password_propietario);
        }
        else // Si es false --> No hay sesión iniciada --> Iniciar sesión
        {
            //Asignamos el layout fragment_acceso
            rootView = inflater.inflate(R.layout.fragment_acceso, container, false);

            // Ponemos el nombre "Área de propietario" en la barra
            ((MainActivity) getActivity()).setActionBarTitle("Área de propietario");

            // Se asocian las variables de tipo EditText y Button con sus controles a nivel de layout
            edUsuario = (EditText) rootView.findViewById(R.id.edUsuario);
            edPassword = (EditText) rootView.findViewById(R.id.edPassword);
            btnIniciarSesion = (Button) rootView.findViewById(R.id.btnIniciarSesion);
            btnNuevoRegistro = (Button) rootView.findViewById(R.id.btnNuevoRegistro);

            //Asignar a variable el Botón Iniciar Sesión y asignar evento OnClick para realizar las
            //acciones correspondientes
            btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickIniciarSesion();
                }
            });

            //Asignar a variable el Botón Nuevo Registro y asignar evento OnClick para realizar las
            //acciones correspondientes
            btnNuevoRegistro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Genera el intent y empieza la actividad a través del intent
                    Intent intent=new Intent(getActivity(), AreaUsuario.class);
                    // Pasamos la acción a realizar a la actividad
                    intent.putExtra("accion", "Registro");
                    startActivity(intent);
                }
            });
        }

        return rootView;
    }

    // Método que define las acciones al hacer click en Iniciar sesión
    public void clickIniciarSesion()
    {
        String usuario_introducido = edUsuario.getText().toString();
        String password_introducida = edPassword.getText().toString();

        if(usuario_introducido.equals("") || password_introducida.equals(""))
            Toast.makeText(getActivity(), "Debe completar los campos usuario y contraseña.", Toast.LENGTH_LONG).show();
        else
            peticionServicio(usuario_introducido, password_introducida);
    }

    // Método que devuelve el estado de la conexión al servidor
    public void peticionServicio(final String usuario_introducido, final String password_introducida)
    {
        String usuario_url = usuario_introducido.replace(" ", "%20"); // Reemplazamos los espacios en blancos para que la ruta funcione correctamente
        String uri = raiz + "/estadoSesion/" + servidor + "/" + puerto + "/" + baseDatos + "/" + usuario + "/" + password + "/" + usuario_url + "/" + password_introducida;

        // Se declara e inicializa una variable de tipo RequestQueue, encargada de crear
        // una nueva petición en la cola del servicio web.
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        // Se declara e inicializa un objeto de tipo JsonArrayRequest, que permite recuperar un
        // JSONArray a partir de la URL que recibe. El constructor de la clase JsonArrayRequest
        // recibe como argumentos de entrada el método para que el cliente realice operaciones
        // sobre el servicio web, la uri para el acceso al recurso, la interfaz Response.Listener,
        // encargada de devolver la respuesta parseada a la petición del cliente, y la interfaz
        // Response.ErrorListener encargada de entregar una respuesta errónea desde el servicio web.
        JsonArrayRequest jArray = new JsonArrayRequest(Request.Method.GET,
                uri,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response)
                    {
                        try {
                            // Se comprueba mediante un condicional if, que el servicio web ha podido
                            // conectar con el servidor MySQL con los datos introducidos por el usuario.
                            if (!response.get(1).toString().equals("-1")) {
                                Log.e("Iniciar sesión", "Fue OK");
                                // Se guardan el estado de la sesión, el usuario_introducido y password_introducida en el fichero de preferencias PREFS_USUARIO
                                SharedPreferences sp_usuario = getActivity().getSharedPreferences("PREFS_USUARIO", 0);
                                SharedPreferences.Editor editor_usuario = sp_usuario.edit();
                                editor_usuario.putBoolean("sesion_iniciada", true);
                                editor_usuario.putString("usuario", usuario_introducido);
                                editor_usuario.putString("password", password_introducida);
                                editor_usuario.commit();

                                // Inflamos el fragment PropietarioFragment, reemplazando al que había y pasándole el id_usuario
                                // para las credenciales introducidas
                                PropietarioFragment propietario = new PropietarioFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("id_usuario", response.get(1).toString());
                                propietario.setArguments(bundle);

                                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.replace(R.id.container, propietario, "PROPIETARIO");
                                ft.commit();
                            }
                            // Si los datos introducidos son incorrectos y se entró con credenciales
                            // guardadas en el fichero de preferencias, es que las credenciales han
                            // sido modificadas desde otro dispositivo, por lo tanto, se limpia el
                            // fichero de preferencias y se infla el fragment AccesoFragment
                            else
                            {
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
                                // Tanto accediendo desde el cuadro de inicio de sesión como a través
                                // de una sesión abierta, se indica que los datos introducidos son incorrectos
                                Toast.makeText(getActivity(), "Los datos introducidos son incorrectos.", Toast.LENGTH_LONG).show();
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        try
                        {
                            Log.e("Estado Conexión", "Falló");
                            Toast.makeText(getActivity(), "No hay conexión. Por favor, inténtelo de nuevo más tarde.", Toast.LENGTH_LONG).show();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
        );

        // Se definen las políticas para la petición realizada. Recibe como argumento una
        // instancia de la clase DefaultRetryPolicy, que recibe como parámetros de entrada
        // el tiempo inicial de espera para la respuesta, el número máximo de intentos,
        // y el multiplicador de retardo de envío por defecto.
        jArray.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Se añade la petición a la cola con el objeto de tipo JsonArrayRequest.
        queue.add(jArray);
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
}