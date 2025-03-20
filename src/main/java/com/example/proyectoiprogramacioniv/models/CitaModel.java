package com.example.proyectoiprogramacioniv.models;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
@Entity
public class CitaModel {
    @Id
    private int identificacion;
    private String medicoID;
    private String pacienteID;
    private int horarioID;
    private String estado; //Pendiente, Completada, Cancelada
    private float precio;

    public CitaModel() {}

    public CitaModel(int id, String medicoID, String pacienteID, int horarioID ) {
        this.identificacion = id;
        this.medicoID = medicoID;
        this.pacienteID = pacienteID;
        this.horarioID = horarioID;
        this.estado = "Pendiente";
        this.precio = 0;
    }

    //      Getters
    public int getIdentificacion() {return identificacion;}

    public String getMedicoID() {return medicoID;}

    public String getPacienteID() {return pacienteID;}

    public int getHorarioID() {return horarioID;}

    public String getEstado() {return estado;}

    public float getPrecio() {return precio;}

    //      setters
    public void setIdentificacion(int identificacion) {this.identificacion = identificacion;}

    public void setMedicoID(String medicoID) {this.medicoID = medicoID;}

    public void setPacienteID(String pacienteID) {this.pacienteID = pacienteID;}

    public void setHorarioID(int horarioID) {this.horarioID = horarioID;}

    public void setEstado(String estado) {this.estado = estado;}

    public void setPrecio(float precio) {this.precio = precio;}
}
