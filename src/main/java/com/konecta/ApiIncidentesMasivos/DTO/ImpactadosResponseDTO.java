package com.konecta.ApiIncidentesMasivos.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Data;


@JsonPOJOBuilder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImpactadosResponseDTO extends RespuestaApiDto {

    //Este campo si se utiliza en otras clases mediante lombook, pero no se imprime en el toString
    private    String tramaRespuesta;
    //Este campo si se utiliza en otras clases mediante lombook, pero no se imprime en el toString
    private String creado_el;

}
