package com.example.topics.Modelo;

import android.net.Uri;

import com.example.topics.Utilidades.Constants;

import java.util.HashMap;
import java.util.Map;

public class Post {

    private String codigoImagen;

    private String urlImagen;

    private String descripcion;

    private int precio;

    private boolean descargable;

    private String emailuser;

    private String user;

    public Post(String codigoImagen) {
        this.codigoImagen = codigoImagen;

    }

    public Post(String codigoImagen, String descripcion) {
        this.codigoImagen = codigoImagen;
        this.descripcion = descripcion;
    }



    public Post() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEmailuser() {
        return emailuser;
    }

    public void setEmailuser(String emailuser) {
        this.emailuser = emailuser;
    }

    public static Map<String, Object> createMapfromPost(Post post){
        Map<String,Object> map = new HashMap<>();
        map.put(Constants.KEY_DESCARGABLE, post.isDescargable());
        map.put(Constants.KEY_DESCRIPCION,post.getDescripcion());
        map.put(Constants.KEY_PRICE,post.getPrecio());
        return map;
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
