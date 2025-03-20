package com.example.proyectoiprogramacioniv.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class HorarioModel {

    @Id
    private int identificacion;
    private String medicoID;
    private String fecha;
    private String hora;
    private Boolean disponible; //= false;

    public HorarioModel() {}

    public HorarioModel(int id, String medicoID ) {
        this.identificacion = id;
        this.medicoID = medicoID;
        this.fecha = "";
        this.hora = "";
        this.disponible = false;
    }


    //  getters
    public int getId() {return identificacion;}
    public String getMedicoID() {return medicoID;}
    public String getHora() {return hora;}
    public String getFecha() {return fecha;}
    public Boolean getDisponible() {return disponible;}

    //  setters
    public void setId(int id) {this.identificacion = id;}
    public void setMedicoID(String medicoID) {this.medicoID = medicoID;}
    public void setHora(String hora) {this.hora = hora;}
    public void setFecha(String fecha) {this.fecha = fecha;}
    public void setDisponible(Boolean disponible) {this.disponible = disponible;}

}
