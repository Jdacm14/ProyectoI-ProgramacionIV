package com.example.proyectoiprogramacioniv.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
public class HorarioModel {

    @Id
    private String identificacion;
    private String medicoID;
    private Boolean disponible;
    private float precio;
    private int frecuenciaMinutos;
    private String diaSemana;
    private String horaInicio;
    private String horaFin;
    private String fecha;



    public HorarioModel() {}

    public HorarioModel(String id, String medicoID ) {
        this.identificacion = id;
        this.medicoID = medicoID;
        this.disponible = false;
        this.precio = 0;
        this.frecuenciaMinutos = 0;
        this.diaSemana = "";
        this.horaInicio = "";
        this.horaFin = "";
        this.fecha = "";

    }


    //  getters
    public String getId() {return identificacion;}
    public String getMedicoID() {return medicoID;}
    public Boolean getDisponible() {return disponible;}
    public float getPrecio() {return precio;}
    public int getFrecuenciaMinutos() {return frecuenciaMinutos;}
    public String getDiaSemana() {return diaSemana;}
    public String getHoraInicio() {return horaInicio;}
    public String getHoraFin() {return horaFin;}
    public String getFecha() {return fecha;}



    //  setters
    public void setId(String id) {this.identificacion = id;}
    public void setMedicoID(String medicoID) {this.medicoID = medicoID;}
    public void setDisponible(Boolean disponible) {this.disponible = disponible;}
    public void setPrecio(float costo) {this.precio = costo;}
    public void setFrecuenciaMinutos(int frecuenciaMinutos) {this.frecuenciaMinutos = frecuenciaMinutos;}
    public void setDiaSemana(String diaSemana) {this.diaSemana = diaSemana;}
    public void setHoraInicio(String horaInicio) {this.horaInicio = horaInicio;}
    public void setHoraFin(String horaFin) {this.horaFin = horaFin;}
    public void setFecha(String fecha) {this.fecha = fecha;}

}
