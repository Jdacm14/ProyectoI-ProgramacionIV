package com.example.proyectoiprogramacioniv.services;

import com.example.proyectoiprogramacioniv.models.AdministradorModel;
import com.example.proyectoiprogramacioniv.repositories.AdministradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdministradorService {
    private final AdministradorRepository administradorRepository;

    @Autowired
    public AdministradorService(AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    public Optional<AdministradorModel> buscarPorIdentificacion(String identificacion) {
        return administradorRepository.findByIdentificacion(identificacion);
    }

    public boolean validarContrasenna(String identificacion, String contrasenna) {
        Optional<AdministradorModel> administrador = administradorRepository.findByIdentificacion(identificacion);
        return administrador.map(p -> p.getContrasenna().equals(contrasenna)).orElse(false);
    }

}
