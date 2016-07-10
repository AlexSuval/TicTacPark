package com.alex.tictacpark.models;

/**
 * Created by Alex on 29/12/2015.
 */
public class Propietario extends Conductor {
    private String DNI;
    private String Telefono;

    public Propietario() {
    }

    public String getDNI() {
        return DNI;
    }

    public void setDNI(String DNI) {
        this.DNI = DNI;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        Telefono = telefono;
    }
}