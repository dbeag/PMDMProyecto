package com.example.proyecto.model;

import com.example.proyecto.MainActivity;

public class Ubicacion {

    private String id;
    private String email;
    private String nombre;
    private Double latitud;
    private Double longitud;
    private String descripcion;
    private String TipoUbicacion;

    public Ubicacion(){
        setEmail(MainActivity.getEmail());
        setId(MainActivity.getEmail() + "-" + (MainActivity.lstUbicaciones.size() + 1));
    };

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoUbicacion() {
        return TipoUbicacion;
    }

    public void setTipoUbicacion(String String) {
        this.TipoUbicacion = String;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

