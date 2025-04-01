package com.example.proyectoiprogramacioniv.services;

import com.example.proyectoiprogramacioniv.models.HorarioModel;
import com.example.proyectoiprogramacioniv.models.MedicoModel;
import com.example.proyectoiprogramacioniv.repositories.HorarioRepository;
import com.example.proyectoiprogramacioniv.repositories.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class HorarioService {

    private final HorarioRepository horarioRepository;

    @Autowired
    public HorarioService(HorarioRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    public List<HorarioModel> obtenerTodosLosHorarios() {
        return horarioRepository.findAll();
    }

    public Optional<HorarioModel> buscarPorIdentificacion(String identificacion) {
        return horarioRepository.findByIdentificacion(identificacion);
    }

    // Ver si sirve
    public List<HorarioModel> buscarPorMedico(String medicoID) {
        return horarioRepository.findByMedicoID(medicoID);
    }

    public void registrarHorario(HorarioModel horario) {
        horarioRepository.save(horario);
    }

    public void eliminarRegistro(String identificacion) {
        horarioRepository.deleteById(identificacion);
    }

    public LocalDate obtenerProximaFecha(String diaSemana) {
        Map<String, DayOfWeek> dias = Map.of(
                "Lunes", DayOfWeek.MONDAY,
                "Martes", DayOfWeek.TUESDAY,
                "Miércoles", DayOfWeek.WEDNESDAY,
                "Jueves", DayOfWeek.THURSDAY,
                "Viernes", DayOfWeek.FRIDAY,
                "Sábado", DayOfWeek.SATURDAY,
                "Domingo", DayOfWeek.SUNDAY
        );

        DayOfWeek diaDeseado = dias.get(diaSemana);
        LocalDate hoy = LocalDate.now();
        return hoy.with(TemporalAdjusters.nextOrSame(diaDeseado));
    }

    // Aquí eliminamos la lógica del while, solo obtenemos los horarios fijos
    public List<HorarioModel> generarHorariosDisponibles(MedicoModel medico) {
        List<HorarioModel> horariosDisponibles = new ArrayList<>();
        List<HorarioModel> horariosFijos = buscarPorMedico(medico.getIdentificacion());

        for (HorarioModel horario : horariosFijos) {
            LocalDate fechaExacta = obtenerProximaFecha(horario.getDiaSemana());
            LocalTime inicio = LocalTime.parse(horario.getHoraInicio());
            LocalTime fin = LocalTime.parse(horario.getHoraFin());

            // Solo agregamos el horario fijo sin el while
            HorarioModel nuevoHorario = new HorarioModel();
            nuevoHorario.setId(UUID.randomUUID().toString());
            nuevoHorario.setMedicoID(medico.getIdentificacion());
            nuevoHorario.setDiaSemana(horario.getDiaSemana());
            nuevoHorario.setHoraInicio(inicio.toString());
            nuevoHorario.setHoraFin(fin.toString());
            nuevoHorario.setPrecio(horario.getPrecio());
            nuevoHorario.setPacienteID(null);

            horariosDisponibles.add(nuevoHorario);
        }

        return horariosDisponibles;
    }

    public void actualizarEstado (String horarioId, String estado){

        Optional<HorarioModel> horarioOptional = horarioRepository.findById(horarioId);

        horarioOptional.ifPresent(horario -> {
            horario.setEstado(estado);
            horarioRepository.save(horario);
        });

    }
}
