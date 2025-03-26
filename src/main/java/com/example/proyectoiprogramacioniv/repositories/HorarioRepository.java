package com.example.proyectoiprogramacioniv.repositories;


import com.example.proyectoiprogramacioniv.models.HorarioModel;
import com.example.proyectoiprogramacioniv.services.HorarioService;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HorarioRepository extends JpaRepository<HorarioModel, String> {
    Optional<HorarioModel> findByIdentificacion(String identificacion);
    List<HorarioModel> findByMedicoID(String medicoID);
   // List<HorarioModel> findAll();
   // void deleteById(String identificacion);

}

