package com.example.proyectoiprogramacioniv.repositories;

import com.example.proyectoiprogramacioniv.models.PacienteModel;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<PacienteModel, String> {
    Optional<PacienteModel> findByIdentificacion(String identificacion);
}
