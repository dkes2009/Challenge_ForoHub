package com.konecta.ApiIncidentesMasivos.Service;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.konecta.ApiIncidentesMasivos.DTO.EventoMasivosResponseDTO;
import com.konecta.ApiIncidentesMasivos.DTO.ImpactadosResponseDTO;
import com.konecta.ApiIncidentesMasivos.Entitymysql.EventosMasivosEntity;
import com.konecta.ApiIncidentesMasivos.Entitymysql.ImpactadosEntity;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public interface ConsumoService {


    ImpactadosResponseDTO findByCedula(String cedula);

    //Metoco que conectar al SFTP y Validar el Archivo y sus registros
    boolean validarArchivosEntradaSftp(String creadoPor) throws JSchException, SftpException, IOException;

    //Metodo Usardo para el alamcenado de las diferentes tablas de BD
    EventoMasivosResponseDTO SaveEventoMasivo(EventosMasivosEntity entity, ArrayList<String> servicios, int consecutivo);

    EventoMasivosResponseDTO SaveEventoHisto (EventosMasivosEntity entity, ArrayList<String> servicios, long IdEventoMasivo,int consecutivo);

    String SaveAll(List<ImpactadosEntity> listClienteImpactados,int consecutivo, String Email_user, int opeCorreo);

    List<ImpactadosEntity> ConexionCargueInserSftp(EventosMasivosEntity entity, ArrayList<String> servicios, long IdEventoMasivo,int consecutivo);

    EventoMasivosResponseDTO SaveReporteria (String Creado_por, LocalDate CreadoEl,int Evento_masivo_id,String Nombre,String Descripcion);

    EventoMasivosResponseDTO SaveServicios (EventosMasivosEntity entity, ArrayList<Integer> servicios, long IdEventoMasivo,int consecutivo);

    boolean consultaIdevento(String id);

    boolean validarArchivosEntradaSftpUpdate(String creadoPor) throws JSchException, SftpException, IOException;

    EventoMasivosResponseDTO UpdateEventoMasivo(EventosMasivosEntity entity, ArrayList<Integer> servicios, int consecutivo);

    EventoMasivosResponseDTO UpdateEventoHisto (EventosMasivosEntity entity, ArrayList<Integer> servicios, long IdEventoMasivo,int consecutivo);

    List<ImpactadosEntity> UpdateConexionCargueInserSftp(EventosMasivosEntity entity, ArrayList<Integer> servicios, long IdEventoMasivo,int consecutivo);
}
