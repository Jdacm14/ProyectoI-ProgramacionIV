package com.example.proyectoiprogramacioniv.models;

import jakarta.persistence.*;

@Entity
public class HorarioModel {

    @Id
    private String identificacion;
    private String medicoID;
    private float precio;
    private String diaSemana;
    private String horaInicio;
    private String horaFin;
    private String fecha;
    private String pacienteID;
    private String estado; // completada, pendiente



    public HorarioModel() {}

    public HorarioModel(String id, String medicoID ) {
        this.identificacion = id;
        this.medicoID = medicoID;
        this.precio = 0;
        this.diaSemana = "";
        this.horaInicio = "";
        this.horaFin = "";
        this.fecha = "";
        this.pacienteID = null;
        this.estado = "";
    }


    //  getters
    public String getId() {return identificacion;}
    public String getMedicoID() {return medicoID;}
    public float getPrecio() {return precio;}
    public String getDiaSemana() {return diaSemana;}
    public String getHoraInicio() {return horaInicio;}
    public String getHoraFin() {return horaFin;}
    public String getFecha() {return fecha;}
    public String getPacienteID() {return pacienteID;}
    public String getEstado() {return estado;}

    //  setters
    public void setId(String id) {this.identificacion = id;}
    public void setMedicoID(String medicoID) {this.medicoID = medicoID;}
    public void setPrecio(float costo) {this.precio = costo;}
    public void setDiaSemana(String diaSemana) {this.diaSemana = diaSemana;}
    public void setHoraInicio(String horaInicio) {this.horaInicio = horaInicio;}
    public void setHoraFin(String horaFin) {this.horaFin = horaFin;}
    public void setFecha(String fecha) {this.fecha = fecha;}
    public void setPacienteID(String pacienteID) {this.pacienteID = pacienteID;}
    public void setEstado(String estado) {this.estado = estado;}
}
