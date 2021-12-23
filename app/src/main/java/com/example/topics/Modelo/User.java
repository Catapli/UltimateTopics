package com.example.topics.Modelo;

import java.io.Serializable;

public class User implements Serializable {
    private String token, id, nombre, password, email,
            fecha, nombreCuenta, urlPerfil, seguidores, seguidos, descripcion,
            UrlDni,UrlRostro, nuevosSubsTop, top;

    private boolean habilitado, descargable;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNombreCuenta() {
        return nombreCuenta;
    }

    public void setNombreCuenta(String nombreCuenta) {
        this.nombreCuenta = nombreCuenta;
    }

    public String getUrlPerfil() {
        return urlPerfil;
    }

    public void setUrlPerfil(String urlPerfil) {
        this.urlPerfil = urlPerfil;
    }

    public String getSeguidores() {
        return seguidores;
    }

    public void setSeguidores(String seguidores) {
        this.seguidores = seguidores;
    }

    public String getSeguidos() {
        return seguidos;
    }

    public void setSeguidos(String seguidos) {
        this.seguidos = seguidos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUrlDni() {
        return UrlDni;
    }

    public void setUrlDni(String urlDni) {
        UrlDni = urlDni;
    }

    public String getUrlRostro() {
        return UrlRostro;
    }

    public void setUrlRostro(String urlRostro) {
        UrlRostro = urlRostro;
    }

    public String getNuevosSubsTop() {
        return nuevosSubsTop;
    }

    public void setNuevosSubsTop(String nuevosSubsTop) {
        this.nuevosSubsTop = nuevosSubsTop;
    }

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public boolean isDescargable() {
        return descargable;
    }

    public void setDescargable(boolean descargable) {
        this.descargable = descargable;
    }
}
