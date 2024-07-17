package com.konecta.ApiIncidentesMasivos.Entitymysql;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;


@Entity
@Data
@Table(name= "eventos_masivos_historicos")
public class EventosMasivosHisEntity implements Serializable {
    //dato serializable que pide el sistema
    private static final long serialVersionUID = 2;
    @Id
    @Column(name="ID")
    private long id;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_HISTORICO")
    private int id_historico;
    @Column(name="NOMBRE")
    private String nombre_evento;
    @Column(name="DESCRIPCION")
    private String descripcion;
    @Column(name="PROTOCOLO")
    private String protocolo;
    @Column(name="CANAL_AFECTADO")
    private String canal_afectado;
    @Column(name="PRODUCTO_AFECTADO")
    private String producto_afectado;
    @Column(name="CODIFICAR")
    private byte codificar;
    @Column(name="CODIGO_ACTIVIDAD")
    private String codigo_actividad;
    @Column(name="FECHA_INICIO")
    private String fecha_inicio;
    @Column(name="FECHA_FIN")
    private String fecha_fin;
    @Column(name="RADICAR")
    private Byte radicar;
    @Column(name="NUMERO_RADICADO")
    private String numero_radicado;
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