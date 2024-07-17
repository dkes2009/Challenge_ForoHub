package com.konecta.ApiIncidentesMasivos.Entitymysql;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;


import java.io.Serializable;
import java.time.LocalDate;


@Entity
@Data
@Table(name= "clientes_impactados")
public class ImpactadosEntity implements Serializable {
    //dato serializable que pide el sistema
    private static final long serialVersionUID = 2;
    @Id
    @PrimaryKeyJoinColumn
    @Column(name="ID")
    private long id;
    @Column(name="EVENTO_MASIVO_ID")
    private int evento_masivo_id;
    @Column(name="CEDULA")
    private String cedula;
    @Column(name="NOMBRE")
    private String nombre;
    @Column(name="PRODUCTO_CANAL")
    private String producto_canal;
    @Column(name="CANAL")
    private String canal;
    @Column(name="PRODUCTO")
    private String producto;
    @Column(name="VALOR_REINTEGRO")
    private String valor_reintegro;
    @Column(name="DESCRIPCION")
    private String descripcion;
    @Column(name="ELIMINADO")
    private byte eliminado;
    @Column(name="CREADO_POR")
    private String creado_por;
    @Column(name="CREADO_EL")
    private LocalDate creadoEl;
    @Column(name="ACTUALIZADO_POR")
    private String actualizado_por;
    @Column(name="ACTUALIZADO_EL")
    private String actualizado_el;
    @Column(name="ELIMINADO_POR")
    private String eliminado_por;
    @Column(name="ELIMINADO_EL")
    private String eliminado_el;
    @Column(name="DATA_ADICIONAL_JSON")
    private String data_adicional_json;
    @Column(name="TIPO_DOCUMENTO")
    private String tipo_documento;

    @Override
    public String toString() {
        return "ImpactadosEntity{" +
                "id=" + id +
                ", evento_masivo_id=" + evento_masivo_id +
                ", cedula='" + cedula + '\'' +
                ", nombre='" + nombre + '\'' +
                ", producto_canal='" + producto_canal + '\'' +
                ", canal='" + canal + '\'' +
                ", producto='" + producto + '\'' +
                ", valor_reintegro='" + valor_reintegro + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", eliminado=" + eliminado +
                ", creado_por='" + creado_por + '\'' +
                ", creado_el='" + creadoEl + '\'' +
                ", actualizado_por='" + actualizado_por + '\'' +
                ", actualizado_el='" + actualizado_el + '\'' +
                ", eliminado_por='" + eliminado_por + '\'' +
                ", eliminado_el='" + eliminado_el + '\'' +
                ", data_adicional_json='" + data_adicional_json + '\'' +
                ", tipo_documento='" + tipo_documento + '\'' +
                '}';
    }
}