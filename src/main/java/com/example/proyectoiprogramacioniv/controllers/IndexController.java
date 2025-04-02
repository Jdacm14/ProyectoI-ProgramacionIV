package com.example.proyectoiprogramacioniv.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller

public class IndexController {
    // Dirige al index
    @GetMapping("/")
    public String index(HttpSession session) {
        session.invalidate();
        return "index";
    }

    @GetMapping("/index")
    public String inicio(HttpSession session) {
        session.invalidate();
        return "index";
    }

    // login
    @GetMapping("/prelogin")
    public String prelogin(HttpSession session) {
        session.invalidate();
        return "prelogin";
    }

    @GetMapping("/about")
    public String about() {
        return "about"; // Asegúrate de que tienes un archivo about.html en templates
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
