package com.example.proyectoiprogramacioniv.controllers;

import com.example.proyectoiprogramacioniv.models.HorarioModel;
import com.example.proyectoiprogramacioniv.models.MedicoModel;
import com.example.proyectoiprogramacioniv.repositories.HorarioRepository;
import com.example.proyectoiprogramacioniv.repositories.MedicoRepository;
import com.example.proyectoiprogramacioniv.services.HorarioService;
import com.example.proyectoiprogramacioniv.services.MedicoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private HorarioService horarioService;

    @Autowired
    private HorarioRepository horarioRepository;

    //---------------------------  login ----------------------------------

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
            if(medico.getEspecialidad() == "" || medico.getUbicacion() == "" ) {
                model.addAttribute("medico", medico);
                session.setAttribute("tipo", "medico");
                session.setAttribute("medico", medico); // Agrega esta línea
                return "redirect:/medicos/MedicoPerfil";
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

            session.setAttribute("medico", medico); //Para pasar el medico al medicoPerfil
            return "redirect:/medicos/MedicoGestionCitas"; // Ahora sí redirige al perfil
          
        } else {
            model.addAttribute("error", "Identificación incorrecta");
            return "medicos/login"; // Mantiene la vista de login con el error
        }
    }


//------------------------  Registro  -----------------------------------------------

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

    //--------------------------------  Medico Perfil --------------------------
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
                                    @RequestParam("presentacion") String presentacion,
                                    Model model, HttpSession session){

         MedicoModel medico = (MedicoModel) session.getAttribute("medico");
         if (medico == null) {
             System.out.println("No se puede obtener un medico");
             return "redirect:/medicos/login";
         }

         medico.setNombre(nombre);
         medico.setEspecialidad(especialidad);
         medico.setUbicacion(ubicacion);
         medico.setPresentacion(presentacion);

         medicoRepository.save(medico); //Guarda los cambios en la base de datos
         session.setAttribute("medico", medico);

        return "redirect:/medicos/MedicoGestionCitas";
    }
//------------------------------- Gestion Citas -----------------------------------------
    @GetMapping("/medicos/MedicoGestionCitas")
    public String MedicoGestionCitas(Model model, HttpSession session) {
        MedicoModel medico = (MedicoModel) session.getAttribute("medico");
        // Si no hay un médico en la sesión , redirige a login
        if (medico == null) { return "redirect:/medicos/login"; }

        model.addAttribute("nombre", medico.getNombre());
        return "medicos/MedicoGestionCitas";
    }


//--------------------------------------Gestion Horarios -------------------------------------

//    @GetMapping("/medicos/MedicoGestionHorarios")
//    public String MedicoGestionHorarios(Model model, HttpSession session) {
//
//        MedicoModel medico = (MedicoModel) session.getAttribute("medico");
//
//        if (medico == null) { return "redirect:/medicos/login";}
//
//        model.addAttribute("medico", medico);
//        model.addAttribute("nombre", medico.getNombre());
//
//        List<HorarioModel> horarios = horarioService.buscarPorMedico(medico.getIdentificacion());
//        model.addAttribute("horarios", horarios);
//
//        return "medicos/MedicoGestionHorarios";
//    }
//
//
//    @PostMapping("/medicos/MedicoGestionHorario")
//    public String MedicosHorariosGuardar(@RequestParam("fecha") String fecha,
//                                         @RequestParam("hora") String hora,
//                                         @RequestParam("precio") float precio,
//                                         Model model, HttpSession session){
//
//        MedicoModel medico = (MedicoModel) session.getAttribute("medico");
//
//        if (medico == null) {
//            System.out.println("No se encontro un medico en la sesion");
//            return "redirect:/medicos/login";
//        }
//
//        HorarioModel horario = new HorarioModel();
//        horario.setId(UUID.randomUUID().toString());
//        horario.setFecha(fecha);
//        horario.setHora(hora);
//        horario.setDisponible(true);
//        horario.setMedicoID(medico.getIdentificacion());
//        horario.setPrecio(precio);
//
//        horarioService.registrarHorario(horario);
//
//
//        return "redirect:/medicos/MedicoGestionHorarios";
//    }
//
//    @PostMapping("/medicos/MedicoGestionHorario/Eliminar")
//    public String MedicosHorariosEliminar(@RequestParam("idHorario") String Id,
//                                          Model model, HttpSession session){
//
//        horarioService.eliminarRegistro(Id);
//
//        return "redirect:/medicos/MedicoGestionHorarios";
//    }


    @GetMapping("/medicos/MedicoGestionHorarios")
    public String MedicoGestionHorarios(Model model, HttpSession session) {
        MedicoModel medico = (MedicoModel) session.getAttribute("medico");

        if (medico == null) { return "redirect:/medicos/login"; }

        model.addAttribute("medico", medico);
        model.addAttribute("nombre", medico.getNombre());

        List<HorarioModel> horarios = horarioService.buscarPorMedico(medico.getIdentificacion());
        model.addAttribute("horarios", horarios);

        return "medicos/MedicoGestionHorarios";
    }

    @PostMapping("/medicos/MedicoGestionHorarios")
    public String MedicosHorariosGuardar(@RequestParam("fechaSeleccionada") String fechaSeleccionada,
                                         @RequestParam("horaInicio") String horaInicio,
                                         @RequestParam("horaFin") String horaFin,
                                         @RequestParam("frecuencia") int frecuencia,
                                         @RequestParam("precio") float precio,
                                         HttpSession session) {

        MedicoModel medico = (MedicoModel) session.getAttribute("medico");

        if (medico == null) {
            return "redirect:/medicos/login";
        }

        // Convertir la fecha seleccionada (String) a un objeto Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date fecha = dateFormat.parse(fechaSeleccionada);

            // Calcular el día de la semana
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fecha);
            int diaSemana = calendar.get(Calendar.DAY_OF_WEEK); // 1 = Domingo, 2 = Lunes, ..., 7 = Sábado

            // Convertir el valor numérico del día a un String
            String diaNombre = "";
            switch (diaSemana) {
                case Calendar.SUNDAY:
                    diaNombre = "Domingo";
                    break;
                case Calendar.MONDAY:
                    diaNombre = "Lunes";
                    break;
                case Calendar.TUESDAY:
                    diaNombre = "Martes";
                    break;
                case Calendar.WEDNESDAY:
                    diaNombre = "Miércoles";
                    break;
                case Calendar.THURSDAY:
                    diaNombre = "Jueves";
                    break;
                case Calendar.FRIDAY:
                    diaNombre = "Viernes";
                    break;
                case Calendar.SATURDAY:
                    diaNombre = "Sábado";
                    break;
            }

            // Formatear la fecha en el formato dd/MM/yyyy
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
            String fechaFormateada = dateFormat2.format(fecha);  // La fecha en formato dd/MM/yyyy

            // Crear el horario y guardar los datos
            HorarioModel horario = new HorarioModel();
            horario.setId(UUID.randomUUID().toString());
            horario.setMedicoID(medico.getIdentificacion());
            horario.setDiaSemana(diaNombre);  // Guardamos el nombre del día
            horario.setFecha(fechaFormateada);  // Guardamos la fecha en formato dd/MM/yyyy
            horario.setHoraInicio(horaInicio);
            horario.setHoraFin(horaFin);
            horario.setFrecuenciaMinutos(frecuencia);
            horario.setPrecio(precio);
            horario.setDisponible(true);

            horarioService.registrarHorario(horario);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/medicos/MedicoGestionHorarios";
    }

    @PostMapping("/medicos/MedicoGestionHorarios/Eliminar")
    public String MedicosHorariosEliminar(@RequestParam("idHorario") String Id) {
        horarioService.eliminarRegistro(Id);
        return "redirect:/medicos/MedicoGestionHorarios";
    }

}
