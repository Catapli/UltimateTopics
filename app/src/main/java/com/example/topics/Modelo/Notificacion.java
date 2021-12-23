package com.example.topics.Modelo;

public class Notificacion {
    private String tittle;
    private String text;

    public Notificacion(String tittle, String text) {
        this.tittle = tittle;
        this.text = text;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTittle() {
        return tittle;
    }

    public String getText() {
        return text;
    }
}


