package com.konecta.ApiIncidentesMasivos.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.konecta.ApiIncidentesMasivos.Entitymysql.EventosMasivosEntity;
import lombok.Data;


@JsonPOJOBuilder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventoMasivosDataDTO {

    private static final long serialVersionUID = 3505246739835239022L;

    private EventosMasivosEntity eventoMasivosData;

    @JsonProperty("servicios")
    private Object servicios;
    @JsonProperty("user_email")
    private String user_email;
    @JsonProperty("archivos")
    private String archivos;

    public EventoMasivosDataDTO(EventosMasivosEntity eventoMasivosData, Object servicios, String user_email, String archivos) {
        this.eventoMasivosData = eventoMasivosData;
        this.servicios = servicios;
        this.user_email = user_email;
        this.archivos = archivos;
    }

    public EventosMasivosEntity getEventoMasivosData() {
        return eventoMasivosData;
    }

    public void setEventoMasivosData(EventosMasivosEntity eventoMasivosData) {
        this.eventoMasivosData = eventoMasivosData;
    }

    public Object getServicios() {
        return servicios;
    }

    public void setServicios(Object servicios) {
        this.servicios = servicios;
    }



    public String getArchivos() {
        return archivos;
    }

    public void setArchivos(String archivos) {
        this.archivos = archivos;
    }

    public EventoMasivosDataDTO() {
    }

    @Override
    public String toString() {
        return "EventoMasivosDataDTO{" +
                "eventoMasivosData=" + eventoMasivosData +
                ", servicios=" + servicios +
                ", user_email='" + user_email + '\'' +
                ", archivos='" + archivos + '\'' +
                '}';
    }
}
