package com.example.proyectoiprogramacioniv.controllers;


import com.example.proyectoiprogramacioniv.services.PacienteService;
import com.example.proyectoiprogramacioniv.models.PacienteModel;
import com.example.proyectoiprogramacioniv.repositories.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller

public class IndexController {
    // Dirige al index
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/index")
    public String inicio() {
        return "index";
    }

    @GetMapping("/buscar-cita")
    public String buscarCita() {
        return "pacientes/PacienteBuscarCita"; // Coincide con la ubicación en templates/pacientes/
    }
    // login
    @GetMapping("/prelogin")
    public String prelogin() {
        return "prelogin";
    }

    @PostMapping("/prelogin")
    public String redirectToLogin(@RequestParam("tipo") String tipo) {
        switch (tipo) {
            case "administrador":
                return "redirect:administrador/login"; // Redirige a login de administrador
            case "medico":
                return "redirect:medicos/login"; // Redirige a login de médico
            case "paciente":
                return "redirect:pacientes/login";
            default:
                return "/index"; // Redirige a login de paciente por defecto
        }
    }
}
