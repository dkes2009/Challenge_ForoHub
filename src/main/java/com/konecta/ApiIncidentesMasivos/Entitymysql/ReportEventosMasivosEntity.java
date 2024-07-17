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
@Table(name= "reporteria_eventos_masivos")
public class ReportEventosMasivosEntity implements Serializable {
    //dato serializable que pide el sistema
    private static final long serialVersionUID = 4;
    @Id
    @PrimaryKeyJoinColumn
    @Column(name="ID")
    private long id;
    @Column(name="CREADO_POR")
    private String creado_por;
    @Column(name="CREADO_EL")
    private String creado_el;
    @Column(name="ID_EVENTO")
    private int id_evento;

    @Column(name="NOMBRE_EVENTO")
    private String nombre_evento;

    @Column(name="NOMBRE_BASE_CARGADA")
    private String nombre_base_cargada;

    @Column(name="ACCION")
    private String accion;
}