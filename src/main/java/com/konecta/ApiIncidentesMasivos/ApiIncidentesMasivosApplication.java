package com.konecta.ApiIncidentesMasivos;

import com.konecta.ApiIncidentesMasivos.DTO.RespuestaApiDto;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ApiIncidentesMasivosApplication extends SpringBootServletInitializer {


    public ResponseEntity<RespuestaApiDto> error500(RuntimeException e)  {
        RespuestaApiDto respuesta = new RespuestaApiDto();
        respuesta.setSuccess(false);
        respuesta.setCodigo("ERR01");
        respuesta.setMessage("Error interno contacte al administrador: " + e.getLocalizedMessage());

        return new ResponseEntity(respuesta, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ApiIncidentesMasivosApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiIncidentesMasivosApplication.class, args);
    }

}
