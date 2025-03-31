package com.example.proyectoiprogramacioniv.models;

import jakarta.persistence.*;


@Entity
public class MedicoModel {

    @Id
    private String identificacion;
    private String nombre;
    private String contrasenna;
    private boolean activo;
    private String especialidad;
    private String ubicacion;
    private String presentacion;


    public MedicoModel() {}
    public MedicoModel(String identificacion, String nombre, String contrasenna) {
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.contrasenna = contrasenna;
        this.activo = false;
        this.especialidad = "";
        this.ubicacion = "";
        this.presentacion = "";

    }

    // getters
    public String getIdentificacion() {return identificacion;}
    public String getNombre() {return nombre;}
    public String getContrasenna() {return contrasenna;}
    public boolean getActivo() {return activo;}
    public String getEspecialidad() {return especialidad;}
    public String getUbicacion() {return ubicacion;}
    public String getPresentacion() {return presentacion;}

    // setters
    public void setNombre(String nombre) {this.nombre = nombre;}
    public void setIdentificacion(String identificacion) {this.identificacion = identificacion;}
    public void setContrasenna(String contrasenna) {this.contrasenna = contrasenna;}
    public void setActivo(boolean activo) {this.activo = activo;}
    public void setEspecialidad(String especialidad) {this.especialidad = especialidad;}
    public void setUbicacion(String ubicacion) {this.ubicacion = ubicacion;}
    public void setPresentacion(String presentacion) {this.presentacion = presentacion;}
}
