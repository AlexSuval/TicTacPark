package com.alex.tictacpark.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Alex on 29/12/2015.
 */
public class Parking implements Parcelable {
    private int Id;
    private String Nombre;
    private String Direccion;
    private String Localidad;
    private String Provincia;
    private double Latitud;
    private double Longitud;
    private String Telefono;
    private String Imagen;
    private String Tipo;
    private String Estado;
    private double Precio;
    private String Horario_Apertura;
    private String Horario_Cierre;
    private int Tiempo_Maximo;
    private int Plazas;
    private double Altura_Minima;
    private byte Adaptado_Discapacidad;
    private byte Plazas_Discapacidad;
    private byte Motos;
    private byte Aseos;
    private byte Tarjeta;
    private byte Seguridad;
    private byte Coches_Electricos;
    private byte Lavado;
    private byte Servicio_24h;
    private String Descripcion;

    // Constructor por defecto
    public Parking() {
    }

    // Constructor con coordenadas
    public Parking(int Id, String Nombre, double Latitud, double Longitud, String Telefono, String Tipo, double Precio, String Estado){
        this.Id = Id;
        this.Nombre = Nombre;
        this.Latitud = Latitud;
        this.Longitud = Longitud;
        this.Telefono = Telefono;
        this.Tipo = Tipo;
        this.Precio = Precio;
        this.Estado = Estado;
    }

    // Constructor completo
    // TODO PRUEBAS: Constructor con información de servicios
    public Parking(int Id, String Nombre, String Direccion, String Localidad, String Provincia,
                   double Latitud, double Longitud, String Telefono, String Imagen, String Tipo,
                   String Estado, double Precio, String Horario_Apertura, String Horario_Cierre,
                   int Tiempo_Maximo, int Plazas, double Altura_Minima, byte Adaptado_Discapacidad,
                   byte Plazas_Discapacidad, byte Motos, byte Aseos, byte Tarjeta, byte Seguridad,
                   byte Coches_Electricos, byte Lavado, byte Servicio_24h, String Descripcion){
        this.Id = Id;
        this.Nombre = Nombre;
        this.Direccion = Direccion;
        this.Localidad = Localidad;
        this.Provincia = Provincia;
        this.Latitud = Latitud;
        this.Longitud = Longitud;
        this.Telefono = Telefono;
        this.Imagen = Imagen;
        this.Tipo = Tipo;
        this.Estado = Estado;
        this.Precio = Precio;
        this.Horario_Apertura = Horario_Apertura;
        this.Horario_Cierre = Horario_Cierre;
        this.Tiempo_Maximo = Tiempo_Maximo;
        this.Plazas = Plazas;
        this.Altura_Minima = Altura_Minima;
        this.Adaptado_Discapacidad = Adaptado_Discapacidad;
        this.Plazas_Discapacidad = Plazas_Discapacidad;
        this.Motos = Motos;
        this.Aseos = Aseos;
        this.Tarjeta = Tarjeta;
        this.Seguridad = Seguridad;
        this.Coches_Electricos = Coches_Electricos;
        this.Lavado = Lavado;
        this.Servicio_24h = Servicio_24h;
        this.Descripcion = Descripcion;
    }

    // TODO PRUEBAS: Constructor con información de servicios
    public Parking(int Id, String Nombre, double Latitud, double Longitud, String Telefono, String Tipo, double Precio, String Estado,
                   byte Adaptado_Discapacidad, byte Plazas_Discapacidad, byte Motos, byte Aseos, byte Tarjeta, byte Seguridad,
                   byte Coches_Electricos, byte Lavado, byte Servicio_24h){
        this.Id = Id;
        this.Nombre = Nombre;
        this.Latitud = Latitud;
        this.Longitud = Longitud;
        this.Telefono = Telefono;
        this.Tipo = Tipo;
        this.Precio = Precio;
        this.Estado = Estado;
        this.Adaptado_Discapacidad = Adaptado_Discapacidad;
        this.Plazas_Discapacidad = Plazas_Discapacidad;
        this.Motos = Motos;
        this.Aseos = Aseos;
        this.Tarjeta = Tarjeta;
        this.Seguridad = Seguridad;
        this.Coches_Electricos = Coches_Electricos;
        this.Lavado = Lavado;
        this.Servicio_24h = Servicio_24h;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public double getLatitud() {
        return Latitud;
    }

    public void setLatitud(double latitud) {
        Latitud = latitud;
    }

    public double getLongitud() {
        return Longitud;
    }

    public void setLongitud(double longitud) {
        Longitud = longitud;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

    public String getLocalidad() {
        return Localidad;
    }

    public void setLocalidad(String localidad) {
        Localidad = localidad;
    }

    public String getProvincia() {
        return Provincia;
    }

    public void setProvincia(String provincia) {
        Provincia = provincia;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        Telefono = telefono;
    }

    public String getImagen() {
        return Imagen;
    }

    public void setImagen(String imagen) {
        Imagen = imagen;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }

    public double getPrecio() {
        return Precio;
    }

    public void setPrecio(double precio) {
        Precio = precio;
    }

    public String getHorario_Apertura() {
        return Horario_Apertura;
    }

    public void setHorario_Apertura(String horario_Apertura) {
        Horario_Apertura = horario_Apertura;
    }

    public String getHorario_Cierre() {
        return Horario_Cierre;
    }

    public void setHorario_Cierre(String horario_Cierre) {
        Horario_Cierre = horario_Cierre;
    }

    public int getTiempo_Maximo() {
        return Tiempo_Maximo;
    }

    public void setTiempo_Maximo(int tiempo_Maximo) {
        Tiempo_Maximo = tiempo_Maximo;
    }

    public int getPlazas() {
        return Plazas;
    }

    public void setPlazas(int plazas) {
        Plazas = plazas;
    }

    public double getAltura_Minima() {
        return Altura_Minima;
    }

    public void setAltura_Minima(double altura_Minima) {
        Altura_Minima = altura_Minima;
    }

    public byte isAdaptado_Discapacidad() {
        return Adaptado_Discapacidad;
    }

    public void setAdaptado_Discapacidad(byte adaptado_Discapacidad) {
        Adaptado_Discapacidad = adaptado_Discapacidad;
    }

    public byte isPlazas_Discapacidad() {
        return Plazas_Discapacidad;
    }

    public void setPlazas_Discapacidad(byte plazas_Discapacidad) {
        Plazas_Discapacidad = plazas_Discapacidad;
    }

    public byte isMotos() {
        return Motos;
    }

    public void setMotos(byte motos) {
        Motos = motos;
    }

    public byte isAseos() {
        return Aseos;
    }

    public void setAseos(byte aseos) {
        Aseos = aseos;
    }

    public byte isTarjeta() {
        return Tarjeta;
    }

    public void setTarjeta(byte tarjeta) {
        Tarjeta = tarjeta;
    }

    public byte isSeguridad() {
        return Seguridad;
    }

    public void setSeguridad(byte seguridad) {
        Seguridad = seguridad;
    }

    public byte isCoches_Electricos() {
        return Coches_Electricos;
    }

    public void setCoches_Electricos(byte coches_Electricos) {
        Coches_Electricos = coches_Electricos;
    }

    public byte isLavado() {
        return Lavado;
    }

    public void setLavado(byte lavado) {
        Lavado = lavado;
    }

    public byte isServicio_24h() {
        return Servicio_24h;
    }

    public void setServicio_24h(byte servicio_24h) {
        Servicio_24h = servicio_24h;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    // Este método nos permitirá pasar el objeto de un fragment a otro con un Intent
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeString (Nombre);
        dest.writeString (Direccion);
        dest.writeString (Localidad);
        dest.writeString (Provincia);
        dest.writeDouble(Latitud);
        dest.writeDouble(Longitud);
        dest.writeString(Telefono);
        dest.writeString (Imagen);
        dest.writeString (Tipo);
        dest.writeString (Estado);
        dest.writeDouble(Precio);
        dest.writeString(Horario_Apertura);
        dest.writeString(Horario_Cierre);
        dest.writeInt(Tiempo_Maximo);
        dest.writeInt(Plazas);
        dest.writeDouble(Altura_Minima);
        dest.writeByte(Adaptado_Discapacidad);
        dest.writeByte(Plazas_Discapacidad);
        dest.writeByte(Motos);
        dest.writeByte(Aseos);
        dest.writeByte(Tarjeta);
        dest.writeByte(Seguridad);
        dest.writeByte(Coches_Electricos);
        dest.writeByte(Lavado);
        dest.writeByte(Servicio_24h);
        dest.writeString (Descripcion);
    }

    // Parcelable protocol requires a Parcelable.Creator object called CREATOR

    // Following is a sample implementation of Parcelable.Creator<ParcelData> interface for my class ParcelData.java-
    /**
     * It will be required during un-marshaling data stored in a Parcel
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Parking createFromParcel(Parcel source) {
            return new Parking(source);
        }

        public Parking[] newArray(int size) {
            return new Parking[size];
        }
    };

    /**
     * This will be used only by the MyCreator
     * @param source
     */
    public Parking(Parcel source){
            /*
             * Reconstruct from the Parcel
             */
        Id=source.readInt();
        Nombre=source.readString();
        Direccion=source.readString();
        Localidad=source.readString();
        Provincia=source.readString ();
        Latitud=source.readDouble();
        Longitud=source.readDouble();
        Telefono=source.readString();
        Imagen=source.readString ();
        Tipo=source.readString();
        Estado=source.readString();
        Precio=source.readDouble();
        Horario_Apertura=source.readString();
        Horario_Cierre=source.readString();
        Tiempo_Maximo=source.readInt();
        Plazas=source.readInt();
        Altura_Minima=source.readDouble();
        Adaptado_Discapacidad=source.readByte();
        Plazas_Discapacidad=source.readByte();
        Motos=source.readByte();
        Aseos=source.readByte();
        Tarjeta=source.readByte();
        Seguridad=source.readByte();
        Coches_Electricos=source.readByte();
        Lavado=source.readByte();
        Servicio_24h=source.readByte();
        Descripcion=source.readString();
    }
}