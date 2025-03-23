package com.example.proyectoiprogramacioniv.services;

import com.example.proyectoiprogramacioniv.models.MedicoModel;
import com.example.proyectoiprogramacioniv.repositories.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;


    @Autowired
    public MedicoService(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }


    public List<MedicoModel> obtenerTodosLosMedicos(){
        return medicoRepository.findAll();
        }

    public Optional<MedicoModel> buscarPorIdentificacion(String identificacion) {
        return medicoRepository.findByIdentificacion(identificacion);
    }

    public boolean validarContrasenna(String identificacion, String contrasenna) {
        Optional<MedicoModel> medico = medicoRepository.findByIdentificacion(identificacion);
        return medico.map(m -> m.getContrasenna().equals(contrasenna)).orElse(false);
    }

    public void cambiarEstadoMedico(Long id) {
        Optional<MedicoModel> medicoOptional = medicoRepository.findById(id);
        if (medicoOptional.isPresent()) {
            MedicoModel medico = medicoOptional.get();
            medico.setActivo(!medico.isActivo()); // Cambia el estado actual
            medicoRepository.save(medico); // Guarda el cambio en la BD
        }
    }


    public void registrarMedico(MedicoModel medico) {
        medicoRepository.save(medico);
    }
}