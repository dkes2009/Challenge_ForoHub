package com.konecta.ApiIncidentesMasivos.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Data;


@JsonPOJOBuilder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfirmacionDTO {

    @JsonProperty("id")
    private String id;
    @JsonProperty("archivos")
    private String archivos;
    @JsonProperty("creado_por")
    private String creado_por;
    @JsonProperty("actualizado_por")
    private String actualizado_por;


}
