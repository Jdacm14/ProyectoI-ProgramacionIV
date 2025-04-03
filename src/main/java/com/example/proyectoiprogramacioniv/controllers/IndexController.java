package com.example.proyectoiprogramacioniv.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller

public class IndexController {
    // Muestra la vista del index
    @GetMapping("/")
    public String index(HttpSession session) {
        session.invalidate();
        return "index";
    }

    // Muestra la vista del index
    @GetMapping("/index")
    public String inicio(HttpSession session) {
        session.invalidate();
        return "index";
    }

    // Muesta la vista del about
    @GetMapping("/about")
    public String about() {
        return "about"; // Asegúrate de que tienes un archivo about.html en templates
    }

    // Muestra la vista del prelogin
    @GetMapping("/prelogin")
    public String prelogin(HttpSession session) {
        session.invalidate();
        return "prelogin";
    }

    // Maneja el prelogin, redirigiendo a cada usuario a su correcto login
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
