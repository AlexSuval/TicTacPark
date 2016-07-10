package com.alex.tictacpark.fragments;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.alex.tictacpark.R;
import com.alex.tictacpark.activities.AreaParking;
import com.alex.tictacpark.models.Parking;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegistroFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RegistroParkingFragment extends Fragment {

    // Declaramos las variables necesarias para introducir los datos de conexión al servidor
    private EditText edNombre, edDireccion, edLocalidad, edProvincia, edLatitud, edLongitud,
            edTelefono, edImagen, edPrecio, edHorarioApertura, edHorarioCierre, edTiempoMaximo,
            edPlazas, edAltura, edDescripcion;
    private Spinner spTipo, spEstado;
    private CheckBox cbAdaptado, cbPlazasDiscapacitados, cbMotos, cbAseos, cbTarjeta, cbSeguridad,
            cbElectricos, cbLavado, cbServicio24h;
    private Button btnNuevoParking;
    private ArrayAdapter<CharSequence> adapter_tipo, adapter_estado;

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

    public RegistroParkingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Asignamos el layout fragment_registro
        View view=inflater.inflate(R.layout.fragment_registro_parking, container, false);

        Bundle b=getActivity().getIntent().getExtras();
        String accion = b.getString("accion");

        // Se asocian las variables de tipo EditText, Spinner, CheckBox y Button con sus controles a nivel de layout
        edNombre = (EditText) view.findViewById(R.id.edNombre);
        edDireccion = (EditText) view.findViewById(R.id.edDireccion);
        edLocalidad = (EditText) view.findViewById(R.id.edLocalidad);
        edProvincia = (EditText) view.findViewById(R.id.edProvincia);
        edLatitud = (EditText) view.findViewById(R.id.edLatitud);
        edLongitud = (EditText) view.findViewById(R.id.edLongitud);
        edTelefono = (EditText) view.findViewById(R.id.edTelefono);
        edImagen = (EditText) view.findViewById(R.id.edImagen);
        edPrecio = (EditText) view.findViewById(R.id.edPrecio);
        edHorarioApertura = (EditText) view.findViewById(R.id.edHorarioApertura);
        edHorarioCierre = (EditText) view.findViewById(R.id.edHorarioCierre);
        edTiempoMaximo = (EditText) view.findViewById(R.id.edTiempoMaximo);
        edPlazas = (EditText) view.findViewById(R.id.edPlazas);
        edAltura = (EditText) view.findViewById(R.id.edAltura);
        spTipo = (Spinner) view.findViewById(R.id.spTipo);
        spEstado = (Spinner) view.findViewById(R.id.spEstado);
        cbAdaptado = (CheckBox) view.findViewById(R.id.cbAdaptado);
        cbPlazasDiscapacitados = (CheckBox) view.findViewById(R.id.cbPlazasDiscapacitados);
        cbMotos = (CheckBox) view.findViewById(R.id.cbMotos);
        cbAseos = (CheckBox) view.findViewById(R.id.cbAseos);
        cbTarjeta = (CheckBox) view.findViewById(R.id.cbTarjeta);
        cbSeguridad = (CheckBox) view.findViewById(R.id.cbSeguridad);
        cbElectricos = (CheckBox) view.findViewById(R.id.cbElectricos);
        cbLavado = (CheckBox) view.findViewById(R.id.cbLavado);
        cbServicio24h = (CheckBox) view.findViewById(R.id.cbServicio24h);
        edDescripcion = (EditText) view.findViewById(R.id.edDescripcion);
        btnNuevoParking = (Button) view.findViewById(R.id.btnNuevoParking);

        // SPINNER TIPO
        // Creamos un ArrayAdapter usando un String Array con las opciones y un spinner layout por defecto
        adapter_tipo = ArrayAdapter.createFromResource(getActivity(),
                R.array.opciones_tipo, android.R.layout.simple_spinner_item);
        // Especificamos el layout a usar cuando la lista de opciones aparece
        adapter_tipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Aplicamos el adapter al spinner
        spTipo.setAdapter(adapter_tipo);

        // SPINNER ESTADO
        // Creamos un ArrayAdapter usando un String Array con las opciones y un spinner layout por defecto
        adapter_estado = ArrayAdapter.createFromResource(getActivity(),
                R.array.opciones_estado, android.R.layout.simple_spinner_item);
        // Especificamos el layout a usar cuando la lista de opciones aparece
        adapter_estado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Aplicamos el adapter al spinner
        spEstado.setAdapter(adapter_estado);



        // Si la acción pasada en el intent es Nuevo parking
        if(accion.equals("Nuevo parking"))
        {
            Log.e("Clic en nuevo parking ", "ok");
            // Ponemos el nombre "Nuevo parking" en la barra
            ((AreaParking) getActivity()).setActionBarTitle("Nuevo parking");

            // Recuperamos el id_usuario
            final String id_usuario = b.getString("id_usuario");

            // Asignar a variable el Botón Nuevo Parking y asignar evento OnClick para realizar las
            // acciones correspondientes
            btnNuevoParking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (nuevoParking(id_usuario)) {
                        // Se refresca el área de propietario con el nuevo parking
                        SharedPreferences sp_usuario = getActivity().getSharedPreferences("PREFS_USUARIO", 0);
                        SharedPreferences.Editor editor_usuario = sp_usuario.edit();
                        editor_usuario.putBoolean("refrescar_acceso", true);
                        editor_usuario.commit();
                        // Vuelve a la actividad principal, refrescándola, cargando el área de propietario
                        getActivity().onBackPressed();
                    }
                }
            });
        }
        // Sino entramos en "Modificar parking"
        else
        {
            Log.e("No entramos en ", "insertar nuevo parking");
            // Ponemos el nombre "Mi cuenta" en la barra
            ((AreaParking) getActivity()).setActionBarTitle("Modificar parking");

            // Recuperamos el objeto Parking con los datos del parking a modificar
            Parking miParking = (Parking)b.get("parking");

            // Recuperamos el id del parking
            final String id = String.valueOf(miParking.getId());

            // Cargamos los editText con los datos del parking
            cargarDatos(miParking);

            // Asignar a variable el Botón Modificar Parking y asignar evento OnClick para realizar las
            // acciones correspondientes
            btnNuevoParking.setText("Modificar parking");
            btnNuevoParking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(modificarParking(id))
                    {
                        // Se guardan el estado de la sesión y la acción de refresco en el fichero de preferencias PREFS_USUARIO
                        SharedPreferences sp_usuario = getActivity().getSharedPreferences("PREFS_USUARIO", 0);
                        SharedPreferences.Editor editor_usuario = sp_usuario.edit();
                        editor_usuario.putBoolean("sesion_iniciada", true);
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

    // Método que invocará a peticionServicioParking(), indicándole como argumento la URI
    // asociada para la modificación del parking, en caso de que los datos introducidos
    // sean correctos.
    public Boolean modificarParking(String id)
    {
        boolean parkingModificado = false;

        // Obtenemos los valores introducidos en los EditText
        String nuevo_nombre = edNombre.getText().toString().replace(" ", "%20"); // Reemplazamos los espacios en blanco para que la ruta funcione correctamente
        String nuevo_direccion = edDireccion.getText().toString().replace(" ", "%20"); // Reemplazamos los espacios en blanco para que la ruta funcione correctamente
        String nuevo_localidad = edLocalidad.getText().toString().replace(" ", "%20"); // Reemplazamos los espacios en blanco para que la ruta funcione correctamente
        String nuevo_provincia = edProvincia.getText().toString().replace(" ", "%20"); // Reemplazamos los espacios en blanco para que la ruta funcione correctamente
        String nuevo_latitud = edLatitud.getText().toString();
        String nuevo_longitud = edLongitud.getText().toString();
        String nuevo_telefono = edTelefono.getText().toString();
        if(nuevo_telefono.equals("0")) // Conversión para que no salte el mensaje de error "El teléfono debe constar de 9 dígitos"
            nuevo_telefono = "";
        String nuevo_imagen = edImagen.getText().toString().replace(" ", "%20"); // Reemplazamos los espacios en blanco para que la ruta funcione correctamente
        String nuevo_precio = edPrecio.getText().toString();
        String nuevo_apertura = edHorarioApertura.getText().toString();
        String nuevo_cierre = edHorarioCierre.getText().toString();
        String nuevo_tiempo = edTiempoMaximo.getText().toString();
        String nuevo_plazas = edPlazas.getText().toString();
        String nuevo_altura = edAltura.getText().toString();
        String nuevo_descripcion = edDescripcion.getText().toString().replace(" ", "%20"); // Reemplazamos los espacios en blanco para que la ruta funcione correctamente

        // Obtenemos los valores introducidos en los Spinners
        String nuevo_tipo = spTipo.getSelectedItem().toString();
        String nuevo_estado = spEstado.getSelectedItem().toString();

        // Obtenemos los valores introducidos en los CheckBox
        String nuevo_adaptado, nuevo_discapacidad, nuevo_motos, nuevo_aseos, nuevo_tarjeta,
                nuevo_seguridad, nuevo_electricos, nuevo_lavado, nuevo_servicio24h;

        if(cbAdaptado.isChecked())
            nuevo_adaptado = "1";
        else
            nuevo_adaptado = "0";

        if(cbPlazasDiscapacitados.isChecked())
            nuevo_discapacidad = "1";
        else
            nuevo_discapacidad = "0";

        if(cbMotos.isChecked())
            nuevo_motos = "1";
        else
            nuevo_motos = "0";

        if(cbAseos.isChecked())
            nuevo_aseos = "1";
        else
            nuevo_aseos = "0";

        if(cbTarjeta.isChecked())
            nuevo_tarjeta = "1";
        else
            nuevo_tarjeta = "0";

        if(cbSeguridad.isChecked())
            nuevo_seguridad = "1";
        else
            nuevo_seguridad = "0";

        if(cbElectricos.isChecked())
            nuevo_electricos = "1";
        else
            nuevo_electricos = "0";

        if(cbLavado.isChecked())
            nuevo_lavado = "1";
        else
            nuevo_lavado = "0";

        if(cbServicio24h.isChecked())
            nuevo_servicio24h = "1";
        else
            nuevo_servicio24h = "0";

        // Comprobación de errores en el formulario

        if(nuevo_nombre.equals("") || nuevo_direccion.equals("") || nuevo_localidad.equals("")
                || nuevo_provincia.equals("") || nuevo_latitud.equals("") || nuevo_longitud.equals("")
                || nuevo_precio.equals("") || nuevo_apertura.equals("") || nuevo_cierre.equals("")
                || nuevo_plazas.equals(""))
        {
            Toast.makeText(getActivity(), "Debe completar los campos obligatorios para actualizar un parking.", Toast.LENGTH_LONG).show();
        }
        else if(nuevo_telefono.length()>0 & nuevo_telefono.length()!=9)
        {
            Toast.makeText(getActivity(), "El teléfono debe constar de 9 dígitos.", Toast.LENGTH_LONG).show();
        }
        else if(nuevo_apertura.length()!=8)
        {
            Toast.makeText(getActivity(), "El formato del horario de apertura introducido es incorrecto.", Toast.LENGTH_LONG).show();
        }
        else if(nuevo_cierre.length()!=8)
        {
            Toast.makeText(getActivity(), "El formato del horario de cierre introducido es incorrecto.", Toast.LENGTH_LONG).show();
        }
        else
        {
            // Actualizamos valores de campos en blanco
            if(nuevo_telefono.equals(""))
                nuevo_telefono = "0";
            if(nuevo_imagen.equals(""))
                nuevo_imagen = "No";
            if(nuevo_tiempo.equals(""))
                nuevo_tiempo = "0";
            if(nuevo_altura.equals(""))
                nuevo_altura = "0";
            if(nuevo_descripcion.equals(""))
                nuevo_descripcion = "No";

            // Creamos la URI
            String uri = raiz + "/modifica/" + servidor + "/" + puerto + "/" + baseDatos + "/" +
                    usuario + "/" + password + "/" + id + "/" + nuevo_nombre + "/" + nuevo_direccion +
                    "/" + nuevo_localidad + "/" + nuevo_provincia + "/" + nuevo_latitud + "/" +
                    nuevo_longitud + "/" + nuevo_telefono + "/" + nuevo_imagen + "/" + nuevo_tipo +
                    "/" + nuevo_estado + "/" + nuevo_precio + "/" + nuevo_apertura + "/" + nuevo_cierre +
                    "/" + nuevo_tiempo + "/" + nuevo_plazas + "/" + nuevo_altura + "/" + nuevo_adaptado +
                    "/" + nuevo_discapacidad + "/" + nuevo_motos + "/" + nuevo_aseos + "/" + nuevo_tarjeta +
                    "/" + nuevo_seguridad + "/" + nuevo_electricos + "/" + nuevo_lavado + "/" +
                    nuevo_servicio24h + "/" + nuevo_descripcion;

            try
            {
                // Se modifican los datos del parking
                peticionServicioParking(uri);
                parkingModificado = true;
            }
            catch (Exception e)
            {
                parkingModificado = false;
            }
        }
        return parkingModificado;
    }

    // Método que invocará a peticionServicioParking(), indicándole como argumento la URI
    // asociada para la creación de un nuevo parking, en caso de que los datos introducidos
    // sean correctos.
    public Boolean nuevoParking(String id_usuario)
    {
        boolean registroInsertado = false;

        // Obtenemos los valores introducidos en los EditText
        String nuevo_nombre = edNombre.getText().toString().replace(" ", "%20"); // Reemplazamos los espacios en blanco para que la ruta funcione correctamente
        String nuevo_direccion = edDireccion.getText().toString().replace(" ", "%20"); // Reemplazamos los espacios en blanco para que la ruta funcione correctamente
        String nuevo_localidad = edLocalidad.getText().toString().replace(" ", "%20"); // Reemplazamos los espacios en blanco para que la ruta funcione correctamente
        String nuevo_provincia = edProvincia.getText().toString().replace(" ", "%20"); // Reemplazamos los espacios en blanco para que la ruta funcione correctamente
        String nuevo_latitud = edLatitud.getText().toString();
        String nuevo_longitud = edLongitud.getText().toString();
        String nuevo_telefono = edTelefono.getText().toString();
        String nuevo_imagen = edImagen.getText().toString().replace(" ", "%20"); // Reemplazamos los espacios en blanco para que la ruta funcione correctamente
        String nuevo_precio = edPrecio.getText().toString();
        String nuevo_apertura = edHorarioApertura.getText().toString();
        String nuevo_cierre = edHorarioCierre.getText().toString();
        String nuevo_tiempo = edTiempoMaximo.getText().toString();
        String nuevo_plazas = edPlazas.getText().toString();
        String nuevo_altura = edAltura.getText().toString();
        String nuevo_descripcion = edDescripcion.getText().toString().replace(" ", "%20"); // Reemplazamos los espacios en blanco para que la ruta funcione correctamente

        // Obtenemos los valores introducidos en los Spinners
        String nuevo_tipo = spTipo.getSelectedItem().toString();
        String nuevo_estado = spEstado.getSelectedItem().toString();

        // Obtenemos los valores introducidos en los CheckBox
        String nuevo_adaptado, nuevo_discapacidad, nuevo_motos, nuevo_aseos, nuevo_tarjeta,
                nuevo_seguridad, nuevo_electricos, nuevo_lavado, nuevo_servicio24h;

        if(cbAdaptado.isChecked())
            nuevo_adaptado = "1";
        else
            nuevo_adaptado = "0";

        if(cbPlazasDiscapacitados.isChecked())
            nuevo_discapacidad = "1";
        else
            nuevo_discapacidad = "0";

        if(cbMotos.isChecked())
            nuevo_motos = "1";
        else
            nuevo_motos = "0";

        if(cbAseos.isChecked())
            nuevo_aseos = "1";
        else
            nuevo_aseos = "0";

        if(cbTarjeta.isChecked())
            nuevo_tarjeta = "1";
        else
            nuevo_tarjeta = "0";

        if(cbSeguridad.isChecked())
            nuevo_seguridad = "1";
        else
            nuevo_seguridad = "0";

        if(cbElectricos.isChecked())
            nuevo_electricos = "1";
        else
            nuevo_electricos = "0";

        if(cbLavado.isChecked())
            nuevo_lavado = "1";
        else
            nuevo_lavado = "0";

        if(cbServicio24h.isChecked())
            nuevo_servicio24h = "1";
        else
            nuevo_servicio24h = "0";

        // Comprobación de errores en el formulario

        if(nuevo_nombre.equals("") || nuevo_direccion.equals("") || nuevo_localidad.equals("")
                || nuevo_provincia.equals("") || nuevo_latitud.equals("") || nuevo_longitud.equals("")
                || nuevo_precio.equals("") || nuevo_apertura.equals("") || nuevo_cierre.equals("")
                || nuevo_plazas.equals(""))
        {
            Toast.makeText(getActivity(), "Debe completar los campos obligatorios para añadir un nuevo parking.", Toast.LENGTH_LONG).show();
        }
        else if(nuevo_telefono.length()>0 & nuevo_telefono.length()!=9)
        {
            Toast.makeText(getActivity(), "El teléfono debe constar de 9 dígitos.", Toast.LENGTH_LONG).show();
        }
        else if(nuevo_apertura.length()!=8)
        {
            Toast.makeText(getActivity(), "El formato del horario de apertura introducido es incorrecto.", Toast.LENGTH_LONG).show();
        }
        else if(nuevo_cierre.length()!=8)
        {
            Toast.makeText(getActivity(), "El formato del horario de cierre introducido es incorrecto.", Toast.LENGTH_LONG).show();
        }
        else
        {
            // Actualizamos valores de campos en blanco
            if(nuevo_telefono.equals(""))
                nuevo_telefono = "0";
            if(nuevo_imagen.equals(""))
                nuevo_imagen = "No";
            if(nuevo_tiempo.equals(""))
                nuevo_tiempo = "0";
            if(nuevo_altura.equals(""))
                nuevo_altura = "0";
            if(nuevo_descripcion.equals(""))
                nuevo_descripcion = "No";

            // Creamos la URI
            String uri = raiz + "/nuevo/" + servidor + "/" + puerto + "/" + baseDatos + "/" +
                    usuario + "/" + password + "/" + nuevo_nombre + "/" + nuevo_direccion + "/" +
                    nuevo_localidad + "/" + nuevo_provincia + "/" + nuevo_latitud + "/" +
                    nuevo_longitud + "/" + nuevo_telefono + "/" + nuevo_imagen + "/" + nuevo_tipo +
                    "/" + nuevo_estado + "/" + nuevo_precio + "/" + nuevo_apertura + "/" + nuevo_cierre +
                    "/" + nuevo_tiempo + "/" + nuevo_plazas + "/" + nuevo_altura + "/" + nuevo_adaptado +
                    "/" + nuevo_discapacidad + "/" + nuevo_motos + "/" + nuevo_aseos + "/" + nuevo_tarjeta +
                    "/" + nuevo_seguridad + "/" + nuevo_electricos + "/" + nuevo_lavado + "/" +
                    nuevo_servicio24h + "/" + nuevo_descripcion + "/" + id_usuario;

            try
            {
                // Se inserta un nuevo parking
                peticionServicioParking(uri);
                registroInsertado = true;
            }
            catch (Exception e)
            {
                registroInsertado = false;
            }
        }

        return registroInsertado;
    }

    // Método encargado de realizar peticiones de creación o modificación de un parking
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
                    // Nuevo parking insertado o modificado correctamente
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

    // Método que carga los EditText con los datos del parking
    public void cargarDatos(Parking miParking)
    {
        // Cargamos los datos en los EditText
        edNombre.setText(miParking.getNombre());
        edDireccion.setText(miParking.getDireccion());
        edLocalidad.setText(miParking.getLocalidad());
        edProvincia.setText(miParking.getProvincia());
        edLatitud.setText(Double.toString(miParking.getLatitud()));
        edLongitud.setText(Double.toString(miParking.getLongitud()));
        edTelefono.setText(miParking.getTelefono());
        edImagen.setText(miParking.getImagen());
        edPrecio.setText(Double.toString(miParking.getPrecio()));
        edHorarioApertura.setText(miParking.getHorario_Apertura());
        edHorarioCierre.setText(miParking.getHorario_Cierre());
        edTiempoMaximo.setText(String.valueOf(miParking.getTiempo_Maximo()));
        edPlazas.setText(String.valueOf(miParking.getPlazas()));
        edAltura.setText(Double.toString(miParking.getAltura_Minima()));
        edDescripcion.setText(miParking.getDescripcion());

        // Cargamos los datos en los Spinners
        String Tipo = miParking.getTipo();
        if (!Tipo.equals(null))
        {
            int spTipoPosition = adapter_tipo.getPosition(Tipo);
            spTipo.setSelection(spTipoPosition);
        }

        String Estado = miParking.getEstado();
        if (!Estado.equals(null))
        {
            int spEstadoPosition = adapter_estado.getPosition(Estado);
            spEstado.setSelection(spEstadoPosition);
        }

        // Cargamos los datos en los CheckBox
        if(miParking.isAdaptado_Discapacidad()!=0)
            cbAdaptado.setChecked(true);

        if(miParking.isPlazas_Discapacidad() !=0)
            cbPlazasDiscapacitados.setChecked(true);

        if(miParking.isMotos() !=0)
            cbMotos.setChecked(true);

        if(miParking.isAseos() !=0)
            cbAseos.setChecked(true);

        if(miParking.isTarjeta() !=0)
            cbTarjeta.setChecked(true);

        if(miParking.isSeguridad() !=0)
            cbSeguridad.setChecked(true);

        if(miParking.isCoches_Electricos() !=0)
            cbElectricos.setChecked(true);

        if(miParking.isLavado() !=0)
            cbLavado.setChecked(true);

        if(miParking.isServicio_24h() !=0)
            cbServicio24h.setChecked(true);
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