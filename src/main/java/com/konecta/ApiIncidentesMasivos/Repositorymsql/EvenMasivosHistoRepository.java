package com.konecta.ApiIncidentesMasivos.Repositorymsql;


import com.konecta.ApiIncidentesMasivos.Entitymysql.EventosMasivosHisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvenMasivosHistoRepository extends JpaRepository<EventosMasivosHisEntity, Long> {


}
