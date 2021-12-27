package com.example.topics.Modelo;

import android.net.Uri;

public class Post {

    private String codigoImagen;

    private String urlImagen;

    private String descripcion;

    private int precio;

    private boolean descargable;

    private String idUser;

    private String user;

    private String userFoto;



    public Post(String codigoImagen) {
        this.codigoImagen = codigoImagen;

    }

    public Post(String codigoImagen, String descripcion) {
        this.codigoImagen = codigoImagen;
        this.descripcion = descripcion;
    }



    public Post() {
    }


    public String getUserFoto() {
        return userFoto;
    }

    public void setUserFoto(String userFoto) {
        this.userFoto = userFoto;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }






    public void convertirUri(Uri url){
        setUrlImagen(url.toString());
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public String getCodigoImagen() {
        return codigoImagen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getPrecio() {
        return precio;
    }

    public boolean isDescargable() {
        return descargable;
    }

    @Override
    public String toString() {
        return "Post{" +
                "codigoImagen='" + codigoImagen + '\'' +
                ", urlImagen='" + urlImagen + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precio=" + precio +
                ", descargable=" + descargable +
                ", idUser='" + idUser + '\'' +
                ", user='" + user + '\'' +
                ", userFoto='" + userFoto + '\'' +
                '}';
    }

    public void setCodigoImagen(String codigoImagen) {
        this.codigoImagen = codigoImagen;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public void setDescargable(boolean descargable) {
        this.descargable = descargable;
    }
}
