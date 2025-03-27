package com.example.proyectoiprogramacioniv.controllers;

import com.example.proyectoiprogramacioniv.models.HorarioModel;
import com.example.proyectoiprogramacioniv.models.MedicoModel;
import com.example.proyectoiprogramacioniv.repositories.HorarioRepository;
import com.example.proyectoiprogramacioniv.repositories.MedicoRepository;
import com.example.proyectoiprogramacioniv.services.HorarioService;
import com.example.proyectoiprogramacioniv.services.PacienteService;
import com.example.proyectoiprogramacioniv.models.PacienteModel;
import com.example.proyectoiprogramacioniv.repositories.PacienteRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Controller

public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private HorarioService horarioService;

    // Muestra el login para los pacientes
    @GetMapping("pacientes/login")
    public String login() {
        return "pacientes/login";
    }

    // Login para pacientes
    @PostMapping("pacientes/login")
    public String loginPaciente(@RequestParam("identificacion") String identificacion,
                                @RequestParam("contrasenna") String contrasenna,
                                Model model, HttpSession session) {
        Optional<PacienteModel> pacienteModel = pacienteRepository.findByIdentificacion(identificacion);

        // Si el paciente existe
        if (pacienteModel.isPresent()) {
            if (pacienteService.validarContrasenna(identificacion, contrasenna)) {
                model.addAttribute("paciente", pacienteModel.get());
                session.setAttribute("tipo", "paciente"); // Establecemos el rol
                return "redirect:/pacientes/buscar"; // Redirige a la página de buscar cita
            } else {
                model.addAttribute("error", "Contraseña incorrecta");
                return "pacientes/login"; // Redirige al login con el mensaje de error
            }
        } else {
            model.addAttribute("error", "Identificación incorrecta");
            return "pacientes/login"; // Redirige al login con el mensaje de error
        }
    }

    // Registro de paciente
    @GetMapping("pacientes/registro")
    public String registro() {;
        return "pacientes/registro";  // Muestra la vista de registro
    }

    @PostMapping("pacientes/registro")
    public String registrarPaciente(@RequestParam("identificacion") String identificacion,
                                    @RequestParam("nombre") String nombre,
                                    @RequestParam("contrasenna") String contrasenna,
                                    @RequestParam("confirmarContrasenna") String confirmarContrasenna,
                                    Model model) {
        // Verifica si las contraseñas coinciden
        if (!contrasenna.equals(confirmarContrasenna)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "pacientes/registro";  // Redirige de nuevo al registro con el mensaje de error
        }

        // Verifica si la identificación ya existe
        Optional<PacienteModel> pacienteExistente = pacienteRepository.findByIdentificacion(identificacion);
        if (pacienteExistente.isPresent()) {
            model.addAttribute("error", "Identificación ya existe");
            return "pacientes/registro";  // Redirige de nuevo al registro con el mensaje de error
        }

        // Crea un nuevo paciente
        PacienteModel paciente = new PacienteModel();
        paciente.setIdentificacion(identificacion);
        paciente.setNombre(nombre);
        paciente.setContrasenna(contrasenna);  // Almacenamos la contraseña ingresada

        pacienteRepository.save(paciente);  // Guarda el paciente en la base de datos

        model.addAttribute("mensaje", "Registro exitoso, por favor inicia sesión");
        return "redirect:/pacientes/login";  // Redirige correctamente al login
        // Redirige al login después del registro exitoso
    }


    @GetMapping("/pacientes/buscar")
    public String buscarCita(@RequestParam(name = "medicoID", required = false) String medicoID,
                             @RequestParam(name = "fecha", required = false) String fecha,
                             Model model) {

        // Obtener todos los médicos disponibles
        List<MedicoModel> listaDeMedicos = medicoRepository.findAll();
        model.addAttribute("medicos", listaDeMedicos);

        // Obtener los horarios disponibles si se proporciona médico y fecha
        if (medicoID != null && fecha != null) {
            List<HorarioModel> horariosDisponibles = horarioRepository.findByMedicoIDAndFechaAndDisponible(medicoID, fecha, true);
            model.addAttribute("horarios", horariosDisponibles);
        }
        // Retornamos la vista con los horarios
        return "pacientes/PacienteBuscarCita";
    }


    @GetMapping("/buscar-horarios")
    public String buscarHorarios(Model model) {
        List<HorarioModel> horarios = horarioService.buscarHorariosDisponibles();
        model.addAttribute("horarios", horarios);
        return "pacientes/PacienteBuscarCita";
    }


}
