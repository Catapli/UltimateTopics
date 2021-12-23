package com.example.topics.Modelo;

import java.util.Date;

public abstract class Mensaje {
    private String idEmisor;

    private String idReceptor;

    private String type;

    private String mensaje;

    private String dateTime;

    private Date dateObject;

    public String conversionID, conversionName, conversionImage;

    public Mensaje(String idEmisor, String idReceptor, String type, String mensaje) {
        this.idEmisor = idEmisor;
        this.idReceptor = idReceptor;
        this.type = type;
        this.mensaje = mensaje;
    }

    public Mensaje() {
    }

    public String getConversionID() {
        return conversionID;
    }

    public void setConversionID(String conversionID) {
        this.conversionID = conversionID;
    }

    public String getConversionName() {
        return conversionName;
    }

    public void setConversionName(String conversionName) {
        this.conversionName = conversionName;
    }

    public String getConversionImage() {
        return conversionImage;
    }

    public void setConversionImage(String conversionImage) {
        this.conversionImage = conversionImage;
    }

    public String getDateTime() {
        return dateTime;
    }

    public Date getDateObject() {
        return dateObject;
    }

    public void setDateObject(Date dateObject) {
        this.dateObject = dateObject;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdEmisor() {
        return idEmisor;
    }

    public void setIdEmisor(String idEmisor) {
        this.idEmisor = idEmisor;
    }

    public String getIdReceptor() {
        return idReceptor;
    }

    public void setIdReceptor(String idReceptor) {
        this.idReceptor = idReceptor;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
