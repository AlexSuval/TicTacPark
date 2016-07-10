package com.alex.tictacpark.parsers;

import android.content.Context;
import android.util.Log;

import com.alex.tictacpark.models.Parking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Alex on 12/01/2016.
 */
public class ParkingsParser {

    // Constructor por defecto
    public ParkingsParser() {
    }

    // Método para parsear
    public ArrayList<Parking> parse(Context c){
        ArrayList<Parking> parkings=new ArrayList<>();
        // Cargamos el documento JSON en un String
        String json=cargar(c);
        if(json!=null) // Ya hay un JSON
        {
            JSONObject jsonObject;
            try
            {
                jsonObject=new JSONObject(json); // Recuperamos el JSON en un JSONObject
            }
            catch(JSONException e)
            {
                e.printStackTrace();
                jsonObject=new JSONObject();
            }


            try
            {
                JSONArray array = jsonObject.getJSONArray("parkings");    // Array que contiene todos nuestros objetos parking
                // Recorremos el array. Cada elemento contendrá un objeto con la info de cada tarjeta
                JSONObject tarjeta;
                for (int i=0; i<array.length(); i++) {
                    tarjeta = array.getJSONObject(i);   // Extraemos cada JSONObject, que tendrá la info de cada tarjeta

                    // Obtenemos los campos de la tarjeta y los vamos metiendo en Parking
                    int id = tarjeta.getInt("id");
                    String nombre = tarjeta.getString("nombre");
                    String direccion = tarjeta.getString("direccion");
                    String localidad = tarjeta.getString("localidad");
                    String provincia = tarjeta.getString("provincia");
                    double latitud = tarjeta.getDouble("latitud");
                    double longitud = tarjeta.getDouble("longitud");
                    String telefono = tarjeta.getString("telefono");
                    String imagen = tarjeta.getString("imagen");
                    String tipo = tarjeta.getString("tipo");
                    String estado = tarjeta.getString("estado");
                    double precio = tarjeta.getDouble("precio");
                    String horario_apertura = tarjeta.getString("horario_apertura");
                    String horario_cierre = tarjeta.getString("horario_cierre");
                    int tiempo_maximo = tarjeta.getInt("tiempo_maximo");
                    int plazas = tarjeta.getInt("plazas");
                    double altura_minima = tarjeta.getDouble("altura_minima");

                    String adaptado_discapacidad_st = tarjeta.getString("adaptado_discapacidad");
                    byte adaptado_discapacidad = Byte.valueOf(adaptado_discapacidad_st);

                    String plazas_discapacidad_st = tarjeta.getString("plazas_discapacidad");
                    byte plazas_discapacidad = Byte.valueOf(plazas_discapacidad_st);

                    String motos_st = tarjeta.getString("motos");
                    byte motos = Byte.valueOf(motos_st);

                    String aseos_st = tarjeta.getString("aseos");
                    byte aseos = Byte.valueOf(aseos_st);

                    String tarjeta_st = tarjeta.getString("tarjeta");
                    byte tarjeta_credito = Byte.valueOf(tarjeta_st);

                    String seguridad_st = tarjeta.getString("seguridad");
                    byte seguridad = Byte.valueOf(seguridad_st);

                    String coches_electricos_st = tarjeta.getString("coches_electricos");
                    byte coches_electricos = Byte.valueOf(coches_electricos_st);

                    String lavado_st = tarjeta.getString("lavado");
                    byte lavado = Byte.valueOf(lavado_st);

                    String servicio_24h_st = tarjeta.getString("servicio_24h");
                    byte servicio_24h = Byte.valueOf(servicio_24h_st);

                    String descripcion = tarjeta.getString("descripcion");

                    Parking nuevoParking = new Parking(id, nombre, direccion, localidad,
                            provincia, latitud, longitud, telefono,
                            imagen, tipo, estado, precio,
                            horario_apertura, horario_cierre, tiempo_maximo,
                            plazas, altura_minima, adaptado_discapacidad,
                            plazas_discapacidad, motos, aseos,
                            tarjeta_credito, seguridad, coches_electricos,
                            lavado, servicio_24h, descripcion);

                    // Añadimos "la tarjeta" en nuestro ArrayList<Parking>
                    parkings.add(nuevoParking);
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }

        return parkings;
    }

    // Método para cargar el documento JSON en un String
    public String cargar(Context c){
        String json_final;
        try{
            BufferedReader bufferedReader=new BufferedReader
                    (new InputStreamReader(c.openFileInput("parkings.json"))); // Nº bits=8 (1 byte)
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