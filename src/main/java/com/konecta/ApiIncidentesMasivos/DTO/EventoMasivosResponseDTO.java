package com.konecta.ApiIncidentesMasivos.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Data;


@JsonPOJOBuilder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventoMasivosResponseDTO extends RespuestaApiDto{

    private static final long serialVersionUID = 3505246739835239022L;

    //Este campo si se utiliza en otras clases mediante lombook, pero no se imprime en el toString
    private long id_evento_masivo;

}
