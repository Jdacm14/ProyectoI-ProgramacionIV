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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
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


    @GetMapping("/medicos/MedicoGestionHorarios")
    public String MedicoGestionHorarios(Model model, HttpSession session) {
        MedicoModel medico = (MedicoModel) session.getAttribute("medico");

        if (medico == null) {
            return "redirect:/medicos/login";
        }

        model.addAttribute("medico", medico);
        model.addAttribute("nombre", medico.getNombre());

        // Obtener los horarios del médico
        List<HorarioModel> horarios = horarioService.buscarPorMedico(medico.getIdentificacion());

        // Ordenar la lista por fecha y luego por hora de inicio
        horarios.sort(Comparator.comparing(HorarioModel::getFecha)
                .thenComparing(HorarioModel::getHoraInicio));

        // Agregar los horarios ordenados al modelo
        model.addAttribute("horarios", horarios);

        return "medicos/MedicoGestionHorarios";
    }


    @PostMapping("/medicos/MedicoGestionHorarios")
    public String crearHorarios(@RequestParam("fechaSeleccionada") String fecha,
                                @RequestParam("horaInicio") String horaInicio,
                                @RequestParam("horaFin") String horaFin,
                                @RequestParam("frecuencia") int frecuenciaMinutos,
                                @RequestParam("precio") int precio,
                                Model model, HttpSession session) {

        // Obtener el objeto Medico desde la sesión
        MedicoModel medico = (MedicoModel) session.getAttribute("medico");

        // Verificar si el medico está presente en la sesión
        if (medico == null) {
            model.addAttribute("error", "El médico no está logueado.");
            return "redirect:/medicos/login"; // Redirige al login si no hay sesión de médico
        }

        String medicoID = medico.getIdentificacion();  // Obtener el ID del médico desde la sesión

        // Convertir las horas de inicio y fin a objetos LocalTime
        LocalTime inicio = LocalTime.parse(horaInicio);
        LocalTime fin = LocalTime.parse(horaFin);

        // Crear una lista para almacenar los horarios generados
        List<HorarioModel> horariosGenerados = new ArrayList<>();

        // Obtener todos los horarios existentes para la fecha seleccionada
        List<HorarioModel> horariosExistentes = horarioRepository.findByFecha(fecha);

        // Llenar la lista con los horarios, respetando la frecuencia
        while (inicio.isBefore(fin)) {
            LocalTime horaFinCita = inicio.plusMinutes(frecuenciaMinutos);
            if (horaFinCita.isAfter(fin)) break; // Evita que se creen horarios fuera del rango

            // Validar si el nuevo horario se solapa con algún horario existente
            for (HorarioModel horarioExistente : horariosExistentes) {
                LocalTime inicioExistente = LocalTime.parse(horarioExistente.getHoraInicio());
                LocalTime finExistente = LocalTime.parse(horarioExistente.getHoraFin());

                // Comprobar si la nueva cita se solapa con una cita existente
                if (inicio.isBefore(finExistente) && horaFinCita.isAfter(inicioExistente)) {
                    model.addAttribute("error", "El horario choca con una cita existente.");
                    return "redirect:/medicos/MedicoGestionHorarios";  // Redirige con el mensaje de error
                }
            }

            // Crear un nuevo horario
            HorarioModel horario = new HorarioModel();
            horario.setId(UUID.randomUUID().toString()); // Generar un ID único para el horario
            horario.setMedicoID(medicoID);  // Usar el ID del médico desde la sesión
            horario.setFecha(fecha);
            horario.setHoraInicio(inicio.toString());
            horario.setHoraFin(horaFinCita.toString());
            horario.setPrecio(precio);
            horario.setDiaSemana(LocalDate.parse(fecha).getDayOfWeek().toString());
            horario.setPacienteID(null); // Este horario está libre hasta que se reserve

            // Agregar el horario a la lista
            horariosGenerados.add(horario);

            // Actualizar la hora de inicio para el siguiente horario
            inicio = horaFinCita;
        }

        // Guardar los horarios generados en la base de datos
        horarioRepository.saveAll(horariosGenerados);

        // Pasar el mensaje al modelo
        model.addAttribute("mensaje", "Horarios generados correctamente");

        // Redirigir a la página del dashboard del médico o donde quieras después de guardar
        return "redirect:/medicos/MedicoGestionHorarios";
    }


    // Método para eliminar un horario (Si lo necesitas)
    @PostMapping("/medicos/MedicoGestionHorarios/Eliminar")
    public String eliminarHorario(@RequestParam("idHorario") String idHorario) {
        horarioRepository.deleteById(idHorario);
        return "redirect:/medicos/MedicoGestionHorarios";
    }

}
