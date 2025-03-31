package com.example.proyectoiprogramacioniv.controllers;

import com.example.proyectoiprogramacioniv.models.AdministradorModel;
import com.example.proyectoiprogramacioniv.models.MedicoModel;
import com.example.proyectoiprogramacioniv.repositories.AdministradorRepository;
import com.example.proyectoiprogramacioniv.services.AdministradorService;
import com.example.proyectoiprogramacioniv.services.MedicoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
@Controller
public class AdministradorController {

    @Autowired
    private AdministradorService administradorService;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private MedicoService medicoService;

    // Muestra la vista de login para el administrador
    @GetMapping("administrador/login")
    public String loginAdministrador() {
        return "administrador/login";  // Redirige a la vista de login del administrador
    }

    // Maneja el login del administrador
    @PostMapping("administrador/login")
    public String loginAdmin(@RequestParam("identificacion") String identificacion,
                             @RequestParam("contrasenna") String contrasenna,
                             Model model, HttpSession session) {
        Optional<AdministradorModel> administradorModel = administradorRepository.findByIdentificacion(identificacion);

        // Si el administrador existe
        if (administradorModel.isPresent()) {
            // Verificar si la contraseña es correcta
            if (administradorService.validarContrasenna(identificacion, contrasenna)) {
                model.addAttribute("administrador", administradorModel.get());
                session.setAttribute("tipo", "administrador"); // Establecemos el rol
                return "redirect:/administrador/ListadoMedico";  // Redirige a la vista del administrador
            } else {
                model.addAttribute("error", "Contraseña incorrecta");
                return "administrador/login";  // Redirige al login con mensaje de error
            }
        } else {
            model.addAttribute("error", "Identificación incorrecta");
            return "administrador/login";  // Redirige al login con mensaje de error
        }
    }

    @GetMapping("/administrador/ListadoMedico") // Ruta completa: "/administrador/AdminListadoMedicos"
    public String listarMedicos(Model model) {
        List<MedicoModel> medicos = medicoService.obtenerTodosLosMedicos();
        model.addAttribute("medicos", medicos);
        System.out.println("Médicos encontrados: " + medicos);
        return "administrador/ListadoMedico"; // Debe coincidir con el nombre del HTML en templates
    }

    @PostMapping("/administrador/cambiarEstadoMedico")
    public String cambiarEstadoMedico(@RequestParam("idMedico") Long id) {
        medicoService.cambiarEstadoMedico(id);
        return "redirect:/administrador/ListadoMedico"; // Recarga la página
    }

}
