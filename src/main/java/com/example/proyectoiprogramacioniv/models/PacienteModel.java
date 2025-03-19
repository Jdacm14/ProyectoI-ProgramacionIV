package com.example.proyectoiprogramacioniv.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class PacienteModel {
    @Id
    private String identificacion;
    private String nombre;
    private String contrasenna;

    public PacienteModel() {}
    public PacienteModel(String identificacion, String nombre, String contrasenna) {
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.contrasenna = contrasenna;
    }

    // getters
    public String getIdentificacion() {return identificacion;}
    public String getNombre() {return nombre;}
    public String getContrasenna() {return contrasenna;}

    // setters
    public void setNombre(String nombre) {this.nombre = nombre;}
    public void setIdentificacion(String identificacion) {this.identificacion = identificacion;}
    public void setContrasenna(String contrasenna) {this.contrasenna = contrasenna;}
}
