package com.example.proyectoiprogramacioniv.repositories;

import com.example.proyectoiprogramacioniv.models.MedicoModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository <MedicoModel, Long> {
    Optional<MedicoModel> findByIdentificacion(String identificacion);
    List<MedicoModel> findAll();
    List<MedicoModel> findByEspecialidadAndUbicacion(String especialidad, String ubicacion);
    @Query("SELECT DISTINCT m.especialidad FROM MedicoModel m")
    List<String> findDistinctEspecialidades();
}
