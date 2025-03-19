package com.example.proyectoiprogramacioniv.controllers;

import com.example.proyectoiprogramacioniv.models.AdministradorModel;
import com.example.proyectoiprogramacioniv.repositories.AdministradorRepository;
import com.example.proyectoiprogramacioniv.services.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
@Controller
public class AdministradorController {

    @Autowired
    private AdministradorService administradorService;

    @Autowired
    private AdministradorRepository administradorRepository;

    // Muestra la vista de login para el administrador
    @GetMapping("administrador/login")
    public String loginAdministrador() {
        return "administrador/login";  // Redirige a la vista de login del administrador
    }

    // Maneja el login del administrador
    @PostMapping("administrador/login")
    public String loginAdmin(@RequestParam("identificacion") String identificacion,
                             @RequestParam("contrasenna") String contrasenna,
                             Model model) {
        Optional<AdministradorModel> administradorModel = administradorRepository.findByIdentificacion(identificacion);

        // Si el administrador existe
        if (administradorModel.isPresent()) {
            // Verificar si la contraseña es correcta
            if (administradorService.validarContrasenna(identificacion, contrasenna)) {
                model.addAttribute("administrador", administradorModel.get());
                return "administrador/AdminListadoMedicos";  // Redirige a la vista del administrador
            } else {
                model.addAttribute("error", "Contraseña incorrecta");
                return "administrador/login";  // Redirige al login con mensaje de error
            }
        } else {
            model.addAttribute("error", "Identificación incorrecta");
            return "administrador/login";  // Redirige al login con mensaje de error
        }
    }
}
