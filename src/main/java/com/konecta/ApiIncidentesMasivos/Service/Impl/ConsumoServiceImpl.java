package com.konecta.ApiIncidentesMasivos.Service.Impl;


import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.JSch;
import com.konecta.ApiIncidentesMasivos.DTO.EventoMasivosResponseDTO;
import com.konecta.ApiIncidentesMasivos.DTO.ImpactadosResponseDTO;
import com.konecta.ApiIncidentesMasivos.Entitymysql.EventosMasivosEntity;
import com.konecta.ApiIncidentesMasivos.Entitymysql.ImpactadosEntity;
import com.konecta.ApiIncidentesMasivos.Entitymysql.ServiciosImpactadosEntity;
import com.konecta.ApiIncidentesMasivos.Entitymysql.ReportEventosMasivosEntity;
import com.konecta.ApiIncidentesMasivos.Entitymysql.EventosMasivosHisEntity;
import com.konecta.ApiIncidentesMasivos.Repositorymsql.ReportEvenMasivosRepository;
import com.konecta.ApiIncidentesMasivos.Repositorymsql.ImpactadosRepository;
import com.konecta.ApiIncidentesMasivos.Repositorymsql.ServiciosImpactadosRepository;
import com.konecta.ApiIncidentesMasivos.Repositorymsql.EventosMasivosRepository;
import com.konecta.ApiIncidentesMasivos.Repositorymsql.EvenMasivosHistoRepository;
import com.konecta.ApiIncidentesMasivos.Service.ConsumoService;
import com.konecta.ApiIncidentesMasivos.utils.Check_file;
import com.konecta.ApiIncidentesMasivos.utils.Utils;
import com.konecta.ApiIncidentesMasivos.utils.Validaciones;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;




@Service
public class ConsumoServiceImpl implements ConsumoService {
    private static final Logger logger = LoggerFactory.getLogger(ConsumoServiceImpl.class);

    @Autowired
    private ServiciosImpactadosRepository servicioEventomasivo;
    @Autowired
    private ReportEvenMasivosRepository reportEventomasivo;
    @Autowired
    private AsyncServices asyncServices;
    @Autowired
    private ImpactadosRepository clientesImpactados;

    @Autowired
    private EventosMasivosRepository eventoMasivos;

    @Autowired
    private EvenMasivosHistoRepository eventosMasivosH;

    @Autowired
    private Utils utils;
    private final Check_file check = new Check_file();


    @Autowired
    private Validaciones Validar;
    @Value("${servidorSFTP}")
    private String servidorSFTP;
    @Value("${puertoSFTP}")
    private String puertoSFTP;
    @Value("${usuarioSFTP}")
    private String usuarioSFTP;
    @Value("${passSFTP}")
    private String passSFTP;
    @Value("${pathArchivoAutomatizacion}")
    private String pathAutomatizacion;

    @Override
    public ImpactadosResponseDTO findByCedula(String cedula) {

        ImpactadosResponseDTO impactadosResponse = new ImpactadosResponseDTO();
        try {
            List<ImpactadosEntity> ListaImpactados = clientesImpactados.findByCedulaOrderByCreadoElDesc(cedula);

            if (ListaImpactados.size() > 0) {
                LocalDate fechaCreadoEl = ListaImpactados.get(0).getCreadoEl();
                if(fechaCreadoEl.isAfter(LocalDate.now())) {
                        impactadosResponse.setCodigo("200");
                        impactadosResponse.setSuccess(false);
                        impactadosResponse.setMessage("Cliente con Fecha Superior");
                        impactadosResponse.setTramaRespuesta("111,200,No Exitoso Fecha Superior,~");
                        impactadosResponse.setCreado_el(String.valueOf(fechaCreadoEl));
                        return impactadosResponse;
                        }else {


                    int IdEvento = ListaImpactados.get(0).getEvento_masivo_id();

                    Optional<EventosMasivosEntity> EventoMasivo = eventoMasivos.findById((long) IdEvento);
                    String DiaGestionFinSemana = null;
                            if (EventoMasivo.isPresent()) {
                                //Valida Dias de gestion con findes de semana
                                DiaGestionFinSemana = Validar.calcularTresDiasHabilesDespues(fechaCreadoEl);
                                }
                            if (DiaGestionFinSemana == "Cliente Con Dias Habiles") {
                                impactadosResponse.setSuccess(true);
                                impactadosResponse.setCodigo("200");
                                impactadosResponse.setMessage("Aplica Para Eventos Masivos");
                                impactadosResponse.setCreado_el(String.valueOf(fechaCreadoEl));
                                impactadosResponse.setTramaRespuesta("111,200,Exitoso,~");

                                return impactadosResponse;
                                }else if (DiaGestionFinSemana == "Cliente Sin Dias Habiles") {
                                    impactadosResponse.setSuccess(true);
                                    impactadosResponse.setCodigo("200");
                                    impactadosResponse.setMessage("No Aplica Para Eventos Masivos sin dias habiles");
                                    impactadosResponse.setCreado_el(String.valueOf(fechaCreadoEl));
                                    impactadosResponse.setTramaRespuesta("111,200,No Exitoso sin dias habiles,~");
                                    return impactadosResponse;

                                    } else {
                                        // else Vacio para termianr logica no hace nada
                            }
                    return impactadosResponse;
                }
            } else {
                impactadosResponse.setCodigo("400");
                impactadosResponse.setSuccess(false);
                impactadosResponse.setMessage("Cliente no encontrado en la BD masivos");
                impactadosResponse.setTramaRespuesta("111,400,No Exitoso,~");

                return impactadosResponse;
            }
        } catch (Exception e) {

            impactadosResponse.setCodigo("500");
            impactadosResponse.setSuccess(false);
           impactadosResponse.setMessage("Error en la BD Mysql Eventos Masivos");
           impactadosResponse.setTramaRespuesta("111,500,No Exitoso,~");
           logger.info(String.valueOf(e));
            return impactadosResponse;

        }
    }

    //Metodo que conecta a la ruta SFTP y realiza la valdiacion del archivo y sus datos
    public boolean validarArchivosEntradaSftp(String creadoPor) throws JSchException, SftpException, IOException {

        //AGREGAR UN LOGS EN EL PROCEOSS sftp
        boolean LecturArchivo = false;
        int encontrado = 0;
        int falso= 0;
        Session session = null;
        ChannelSftp channelSftp = null;

        try(InputStream known_host = new FileInputStream(System.getProperties().getProperty("catalina.home") + File.separator
                + "Logs-ApiIncidentesMasivos"+ File.separator + "ssh" + File.separator + "known_hosts_"+ servidorSFTP)) {
            JSch jsch = new JSch();
            jsch.setKnownHosts(known_host);
            session = jsch.getSession(usuarioSFTP, servidorSFTP, Integer.parseInt(puertoSFTP));
            session.setPassword(passSFTP);

            // Establecer la configuración para verificar la clave del host
            session.setConfig("StrictHostKeyChecking", "yes");
            session.setConfig("PreferredAuthentications", "password");

            // Conexión al servidor SFTP
            session.connect();
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            List<ChannelSftp.LsEntry> listaDirectorio = channelSftp.ls(pathAutomatizacion);
            for (ChannelSftp.LsEntry file : listaDirectorio) {
                String fechaactual =    utils.obtenerFechaActual();
                String Nombrearchivo = "";
                if(creadoPor.isEmpty()){
                    Nombrearchivo =  utils.nombreArchivo(fechaactual,creadoPor);
                }
                Nombrearchivo =  utils.nombreArchivo(fechaactual,creadoPor);

                if (file != null && file.getFilename().startsWith(Nombrearchivo)) {
                    LecturArchivo = check.verificar(channelSftp, pathAutomatizacion, file.getFilename());
                    if(LecturArchivo){
                        encontrado++;
                        if(encontrado >= 1){
                            LecturArchivo= true;
                        }
                    }else{
                        falso++;
                    }
                }
                if (falso >= 1){
                    LecturArchivo=false;
                }
            }
            session.disconnect();

        } catch (Exception e) {
            String message = "Error exeption" + e.getMessage();
            logger.warn(message);
            utils.logApiError("Error en la validar archivos EntradaSftp:" + LecturArchivo);
            throw new RuntimeException(e);

        }
        utils.logApiError("Retorno de Valadiar Archivo SFTP:" + LecturArchivo);
        return LecturArchivo;
    }


    //Metodo realiza la creacion el evento masivo en la BD
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventoMasivosResponseDTO SaveEventoMasivo(EventosMasivosEntity entity, ArrayList<String> servicios, int consecutivo) {
        //AjUSTAR lOG SERVICIO

        EventoMasivosResponseDTO response = new EventoMasivosResponseDTO();
        // crea el evento masivo con la informacion del json que viene del front

        EventosMasivosEntity responder = eventoMasivos.save(entity);
        if(responder != null) {
            List<EventosMasivosEntity> listIdEvento = eventoMasivos.findAllByOrderByIdDesc();
            long  IdEventoMasivo = listIdEvento.get(0).getId();
            response.setCodigo("200");
            response.setSuccess(true);
            response.setMessage("Cargue Realizado Correctamente");
            response.setId_evento_masivo(IdEventoMasivo);
        }
        //Else de error en la creacion del evento masivo
        else{
            response.setCodigo("400");
            response.setSuccess(false);
            response.setMessage("Error insertando en la Bd Eventos Masivos");
            response.setData("Error Realizando al insertar la BD de Eventos Masivos");
            utils.logApiError("Error Realizando al insertar la BD de Eventos Masivos");
        }
        return response;
    }
    //Metodo realiza el insert en la BD de  evento masivo Historico
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventoMasivosResponseDTO SaveEventoHisto(EventosMasivosEntity entity, ArrayList<String> servicios,long IdEventoMasivo, int consecutivo) {
        EventoMasivosResponseDTO response = new EventoMasivosResponseDTO();

        EventosMasivosHisEntity eventMasivoHist = new EventosMasivosHisEntity();
        eventMasivoHist.setId_historico((int) IdEventoMasivo);
        eventMasivoHist.setNombre_evento(entity.getNombre_evento());
        eventMasivoHist.setDescripcion(entity.getDescripcion());
        eventMasivoHist.setProtocolo(entity.getProtocolo());
        eventMasivoHist.setCanal_afectado(entity.getCanal_afectado());
        eventMasivoHist.setProducto_afectado(entity.getProducto_afectado());
        eventMasivoHist.setCodificar(entity.getCodificar());
        eventMasivoHist.setCodigo_actividad(entity.getCodigo_actividad());
        eventMasivoHist.setFecha_inicio(entity.getFecha_inicio());
        eventMasivoHist.setFecha_fin(entity.getFecha_fin());
        eventMasivoHist.setRadicar(entity.getRadicar());
        eventMasivoHist.setNumero_radicado(entity.getNumero_radicado());
        eventMasivoHist.setEliminado(entity.getEliminado());
        eventMasivoHist.setCreado_por(entity.getCreado_por());
        eventMasivoHist.setCreado_el(entity.getCreado_el());
        eventMasivoHist.setActualizado_por(entity.getActualizado_por());
        eventMasivoHist.setActualizado_el(entity.getActualizado_el());
        eventMasivoHist.setEliminado_por(entity.getEliminado_por());
        eventMasivoHist.setEliminado_el(entity.getEliminado_el());

        EventosMasivosHisEntity responder1 = eventosMasivosH.save(eventMasivoHist);
        if(responder1 != null) {
            response.setCodigo("200");
            response.setSuccess(true);
            response.setMessage("se crea exitosamente el evento masivos Historivo");

        }
        //Else de error en la creacion del evento masivo
        else{
            response.setCodigo("400");
            response.setSuccess(false);
            response.setMessage("Error insertando en la Bd Eventos Masivos Historico");
            response.setData("Error Realizando al insertar la BD de Eventos Masivos Historico");
            utils.logApiError("Error Realizando al insertar la BD de Eventos Masivos Historico");
        }
        return response;
    }

    //Metodo realiza el insert en la BD de  evento masivo Historico
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String SaveAll(List<ImpactadosEntity> listClienteImpactados,int consecutivo, String Email_user, int opeCorreo) {
        String respuesta = "";
        respuesta = asyncServices.guardarImpactadosAsync(listClienteImpactados, consecutivo, Email_user, opeCorreo);
        if (respuesta != null){
            respuesta= "Error en insert de clientes impactados";
        }
        return respuesta;
    }

    //Metodo que lee el archivo de la Ruta SFTP
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ImpactadosEntity> ConexionCargueInserSftp(EventosMasivosEntity entity, ArrayList<String> servicios, long IdEventoMasivo, int consecutivo) throws RuntimeException {
        Session session = null;
        ChannelSftp channelSftp = null;
        List<ImpactadosEntity> StartCargaBD = new ArrayList<>();

        try (InputStream known_host = new FileInputStream(System.getProperties().getProperty("catalina.home") + File.separator
                + "Logs-ApiIncidentesMasivos"+ File.separator + "ssh" + File.separator + "known_hosts_"+ servidorSFTP)) {

            JSch jsch = new JSch();
            jsch.setKnownHosts(known_host);
            session = jsch.getSession(usuarioSFTP, servidorSFTP, Integer.parseInt(puertoSFTP));
            session.setPassword(passSFTP);
            // Establecer la configuración para verificar la clave del host
            session.setConfig("StrictHostKeyChecking", "yes");
            session.setConfig("PreferredAuthentications", "password");
            // Conexión al servidor SFTP
            session.connect();
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            List<ChannelSftp.LsEntry> listaDirectorio = channelSftp.ls(pathAutomatizacion);
            String fechaactual = utils.obtenerFechaActual();
            String nombreArchivo = "";

            List<String> fileNames = new ArrayList<>();
            for (ChannelSftp.LsEntry entry : listaDirectorio) {
                fileNames.add(entry.getFilename());
            }
            // Ordena la lista de nombres
            Collections.sort(fileNames);

            if (entity.getCreado_por().isEmpty()) {

                nombreArchivo = utils.nombreArchivo(fechaactual,entity.getCreado_por());
            }
                nombreArchivo = utils.nombreArchivo(fechaactual,entity.getCreado_por());

            int ii = 1;
            for (String fileName : fileNames) {
                            if (fileName != null && fileName.startsWith(nombreArchivo+"-"+ii)) {

                                List<ImpactadosEntity>  clien = clientesImpactados.findAllByOrderByIdDesc();
                                int idIncrementable = clien.size();
                                if(StartCargaBD.isEmpty()){
                                    StartCargaBD.addAll(CargueClientesImpactados(channelSftp, pathAutomatizacion, fileName, entity, IdEventoMasivo,idIncrementable));
                                }else{
                                    ImpactadosEntity ultimoId = StartCargaBD.get(StartCargaBD.size()-1);
                                    StartCargaBD.addAll(CargueClientesImpactados(channelSftp, pathAutomatizacion, fileName, entity, IdEventoMasivo, (int) ultimoId.getId()));
                                }
                                ii++;
                            }
            }
            session.disconnect();
        } catch (IOException e) {
            String message = "Error exeption" + e.getMessage();
            logger.warn(message);
            utils.logApiError("Exeption Error  de conexion al SFTP  Conexion Cargue Inser Sftp del save Clientes impactados  ");
            throw new RuntimeException(e);
        } catch (JSchException e) {
            String message = "Error exeption" + e.getMessage();
            logger.warn(message);
            utils.logApiError(" Exeption  RuntimeException Error  de conexion al SFTP  Conexion Cargue Inser Sftp del save Clientes impactados  ");
            throw new RuntimeException(e);
        } catch (SftpException e) {
            String message = "Error exeption" + e.getMessage();
            logger.warn(message);
            utils.logApiError("Exeption SftpException  Error  de conexion al SFTP  Conexion Cargue Inser Sftp del save Clientes impactados  ");
            throw new RuntimeException(e);
        }
        return StartCargaBD;
    }



    public List<ImpactadosEntity> CargueClientesImpactados(ChannelSftp c, String ruta, String archivoName, EventosMasivosEntity entity,long IdEventoMasivo, int idIncrementable) throws SftpException, IOException {
        List<ImpactadosEntity> listaClienesimpactados = null;
        try (BufferedReader Buffer = new BufferedReader(new InputStreamReader(c.get(ruta + "/" + archivoName.trim())));) {
            listaClienesimpactados = new ArrayList<>();
            JSONObject dataJsonBd = new JSONObject();
            int i = 0;
            String Cadena;
            int idIncrementado = idIncrementable + 1;
            while ((Cadena = Buffer.readLine()) != null) {
                String[] caden = Cadena.split(";");

                if (i > 0) {
                    ImpactadosEntity impactadosclientes = new ImpactadosEntity();
                    if(caden.length <= 7){
                        //Datos del archivo
                        impactadosclientes.setCedula(caden[0]);
                        impactadosclientes.setNombre(caden[1]);
                        impactadosclientes.setValor_reintegro(caden[4]);
                        impactadosclientes.setDescripcion(caden[5]);
                        impactadosclientes.setTipo_documento(caden[6]);

                        impactadosclientes.setId(idIncrementado++);
                        impactadosclientes.setCanal(entity.getCanal_afectado());
                        impactadosclientes.setProducto(entity.getProducto_afectado());
                        impactadosclientes.setEvento_masivo_id((int) IdEventoMasivo);
                        impactadosclientes.setProducto_canal(entity.getProducto_afectado());
                        impactadosclientes.setEliminado(entity.getEliminado());
                    }else{
                        //Datos del archivo
                        impactadosclientes.setCedula(caden[0]);
                        impactadosclientes.setNombre(caden[1]);
                        impactadosclientes.setValor_reintegro(caden[4]);
                        impactadosclientes.setDescripcion(caden[5]);
                        impactadosclientes.setTipo_documento(caden[6]);

                        dataJsonBd.put("fecha", caden[7]);
                        dataJsonBd.put("codigo de convenio", caden[8]);
                        dataJsonBd.put("tipo de cuenta", caden[9]);

                        impactadosclientes.setData_adicional_json(dataJsonBd.toString());
                        impactadosclientes.setId(idIncrementado++);
                        impactadosclientes.setCanal(entity.getCanal_afectado());
                        impactadosclientes.setProducto(entity.getProducto_afectado());
                        impactadosclientes.setEvento_masivo_id((int) IdEventoMasivo);
                        impactadosclientes.setProducto_canal(entity.getProducto_afectado());
                        impactadosclientes.setEliminado(entity.getEliminado());
                    }
                    if (entity.getCreado_por() == null) {
                        impactadosclientes.setActualizado_por(entity.getActualizado_por());
                        impactadosclientes.setActualizado_el(String.valueOf(LocalDate.now()));
                    } else {
                        impactadosclientes.setCreado_por(entity.getCreado_por());
                        impactadosclientes.setCreadoEl(LocalDate.now());
                    }
                    impactadosclientes.setEliminado_por(entity.getEliminado_por());
                    impactadosclientes.setEliminado_el(entity.getEliminado_el());
                    listaClienesimpactados.add(impactadosclientes);
                }

                i++;
            }
            if (listaClienesimpactados.size() > 0) {
                return listaClienesimpactados;
            }
        }catch (Exception e) {
            //Exeption del buferRaodet ruta sftp
            utils.logApiError("Exeption  del BufferedReader ruta sftp");
            throw new RuntimeException(e);
        }
        return listaClienesimpactados;
    }

    //Metodo realiza el insert en la BD de  servicios impactaos
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventoMasivosResponseDTO SaveServicios(EventosMasivosEntity entity, ArrayList<Integer> servicios, long IdEventoMasivo, int consecutivo) {

        EventoMasivosResponseDTO response = new EventoMasivosResponseDTO();
        
        for (int i = 0; i < servicios.size(); i++) {
            ServiciosImpactadosEntity serviImpactados = new ServiciosImpactadosEntity();
            serviImpactados.setId_evento((int) IdEventoMasivo);
            int servi = servicios.get(i);
            serviImpactados.setId_servicio_impactado(servi);
            serviImpactados.setNombre_evento(entity.getNombre_evento());
            serviImpactados.setEliminado(entity.getEliminado());
            serviImpactados.setCreado_por(entity.getCreado_por());
            serviImpactados.setCreado_el(entity.getCreado_el());
            serviImpactados.setActualizado_por(entity.getActualizado_por());
            serviImpactados.setActualizado_el(entity.getActualizado_el());
            serviImpactados.setEliminado_por(entity.getEliminado_por());
            serviImpactados.setEliminado_el(entity.getEliminado_el());

            ServiciosImpactadosEntity respuestaServi = servicioEventomasivo.save(serviImpactados);
            if(respuestaServi != null) {
                response.setCodigo("200");
                response.setSuccess(true);
                response.setMessage("Cargue Realizado Correctamente");
                response.setId_evento_masivo(IdEventoMasivo);

            }else{
                response.setCodigo("400");
                response.setSuccess(false);
                response.setId_evento_masivo(IdEventoMasivo);
                response.setMessage("Cargue No Realizado en Servicios Impactados Error");
                response.setData("Error Realizando el insert de la BD de Servicios Impactados");
                utils.logApiError("Error Realizando el insert de la BD de Servicios Impactados" + "Id evento masivo " + IdEventoMasivo );
            }
        }

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventoMasivosResponseDTO SaveReporteria(String Creado_por, LocalDate CreadoEl, int Evento_masivo_id, String Nombre, String Descripcion) {

        EventoMasivosResponseDTO response = new EventoMasivosResponseDTO();
        //Log de inicio
        ReportEventosMasivosEntity reporteEventMasivo = new ReportEventosMasivosEntity();
        reporteEventMasivo.setCreado_por(Creado_por);
        reporteEventMasivo.setCreado_el(String.valueOf(CreadoEl));
        reporteEventMasivo.setId_evento(Evento_masivo_id);
        reporteEventMasivo.setNombre_evento(Nombre);
        reporteEventMasivo.setNombre_base_cargada(Descripcion);
        reporteEventMasivo.setAccion("Creado");

        ReportEventosMasivosEntity respuestaResporte = reportEventomasivo.save(reporteEventMasivo);

        if(respuestaResporte != null) {
            response.setCodigo("200");
            response.setSuccess(true);
            response.setMessage("Cargue Realizado Correctamente");
            response.setId_evento_masivo(Evento_masivo_id);

        }else {
            response.setCodigo("400");
            response.setSuccess(false);
            response.setId_evento_masivo(Evento_masivo_id);
            response.setMessage("Cargue No Realizado en Servicios Impactados Error");
            response.setData("Error Realizando el insert de la BD de Servicios Impactados");
            utils.logApiError("Error Realizando el insert de la BD de Servicios Impactados" + "Id evento masivo " + Evento_masivo_id );
        }

        return response;
    }


    /*
    METODO DE UPDATES REALIZA DOS COSUMOS UNO DE VALIDACION DEL ARCHIVO Y EL OTRO DEL EL CARGUE CON EL FIN DE QUE EL FRONT
    REALIZA EL dELELTE DEL EVENTO MASAVO Y SE INSERTA LA NUEVA INFORMACION
    */
    //Metodo confirmacion consulta el id_evento Masivo
    @Override
    public boolean consultaIdevento(String id) {
        try {
            long idEvento = Long.parseLong(id);
            List<EventosMasivosEntity> responder1 = eventoMasivos.findAllById(idEvento);
            if(responder1.size() > 0) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            utils.logApiError("Exeptcion Actualizacion  de la BD de GuardarHisorico" + "Id evento masivo " + id);
            throw new RuntimeException(e);
        }
    }

    //Metodo confirmacion de validacion del archvio en el Update
    public boolean validarArchivosEntradaSftpUpdate(String creadoPor) throws JSchException, SftpException, IOException {

        boolean LecturArchivo = false;
        int encontrado = 0;
        int falso= 0;
        Session session = null;
        ChannelSftp channelSftp = null;

        try(InputStream known_host = new FileInputStream(System.getProperties().getProperty("catalina.home") + File.separator
                + "Logs-ApiIncidentesMasivos"+File.separator + "ssh" + File.separator + "known_hosts_"+ servidorSFTP)) {

            JSch jsch = new JSch();

            jsch.setKnownHosts(known_host);
            session = jsch.getSession(usuarioSFTP, servidorSFTP, Integer.parseInt(puertoSFTP));
            session.setPassword(passSFTP);
            // Establecer la configuración para verificar la clave del host
            session.setConfig("StrictHostKeyChecking", "yes");
            session.setConfig("PreferredAuthentications", "password");
            // Conexión al servidor SFTP
            session.connect();
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            List<ChannelSftp.LsEntry> listaDirectorio = channelSftp.ls(pathAutomatizacion);

            for (ChannelSftp.LsEntry file : listaDirectorio) {
                String fechaactual =    utils.obtenerFechaActual();
                String Nombrearchivo = "";
                if(creadoPor == null){

                    Nombrearchivo = "UP_"+ utils.nombreArchivo(fechaactual,creadoPor);
                }
                Nombrearchivo = "UP_" + utils.nombreArchivo(fechaactual,creadoPor);

                if (file != null && file.getFilename().startsWith(Nombrearchivo)) {

                    LecturArchivo = check.verificar(channelSftp, pathAutomatizacion, file.getFilename());
                    if(LecturArchivo){
                        encontrado++;
                        if(encontrado >= 1){
                            LecturArchivo= true;
                        }
                    }else{
                        falso++;
                    }
                }
                if (falso >= 1){
                    LecturArchivo=false;
                }
            }
            session.disconnect();

        } catch (Exception e) {
            utils.logApiError("Error en la Validacion de Archivos del sftp:" + e);
            return false; // La conexión falló
        }
        return LecturArchivo;
    }
    //Metodo confirmacion de actualizacion dela  Bd Evento Masivos en el Update
    @Override
    public EventoMasivosResponseDTO UpdateEventoMasivo(EventosMasivosEntity entity, ArrayList<Integer> servicios, int consecutivo) {
        utils.logRespuestasConsultasBD(0,consecutivo,"Json que recibo  "+ entity,entity.getNombre_evento());
        EventoMasivosResponseDTO response = new EventoMasivosResponseDTO();
        // Actualiza el evento masivo con la informacion del json que viene del front
        EventosMasivosEntity responder = eventoMasivos.save(entity);
        if(responder != null) {
            response.setCodigo("200");
            response.setSuccess(true);
            response.setMessage("Exitoso");
            response.setId_evento_masivo(entity.getId());
        }
        //Else de error en la creacion del evento masivo
        else{
            response.setCodigo("400");
            response.setSuccess(false);
            response.setMessage("Error insertando en la Bd Eventos Masivos");
            response.setData("Error Realizando al insertar la BD de Eventos Masivos");
            utils.logApiError("Error Realizando al insertar la BD de Eventos Masivos");
        }
        return response;

    }
    //Metodo confirmacion de actualizacion dela  Bd Evento Masivos historico en el Update
    @Override
    public EventoMasivosResponseDTO UpdateEventoHisto(EventosMasivosEntity entity, ArrayList<Integer> servicios,long IdEventoMasivo, int consecutivo) {
        utils.logRespuestasConsultasBD(0,consecutivo,"Json que recibo  "+ entity, entity.getNombre_evento());
        EventoMasivosResponseDTO response = new EventoMasivosResponseDTO();

        EventosMasivosHisEntity eventMasivoHist = new EventosMasivosHisEntity();
        eventMasivoHist.setId_historico((int) IdEventoMasivo);
        eventMasivoHist.setNombre_evento(entity.getNombre_evento());
        eventMasivoHist.setDescripcion(entity.getDescripcion());
        eventMasivoHist.setProtocolo(entity.getProtocolo());
        eventMasivoHist.setCanal_afectado(entity.getCanal_afectado());
        eventMasivoHist.setProducto_afectado(entity.getProducto_afectado());
        eventMasivoHist.setCodificar(entity.getCodificar());
        eventMasivoHist.setCodigo_actividad(entity.getCodigo_actividad());
        eventMasivoHist.setFecha_inicio(entity.getFecha_inicio());
        eventMasivoHist.setFecha_fin(entity.getFecha_fin());
        eventMasivoHist.setRadicar(entity.getRadicar());
        eventMasivoHist.setNumero_radicado(entity.getNumero_radicado());
        eventMasivoHist.setEliminado(entity.getEliminado());
        eventMasivoHist.setCreado_por(entity.getCreado_por());
        eventMasivoHist.setCreado_el(entity.getCreado_el());
        eventMasivoHist.setActualizado_por(entity.getActualizado_por());
        eventMasivoHist.setActualizado_el(entity.getActualizado_el());
        eventMasivoHist.setEliminado_por(entity.getEliminado_por());
        eventMasivoHist.setEliminado_el(entity.getEliminado_el());

        EventosMasivosHisEntity responder1 = eventosMasivosH.save(eventMasivoHist);
        if(responder1 != null) {
            response.setCodigo("200");
            response.setSuccess(true);
            response.setMessage("se crea exitosamente el evento masivos Historivo");
            response.getId_evento_masivo();
        }
        //Else de error en la creacion del evento masivo
        else{
            response.setCodigo("400");
            response.setSuccess(false);
            response.setMessage("Error insertando en la Bd Eventos Masivos Historico");
            response.setData("Error Realizando al insertar la BD de Eventos Masivos Historico");
            utils.logApiError("Error Realizando al insertar la BD de Eventos Masivos Historico");
        }
        return response;
    }

    @Override
    public List<ImpactadosEntity> UpdateConexionCargueInserSftp(EventosMasivosEntity entity, ArrayList<Integer> servicios, long IdEventoMasivo, int consecutivo) {

        Session session = null;
        ChannelSftp channelSftp = null;
        List<ImpactadosEntity> StartCargaBD = new ArrayList<>();
        try (InputStream known_host = new FileInputStream(System.getProperties().getProperty("catalina.home") + File.separator
                + "Logs-ApiIncidentesMasivos"+  File.separator + "ssh" + File.separator + "known_hosts_"+ servidorSFTP)) {
            JSch jsch = new JSch();
            jsch.setKnownHosts(known_host);
            session = jsch.getSession(usuarioSFTP, servidorSFTP, Integer.parseInt(puertoSFTP));
            session.setPassword(passSFTP);
            // Establecer la configuración para verificar la clave del host
            session.setConfig("StrictHostKeyChecking", "yes");
            session.setConfig("PreferredAuthentications", "password");
            // Conexión al servidor SFTP
            session.connect();
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            List<ChannelSftp.LsEntry> listaDirectorio = channelSftp.ls(pathAutomatizacion);
            String fechaactual = utils.obtenerFechaActual();
            String nombreArchivo = "";

            List<String> fileNames = new ArrayList<>();
            for (ChannelSftp.LsEntry entry : listaDirectorio) {
                fileNames.add(entry.getFilename());
            }
            // Ordena la lista de nombres
            Collections.sort(fileNames);

            // Imprime la lista ordenada

            if (entity.getCreado_por() == null) {

                nombreArchivo = "UP_"+utils.nombreArchivo(fechaactual,entity.getCreado_el());
            }
                nombreArchivo = "UP_"+utils.nombreArchivo(fechaactual,entity.getActualizado_por());
            int ii =1;
            for (String fileName : fileNames) {
                if (fileName != null && fileName.startsWith(nombreArchivo+"-"+ii)) {
                    List<ImpactadosEntity> clien = clientesImpactados.findAllByOrderByIdDesc();
                    int idIncrementable = clien.size();
                    if(StartCargaBD.isEmpty()){
                        StartCargaBD.addAll(CargueClientesImpactados(channelSftp, pathAutomatizacion, fileName, entity, IdEventoMasivo,idIncrementable));
                    }else{
                        ImpactadosEntity ultimoId = StartCargaBD.get(StartCargaBD.size()-1);
                        StartCargaBD.addAll(CargueClientesImpactados(channelSftp, pathAutomatizacion, fileName, entity, IdEventoMasivo, (int) ultimoId.getId()));
                    }
                    ii++;
                }
            }
            session.disconnect();

            //return true; // La conexión fue exitosa
        } catch (IOException e) {
            utils.logApiError("Error en el ConexionCargueInserSftp IOExeption");
            throw new RuntimeException(e);
        } catch (JSchException e) {
            utils.logApiError("Error en el ConexionCargueInserSftp JSchException");
            throw new RuntimeException(e);
        } catch (SftpException e) {
            utils.logApiError("Error en el ConexionCargueInserSftp SftpException");
            throw new RuntimeException(e);
        }


        return StartCargaBD;
    }

}

