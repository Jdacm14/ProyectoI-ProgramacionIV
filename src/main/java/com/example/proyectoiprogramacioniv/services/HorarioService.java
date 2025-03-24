package com.example.proyectoiprogramacioniv.services;

import com.example.proyectoiprogramacioniv.models.HorarioModel;
import com.example.proyectoiprogramacioniv.repositories.HorarioRepository;
import com.example.proyectoiprogramacioniv.repositories.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HorarioService {

    private final HorarioRepository horarioRepository;


    @Autowired
    public HorarioService(HorarioRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }



    public List<HorarioModel> obtenerTodosLosHorarios(){
        return horarioRepository.findAll();
    }

    public Optional<HorarioModel> buscarPorIdentificacion(String identificacion) {
        return horarioRepository.findByIdentificacion(identificacion);
    }

    //Ver si sirve
    public List<HorarioModel> buscarPorMedico(String medicoID) {
        return horarioRepository.findByMedicoID(medicoID);
    }

    public void registrarHorario(HorarioModel horario) {
        horarioRepository.save(horario);
    }

    public void eliminarRegistro(String identificacion) {horarioRepository.deleteById(identificacion);}

}
