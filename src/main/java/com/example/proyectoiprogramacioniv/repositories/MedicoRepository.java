package com.example.proyectoiprogramacioniv.repositories;

import com.example.proyectoiprogramacioniv.models.MedicoModel;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository <MedicoModel, Long> {
    Optional<MedicoModel> findByIdentificacion(String identificacion);
}
