package com.example.proyectoiprogramacioniv.controllers;

import com.example.proyectoiprogramacioniv.models.MedicoModel;
import com.example.proyectoiprogramacioniv.repositories.MedicoRepository;
import com.example.proyectoiprogramacioniv.services.MedicoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    @Autowired
    private MedicoRepository medicoRepository;

    // Muestra el login para los médicos
    @GetMapping("medicos/login")
    public String login() {
        return "medicos/login";
    }

    // Login para médicos
    @PostMapping("/medicos/login")
    public String loginMedico(@RequestParam("identificacion") String identificacion,
                              @RequestParam("contrasenna") String contrasenna,
                              Model model, HttpSession session) {
        Optional<MedicoModel> medicoOptional = medicoService.buscarPorIdentificacion(identificacion);

        if (medicoOptional.isPresent()) {
            MedicoModel medico = medicoOptional.get();

            // Primero valida la contraseña
            if (!medicoService.validarContrasenna(identificacion, contrasenna)) {
                model.addAttribute("error", "Contraseña incorrecta");
                return "medicos/login"; // Mantiene la vista de login con el error
            }

            // Si la contraseña es correcta, ahora sí verificar si el médico está activo
            if (!medico.isActivo()) {
                model.addAttribute("medico", medico);
                session.setAttribute("tipo", "medico");
                return "redirect:/medicos/esperaAprobacion"; // Redirige a la vista de espera
            }

            // Si el médico está activo, permite el acceso
            model.addAttribute("medico", medico);
            session.setAttribute("tipo", "medico");
            return "redirect:/medicos/MedicoPerfil"; // Ahora sí redirige al perfil
        } else {
            model.addAttribute("error", "Identificación incorrecta");
            return "medicos/login"; // Mantiene la vista de login con el error
        }
    }


    // Registro de médico
    @GetMapping("medicos/registro")
    public String registro() {
        return "medicos/registro";  // Muestra la vista de registro
    }

    @PostMapping("medicos/registro")
    public String registrarMedico(@RequestParam("identificacion") String identificacion,
                                  @RequestParam("nombre") String nombre,
                                  @RequestParam("contrasenna") String contrasenna,
                                  @RequestParam("confirmarContrasenna") String confirmarContrasenna,
                                  Model model) {
        // Verifica si las contraseñas coinciden
        if (!contrasenna.equals(confirmarContrasenna)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "medicos/registro";  // Redirige de nuevo al registro con el mensaje de error
        }

        // Verifica si la identificación ya existe
        Optional<MedicoModel> medicoExistente = medicoRepository.findByIdentificacion(identificacion);
        if (medicoExistente.isPresent()) {
            model.addAttribute("error", "Identificación ya existe");
            return "medicos/registro";  // Redirige de nuevo al registro con el mensaje de error
        }

        // Crea un nuevo médico
        MedicoModel medico = new MedicoModel();
        medico.setIdentificacion(identificacion);
        medico.setNombre(nombre);
        medico.setContrasenna(contrasenna);  // Almacena la contraseña ingresada

        medicoRepository.save(medico);  // Guarda el médico en la base de datos

        model.addAttribute("mensaje", "Registro exitoso, por favor inicia sesión");
        return "redirect:/medicos/login";  // Redirige correctamente al login
        // Redirige al login después del registro exitoso
    }

    @GetMapping("medicos/esperaAprobacion")
    public String esperaAprobacion() {
        return "medicos/esperaAprobacion";
    }

    @GetMapping("medicos/MedicoPerfil")
    public String MedicoPerfil() {
        return "medicos/MedicoPerfil";
    }

}
