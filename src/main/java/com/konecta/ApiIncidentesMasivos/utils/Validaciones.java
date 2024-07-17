package com.konecta.ApiIncidentesMasivos.utils;

import com.konecta.ApiIncidentesMasivos.DTO.EventoMasivosDataDTO;
import com.konecta.ApiIncidentesMasivos.DTO.MasivoDto;
import org.springframework.stereotype.Component;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



@Component
public class Validaciones {
    private static final Pattern numeros = Pattern.compile("[0-9-]*");
    private static final Pattern letrasYNumeros = Pattern.compile("[A-Za-z0-9=?')¿áéíóú(/¡; :._-]*");
    private static final Pattern letras = Pattern.compile("[A-Za-z._-]*");
    private static final String ERRORDATA = "ERROR EN EL INGRESO DE DATOS: ";
    private Utils utilLogs = new Utils();
    private static final Set<LocalDate> holidays = new HashSet<>();

    public Validaciones() {
        this.utilLogs = utilLogs;
    }



    static {
        // Aquí se añaden algunos días festivos
        holidays.add(LocalDate.of(2024, 5, 1));
        holidays.add(LocalDate.of(2024, 5, 13));
        holidays.add(LocalDate.of(2024, 6, 3));
        holidays.add(LocalDate.of(2024, 6, 10));
        holidays.add(LocalDate.of(2024, 7, 1));
        holidays.add(LocalDate.of(2024, 7, 20));
        holidays.add(LocalDate.of(2024, 8, 7));
        holidays.add(LocalDate.of(2024, 8, 19));
        holidays.add(LocalDate.of(2024, 10, 14));
        holidays.add(LocalDate.of(2024, 11, 4));
        holidays.add(LocalDate.of(2024, 11, 11));
        holidays.add(LocalDate.of(2024, 12, 8));
        holidays.add(LocalDate.of(2024, 12, 25));
        holidays.add(LocalDate.of(2025, 1, 1));
        holidays.add(LocalDate.of(2025, 1, 6));
        holidays.add(LocalDate.of(2025, 3, 24));
        holidays.add(LocalDate.of(2025, 4, 13));
        holidays.add(LocalDate.of(2025, 4, 17));
        holidays.add(LocalDate.of(2025, 4, 18));
        holidays.add(LocalDate.of(2025, 5, 1));
        holidays.add(LocalDate.of(2025, 6, 2));
        holidays.add(LocalDate.of(2025, 6, 23));
        holidays.add(LocalDate.of(2025, 6, 30));
        holidays.add(LocalDate.of(2025, 7, 20));
        holidays.add(LocalDate.of(2025, 8, 7));
        holidays.add(LocalDate.of(2025, 8, 18));
        holidays.add(LocalDate.of(2025, 10, 13));
        holidays.add(LocalDate.of(2025, 11, 3));
        // Añadir más días festivos según sea necesario
    }

    public static boolean isHoliday(LocalDate date) {
        return holidays.contains(date);
    }
    public boolean validarNumeros(String cadena, Integer longitud){
        try {
            if (cadena.length() <= longitud) {
                Matcher mat = numeros.matcher(cadena);
                if (mat.matches()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }catch (Exception e){
            utilLogs.logApiError(ERRORDATA+e);
            return false;
        }
    }

    public boolean validarLetras(String cadena, Integer longitud){
        try{
            if(cadena.length()<=longitud) {
                Matcher mat = letras.matcher(cadena);
                if(mat.matches()){
                    return true;
                } else {
                    return false;
                }
            }else {
                return false;
            }
        }catch (Exception e){
            utilLogs.logApiError(ERRORDATA+e);
            return false;
        }
    }

    public boolean validarLetrasYNumeros(String cadena, Integer longitud) {
        try {
            if (cadena.length() <= longitud) {
                Matcher mat = letrasYNumeros.matcher(cadena);
                if (mat.matches()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            utilLogs.logApiError(ERRORDATA+ e);
            return false;
        }
    }


    public static String calcularTresDiasHabilesDespues(LocalDate fecha) {
        int diasHabilesContados = 0;
        LocalDate fechaActual = fecha; // Empezar con la fecha de creacion
        String respuesta = "";

        while (diasHabilesContados < 4) {
            // Verificar si la fecha actual es un día hábil (de lunes a viernes)
            if (fechaActual.getDayOfWeek() != DayOfWeek.SATURDAY && fechaActual.getDayOfWeek() != DayOfWeek.SUNDAY) {
                if(isHoliday(fechaActual)) {
                    //no hace nada sigue el proceso
                }else{
                    diasHabilesContados++;
                }
            }if(diasHabilesContados != 4){
                fechaActual = fechaActual.plusDays(1);// aumenta  un día
            }
        }
        int dias = (int) ChronoUnit.DAYS.between(fechaActual,LocalDate.now());

        // Valido dias negativos
        if(dias <= 0 ){ 
                ///Valido que dias negativos sea entre -1 y -8
            if ( dias == 0 || dias == -1  || dias == -2 || dias == -3 || dias == -4 || dias == -5 || dias == -6 || dias == -7 || dias == -8){
                respuesta= "Cliente Con Dias Habiles";
                }else{
                respuesta= "Cliente Sin Dias Habiles";
                    }
                        }else{
                            respuesta= "Cliente Sin Dias Habiles";
                            }
                        return respuesta;
        }


    public boolean validarincidentesMasivos(MasivoDto dto) {
        if (dto.getIdentificacion() != null && dto.getIdentificacion() != "" &&
                dto.getConnid() != null && dto.getConnid() != ""  &&
                validarLetrasYNumeros(dto.getConnid(), 34)  &&
                validarNumeros(dto.getIdentificacion(), 20)){
            return true;
        }else{
            return false;
        }
    }



    public boolean validarDataInsert(EventoMasivosDataDTO dto) {
        if (dto.getEventoMasivosData().getNombre_evento() != null && dto.getEventoMasivosData().getNombre_evento() != "" &&
                validarLetrasYNumeros(dto.getEventoMasivosData().getNombre_evento(), 50)  &&

                dto.getEventoMasivosData().getDescripcion() != null && dto.getEventoMasivosData().getDescripcion() != "" &&
                validarTamaño(dto.getEventoMasivosData().getDescripcion(), 2000)  &&

                dto.getEventoMasivosData().getProtocolo() != null && dto.getEventoMasivosData().getProtocolo() != "" &&
                validarTamaño(dto.getEventoMasivosData().getProtocolo(), 2000)  &&

                dto.getEventoMasivosData().getCanal_afectado() != null && dto.getEventoMasivosData().getCanal_afectado() != "" &&
                validarNumeros(dto.getEventoMasivosData().getCanal_afectado(),3)  &&

                dto.getEventoMasivosData().getProducto_afectado() != null && dto.getEventoMasivosData().getProducto_afectado() != "" &&
                validarNumeros(dto.getEventoMasivosData().getProducto_afectado(),3)  &&


                validarLetrasYNumeros(dto.getEventoMasivosData().getNotificacion_cliente(),100)  &&

                validarNumeros(String.valueOf(dto.getEventoMasivosData().getCodificar()),2)  &&

                dto.getEventoMasivosData().getCodigo_actividad() != null && dto.getEventoMasivosData().getCodigo_actividad() != "" &&
                validarNumeros(dto.getEventoMasivosData().getCodigo_actividad(),3)  &&

                dto.getEventoMasivosData().getFecha_inicio() != null && dto.getEventoMasivosData().getFecha_inicio() != "" &&
                validarLetrasYNumeros(dto.getEventoMasivosData().getFecha_inicio(),30)  &&

                dto.getEventoMasivosData().getFecha_fin() != null && dto.getEventoMasivosData().getFecha_fin() != "" &&
                validarLetrasYNumeros(dto.getEventoMasivosData().getFecha_fin(),30) &&

                validarLetrasYNumeros(dto.getEventoMasivosData().getNumero_radicado(),50) &&

                validarLetrasYNumeros(String.valueOf(dto.getEventoMasivosData().getIs_bank()),2) &&

                validarLetrasYNumeros(String.valueOf(dto.getEventoMasivosData().getEliminado()),2) &&

                dto.getEventoMasivosData().getCreado_por() != null && dto.getEventoMasivosData().getCreado_por() != "" &&
                validarLetrasYNumeros(dto.getEventoMasivosData().getCreado_por(),50) &&

                dto.getServicios() != null && dto.getServicios() != "" &&
                validarTamaño(String.valueOf(dto.getServicios()),100) &&

                dto.getArchivos() != null && dto.getArchivos() != "" &&
                validarLetras(dto.getArchivos(),10)
        ){
            return true;
        }else{
            return false;
        }
    }
    public boolean validarTamaño(String cadena, Integer longitud) {
        try {
            if (cadena.length() <= longitud) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            utilLogs.logApiError(ERRORDATA+ e);
            return false;
        }
    }
    public boolean validarDataUpdate(EventoMasivosDataDTO dto) {
        if (dto.getEventoMasivosData().getNombre_evento() != null && dto.getEventoMasivosData().getNombre_evento() != "" &&
                validarLetrasYNumeros(dto.getEventoMasivosData().getNombre_evento(), 50)  &&

                dto.getEventoMasivosData().getId() != 0 &&
                validarNumeros(String.valueOf(dto.getEventoMasivosData().getId()), 50)  &&

                dto.getEventoMasivosData().getDescripcion() != null && dto.getEventoMasivosData().getDescripcion() != "" &&
                validarTamaño(dto.getEventoMasivosData().getDescripcion(), 2000)  &&

                dto.getEventoMasivosData().getProtocolo() != null && dto.getEventoMasivosData().getProtocolo() != "" &&
                validarTamaño(dto.getEventoMasivosData().getProtocolo(), 2000)  &&

                dto.getEventoMasivosData().getCanal_afectado() != null && dto.getEventoMasivosData().getCanal_afectado() != "" &&
                validarNumeros(dto.getEventoMasivosData().getCanal_afectado(),3)  &&

                dto.getEventoMasivosData().getProducto_afectado() != null && dto.getEventoMasivosData().getProducto_afectado() != "" &&
                validarNumeros(dto.getEventoMasivosData().getProducto_afectado(),3)  &&


                validarLetrasYNumeros(dto.getEventoMasivosData().getNotificacion_cliente(),100)  &&

                validarNumeros(String.valueOf(dto.getEventoMasivosData().getCodificar()),2)  &&

                dto.getEventoMasivosData().getCodigo_actividad() != null && dto.getEventoMasivosData().getCodigo_actividad() != "" &&
                validarNumeros(dto.getEventoMasivosData().getCodigo_actividad(),3)  &&

                dto.getEventoMasivosData().getFecha_inicio() != null && dto.getEventoMasivosData().getFecha_inicio() != "" &&
                validarLetrasYNumeros(dto.getEventoMasivosData().getFecha_inicio(),30)  &&

                dto.getEventoMasivosData().getFecha_fin() != null && dto.getEventoMasivosData().getFecha_fin() != "" &&
                validarLetrasYNumeros(dto.getEventoMasivosData().getFecha_fin(),30) &&

                validarLetrasYNumeros(String.valueOf(dto.getEventoMasivosData().getRadicar()),100) &&

                validarLetrasYNumeros(dto.getEventoMasivosData().getNumero_radicado(),50) &&

                validarLetrasYNumeros(String.valueOf(dto.getEventoMasivosData().getIs_bank()),2) &&

                validarLetrasYNumeros(String.valueOf(dto.getEventoMasivosData().getEliminado()),2) &&

                dto.getServicios() != null && dto.getServicios() != "" &&
                validarTamaño(String.valueOf(dto.getServicios()),100) &&

                dto.getArchivos() != null && dto.getArchivos() != "" &&
                validarLetras(dto.getArchivos(),10)
        ){
            return true;
        }else{
            return false;
        }
    }

}
