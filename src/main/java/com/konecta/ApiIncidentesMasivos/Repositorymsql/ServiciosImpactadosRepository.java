package com.konecta.ApiIncidentesMasivos.Repositorymsql;



import com.konecta.ApiIncidentesMasivos.Entitymysql.ServiciosImpactadosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ServiciosImpactadosRepository extends JpaRepository<ServiciosImpactadosEntity, Long> {





}
