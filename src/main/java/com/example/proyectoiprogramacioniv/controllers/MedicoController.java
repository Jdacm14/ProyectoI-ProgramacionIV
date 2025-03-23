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

            // Si la contraseña es correcta, ahora verificar si el médico está activo
            if (!medico.getActivo()) {
                model.addAttribute("medico", medico);
                session.setAttribute("tipo", "medico");
                return "redirect:/medicos/esperaAprobacion"; // Redirige a la vista de espera
            }

            // Si no ha especificado la especialidad o su ubicación lo redirige al medicoPerfil
            if(medico.getEspecialidad() == null || medico.getUbicacion() == null) {
                model.addAttribute("medico", medico);
                session.setAttribute("tipo", "medico");
                session.setAttribute("medico", medico); // Agrega esta línea
                return "redirect:/medicos/MedicoPerfil";
            }


            // Si el médico está activo, permite el acceso
            model.addAttribute("medico", medico);
            session.setAttribute("tipo", "medico");
            return "redirect:/medicos/MedicoGestionCitas"; // Redirige al perfil del médico
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

    //Ingreso al perfil
    @GetMapping("medicos/MedicoPerfil")
    public String MedicoPerfil(HttpSession session,
                               Model model) {
        MedicoModel medico = (MedicoModel) session.getAttribute("medico");

        if (medico == null) {
            System.out.println("No se puede obtener un medico");
            return "redirect:/medicos/login";
        }
        model.addAttribute("medico", medico);

        return "medicos/MedicoPerfil";
    }
    //Editar informacion del perfil
    @PostMapping("/medicos/MedicoPerfil")
    public String ActualizarPerfil (@RequestParam("nombre") String nombre,
                                    @RequestParam("especialidad") String especialidad,
                                    @RequestParam("ubicacion") String ubicacion,
                                    Model model, HttpSession session){

         MedicoModel medico = (MedicoModel) session.getAttribute("medico");
         if (medico == null) {
             System.out.println("No se puede obtener un medico");
             return "redirect:/medicos/login";
         }

         medico.setNombre(nombre);
         medico.setEspecialidad(especialidad);
         medico.setUbicacion(ubicacion);

         medicoRepository.save(medico); //Guarda los cambios en la base de datos
         session.setAttribute("medico", medico);

        return "redirect:/medicos/MedicoGestionCitas";
    }

    @GetMapping("/medicos/MedicoGestionCitas")
    public String MedicoGestionCitas(Model model, HttpSession session) {
        MedicoModel medico = (MedicoModel) session.getAttribute("medico");
        // Si no hay un médico en la sesión , redirige a login
        if (medico == null) { return "redirect:/medicos/login"; }

        model.addAttribute("nombre", medico.getNombre());
        return "medicos/MedicoGestionCitas";
    }


    @GetMapping("/medicos/MedicoGestionHorarios")
    public String MedicoGestionHorarios(Model model, HttpSession session) {

        MedicoModel medico = (MedicoModel) session.getAttribute("medico");

        if (medico == null) { return "redirect:/medicos/login";}

        model.addAttribute("nombre", medico.getNombre());
        return "medicos/MedicoGestionHorarios";
    }
}
