package com.alex.tictacpark.parsers;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.io.FileInputStream;

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
    public HashMap<String, String> parse(Context c){
        HashMap<String,String> historial=new HashMap<>();
        // Cargamos el documento JSON en un String
        String json=cargar(c);
        if(json!=null)        Log.e("JSON", json);
        return historial;
    }

    // Método para cargar el documento JSON en un String
    private String cargar(Context c){
        String json_final;
        try{
            BufferedReader bufferedReader=new BufferedReader
                    (new InputStreamReader(c.openFileInput("/storage/sdcard0/Android/data/com.alex.tictacpark/files/historial.json"))); // Nº bits=8 (1 byte)
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
