package com.konecta.ApiIncidentesMasivos.Repositorymsql;


import com.konecta.ApiIncidentesMasivos.Entitymysql.EventosMasivosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface EventosMasivosRepository extends JpaRepository<EventosMasivosEntity, Long> {

    Optional<EventosMasivosEntity> findById(Long id);
    List<EventosMasivosEntity> findAllByOrderByIdDesc();
    List<EventosMasivosEntity> findAllById(long idEvento);
}
