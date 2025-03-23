package com.example.proyectoiprogramacioniv.models;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class MedicoModel {

    @Id
    private String identificacion;
    private String nombre;
    private String contrasenna;
    private boolean activo;
    private String especialidad;
    private double costoConsulta;
    private String ubicacion;
    private String presentacion;

    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Duration frecuencia;

    @ElementCollection
    private List<LocalTime> horariosDisponibles;

    public MedicoModel() {
        horariosDisponibles = new ArrayList<>();
    }
    public MedicoModel(String identificacion, String nombre, String contrasenna) {
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.contrasenna = contrasenna;
        this.activo = false;
        this.especialidad = "";
        this.ubicacion = "";
        this.presentacion = "";
        this.horaInicio = null;
        this.horaFin = null;
        this.frecuencia = null;
        horariosDisponibles = new ArrayList<>();
    }

    public void generarHorariosDisponibles() {
        LocalTime temp = this.horaInicio;
        while (temp.isBefore(this.horaFin)) {
            horariosDisponibles.add(temp);
            temp = temp.plus(frecuencia);
        }
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContrasenna() {
        return contrasenna;
    }

    public void setContrasenna(String contrasenna) {
        this.contrasenna = contrasenna;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public double getCostoConsulta() {
        return costoConsulta;
    }

    public void setCostoConsulta(double costoConsulta) {
        this.costoConsulta = costoConsulta;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public Duration getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(Duration frecuencia) {
        this.frecuencia = frecuencia;
    }

    public List<LocalTime> getHorariosDisponibles() {
        return horariosDisponibles;
    }

    public void setHorariosDisponibles(List<LocalTime> horariosDisponibles) {
        this.horariosDisponibles = horariosDisponibles;
    }
}
