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
@Table(name= "eventos_masivos")
public class EventosMasivosEntity implements Serializable {
    //dato serializable que pide el sistema
    private static final long serialVersionUID = 1;
    @Id
    @PrimaryKeyJoinColumn
    @Column(name="ID")
    private long id;
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
    @Column(name="URL")
    private String url;
    @Column(name="IS_BANK")
    private byte is_bank;

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
    @Column(name="NOTIFICACION_CLIENTE")
    private String notificacion_cliente;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre_evento() {
        return nombre_evento;
    }

    public void setNombre_evento(String nombre_evento) {
        this.nombre_evento = nombre_evento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public String getCanal_afectado() {
        return canal_afectado;
    }

    public void setCanal_afectado(String canal_afectado) {
        this.canal_afectado = canal_afectado;
    }

    public String getProducto_afectado() {
        return producto_afectado;
    }

    public void setProducto_afectado(String producto_afectado) {
        this.producto_afectado = producto_afectado;
    }

    public byte getCodificar() {
        return codificar;
    }

    public void setCodificar(byte codificar) {
        this.codificar = codificar;
    }

    public String getCodigo_actividad() {
        return codigo_actividad;
    }

    public void setCodigo_actividad(String codigo_actividad) {
        this.codigo_actividad = codigo_actividad;
    }

    public String getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(String fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public String getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(String fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public Byte getRadicar() {
        return radicar;
    }

    public void setRadicar(Byte radicar) {
        this.radicar = radicar;
    }

    public String getNumero_radicado() {
        return numero_radicado;
    }

    public void setNumero_radicado(String numero_radicado) {
        this.numero_radicado = numero_radicado;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public byte getIs_bank() {
        return is_bank;
    }

    public void setIs_bank(byte is_bank) {
        this.is_bank = is_bank;
    }

    public byte getEliminado() {
        return eliminado;
    }

    public void setEliminado(byte eliminado) {
        this.eliminado = eliminado;
    }

    public String getCreado_por() {
        return creado_por;
    }

    public void setCreado_por(String creado_por) {
        this.creado_por = creado_por;
    }

    public String getCreado_el() {
        return creado_el;
    }

    public void setCreado_el(String creado_el) {
        this.creado_el = creado_el;
    }

    public String getActualizado_por() {
        return actualizado_por;
    }

    public void setActualizado_por(String actualizado_por) {
        this.actualizado_por = actualizado_por;
    }

    public String getActualizado_el() {
        return actualizado_el;
    }

    public void setActualizado_el(String actualizado_el) {
        this.actualizado_el = actualizado_el;
    }

    public String getEliminado_por() {
        return eliminado_por;
    }

    public void setEliminado_por(String eliminado_por) {
        this.eliminado_por = eliminado_por;
    }

    public String getEliminado_el() {
        return eliminado_el;
    }

    public void setEliminado_el(String eliminado_el) {
        this.eliminado_el = eliminado_el;
    }

    public String getNotificacion_cliente() {
        return notificacion_cliente;
    }

    public void setNotificacion_cliente(String notificacion_cliente) {
        this.notificacion_cliente = notificacion_cliente;
    }
}