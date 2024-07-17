package com.konecta.ApiIncidentesMasivos.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
public class Utils {
    private static int idHilo = 0;
    private transient int indicador = 0;
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss");
    private String message;
    private static Transport transport = null;


    @Value("${ServidorSMTP}")
    private String ServidorSMTP;

    @Value("${RemitenteCorreo}")
    private String RemitenteCorreo;

    @Value("${ProtocoloEmail}")
    private String ProtocoloEmail;

    @Value("${PuertoSMTP}")
    private String PuertoSMTP;

    public void logApi(int respuestaApi, String mensaje, String ip, int consecutivo, String nombreEndpoint, String connid) {
        try {
            String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            File dir = new File(System.getProperties().getProperty("catalina.home") + File.separator
                    + "Logs-ApiIncidentesMasivos");
            if (!dir.exists()) {
                dir.mkdir();
            }
            String fileName = dir + File.separator + fecha + "-TramasApi.txt";
            message = respuestaApi == 0 ? consecutivo + "  RECIBO: " + simpleDateFormat.format(new Date()) + "   " + nombreEndpoint + " :  " + mensaje +  " IP_CONSUMO: " + ip : consecutivo + "  RESPONDO: " + simpleDateFormat.format(new Date()) + "   " + nombreEndpoint + " :  " + mensaje + "   Connid: " +connid+"  IP_CONSUMO: " + ip + "\n";
            imprimir(message, fileName);
        } catch (Exception e) {
            message = "Error Utils: fall in logs ";
            logApiError("ERROR AL GUARDAR EL LOG" + message);
            logger.error(e.getMessage(), e);

        }
    }
    public void logApiError(String mensaje) {
        try {
            String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            File dir = new File(System.getProperties().getProperty("catalina.home") + File.separator
                    + "Logs-ApiIncidentesMasivos");
            if (!dir.exists()) {
                dir.mkdir();
            }
            String fileName = dir + File.separator + fecha + "-Errores.txt";
            message = simpleDateFormat.format(new Date()) + " ------> " + mensaje + "\n";
            imprimir(message, fileName);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void imprimir(final String message, String name) {
        synchronized (this) {
            try (final FileWriter fileWriter = new FileWriter(name, true); final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                bufferedWriter.write(message);
                bufferedWriter.newLine();
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public int consecutivo() {
        synchronized (this) {
            if (idHilo == 9999)
                idHilo = 0;
            this.indicador = ++idHilo;
            return indicador;
        }
    }

    public String obtenerFechaActual() {
        final Calendar capturar_fecha = Calendar.getInstance();
        String mes= Integer.toString(capturar_fecha.get(2) + 1) ;
        String dia = Integer.toString(capturar_fecha.get(Calendar.DATE));
        mes = agregarCerosIzquierda(mes);
        dia = agregarCerosIzquierda(dia);
        return Integer.toString(capturar_fecha.get(Calendar.YEAR)) + mes + dia;
    }

    private String agregarCerosIzquierda(final String diames) {
        final StringBuilder retorno = new StringBuilder();
        if (Integer.parseInt(diames) < 10) {
            final String add = "0" + diames;
            retorno.append(add);
        } else {
            retorno.append(diames);
        }
        return retorno.toString();
    }


    public void logRespuestasConsultasBD(int EnvioRecibo, int consecutivo, String bodyEnvio, String bodyRecibo) {
        try {
            String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            File dir = new File(System.getProperties().getProperty("catalina.home") + File.separator
                    +"Logs-ApiIncidentesMasivos");
            if (!dir.exists()) {
                dir.mkdir();
            }
            String fileName = dir + File.separator + fecha + "-eventosBD.txt";
            message = EnvioRecibo == 0 ? consecutivo +" ENVIO A BD: "+simpleDateFormat.format(new Date()) + " ----> "+ bodyEnvio : consecutivo +" RECIBO BD: "+simpleDateFormat.format(new Date()) +" ----> "+bodyRecibo+"\n";
            imprimir(message, fileName);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    public String obtenerHoraActual() {
        final Time sqlTime = new Time(new Date().getTime());
        return sqlTime.toString();
    }


    public void enviarCorreo(String resultado, int ope, String Email_user, int id_evento_masivo, int iDatoscargados) {

        String remitentes = RemitenteCorreo;
        String toEmail;
        final String fecha = obtenerFechaActual();
        final String hora = obtenerHoraActual();
        String body;
        Properties props = new Properties();
        props.put("mail.transport.protocol", ProtocoloEmail);
        props.put("mail.smtp.host", ServidorSMTP);
        props.setProperty("mail.smtp.port", PuertoSMTP);
        props.setProperty("mail.smtp.auth", "false");
        Session session = Session.getDefaultInstance(props);
        if (Email_user != null ) {
            switch (ope) {
                case 1:
                    toEmail = Email_user;
                    body = " Buen dia. \n"
                            + "El proceso de creacion del  Evento masivo"
                            + " se hizo correctamente. \n"
                            + "\n"
                            + "Resultado: " + resultado
                            + "\n"
                            + "El evento masivo creado es: " + id_evento_masivo
                            + "\n"
                            + "La cantidad de registros insertados fueron: " + iDatoscargados
                            + "\n"
                            + "A la hora: \n"
                            + "      " + hora + "\n"
                            + "Del dia: \n"
                            + "      " + fecha + "\n"
                            + "\n"
                            + "\n"
                            + "\n"
                            + "\n"
                            + "Este es un correo de alerta generado por el componente de comunicacion ApiIncidentesMasivos.\n\n"
                            + "\n"
                            + "Este correo es generado automaticamente por una aplicacion.\n"
                            + "Por favor no responder a este mensaje automotico.\n"
                            + "Cualquier inquietud o duda comunicarse con los administradores del Servidor.\n";
                    break;
                case 2:
                    toEmail = Email_user;
                    body = " Buen dia. \n"
                            + " El proceso actualizacion del Evento masivo"
                            + " se hizo correctamente."
                            + "\n"
                            + "Resultado: " + resultado
                            + "\n"
                            + "El evento masivo actualizado es: " + id_evento_masivo
                            + "\n"
                            + "La cantidad de registros insertados fueron: " + iDatoscargados
                            + "\n"
                            + "A la hora: \n"
                            + "      " + hora + "\n"
                            + "Del dia: \n"
                            + "      " + fecha + "\n"
                            + "\n"
                            + "\n"
                            + "\n"
                            + "\n"
                            + "Este es un correo de alerta generado por el componente de comunicacion ApiIncidentesMasivos.\n\n"
                            + "\n"
                            + "Este correo es generado automoticamente por una aplicacion.\n"
                            + "Por favor no responder a este mensaje automotico.\n"
                            + "Cualquier inquietud o duda comunicarse con los administradores del Servidor.\n";
                    break;
                default:
                    toEmail = Email_user;
                    body = " Buen dia. \n"
                            + " El proceso creacion del  Evento masivo"
                            + " genero un error:  \n"
                            + resultado
                            + "\n"
                            + " Por lo cual, no se hizo el cargue de la informacion en la BD"
                            + "\n"
                            + " por favor corregir el archivo antes de la proxima ejecucion del proceso"
                            + "\n"
                            + "El evento es: " + id_evento_masivo
                            + "\n"
                            + "La cantida de registros insertados fueron: " + iDatoscargados
                            + "\n"
                            + "A la hora: \n"
                            + "      " + hora + "\n"
                            + "Del dia: \n"
                            + "      " + fecha + "\n"
                            + "\n"
                            + "\n"
                            + "\n"
                            + "\n"
                            + "Este es un correo de alerta generado por el componente de comunicacion ApiIncidentesMasivos.\n\n"
                            + "\n"
                            + "Este correo es generado automoticamente por una aplicacion.\n"
                            + "Por favor no responder a este mensaje automotico.\n"
                            + "Cualquier inquietud o duda comunicarse con los administradores del Servidor.\n";
                    break;
            }

            Utils.sendEmail(session, toEmail, "Notificacion - proceso de creacion del evento masivo ", body, remitentes);
        }else{
            logApiError("Error No envia el correo en el json del frontEnd correo Null");
        }

    }

    public static void sendEmail(Session session, String toEmail, String subject, String body, String remitente) {
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.setFrom(new InternetAddress(remitente));
            msg.setReplyTo(InternetAddress.parse("no_reply@example.com", false));
            msg.setSubject(subject, "UTF-8");
            msg.setText(body, "UTF-8");
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            transport = session.getTransport();
            transport.connect();
            transport.send(msg, msg.getAllRecipients());
        } catch (MessagingException e) {
            String message = "Error exeption" + e.getMessage();
            logger.warn(message); // Compliant - exception message logged with some contextual information
            throw new RuntimeException(e);
        }
    }



    public String nombreArchivo(String fechaactual,String creadoPor) {
        String nombre;
        nombre = fechaactual+"_"+creadoPor;
        return nombre;
    }

}
