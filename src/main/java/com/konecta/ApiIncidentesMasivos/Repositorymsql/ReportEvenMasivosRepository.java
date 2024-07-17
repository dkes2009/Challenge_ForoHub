package com.konecta.ApiIncidentesMasivos.Repositorymsql;


import com.konecta.ApiIncidentesMasivos.Entitymysql.ReportEventosMasivosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportEvenMasivosRepository extends JpaRepository<ReportEventosMasivosEntity, Long> {


}
