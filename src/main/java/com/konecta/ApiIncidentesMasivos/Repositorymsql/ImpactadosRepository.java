package com.konecta.ApiIncidentesMasivos.Repositorymsql;


import com.konecta.ApiIncidentesMasivos.Entitymysql.ImpactadosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface ImpactadosRepository extends JpaRepository<ImpactadosEntity, Long> {

    List<ImpactadosEntity> findByCedulaOrderByCreadoElDesc(String cedula);

    List<ImpactadosEntity>  findAllByOrderByIdDesc();


}
