package com.example.proyectoiprogramacioniv.repositories;

import com.example.proyectoiprogramacioniv.models.CitaModel;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface CitaRepository extends JpaRepository<CitaModel, Long> {

    Optional<CitaModel> findByIdentificacion(String identificacion);

}
