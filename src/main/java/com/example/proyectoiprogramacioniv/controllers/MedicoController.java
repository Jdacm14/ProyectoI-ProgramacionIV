package com.example.proyectoiprogramacioniv.controllers;

import com.example.proyectoiprogramacioniv.models.HorarioModel;
import com.example.proyectoiprogramacioniv.models.MedicoModel;
import com.example.proyectoiprogramacioniv.models.PacienteModel;
import com.example.proyectoiprogramacioniv.repositories.HorarioRepository;
import com.example.proyectoiprogramacioniv.repositories.MedicoRepository;
import com.example.proyectoiprogramacioniv.repositories.PacienteRepository;
import com.example.proyectoiprogramacioniv.services.HorarioService;
import com.example.proyectoiprogramacioniv.services.MedicoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @Autowired
    private PacienteRepository pacienteRepository;

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
                return "medicos/login";
            }

            // Si la contraseña es correcta, ahora verificar si el médico está activo
            if (!medico.getActivo()) {
                model.addAttribute("medico", medico);
                session.setAttribute("tipo", "medico");
                return "redirect:/medicos/esperaAprobacion";
            }
            // Si no ha especificado la especialidad o su ubicación lo redirige al medicoPerfil
            if(medico.getEspecialidad() == "" || medico.getUbicacion() == "" ) {
                model.addAttribute("medico", medico);
                session.setAttribute("tipo", "medico");
                session.setAttribute("medico", medico);
                return "redirect:/medicos/MedicoPerfil";
            }

            // Si no ha especificado la especialidad o su ubicación lo redirige al medicoPerfil
            if(medico.getEspecialidad() == null || medico.getUbicacion() == null) {
                model.addAttribute("medico", medico);
                session.setAttribute("tipo", "medico");
                session.setAttribute("medico", medico);
                return "/medicos/MedicoPerfil";
            }


            // Si el médico está activo, permite el acceso
            model.addAttribute("medico", medico);
            session.setAttribute("tipo", "medico");

            session.setAttribute("medico", medico);
            return "redirect:/medicos/MedicoGestionCitas";
          
        } else {
            model.addAttribute("error", "Identificación incorrecta");
            return "medicos/login";
        }
    }


//------------------------  Registro  -----------------------------------------------

    // Registro de médico
    @GetMapping("medicos/registro")
    public String registro() {
        return "medicos/registro";
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
            return "medicos/registro";
        }

        // Verifica si la identificación ya existe
        Optional<MedicoModel> medicoExistente = medicoRepository.findByIdentificacion(identificacion);
        if (medicoExistente.isPresent()) {
            model.addAttribute("error", "Identificación ya existe");
            return "medicos/registro";
        }

        // Crea un nuevo médico
        MedicoModel medico = new MedicoModel();
        medico.setIdentificacion(identificacion);
        medico.setNombre(nombre);
        medico.setContrasenna(contrasenna);

        // Guarda el nuevo medico
        medicoRepository.save(medico);

        model.addAttribute("mensaje", "Registro exitoso, por favor inicia sesión");
        return "redirect:/medicos/login";
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

         medicoRepository.save(medico);
         session.setAttribute("medico", medico);

        return "redirect:/medicos/MedicoGestionCitas";
    }
//------------------------------- Gestion Citas -----------------------------------------

    @GetMapping("/medicos/MedicoGestionCitas")
    public String MedicoGestionCitas(@RequestParam(value = "filtroEstado", required = false) String estado,
                                     @RequestParam(value = "filtroPaciente", required = false) String pacienteNombre,
                                     Model model, HttpSession session) {

        MedicoModel medico = (MedicoModel) session.getAttribute("medico");

        // Si el médico no está en sesión, redirigir al login
        if (medico == null) {
            return "redirect:/medicos/login";
        }

        // Obtener todas las citas del médico actual
        List<HorarioModel> todasLasCitas = horarioRepository.findByMedicoID(medico.getIdentificacion());
        List<HorarioModel> citasFiltradas = new ArrayList<>();

        // Crear un mapa para almacenar el ID y el nombre del paciente
        Map<String, String> nombresPacientes = new HashMap<>();

        // Filtrar por estado y paciente (si se ingresaron)
        for (HorarioModel cita : todasLasCitas) {
            boolean coincideEstado = (estado == null || estado.equalsIgnoreCase("todas") ||
                    (cita.getEstado() != null && cita.getEstado().equalsIgnoreCase(estado)));

            //pacienteNombre proviene del html, nombrePaciente proviene del id de Horario
            String nombrePaciente = "No asignado";
            if (cita.getPacienteID() != null) {
                PacienteModel paciente = pacienteRepository.findById(cita.getPacienteID()).orElse(null);
                if (paciente != null) {
                    nombrePaciente = paciente.getNombre();
                }
            }

            boolean coincidePaciente = (pacienteNombre == null || pacienteNombre.isEmpty() ||
                    pacienteNombre.equalsIgnoreCase("Ingrese nombre del paciente") ||
                    nombrePaciente.toLowerCase().contains(pacienteNombre.toLowerCase()));

            if (coincideEstado && coincidePaciente) {
                citasFiltradas.add(cita);

                // Buscar el nombre del paciente si aún no lo hemos obtenido
                if (cita.getPacienteID() != null && !nombresPacientes.containsKey(cita.getPacienteID())) {
                    PacienteModel paciente = pacienteRepository.findById(cita.getPacienteID()).orElse(null);
                    if (paciente != null) {
                        nombresPacientes.put(cita.getPacienteID(), paciente.getNombre());
                    }
                }
            }
        }

        // Ordenar citas por fecha y hora
        citasFiltradas.sort(Comparator.comparing(HorarioModel::getFecha)
                .thenComparing(HorarioModel::getHoraInicio));

        // Pasar datos a la vista
        model.addAttribute("nombre", medico.getNombre());
        model.addAttribute("citas", citasFiltradas);
        model.addAttribute("nombresPacientes", nombresPacientes);

        return "medicos/MedicoGestionCitas";
    }

    @PostMapping("/medicos/actualizarCita")
    public String ActualizarCita (@RequestParam("citaIdCambiar") String idCita,
                                  @RequestParam("estadoCita") String estadoCita,
                                  Model model, HttpSession session ){

        horarioService.actualizarEstado(idCita, estadoCita);

        return "redirect:/medicos/MedicoGestionCitas";

    }

    //------------------------------- Gestion Horarios -----------------------------------------

    // Muestra la vista de Gestion de horarios
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

    // Maneja la gestión de usuario, maneja la creación de horarios
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
            return "redirect:/medicos/login";
        }

        String medicoID = medico.getIdentificacion();

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
            if (horaFinCita.isAfter(fin)) break;

            // Validar si el nuevo horario se solapa con algún horario existente
            for (HorarioModel horarioExistente : horariosExistentes) {
                LocalTime inicioExistente = LocalTime.parse(horarioExistente.getHoraInicio());
                LocalTime finExistente = LocalTime.parse(horarioExistente.getHoraFin());

                // Comprobar si la nueva cita se solapa con una cita existente
                if (inicio.isBefore(finExistente) && horaFinCita.isAfter(inicioExistente)) {
                    model.addAttribute("error", "El horario choca con una cita existente.");
                    return "redirect:/medicos/MedicoGestionHorarios";
                }
            }

            // Crear un nuevo horario
            HorarioModel horario = new HorarioModel();
            horario.setId(UUID.randomUUID().toString());
            horario.setMedicoID(medicoID);
            horario.setFecha(fecha);
            horario.setHoraInicio(inicio.toString());
            horario.setHoraFin(horaFinCita.toString());
            horario.setPrecio(precio);
            horario.setDiaSemana(LocalDate.parse(fecha).getDayOfWeek().toString());
            horario.setPacienteID(null);

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
