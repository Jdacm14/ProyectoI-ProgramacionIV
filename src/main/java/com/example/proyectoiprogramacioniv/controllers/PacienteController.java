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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                return "redirect:/pacientes/buscar";
            } else {
                model.addAttribute("error", "Contraseña incorrecta");
                return "pacientes/login";
            }
        } else {
            model.addAttribute("error", "Identificación incorrecta");
            return "pacientes/login";
        }
    }


    // Registro de paciente
    @GetMapping("pacientes/registro")
    public String registro() {
        return "pacientes/registro";
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
            return "pacientes/registro";
        }

        // Verifica si la identificación ya existe
        Optional<PacienteModel> pacienteExistente = pacienteRepository.findByIdentificacion(identificacion);
        if (pacienteExistente.isPresent()) {
            model.addAttribute("error", "Identificación ya existe");
            return "pacientes/registro";
        }

        // Crea un nuevo paciente
        PacienteModel paciente = new PacienteModel();
        paciente.setIdentificacion(identificacion);
        paciente.setNombre(nombre);
        paciente.setContrasenna(contrasenna);

        pacienteRepository.save(paciente);

        model.addAttribute("mensaje", "Registro exitoso, por favor inicia sesión");
        return "redirect:/pacientes/login";
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

    @PostMapping("/pacientes/reservar")
    public String reservarCita(@RequestParam("horarioID") String horarioID,
                               HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("tipo") == null || !session.getAttribute("tipo").equals("paciente")) {
            redirectAttributes.addFlashAttribute("error", "Por favor inicie sesión antes de continuar");
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
            model.addAttribute("mostrarPopup", true);

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

            return "pacientes/PacienteBuscarCita";
        } else {
            model.addAttribute("error", "Horario no disponible.");
            return "pacientes/PacienteBuscarCita";
        }
    }

    @PostMapping("/pacientes/confirmar")
    public String confirmarCita(@RequestParam("horarioID") String horarioID,
                                HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // Verificar si hay sesión activa de paciente
        if (session.getAttribute("tipo") == null || !session.getAttribute("tipo").equals("paciente")) {
            redirectAttributes.addFlashAttribute("error", "Por favor inicie sesión antes de continuar");
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

    @PostMapping("/pacientes/cancelar")
    public String cancelarCita(@RequestParam("horarioID") String horarioID,
                               HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // Verificar si hay sesión activa de paciente
        if (session.getAttribute("tipo") == null || !session.getAttribute("tipo").equals("paciente")) {
            redirectAttributes.addFlashAttribute("error", "Por favor inicie sesión antes de continuar");
            return "redirect:/pacientes/login";
        }

        // Al cancelar en el pop-up, simplemente se retorna a la vista de búsqueda de citas,
        // sin guardar ningún cambio y sin activar el pop-up (por lo que desaparece).
        return "redirect:/pacientes/buscar";
    }

    @GetMapping("/pacientes/PacienteHistoricoCitas")
    public String listarCitasPaciente(@RequestParam(value = "estado", required = false) String estado,
                                      @RequestParam(value = "medico", required = false) String medico,
                                      HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        PacienteModel paciente = (PacienteModel) session.getAttribute("paciente");
        if (paciente == null) {
            redirectAttributes.addFlashAttribute("error", "Por favor inicie sesión antes de continuar");
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

    @GetMapping("pacientes/pacienteHorarioExtendido")
    public String mostrarHorarioExtendido(@RequestParam("medicoID") String medicoID,
                                          @RequestParam(value = "fecha", required = false) String fechaStr,
                                          Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Verificar sesión de paciente
        if (session.getAttribute("tipo") == null || !session.getAttribute("tipo").equals("paciente")) {
            redirectAttributes.addFlashAttribute("error", "Por favor inicie sesión antes de continuar");
            return "redirect:/pacientes/login";
        }

        // Obtener el médico
        Optional<MedicoModel> medicoOpt = medicoRepository.findByIdentificacion(medicoID);
        if (!medicoOpt.isPresent()) {
            model.addAttribute("error", "Médico no encontrado.");
            return "redirect:/pacientes/buscar";
        }
        MedicoModel medico = medicoOpt.get();

        // Obtener todos los horarios para ese médico (sin streams)
        List<HorarioModel> horariosMedico = new ArrayList<>();
        List<HorarioModel> todosHorarios = horarioRepository.findAll();
        for (HorarioModel h : todosHorarios) {
            if (h.getMedicoID().equals(medicoID)) {
                horariosMedico.add(h);
            }
        }

        // Extraer las fechas únicas de los horarios (se incluyen todas, sin filtrar las anteriores a hoy)
        List<LocalDate> fechasDisponibles = new ArrayList<>();
        for (HorarioModel h : horariosMedico) {
            LocalDate fechaHorario = LocalDate.parse(h.getFecha());
            if (!fechasDisponibles.contains(fechaHorario)) {
                fechasDisponibles.add(fechaHorario);
            }
        }

        // Ordenar la lista de fechas (orden ascendente)
        Collections.sort(fechasDisponibles);

        // Si no hay fechas disponibles, redirigir o mostrar mensaje de error
        if (fechasDisponibles.isEmpty()) {
            model.addAttribute("mensaje", "No hay horarios disponibles para este médico.");
            return "pacientes/PacienteHorarioExtendido";
        }

        LocalDate hoy = LocalDate.now();
        LocalDate fechaActual = null;

        // Determinar la fecha a mostrar
        if (fechaStr == null || fechaStr.isEmpty()) {
            // Buscar la fecha más próxima que sea hoy o posterior
            for (LocalDate f : fechasDisponibles) {
                if (!f.isBefore(hoy)) {  // f >= hoy
                    fechaActual = f;
                    break;
                }
            }
            // Si todas las fechas son anteriores a hoy, tomar la última (la más reciente del pasado)
            if (fechaActual == null) {
                fechaActual = fechasDisponibles.get(fechasDisponibles.size() - 1);
            }
        } else {
            fechaActual = LocalDate.parse(fechaStr);
            // Validar que la fecha actual esté en la lista; de lo contrario, asignar la fecha más próxima >= hoy
            if (!fechasDisponibles.contains(fechaActual)) {
                fechaActual = null;
                for (LocalDate f : fechasDisponibles) {
                    if (!f.isBefore(hoy)) {
                        fechaActual = f;
                        break;
                    }
                }
                if (fechaActual == null) {
                    fechaActual = fechasDisponibles.get(fechasDisponibles.size() - 1);
                }
            }
        }

        // Filtrar horarios para la fecha actual sin usar streams
        List<HorarioModel> horariosDelDia = new ArrayList<>();
        for (HorarioModel h : horariosMedico) {
            LocalDate fechaHorario = LocalDate.parse(h.getFecha());
            if (fechaHorario.equals(fechaActual)) {
                horariosDelDia.add(h);
            }
        }

        // Determinar la posición actual en la lista de fechas disponibles
        int indiceActual = fechasDisponibles.indexOf(fechaActual);

        // Calcular fecha anterior y siguiente (si existen)
        LocalDate fechaAnterior = (indiceActual > 0) ? fechasDisponibles.get(indiceActual - 1) : null;
        LocalDate fechaSiguiente = (indiceActual < fechasDisponibles.size() - 1) ? fechasDisponibles.get(indiceActual + 1) : null;

        // Agregar datos al modelo
        model.addAttribute("medico", medico);
        model.addAttribute("horarios", horariosDelDia);
        model.addAttribute("fechaActual", fechaActual);
        model.addAttribute("fechaAnterior", fechaAnterior);
        model.addAttribute("fechaSiguiente", fechaSiguiente);

        return "pacientes/PacienteHorarioExtendido";
    }

    @PostMapping("/pacientes/horarioExtendido/reservar")
    public String horarioExtendidoReservarCita(@RequestParam("horarioID") String horarioID,
                                               HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("tipo") == null || !session.getAttribute("tipo").equals("paciente")) {
            redirectAttributes.addFlashAttribute("error", "Por favor inicie sesión antes de continuar");
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

            // Se recargan los datos de búsqueda para mantener la vista con toda la información
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

            // Ahora retornamos la vista extendida para que se muestre el popup en ella
            return "pacientes/PacienteHorarioExtendido";
        } else {
            model.addAttribute("error", "Horario no disponible.");
            return "pacientes/PacienteBuscarCita";
        }
    }

    @PostMapping("/pacientes/horarioExtendido/confirmar")
    public String horarioExtendidoConfirmarCita(@RequestParam("horarioID") String horarioID,
                                                HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // Verificar si hay sesión activa de paciente
        if (session.getAttribute("tipo") == null || !session.getAttribute("tipo").equals("paciente")) {
            redirectAttributes.addFlashAttribute("error", "Por favor inicie sesión antes de continuar");
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
            return "pacientes/PacienteHorarioExtendido";
        }
    }

    @PostMapping("/pacientes/horarioExtendido/cancelar")
    public String horarioExtendidoCancelarCita(@RequestParam("horarioID") String horarioID,
                                               HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // Verificar si hay sesión activa de paciente
        if (session.getAttribute("tipo") == null || !session.getAttribute("tipo").equals("paciente")) {
            redirectAttributes.addFlashAttribute("error", "Por favor inicie sesión antes de continuar");
            return "redirect:/pacientes/login";
        }
        // Al cancelar en el pop-up, simplemente se retorna a la vista de búsqueda de citas,
        // sin guardar ningún cambio y sin activar el pop-up (por lo que desaparece).
        return "redirect:/pacientes/pacienteHorarioExtendido";
    }
}
