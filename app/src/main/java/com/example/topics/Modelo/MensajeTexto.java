package com.example.topics.Modelo;

public class MensajeTexto extends Mensaje{

    private String mensaje;

    public MensajeTexto(String idEmisor, String idReceptor, String mensaje,String type) {
        super(idEmisor, idReceptor,type, mensaje);
        this.mensaje = mensaje;
    }

    public MensajeTexto() {
    }
}
