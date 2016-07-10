package com.alex.tictacpark.parsers;

import android.content.Context;
import android.util.Log;

import com.alex.tictacpark.models.Historial;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Alex on 12/01/2016.
 */
public class HistorialParser {

    // Constantes --> Se inicializan al nombre de las etiquetas del model Historial
    private static final String NOMBRE="nombre";
    private static final String FECHA="fecha";
    private static final String DURACION="duracion";
    private static final String PRECIO="precio";
    private static final String PRECIO_HORA="precio_hora";

    // Constructor por defecto
    public HistorialParser() {
    }

    // Método para parsear
    public ArrayList<Historial> parse(Context c){
        ArrayList<Historial> historial=new ArrayList<>();
        // Cargamos el documento JSON en un String
        String json=cargar(c);
        if(json!=null)
        {
            JSONObject jsonObject;
            try{
                jsonObject=new JSONObject(json); // Recuperamos el JSON en un JSONObject
            }
            catch(JSONException e){
                e.printStackTrace();
                jsonObject=new JSONObject();
            }

            try{
                JSONArray array = jsonObject.getJSONArray("historial");    // Array que contiene todos nuestros objetos historial
                // Recorremos el array. Cada elemento contendrá un objeto con la info de cada tarjeta
                JSONObject tarjeta;
                for (int i=0; i<array.length(); i++) {
                    tarjeta = array.getJSONObject(i);   // Extraemos cada JSONObject, que tendrá la info de cada tarjeta
                    Historial h = new Historial();

                    // Obtenemos los campos de la tarjeta y los vamos metiendo en Historial
                    int Id = tarjeta.getInt("id");
                    h.setId(Id);

                    String Nombre = tarjeta.getString("nombre");
                    h.setNombre(Nombre);

                    String Fecha = tarjeta.getString("fecha");
                    h.setFecha(Fecha);

                    String Duracion = tarjeta.getString("duracion");
                    h.setDuracion(Duracion);

                    String Precio = tarjeta.getString("precio");
                    h.setPrecio(Precio);

                    String Precio_hora = tarjeta.getString("precio_hora");
                    h.setPrecio_hora(Precio_hora);

                    // Añadimos "la tarjeta" en nuestro ArrayList<Historial>
                    historial.add(h);
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }

        return historial;
    }

    // Método para cargar el documento JSON en un String
    public String cargar(Context c){
        String json_final;
        try{
            BufferedReader bufferedReader=new BufferedReader
                    (new InputStreamReader(c.openFileInput("historial.json"))); // Nº bits=8 (1 byte)
            StringBuilder stringBuilder=new StringBuilder();
            String line=null;
            while((line=bufferedReader.readLine())!=null)
            {
                stringBuilder.append(line+"\n");    // Para tenerlo identado (salto de línea)
            }
            json_final=stringBuilder.toString();
        }
        catch(Exception e) {
            Log.e("StringBuilding", "Error" + e.toString());
            json_final=null;
        }

        return json_final;
    }
}