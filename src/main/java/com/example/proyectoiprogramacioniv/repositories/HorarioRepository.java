package com.example.proyectoiprogramacioniv.repositories;


import com.example.proyectoiprogramacioniv.models.HorarioModel;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface HorarioRepository extends JpaRepository<HorarioModel, Long> {
    Optional<HorarioModel> findByIdentificacion(String identificacion);
}

