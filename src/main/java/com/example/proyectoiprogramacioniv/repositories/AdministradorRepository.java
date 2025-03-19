package com.example.proyectoiprogramacioniv.repositories;

import com.example.proyectoiprogramacioniv.models.AdministradorModel;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface AdministradorRepository extends JpaRepository<AdministradorModel, Long> {
    Optional<AdministradorModel> findByIdentificacion(String identificacion);
}