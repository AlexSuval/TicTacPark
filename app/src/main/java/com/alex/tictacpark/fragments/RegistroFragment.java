package com.alex.tictacpark.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alex.tictacpark.R;
import com.alex.tictacpark.activities.AreaUsuario;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegistroFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RegistroFragment extends Fragment {

    // Declaramos las variables necesarias para introducir los datos de conexión al servidor
    private EditText edNombre, edApellidos, edDNI, edTelefono, edEmail, edRepetirEmail, edUsuario, edPassword, edRepetirPassword;
    private Button btnNuevoRegistro, btnDarmeBaja;

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

    public RegistroFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Asignamos el layout fragment_registro
        View view=inflater.inflate(R.layout.fragment_registro, container, false);

        Bundle b=getActivity().getIntent().getExtras();
        String accion = b.getString("accion");
        final String id_usuario = b.getString("id_usuario");

        // Se asocian las variables de tipo EditText con sus controles a nivel de layout
        edNombre = (EditText) view.findViewById(R.id.edNombre);
        edApellidos = (EditText) view.findViewById(R.id.edApellidos);
        edDNI = (EditText) view.findViewById(R.id.edDNI);
        edTelefono = (EditText) view.findViewById(R.id.edTelefono);
        edEmail = (EditText) view.findViewById(R.id.edEmail);
        edRepetirEmail = (EditText) view.findViewById(R.id.edRepetirEmail);
        edUsuario = (EditText) view.findViewById(R.id.edUsuario);
        edPassword = (EditText) view.findViewById(R.id.edPassword);
        edRepetirPassword = (EditText) view.findViewById(R.id.edRepetirPassword);
        btnNuevoRegistro = (Button) view.findViewById(R.id.btnNuevoRegistro);
        btnDarmeBaja = (Button) view.findViewById(R.id.btnDarmeBaja);

        // Si la acción pasada en el intent es Registro
        if(accion.equals("Registro"))
        {
            Log.e("Clic en nuevo registro ", "ok");
            // Ponemos el nombre "Nuevo registro" en la barra
            ((AreaUsuario) getActivity()).setActionBarTitle("Nuevo registro");

            // Asignar a variable el Botón Nuevo Registro y asignar evento OnClick para realizar las
            // acciones correspondientes
            btnNuevoRegistro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (nuevoRegistro()) {
                        // Se guardan el estado de la sesión, el usuario_introducido y password_introducida en el fichero de preferencias PREFS_USUARIO
                        SharedPreferences sp_usuario = getActivity().getSharedPreferences("PREFS_USUARIO", 0);
                        SharedPreferences.Editor editor_usuario = sp_usuario.edit();
                        editor_usuario.putBoolean("sesion_iniciada", true);
                        editor_usuario.putString("usuario", edUsuario.getText().toString());
                        editor_usuario.putString("password", edPassword.getText().toString());
                        editor_usuario.putBoolean("refrescar_acceso", true); // Refrescar el área de propietario
                        editor_usuario.commit();
                        // Vuelve a la actividad principal, refrescándola, cargando el área de propietario
                        getActivity().onBackPressed();
                    }
                }
            });
        }
        // Sino entramos en la cuenta de usuario
        else
        {
            // Ponemos el nombre "Mi cuenta" en la barra
            ((AreaUsuario) getActivity()).setActionBarTitle("Mi cuenta");

            // Cargamos los editText con los datos del usuario
            cargarDatos(id_usuario);

            // Usuario y DNI no editables
            edUsuario.setEnabled(false);
            edUsuario.setInputType(InputType.TYPE_NULL);
            edDNI.setEnabled(false);
            edDNI.setInputType(InputType.TYPE_NULL);

            // Ponemos visible el botón "Darme de baja"
            btnDarmeBaja.setVisibility(View.VISIBLE);
            btnDarmeBaja.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Se crea un cuadro de diálogo
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Darme de baja");
                    builder.setMessage("¿Seguro que desea eliminar su usuario de propietario, así como todos sus parkings asociados?");
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(eliminarUsuario(id_usuario))
                            {
                                // Se limpian todos los campos del fichero de preferencias usuario,
                                // para cerrar sesión
                                SharedPreferences sp_usuario = getActivity().getSharedPreferences("PREFS_USUARIO", 0);
                                SharedPreferences.Editor editor_usuario = sp_usuario.edit();
                                editor_usuario.clear();
                                // Se refresca el área de propietario
                                editor_usuario.putBoolean("refrescar_acceso", true);
                                editor_usuario.commit();
                                // Vuelve a la actividad principal, refrescándola, cargando el área de propietario
                                getActivity().onBackPressed();
                            }
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
                }
            });

            // Asignar a variable el Botón Modificar Usuario y asignar evento OnClick para realizar las
            // acciones correspondientes
            btnNuevoRegistro.setText("Modificar mis datos");
            btnNuevoRegistro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(modificarUsuario(id_usuario))
                    {
                        // Se guardan el estado de la sesión, el usuario_introducido y password_introducida en el fichero de preferencias PREFS_USUARIO
                        SharedPreferences sp_usuario = getActivity().getSharedPreferences("PREFS_USUARIO", 0);
                        SharedPreferences.Editor editor_usuario = sp_usuario.edit();
                        editor_usuario.putBoolean("sesion_iniciada", true);
                        editor_usuario.putString("usuario", edUsuario.getText().toString());
                        // Importante guardar nuevamente la contraseña pues pudo modificarse y
                        // se requiere para mantener la sesión abierta
                        editor_usuario.putString("password", edPassword.getText().toString());
                        editor_usuario.putBoolean("refrescar_acceso", true); // Refrescar el área de propietario
                        editor_usuario.commit();
                        // Vuelve a la actividad principal, refrescándola, cargando el área de propietario
                        getActivity().onBackPressed();
                    }
                }
            });
        }

        return view;
    }

    // Método que invocará a peticionServicioUsuario(), indicándole como argumento la URI
    // asociada para la eliminación de un propietario
    public Boolean eliminarUsuario(String id_usuario)
    {
        boolean registroBorrado = false;

        String uri = raiz + "/borraUsuario/" + servidor + "/" + puerto + "/" + baseDatos + "/" +
                usuario + "/" + password + "/" + id_usuario;

        try
        {
            // Se elimina el usuario
            peticionServicioUsuario(uri);
            registroBorrado = true;
        }
        catch (Exception e)
        {
            registroBorrado = false;
        }

        return registroBorrado;
    }

    // Método que invocará a peticionServicioUsuario(), indicándole como argumento la URI
    // asociada para la modificación de los datos de propietario, en caso de que los datos introducidos
    // sean correctos.
    public Boolean modificarUsuario(String id_usuario)
    {
        boolean usuarioModificado = false;
        String nuevo_nombre = edNombre.getText().toString().replace(" ", "%20"); // Reemplazamos los espacios en blanco para que la ruta funcione correctamente
        String nuevo_apellidos = edApellidos.getText().toString().replace(" ", "%20"); // Reemplazamos los espacios en blanco para que la ruta funcione correctamente
        String nuevo_telefono = edTelefono.getText().toString();
        String nuevo_email = edEmail.getText().toString();
        String repetir_email = edRepetirEmail.getText().toString();
        String nuevo_password = edPassword.getText().toString();
        String repetir_password = edRepetirPassword.getText().toString();

        String uri = raiz + "/modificaUsuario/" + servidor + "/" + puerto + "/" + baseDatos + "/" +
                usuario + "/" + password + "/" + id_usuario + "/" + nuevo_nombre + "/" + nuevo_apellidos + "/" +
                nuevo_telefono + "/" + nuevo_email + "/" + nuevo_password;

        if(nuevo_nombre.equals("") || nuevo_apellidos.equals("") || nuevo_telefono.equals("")
                || nuevo_email.equals("") || nuevo_password.equals(""))
        {
            Toast.makeText(getActivity(), "Debe completar todos los campos para modificar sus datos.", Toast.LENGTH_LONG).show();
        }
        else if (!emailValido(nuevo_email))
        {
            Toast.makeText(getActivity(), "El formato del email introducido es incorrecto.", Toast.LENGTH_LONG).show();
        }
        else if (!passwordValida(nuevo_password))
        {
            Toast.makeText(getActivity(), "La contraseña debe contener entre 8 y 50 caracteres.", Toast.LENGTH_LONG).show();
        }
        else if(!nuevo_email.equals(repetir_email))
        {
            Toast.makeText(getActivity(), "Los emails no coinciden.", Toast.LENGTH_LONG).show();
        }
        else if(!nuevo_password.equals(repetir_password))
        {
            Toast.makeText(getActivity(), "Las contraseñas no coinciden.", Toast.LENGTH_LONG).show();
        }
        else if(nuevo_telefono.length()!=9)
        {
            Toast.makeText(getActivity(), "El teléfono debe constar de 9 dígitos.", Toast.LENGTH_LONG).show();
        }
        else
        {
            try
            {
                // Se modifican los datos del usuario
                peticionServicioUsuario(uri);
                usuarioModificado = true;
            }
            catch (Exception e)
            {
                usuarioModificado = false;
            }
        }
        return usuarioModificado;
    }

    // Método que invocará a peticionServicioUsuario(), indicándole como argumento la URI
    // asociada para la creación de un nuevo propietario, en caso de que los datos introducidos
    // sean correctos.
    public Boolean nuevoRegistro()
    {
        boolean registroInsertado = false;
        String nuevo_nombre = edNombre.getText().toString().replace(" ", "%20"); // Reemplazamos los espacios en blanco para que la ruta funcione correctamente
        String nuevo_apellidos = edApellidos.getText().toString().replace(" ", "%20"); // Reemplazamos los espacios en blanco para que la ruta funcione correctamente
        String nuevo_dni = edDNI.getText().toString();
        String nuevo_telefono = edTelefono.getText().toString();
        String nuevo_email = edEmail.getText().toString();
        String repetir_email = edRepetirEmail.getText().toString();
        String nuevo_usuario = edUsuario.getText().toString().replace(" ", "%20"); // Reemplazamos los espacios en blanco para que la ruta funcione correctamente
        String nuevo_password = edPassword.getText().toString();
        String repetir_password = edRepetirPassword.getText().toString();

        String uri = raiz + "/registro/" + servidor + "/" + puerto + "/" + baseDatos + "/" +
                usuario + "/" + password + "/" + nuevo_nombre + "/" + nuevo_apellidos + "/" +
                nuevo_dni + "/" + nuevo_telefono + "/" + nuevo_email + "/" + nuevo_usuario + "/" +
                nuevo_password;

        if(nuevo_nombre.equals("") || nuevo_apellidos.equals("") || nuevo_dni.equals("")
                || nuevo_telefono.equals("") || nuevo_email.equals("") || nuevo_usuario.equals("")
                || nuevo_password.equals(""))
        {
            Toast.makeText(getActivity(), "Debe completar todos los campos para completar un nuevo registro.", Toast.LENGTH_LONG).show();
        }
        else if (!emailValido(nuevo_email))
        {
            Toast.makeText(getActivity(), "El formato del email introducido es incorrecto.", Toast.LENGTH_LONG).show();
        }
        else if (!passwordValida(nuevo_password))
        {
            Toast.makeText(getActivity(), "La contraseña debe contener entre 8 y 50 caracteres.", Toast.LENGTH_LONG).show();
        }
        else if(!nuevo_email.equals(repetir_email))
        {
            Toast.makeText(getActivity(), "Los emails no coinciden.", Toast.LENGTH_LONG).show();
        }
        else if(!nuevo_password.equals(repetir_password))
        {
            Toast.makeText(getActivity(), "Las contraseñas no coinciden.", Toast.LENGTH_LONG).show();
        }
        else if(nuevo_telefono.length()!=9)
        {
            Toast.makeText(getActivity(), "El teléfono debe constar de 9 dígitos.", Toast.LENGTH_LONG).show();
        }
        else if(nuevo_dni.length()!=9)
        {
            Toast.makeText(getActivity(), "El DNI debe constar de 9 caracteres.", Toast.LENGTH_LONG).show();
        }
        else
        {
            try
            {
                // Se inserta un nuevo usuario
                peticionServicioUsuario(uri);
                registroInsertado = true;
            }
            catch (Exception e)
            {
                registroInsertado = false;
            }
        }
        return registroInsertado;
    }

    /**
     * Método usado para chequear si el formato de email es correcto.
     *
     * @param email
     * @return boolean true para correcto false para incorrecto
     */
    private boolean emailValido(String email) {
        boolean esCorrecto = false;

        String expresion = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expresion, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches())
        {
            esCorrecto = true;
        }
        return esCorrecto;
    }

    /**
     * Método usado para chequear si el formato de contraseña es correcto, debe contener entre 8 y 50 caracteres.
     *
     * @param password
     * @return boolean true para correcto false para incorrecto
     */
    private boolean passwordValida(String password)
    {
        Log.e("Tamaño password ", Integer.toString(password.length()));
        return (password.length() > 7 & password.length()<51);
    }

    // Método encargado de realizar peticiones de creación, modificación o eliminación de un usuario
    // y que recibe como parámetro de entrada la URI para realizar dicha petición al servicio web.
    public void peticionServicioUsuario(String uri)
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
                    Toast.makeText(getActivity(), "Error en la petición de servicio de usuario: " + error.toString(),Toast.LENGTH_LONG).show();
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

    // Método que carga los editText con los datos del usuario
    public void cargarDatos(String id_usuario)
    {
        // URI asociada al listado de parkings disponibles
        String uri = raiz + "/miCuenta/" + servidor + "/" + puerto + "/" + baseDatos + "/" + usuario + "/" + password + "/" + id_usuario;

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
                            // Se construye un bucle for() para recorrer la respuesta parseada y
                            // construir un nuevo objeto Usuario para el registro de la base de datos MySQL.
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsObjectParking = (JSONObject) response.get(i);

                                String nombre = jsObjectParking.getString("nombre");
                                String apellidos = jsObjectParking.getString("apellidos");
                                String dni = jsObjectParking.getString("dni");
                                String telefono = jsObjectParking.getString("telefono");
                                String email = jsObjectParking.getString("email");
                                String usuario = jsObjectParking.getString("usuario");
                                String contrasena = jsObjectParking.getString("contrasena");

                                edNombre.setText(nombre);
                                edApellidos.setText(apellidos);
                                edDNI.setText(dni);
                                edTelefono.setText(telefono);
                                edEmail.setText(email);
                                edRepetirEmail.setText(email);
                                edUsuario.setText(usuario);
                                edPassword.setText(contrasena);
                                edRepetirPassword.setText(contrasena);
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
                    Toast.makeText(getActivity(), "Error de petición de servicio: " + error.toString(), Toast.LENGTH_LONG).show();
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