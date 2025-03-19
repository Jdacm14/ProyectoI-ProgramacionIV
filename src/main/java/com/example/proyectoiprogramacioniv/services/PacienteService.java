package com.example.proyectoiprogramacioniv.services;

import com.example.proyectoiprogramacioniv.models.PacienteModel;
import com.example.proyectoiprogramacioniv.repositories.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PacienteService {
    private final PacienteRepository pacienteRepository;

    @Autowired
    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public Optional<PacienteModel> buscarPorIdentificacion(String identificacion) {
        return pacienteRepository.findByIdentificacion(identificacion);
    }

    public boolean validarContrasenna(String identificacion, String contrasenna) {
        Optional<PacienteModel> paciente = pacienteRepository.findByIdentificacion(identificacion);
        return paciente.map(p -> p.getContrasenna().equals(contrasenna)).orElse(false);
    }

    public void registrarPaciente(PacienteModel paciente) {
        pacienteRepository.save(paciente);
    }

}
