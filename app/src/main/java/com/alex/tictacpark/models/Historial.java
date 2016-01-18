package com.alex.tictacpark.models;

/**
 * Created by Alex on 12/01/2016.
 */
public class Historial {

    private String Nombre;
    private String Fecha;
    private String Duracion;
    private String Precio;
    private String Precio_hora;

    public Historial() {
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public String getDuracion() {
        return Duracion;
    }

    public void setDuracion(String duracion) {
        Duracion = duracion;
    }

    public String getPrecio() {
        return Precio;
    }

    public void setPrecio(String precio) {
        Precio = precio;
    }

    public String getPrecio_hora() {
        return Precio_hora;
    }

    public void setPrecio_hora(String precio_hora) {
        Precio_hora = precio_hora;
    }
}
