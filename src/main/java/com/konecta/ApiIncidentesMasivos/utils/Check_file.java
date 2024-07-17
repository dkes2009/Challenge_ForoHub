package com.konecta.ApiIncidentesMasivos.utils;


import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


@Component
public class Check_file {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    @Value("${EstructuraArchivoAutomatizacion}")
    private String EstructuraArchivo;

    @Value("${PorcentajeDeAceptacion}")
    private String PorcentajeDeAceptacion;

    public boolean verificar(ChannelSftp c, String ruta, String archivoName) throws SftpException, IOException {

        String estructuraDeDatos ="cedula;nombre;canal;producto;valor a corregir;descripcion;tipo de documento;fecha;codigo de convenio;tipo de cuenta";
        String[] nombresrequeridos = estructuraDeDatos.split(";");
        int Porcentaje = 100;

        boolean datosValidos = false;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(c.get(ruta + "/" + archivoName.trim())));) {
            String linea = br.readLine();

            String[] nombresColumnas = linea.split(";");
            datosValidos = verificarNombresColumnas(nombresColumnas, nombresrequeridos);

                if (datosValidos) {
                    int columna = 0;
                    int Datos_correctos = 0;
                    int Datos_incorrectos = 0;
                    while ((linea = br.readLine()) != null) {
                        if (!linea.isEmpty()) {
                            String trim = linea.trim();
                            String[] datos = trim.split(";");
                            if (!verificarDatoColumna(datos[columna])) {
                                Datos_incorrectos++;
                            } else {
                                Datos_correctos++;
                            }
                        }
                    }
                    datosValidos = calcular(Datos_correctos, Datos_incorrectos) >= Porcentaje;
                }
        } catch (SftpException | NumberFormatException e ) {
            //condicion Vacia
            String message = "Error exeption" + e.getMessage();
            Porcentaje = 0;
            logger.warn(message);
            throw new RuntimeException(e);
        }

        return datosValidos;
    }

    private static boolean verificarNombresColumnas(String[] nombresColumnas, String[] nombresRequeridos) {
        if (nombresColumnas.length != nombresRequeridos.length && nombresColumnas.length >= 6) {
            //condicion Vacia
        }else if (nombresColumnas.length == nombresRequeridos.length && nombresColumnas.length >= 6) {
            //condiciocn Vacia
        }else{
            return false;
        }
        for (int i = 0; i < nombresColumnas.length; i++) {
            if (!nombresRequeridos[i].trim().equalsIgnoreCase(nombresColumnas[i].trim())) {
                return false;
            }
        }
        return true;
    }
    private static   boolean verificarDatoColumna(String dato) {
        try {
            Long.parseLong(dato);
            return true;
        } catch (NumberFormatException e) {
            return false; 
        }
    }
    public static double calcular(int datos_correctos, int datos_incorrectos) {
        int total = datos_correctos + datos_incorrectos;
        double resultado = (datos_correctos / (double) total) * 100;

        return resultado;
    }

}
