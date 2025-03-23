package com.example.proyectoiprogramacioniv.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller

public class IndexController {
    // Dirige al index
    @GetMapping("/")
    public String index(HttpSession session) {
        // Si la sesión contiene el atributo 'tipo', significa que el usuario está logueado.
        if (session.getAttribute("tipo") != null) {
            // Si hay un 'tipo' en la sesión, eliminamos el atributo 'tipo' (logout)
            session.removeAttribute("tipo");
        }
        // Redirige al index
        return "index";
    }

    @GetMapping("/index")
    public String inicio(HttpSession session) {
        // Si la sesión contiene el atributo 'tipo', significa que el usuario está logueado.
        if (session.getAttribute("tipo") != null) {
            // Si hay un 'tipo' en la sesión, eliminamos el atributo 'tipo' (logout)
            session.removeAttribute("tipo");
        }
        // Redirige al index
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
                return "redirect:/administrador/login"; // Redirige a login de administrador
            case "medico":
                return "redirect:/medicos/login"; // Redirige a login de médico
            case "paciente":
                return "redirect:/pacientes/login"; // Redirige a login de paciente
            default:
                return "redirect:/index"; // Redirige a index
        }
    }
}
