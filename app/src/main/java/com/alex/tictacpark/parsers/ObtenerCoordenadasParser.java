package com.alex.tictacpark.parsers;

/**
 * Created by Alex on 22/01/2016.
 */

        import android.util.Log;

        import com.google.android.gms.maps.model.LatLng;

        import org.apache.http.HttpEntity;
        import org.apache.http.HttpResponse;
        import org.apache.http.NameValuePair;
        import org.apache.http.client.HttpClient;
        import org.apache.http.client.entity.UrlEncodedFormEntity;
        import org.apache.http.client.methods.HttpPost;
        import org.apache.http.impl.client.DefaultHttpClient;
        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.UnsupportedEncodingException;
        import java.util.ArrayList;
        import java.util.HashMap;

/**
 * Parseador de Obtener coordenadas a partir de la dirección completa
 * Descarga y parsea el JSON de la API de Google Maps Places API.
 */
public class ObtenerCoordenadasParser {

    // Constantes
    private static final String TIPO_DATOS = "utf-8";

    // Constructor por defecto
    public ObtenerCoordenadasParser(){}

    /**
     * Descargar datos
     * @param URL URL de la que se descargan los datos
     * @return Objeto String con el documento descargado.
     */
    public String download(String URL) {
        ArrayList<NameValuePair> param = new ArrayList<>();
        InputStream inputStream = null;
        String result = "";

        // Conectar a la URL.
        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(URL);
            httpPost.setEntity(new UrlEncodedFormEntity(param));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();

            inputStream = httpEntity.getContent();
        }
        catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        catch (IllegalStateException e2) {
            Log.e("IllegalStateException", e2.toString());
            e2.printStackTrace();
        }
        catch (IOException e3) {
            Log.e("IOException", e3.toString());
            e3.printStackTrace();
        }

        // Convertir a un string
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, TIPO_DATOS), 8);
            StringBuilder sBuilder = new StringBuilder();

            String line = null;
            while ((line = bReader.readLine()) != null) {
                sBuilder.append(line + "\n");
            }

            inputStream.close();
            result = sBuilder.toString();

        }
        catch (Exception e) {
            Log.e("StringBuilding", "Error " + e.toString());
        }
        return result;
    }

    /**
     * Parsea el documento descargado previamente.
     * @param result String descargado.
     * @return Los minutos estimados para volver al coche.
     */
    public LatLng parse(String  result){
        JSONObject jObj;
        String latitud="";
        String longitud="";
        LatLng coordenadas;

        // Se transforma en un JSONObject y se recorre
        try {
            jObj=new JSONObject(result);
            JSONArray results=jObj.getJSONArray("results");
            JSONObject geometry=results.getJSONObject(0);
            JSONObject geometry_object=geometry.getJSONObject("geometry");
            JSONObject location=geometry_object.getJSONObject("location");
            latitud=location.getString("lat");
            longitud=location.getString("lng");
        }
        catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        coordenadas=new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud));
        return coordenadas;
    }
}