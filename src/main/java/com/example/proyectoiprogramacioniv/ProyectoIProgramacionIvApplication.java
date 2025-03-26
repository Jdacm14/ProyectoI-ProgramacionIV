package com.example.proyectoiprogramacioniv;

import com.example.proyectoiprogramacioniv.models.AdministradorModel;
import com.example.proyectoiprogramacioniv.repositories.AdministradorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ProyectoIProgramacionIvApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProyectoIProgramacionIvApplication.class, args);
    }

    @Bean
    CommandLineRunner init(AdministradorRepository repository)
    {
        return args -> {
        if(repository.findAll().isEmpty()) {

               AdministradorModel admin  = new AdministradorModel();
               admin.setIdentificacion("101110111");
               admin.setContrasenna("12");
               repository.save(admin);
        }
        };

    }
}
