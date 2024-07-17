package com.konecta.ApiIncidentesMasivos.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.io.Serializable;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class TokenResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String trasactionID;
    private String connid;
    private String jwtToken;

    @Override
    public String toString() {
        return trasactionID+","+connid+","+jwtToken;
    }
}
