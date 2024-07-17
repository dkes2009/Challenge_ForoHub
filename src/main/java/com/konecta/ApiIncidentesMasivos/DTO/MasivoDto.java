package com.konecta.ApiIncidentesMasivos.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Data;


@JsonPOJOBuilder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MasivoDto {

    //Este campo si se utiliza en otras clases mediante lombook, pero no se imprime en el toString
    private long id;

    //Este campo si se utiliza en otras clases mediante lombook, pero no se imprime en el toString
    private String connid;

    //Este campo si se utiliza en otras clases mediante lombook, pero no se imprime en el toString
    private String identificacion;

    @Override
    public String toString() {
        return "MasivoDto{" +
                "id=" + id +
                ", connid='" + connid + '\'' +
                ", identificacion='" + identificacion + '\'' +
                '}';
    }

    public MasivoDto() {
        //Constructor vacio para la clase
    }


}
