package com.example.proyectoiprogramacioniv.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class AdministradorModel {
    @Id
    private String identificacion;
    private String contrasenna;

    public AdministradorModel() {}
    public AdministradorModel(String identificacion, String contrasenna) {
        this.identificacion = identificacion;
        this.contrasenna = contrasenna;
    }

    // getters
    public String getIdentificacion() {return identificacion;}
    public String getContrasenna() {return contrasenna;}

    // setters
    public void setIdentificacion(String identificacion) {this.identificacion = identificacion;}
    public void setContrasenna(String contrasenna) {this.contrasenna = contrasenna;}
}
