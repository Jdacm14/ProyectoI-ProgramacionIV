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
import java.util.*;

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
                // Guardar en la sesión
                session.setAttribute("paciente", pacienteModel.get());
                session.setAttribute("tipo", "paciente");
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
    public String registro() {
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
    public String buscarCita(@RequestParam(value = "especialidad", required = false) String especialidad,
                             @RequestParam(value = "ubicacion", required = false) String ubicacion,
                             Model model, HttpSession session) {

        PacienteModel paciente = (PacienteModel) session.getAttribute("paciente");

        // Obtener los medicos de la base de datos
        List<MedicoModel> medicos = medicoRepository.findAll();
        List<MedicoModel> medicosFiltrados = new ArrayList<>();

        // Agarra la fecha actual para poder filtrar las citas
        // correctamente
        LocalDate currentDate = LocalDate.now();
        LocalDate fechaLimite = currentDate.plusDays(3);

        // Obtener los horarios de la base de datos
        List<HorarioModel> allHorarios = horarioRepository.findAll();
        List<HorarioModel> horarios = new ArrayList<>();
        for (HorarioModel horario : allHorarios) {
            LocalDate fechaHorario = LocalDate.parse(horario.getFecha());
            if (!fechaHorario.isBefore(currentDate) && !fechaHorario.isAfter(fechaLimite)) {
                horarios.add(horario);
            }
        }

        // Ordena los horarios para que se muestren de la fecha
        // más próxima hasta la más lejana, y luego por hora
        horarios.sort(Comparator.comparing(HorarioModel::getFecha)
                .thenComparing(HorarioModel::getHoraInicio));


        // Filtra los medicos por especialidad, ubicación,
        // y verifica que médico tenga horarios disponibles en el lapso de tiempo correcto
        for (MedicoModel medico : medicos) {
            boolean coincideEspecialidad = (especialidad == null || especialidad.isEmpty() ||
                    (medico.getEspecialidad() != null && medico.getEspecialidad().equals(especialidad)));

            boolean coincideUbicacion = (ubicacion == null || ubicacion.isEmpty() ||
                    medico.getUbicacion() != null && medico.getUbicacion().toLowerCase().contains(ubicacion.toLowerCase()));

            boolean tieneHorarios = false;
            for (HorarioModel horario : horarios) {
                if (horario.getMedicoID().equals(medico.getIdentificacion())) {
                    tieneHorarios = true;
                    break;
                }
            }
            if (coincideEspecialidad && coincideUbicacion && tieneHorarios) {
                medicosFiltrados.add(medico);
            }
        }

        // Agrega atributos al modelo para pasar la vista
        model.addAttribute("paciente", paciente);
        model.addAttribute("medicos", medicosFiltrados);
        model.addAttribute("horarios", horarios);
        model.addAttribute("especialidades", medicoRepository.findDistinctEspecialidades());

        return "pacientes/PacienteBuscarCita";
    }

    @PostMapping("/paciente/reservar")
    public String reservarCita(@RequestParam("horarioID") String horarioID, HttpSession session, Model model) {
        if (session.getAttribute("tipo") == null || !session.getAttribute("tipo").equals("paciente")) {
            return "redirect:/pacientes/login";
        }

        Optional<HorarioModel> horario = horarioRepository.findById(horarioID);
        if (horario.isPresent()) {
            PacienteModel paciente = (PacienteModel) session.getAttribute("paciente");
            String medicoId = horario.get().getMedicoID();
            Optional<MedicoModel> medico = medicoRepository.findByIdentificacion(medicoId);
            if (medico.isPresent()) {
                model.addAttribute("medico", medico.get());
            } else {
                model.addAttribute("error", "Médico no encontrado.");
                return "pacientes/PacienteBuscarCita";
            }
            model.addAttribute("horario", horario.get());
            model.addAttribute("paciente", paciente);
            model.addAttribute("mostrarPopup", true);  // Bandera para activar el pop-up

            // Además, se deben volver a cargar los datos de búsqueda para que la vista tenga toda la información
            List<MedicoModel> medicos = medicoRepository.findAll();
            List<MedicoModel> medicosFiltrados = new ArrayList<>();
            LocalDate currentDate = LocalDate.now();
            LocalDate fechaLimite = currentDate.plusDays(3);
            List<HorarioModel> allHorarios = horarioRepository.findAll();
            List<HorarioModel> horarios = new ArrayList<>();
            for (HorarioModel h : allHorarios) {
                LocalDate fechaHorario = LocalDate.parse(h.getFecha());
                if (!fechaHorario.isBefore(currentDate) && !fechaHorario.isAfter(fechaLimite)) {
                    horarios.add(h);
                }
            }
            horarios.sort(Comparator.comparing(HorarioModel::getFecha)
                    .thenComparing(HorarioModel::getHoraInicio));
            for (MedicoModel m : medicos) {
                boolean tieneHorarios = horarios.stream().anyMatch(h -> h.getMedicoID().equals(m.getIdentificacion()));
                if (tieneHorarios) {
                    medicosFiltrados.add(m);
                }
            }
            model.addAttribute("medicos", medicosFiltrados);
            model.addAttribute("horarios", horarios);
            model.addAttribute("especialidades", medicoRepository.findDistinctEspecialidades());

            return "pacientes/PacienteBuscarCita"; // Se retorna la misma vista con el pop-up activo
        } else {
            model.addAttribute("error", "Horario no disponible.");
            return "pacientes/PacienteBuscarCita";
        }
    }



    @PostMapping("/paciente/confirmar")
    public String confirmarCita(@RequestParam("horarioID") String horarioID, HttpSession session, Model model) {
        // Verificar si hay sesión activa de paciente
        if (session.getAttribute("tipo") == null || !session.getAttribute("tipo").equals("paciente")) {
            return "redirect:/pacientes/login";
        }

        // Obtener el horario a reservar
        Optional<HorarioModel> horarioOpt = horarioRepository.findById(horarioID);
        if (horarioOpt.isPresent()) {
            HorarioModel horario = horarioOpt.get();
            PacienteModel paciente = (PacienteModel) session.getAttribute("paciente");

            // Asignar el paciente al horario y guardar en la base de datos
            horario.setPacienteID(paciente.getIdentificacion());
            horario.setEstado("Pendiente");
            horarioRepository.save(horario);

            // Redirigir al listado de citas del paciente (ruta que programarás en el futuro)
            session.setAttribute("tipo", "paciente");
            return "redirect:/pacientes/PacienteHistoricoCitas";
        } else {
            // En caso de que el horario no exista, se regresa a la vista de búsqueda con error
            model.addAttribute("error", "Horario no disponible.");
            return "pacientes/PacienteBuscarCita";
        }
    }

    @PostMapping("/paciente/cancelar")
    public String cancelarCita(@RequestParam("horarioID") String horarioID, HttpSession session, Model model) {
        // Verificar si hay sesión activa de paciente
        if (session.getAttribute("tipo") == null || !session.getAttribute("tipo").equals("paciente")) {
            return "redirect:/pacientes/login";
        }

        // Al cancelar en el pop-up, simplemente se retorna a la vista de búsqueda de citas,
        // sin guardar ningún cambio y sin activar el pop-up (por lo que desaparece).
        return "redirect:/pacientes/buscar";
    }

    @GetMapping("/pacientes/PacienteHistoricoCitas")
    public String listarCitasPaciente(@RequestParam(value = "estado", required = false) String estado,
                                      @RequestParam(value = "medico", required = false) String medico,
                                      HttpSession session, Model model) {
        PacienteModel paciente = (PacienteModel) session.getAttribute("paciente");
        if (paciente == null) {
            return "redirect:/pacientes/login";
        }

        List<HorarioModel> citasPaciente = horarioRepository.findByPacienteID(paciente.getIdentificacion());
        Map<String, MedicoModel> medicosMap = new HashMap<>();

        // Filtrar citas por estado si es necesario
        if (estado != null && !estado.equals("todas")) {
            citasPaciente.removeIf(cita -> !cita.getEstado().equalsIgnoreCase(estado));
        }

        // Filtrar citas por médico si es necesario
        if (medico != null && !medico.isEmpty()) {
            citasPaciente.removeIf(cita -> {
                Optional<MedicoModel> medicoOpt = medicoRepository.findByIdentificacion(cita.getMedicoID());
                return medicoOpt.isEmpty() || !medicoOpt.get().getNombre().toLowerCase().contains(medico.toLowerCase());
            });
        }

        // Para cada cita, se busca el médico y se guarda en el mapa usando el ID como clave
        for (HorarioModel horario : citasPaciente) {
            Optional<MedicoModel> medicoOpt = medicoRepository.findByIdentificacion(horario.getMedicoID());
            medicoOpt.ifPresent(value -> medicosMap.put(horario.getMedicoID(), value));
        }

        citasPaciente.sort(Comparator.comparing(HorarioModel::getFecha)
                .thenComparing(HorarioModel::getHoraInicio));

        model.addAttribute("medicosMap", medicosMap);
        model.addAttribute("paciente", paciente);
        model.addAttribute("horarios", citasPaciente);
        return "pacientes/PacienteHistoricoCitas";
    }





}
