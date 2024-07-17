package com.konecta.ApiIncidentesMasivos.Controller;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.konecta.ApiIncidentesMasivos.DTO.MasivoDto;
import com.konecta.ApiIncidentesMasivos.DTO.EventoMasivosResponseDTO;
import com.konecta.ApiIncidentesMasivos.DTO.ConfirmacionDTO;
import com.konecta.ApiIncidentesMasivos.DTO.EventoMasivosDataDTO;
import com.konecta.ApiIncidentesMasivos.DTO.ImpactadosResponseDTO;
import com.konecta.ApiIncidentesMasivos.Entitymysql.ImpactadosEntity;
import com.konecta.ApiIncidentesMasivos.Service.ConsumoService;
import com.konecta.ApiIncidentesMasivos.utils.Utils;
import com.konecta.ApiIncidentesMasivos.utils.Validaciones;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class ConsumoController {
    @Autowired
    public ConsumoService consumoService;
    private final Utils utilLogs;

    private long startTime;
    private long endTime;
    private long totalTime;
    private static int consecutivo;

    private String mensajeRespuesta;
    private final Validaciones validar;


    @Value("${profile.aes}")
    private String perfilAes;

    public ConsumoController(Utils utilLogs, Validaciones validar) {
        this.utilLogs = utilLogs;
        this.validar = validar;
    }


    @PostMapping(value = "/incidentesMasivos")
    public Object ConsultaMasivos(@RequestBody MasivoDto requestEntity, HttpServletRequest request) {
        consecutivo = utilLogs.consecutivo();
        startTime = System.nanoTime();

        String mensaje = "Recibo Cliente IncidentesMasivos: " + "Cedula_cliente: " + requestEntity.getIdentificacion();
        ImpactadosResponseDTO respuesta = new ImpactadosResponseDTO();
        utilLogs.logApi(0, mensaje, request.getRemoteHost(), consecutivo, "incidentesMasivos", requestEntity.getConnid());
        if (validar.validarincidentesMasivos(requestEntity)) {
            respuesta = consumoService.findByCedula(requestEntity.getIdentificacion());
            if (Objects.equals(respuesta.getCodigo(), "200")) {
                mensaje = "Consulta Exitosa";
                respuesta.setSuccess(true);
                endTime = System.nanoTime();
                totalTime = (endTime - startTime) / 1000000;
                utilLogs.logApi(1, mensaje + " Demoro: " + totalTime, request.getRemoteAddr(), consecutivo, "incidentesMasivos", requestEntity.getConnid());
                return respuesta;

            } else if (Objects.equals(respuesta.getCodigo(), "400")) {
                mensaje = "Cliente no encontrado en la BD masivos";
                respuesta.setSuccess(true);
                HttpStatus.BAD_REQUEST.value();
                respuesta.setCodigo("400");
                respuesta.setMessage(mensaje);
                utilLogs.logApiError(respuesta.getMessage() + " connid: " + requestEntity.getConnid());
                utilLogs.logApi(1, mensaje + " Demoro: " + totalTime, request.getRemoteAddr(), consecutivo, "incidentesMasivos", requestEntity.getConnid());
                return respuesta;

            } else {
                mensaje = "Error en la BD Mysql Eventos Masivos";
                respuesta.setCodigo("500");
                utilLogs.logApiError(respuesta.getMessage() + " " + mensaje + " connid: " + requestEntity.getConnid());
                utilLogs.logApi(1, mensaje + " Demoro: " + totalTime, request.getRemoteAddr(), consecutivo, "incidentesMasivos", requestEntity.getConnid());
                return respuesta;

            }

        } else {
            mensajeRespuesta = "ERROR EN EL INGRESO DE DATOS";
            respuesta.setSuccess(false);
            respuesta.setMessage(mensajeRespuesta);
            respuesta.setCodigo("405");
            utilLogs.logApi(1, mensajeRespuesta + " Demoro: " + totalTime, request.getRemoteAddr(), consecutivo, "incidentesMasivos", requestEntity.getConnid());
            utilLogs.logApiError(mensajeRespuesta + " incidentesMasivos: Connid" + requestEntity.getConnid());
            return respuesta;
        }

    }


    //Endpoint para guardar En la BD de Eventos masivos y validacion del archivo sftp


    @PostMapping(value = "/insertMasivo")
    public Object InsertMasivo(@RequestBody EventoMasivosDataDTO eventoMasivo, HttpServletRequest request, HttpServletResponse httpResponse) throws IOException, JSchException, SftpException {
        String mensaje = "Recibo json del frontEnd  eventos masivos: " + eventoMasivo.toString();
        consecutivo = utilLogs.consecutivo();
        utilLogs.logApi(0, mensaje, request.getRemoteHost(), consecutivo, "Incidentes Masivos: Nombre Evento:", eventoMasivo.getEventoMasivosData().getNombre_evento());
        EventoMasivosResponseDTO response = new EventoMasivosResponseDTO();

        if (validar.validarDataInsert(eventoMasivo)) {
            if (eventoMasivo.getArchivos().equals("true")) {
                boolean CheckArchivo = consumoService.validarArchivosEntradaSftp(eventoMasivo.getEventoMasivosData().getCreado_por());
                if (CheckArchivo) {

                    LocalDateTime creado_el = LocalDateTime.now();
                    eventoMasivo.getEventoMasivosData().setCreado_el(String.valueOf(creado_el));

                    response = consumoService.SaveEventoMasivo(eventoMasivo.getEventoMasivosData(), (ArrayList) eventoMasivo.getServicios(), consecutivo);
                    long idEventoMasivo = response.getId_evento_masivo();

                    if (response.getCodigo() == "200") {

                        EventoMasivosResponseDTO respuestaHistorico = consumoService.SaveEventoHisto(eventoMasivo.getEventoMasivosData(), (ArrayList) eventoMasivo.getServicios(), idEventoMasivo, consecutivo);

                        if (respuestaHistorico.getCodigo() == "200") {

                            List<ImpactadosEntity> listaImpactadosRetorno = consumoService.ConexionCargueInserSftp(eventoMasivo.getEventoMasivosData(), (ArrayList) eventoMasivo.getServicios(), idEventoMasivo, consecutivo);
                            if (listaImpactadosRetorno.size() > 0) {
                                int opeCorreo = 1;
                                String rClietesImpatados = consumoService.SaveAll(listaImpactadosRetorno, consecutivo, eventoMasivo.getUser_email(),opeCorreo);

                                if (rClietesImpatados  == null) {
                                        String Creado_por = listaImpactadosRetorno.get(0).getCreado_por();
                                        LocalDate CreadoEl = listaImpactadosRetorno.get(0).getCreadoEl();
                                        int Evento_masivo_id = listaImpactadosRetorno.get(0).getEvento_masivo_id();
                                        String Nombre = listaImpactadosRetorno.get(0).getNombre();
                                        String Descripcion = listaImpactadosRetorno.get(0).getDescripcion();
                                        EventoMasivosResponseDTO respuestaRespote = consumoService.SaveReporteria(Creado_por, CreadoEl, Evento_masivo_id, Nombre, Descripcion);

                                    if (respuestaRespote.getCodigo() == "200") {
                                        EventoMasivosResponseDTO respuestaSaveServicios = consumoService.SaveServicios(eventoMasivo.getEventoMasivosData(), (ArrayList) eventoMasivo.getServicios(), idEventoMasivo, consecutivo);
                                        response.setCodigo(respuestaSaveServicios.getCodigo());
                                    } else {
                                        //Else de la respuesta respuestaRespote.getCodigo() de el saveServicios
                                        httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                                        response.setSuccess(false);
                                        response.setCodigo("400");
                                        response.setId_evento_masivo(0);
                                        response.setMessage(" respuesta respuestaRespote.getCodigo() de el saveServicios ");
                                        mensajeRespuesta = response.getMessage();
                                        utilLogs.logApiError(mensajeRespuesta);
                                    }
                                } else {
                                    //Else de la respuesta rClientesImpatados
                                    httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                                    response.setSuccess(false);
                                    response.setCodigo("400");
                                    response.setId_evento_masivo(0);
                                    response.setMessage("Lista de Clientes Impactados Vacio Validar archivo String Vacio" + rClietesImpatados);
                                    mensajeRespuesta = response.getMessage();
                                    utilLogs.logApiError(mensajeRespuesta);
                                }
                            } else {
                                //Else de la respuesta listaImpactadosRetorno.size() > 0  del cargue SFTP
                                httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                                response.setSuccess(false);
                                response.setCodigo("400");
                                response.setMessage("Archivo con data erronea antes del el save Lista vacia");
                                response.setId_evento_masivo(0);
                                mensajeRespuesta = response.getMessage();
                                utilLogs.logApiError(mensajeRespuesta);
                            }
                        } else {
                            //Else de la respuesta respuestaHistorico.getCodigo() de el saveHistorico
                            httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                            response.setSuccess(false);
                            response.setCodigo("400");
                            response.setId_evento_masivo(0);
                            response.setMessage("Erro en la Bd de Evento masivo historico");
                            mensajeRespuesta = response.getMessage();
                            utilLogs.logApiError(mensajeRespuesta);
                        }
                    }else{
                        //Else de la respuesta respuestaHistorico.getCodigo() de el eentoMasivo
                        httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                        response.setSuccess(false);
                        response.setCodigo("400");
                        response.setId_evento_masivo(0);
                        response.setMessage("Erro en la Bd de Evento masivo");
                        mensajeRespuesta = response.getMessage();
                        utilLogs.logApiError(mensajeRespuesta);
                    }
                } else {
                    //Else de la respuesta del check del archivo
                    httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                    response.setSuccess(false);
                    response.setCodigo("400");
                    response.setId_evento_masivo(0);
                    response.setMessage("Validacion del archivo SFTP Error ");
                    mensajeRespuesta = response.getMessage();
                    utilLogs.logApiError(mensajeRespuesta);
                }
            } else {
                //Else de las validaciones
                httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setSuccess(false);
                response.setCodigo("400");
                response.setId_evento_masivo(0);
                response.setMessage("Archivo en False");
                mensajeRespuesta = response.getMessage();
                utilLogs.logApiError(mensajeRespuesta);
            }//cierre del Validar Archivo

        } else {
            //ELSE DE LA VALDIACION DE DATOS QUE LLEGAN DEL JSON
            httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setSuccess(false);
            response.setCodigo("400");
            response.setMessage("ERROR EN EL INGRESO DE DATOS");
            mensajeRespuesta = "ERROR EN EL INGRESO DE DATOS";
            utilLogs.logApiError(mensajeRespuesta);

        }
        utilLogs.logApi(1, mensaje + " Demoro: " + totalTime, request.getRemoteAddr(), consecutivo, " Insert  incicentes masivos ", response.toString());
        return response;
    }

    //Endpoint de confirmacion y validacion del archivo sftp
    @PostMapping(value = "/metodoConfirmacion")
    public Object UpdateMasivo(@RequestBody ConfirmacionDTO eventoMasivo, HttpServletRequest request, HttpServletResponse httpResponse) throws IOException, JSchException, SftpException {
        String mensaje = "Recibo confirmacion: " + "id_evento_masivo" + eventoMasivo.getId();

        utilLogs.logApi(0, mensaje, request.getRemoteHost(), consecutivo, "metodoConfirmacion:", eventoMasivo.toString());
        EventoMasivosResponseDTO response = new EventoMasivosResponseDTO();

        if (eventoMasivo.getArchivos().equals("true")) {
            boolean respuesta = consumoService.consultaIdevento(eventoMasivo.getId());
            if (respuesta) {
                boolean CheckArchivo = consumoService.validarArchivosEntradaSftpUpdate(eventoMasivo.getCreado_por());
                if (CheckArchivo) {
                    response.setSuccess(true);
                    response.setCodigo("200");
                    response.setMessage("Archivo y Data Validado Exitosamente");
                    response.setId_evento_masivo(Long.parseLong(eventoMasivo.getId()));
                } else {
                    httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                    response.setSuccess(false);
                    response.setCodigo("401");
                    response.setMessage("Archivo no encontrado, Archivo con estructura erronea metodo Confirmacion");
                    mensajeRespuesta = "Archivo no encontrado, Archivo con estructura erronea metodo Confirmacion";
                    utilLogs.logApiError(mensajeRespuesta);
                }
            } else {
                httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setSuccess(false);
                response.setCodigo("401");
                response.setMessage("IdEvento No existe");
                mensajeRespuesta = "IdEvento No existe";
                utilLogs.logApiError(mensajeRespuesta);
            }
        } else {
            httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setSuccess(false);
            response.setCodigo("401");
            response.setMessage("Archivo en False");
            mensajeRespuesta = "Archivo en False";
            utilLogs.logApiError(mensajeRespuesta);
        }
        utilLogs.logApi(1, mensaje, request.getRemoteHost(), consecutivo, "metodoConfirmacion:", eventoMasivo.toString());
        return response;
    }

    //Endpoint de Update
    @PostMapping(value = "/updateMasivo")
    public Object UpdateMasivo(@RequestBody EventoMasivosDataDTO eventoMasivo, HttpServletRequest request, HttpServletResponse httpResponse) throws IOException, JSchException, SftpException {

        String mensaje = "Recibo Cliente IncidentesMasivos: " + "Id_evento masivos " + eventoMasivo.getEventoMasivosData().getId();
        utilLogs.logApi(0, mensaje, request.getRemoteHost(), consecutivo, "update incedentes masivos:", eventoMasivo.toString());
        EventoMasivosResponseDTO response = new EventoMasivosResponseDTO();

            if(validar.validarDataUpdate(eventoMasivo)){
                if(eventoMasivo.getArchivos().equals("true") ){

                    LocalDateTime actualizado_el  =LocalDateTime.now();
                    eventoMasivo.getEventoMasivosData().setActualizado_el(String.valueOf(actualizado_el));

                    response = consumoService.UpdateEventoMasivo(eventoMasivo.getEventoMasivosData(), (ArrayList) eventoMasivo.getServicios(), consecutivo);

                    long idEventoMasivo = response.getId_evento_masivo();
                    if (response.getCodigo() == "200") {
                        EventoMasivosResponseDTO respuestaHistorico = consumoService.UpdateEventoHisto(eventoMasivo.getEventoMasivosData(), (ArrayList) eventoMasivo.getServicios(), idEventoMasivo, consecutivo);
                       if (respuestaHistorico.getCodigo() == "200") {
                            List<ImpactadosEntity> listaImpactadosRetorno = consumoService.UpdateConexionCargueInserSftp(eventoMasivo.getEventoMasivosData(), (ArrayList) eventoMasivo.getServicios(), idEventoMasivo, consecutivo);
                            if (listaImpactadosRetorno.size() > 0) {
                                int opeCorreo = 2;
                                String rClietesImpatados = consumoService.SaveAll(listaImpactadosRetorno, consecutivo, eventoMasivo.getUser_email(), opeCorreo);
                                if (rClietesImpatados == null) {
                                    consumoService.SaveServicios(eventoMasivo.getEventoMasivosData(), (ArrayList) eventoMasivo.getServicios(), idEventoMasivo, consecutivo);
                                } else {
                                    //Else de la respuesta rClientesImpatados
                                    httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                                    response.setSuccess(false);
                                    response.setCodigo("400");
                                    response.setId_evento_masivo(0);
                                    response.setMessage("Lista de Clientes Impactados Vacio Validar archivo " +" metodo Update : "+ rClietesImpatados);
                                    mensajeRespuesta = response.getMessage();
                                    utilLogs.logApiError(mensajeRespuesta);
                                }
                            } else {
                                //Else de la respuesta listaImpactadosRetorno.size() > 0  del cargue SFTP
                                httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                                response.setSuccess(false);
                                response.setCodigo("400");
                                response.setId_evento_masivo(0);
                                response.setMessage("Archivo con data erronea antes del el save metodo Update");
                                mensajeRespuesta = response.getMessage();
                                utilLogs.logApiError(mensajeRespuesta);
                            }
                        }else{
                            //Else de la validcion del respose code de el Update de Eventomasivos Historico
                           httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                           response.setSuccess(false);
                           response.setCodigo("400");
                           response.setId_evento_masivo(0);
                           response.setMessage("Erro en la Bd de Evento masivo historico metodo Update");
                           mensajeRespuesta = response.getMessage();
                           utilLogs.logApiError(mensajeRespuesta);
                        }
                    }else{
                        //else de la validacion del response Code del update de evento masivo
                        httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                        response.setSuccess(false);
                        response.setCodigo("400");
                        response.setId_evento_masivo(0);
                        response.setMessage("Erro en la Bd de Evento masivo");
                        mensajeRespuesta = response.getMessage();
                        utilLogs.logApiError(mensajeRespuesta);
                    }
                }else{
                    //Else de lala
                    httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                    response.setSuccess(false);
                    response.setCodigo("400");
                    response.setMessage("Archivo en False metodo Update");
                    mensajeRespuesta= response.getMessage();
                    utilLogs.logApiError(mensajeRespuesta);
                }
            }else {
                //ELSE DE LA VALDIACION DE DATOS QUE LLEGAN DEL JSON
                httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setSuccess(false);
                response.setCodigo("400");
                response.setMessage("ERROR EN EL INGRESO DE DATOS metodo Update");
                mensajeRespuesta = "ERROR EN EL INGRESO DE DATOS metodo Update";
                utilLogs.logApiError(mensajeRespuesta);
                return response;
            }
        utilLogs.logApi(1, mensaje + " Demoro: " + totalTime, request.getRemoteAddr(), consecutivo, " update incedentes masivos: ", response.toString());
        return response;
    }

}