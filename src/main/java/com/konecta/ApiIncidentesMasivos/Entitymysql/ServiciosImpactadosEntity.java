package com.konecta.ApiIncidentesMasivos.Entitymysql;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import java.io.Serializable;


@Entity
@Data
@Table(name= "servicios_impactados")
public class ServiciosImpactadosEntity implements Serializable {
    //dato serializable que pide el sistema
    private static final long serialVersionUID = 5;
    @Id
    @PrimaryKeyJoinColumn
    @Column(name="ID")
    private long id;
    @Column(name="ID_EVENTO")
    private int id_evento;

    @Column(name="ID_SERVICIO_IMPACTADO")
    private int id_servicio_impactado;

    @Column(name="NOMBRE")
    private String nombre_evento;

    @Column(name="ELIMINADO")
    private byte eliminado;

    @Column(name="CREADO_POR")
    private String creado_por;
    @Column(name="CREADO_EL")
    private String creado_el;

    @Column(name="ACTUALIZADO_POR")
    private String actualizado_por;
    @Column(name="ACTUALIZADO_EL")
    private String actualizado_el;
    @Column(name="ELIMINADO_POR")
    private String eliminado_por;
    @Column(name="ELIMINADO_EL")
    private String eliminado_el;



}