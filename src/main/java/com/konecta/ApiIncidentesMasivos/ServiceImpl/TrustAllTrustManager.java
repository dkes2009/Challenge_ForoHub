package com.konecta.ApiIncidentesMasivos.ServiceImpl;

import com.konecta.ApiIncidentesMasivos.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;
import java.util.Date;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class TrustAllTrustManager implements TrustManager, X509TrustManager {
    private X509TrustManager standardTrustManager = null;
    private final Utils utilLogs = new Utils() ;
    private Date fechaCertificadoWeb;
    private Date fechaCertificadoAlmacenado;
    private KeyStore ks;
    private String ubicacionJks;
    private  String pwd;

    private String nombreCertificado;
    private boolean noExiste;
    public TrustAllTrustManager(String PassWord, String kS){
        try {
            char[] password = PassWord.toCharArray();

            //Ubicacion del Keystore para probar en el servidor
            //Para pruebas locales cambiar esta variable por la variable "kS" que recibe el metodo
            ubicacionJks = System.getProperties().getProperty("catalina.home") + File.separator + "webapps"+ File.separator +"ApiIncidentesMasivos"+ File.separator +"WEB-INF"+ File.separator +"classes" + File.separator + "crt" +  File.separator + kS;
            pwd = PassWord;
            File file = new File(""+ubicacionJks);
            ks = loadKey(file, password);
            TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            if(ks!=null){
                factory.init(ks);
                TrustManager[] trustmanagers = factory.getTrustManagers();
                if (trustmanagers.length == 0)
                    throw new IllegalArgumentException("no trust manager found");
                this.standardTrustManager = (X509TrustManager) trustmanagers[0];

            }else{
                this.utilLogs.logApiError( "Error TrustAllTrustManager ");
            }

        } catch (Exception ex) {
            this.utilLogs.logApiError( "Error TrustAllTrustManager: " + ex);
        }
    }

    private KeyStore loadKey(File file, char[] password) {
        KeyStore ks = null;
        try (FileInputStream fis = new FileInputStream(file)) {
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(fis, password);
            return ks;
        } catch (Exception ex) {
            utilLogs.logApiError( "Error loadKey: " + ex);
            return ks;
        }
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            this.standardTrustManager.checkClientTrusted(chain, authType);
            return;
        } catch (CertificateException ex) {
            utilLogs.logApiError("Error certificado: " + TrustAllTrustManager.class.getName() + ex);
            return;
        }
    }

    public void checkServerTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
        try {
            String nombres[] = certificates[0].getSubjectDN().getName().split(",");
            nombreCertificado = nombres[0].replace("CN=", "").trim();

            if (certificates != null && certificates.length == 1) {
                certificates[0].checkValidity();
            } else {
                if (ks.containsAlias(nombreCertificado)){
                    Certificate certificate = ks.getCertificate(nombreCertificado);
                    X509Certificate certificadoAlmacen = (X509Certificate) certificate;
                    fechaCertificadoWeb=certificates[0].getNotAfter();
                    fechaCertificadoAlmacenado=certificadoAlmacen.getNotAfter();
                    if(fechaCertificadoWeb.equals(fechaCertificadoAlmacenado)){
                        this.standardTrustManager.checkServerTrusted(certificates, authType);
                    }else{
                        //si son fechas diferentes el certificado se va a actualizar automaticamente una vez al dia
                        actualizarCertificado(certificates, nombreCertificado);
                        utilLogs.logApiError("UPDATE ---> JKS  : Se ha actualizado el certificado almacenado " + nombreCertificado + " en el JKS");
                    }
                }else {
                    noExiste=true;
                    utilLogs.logApiError("ERROR ---> SSL  : El certificado "+nombreCertificado+" NO existe en el almacen jks" );
                    throw new CertificateException("ERROR ---> SSL  : El certificado "+nombreCertificado+" NO existe en el almacen jks");
                }
            }
        } catch (CertificateException ex) {

            if(noExiste) {
                utilLogs.logApiError("El certificado "+nombreCertificado+" NO existe en el almacen jks");
                throw new CertificateException(ex);
            }else{
                utilLogs.logApiError("Certificate is not trusted by any of the trust managers");
                throw new CertificateException(ex);
            }
        } catch (KeyStoreException e) {
            utilLogs.logApiError("Exception Keystore: "+e);
            throw new RuntimeException("Exception Keystore: "+e);
        } catch (Exception e) {
            utilLogs.logApiError("Exception no controlada en trustmanager: "+e);
            throw new RuntimeException(e);
        }
    }
    
    public void actualizarCertificado(X509Certificate[] certificates, String nombreCertificado) {

        String ruta = System.getProperties().getProperty("catalina.home") + File.separator + "webapps"+ File.separator +"ApiIncidentesMasivos"+ File.separator +"WEB-INF"+ File.separator +"classes" + File.separator + "crt" +  File.separator + "crtTemp.crt";
        descargarCertificadoTemporal(certificates, ruta);
        guardarCertificadoEnJks(ruta,nombreCertificado);
        eliminarCertificadoTemporal(ruta);
    }

    public void descargarCertificadoTemporal(X509Certificate[] certificates, String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {

            for (X509Certificate certificate : certificates) {
                // Escribir cada certificado en el archivo
                byte[] certBytes = certificate.getEncoded();
                fos.write(certBytes);
            }
        } catch (CertificateEncodingException | IOException e) {
            utilLogs.logApiError("Exception Keystore: "+e);
        }
    }

    public void guardarCertificadoEnJks(String rutaCrt, String aliasCrt) {
        // Cargar el nuevo certificado
        try(FileInputStream certStream = new FileInputStream(rutaCrt)) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate cert = cf.generateCertificate(certStream);


            // Reemplazar el certificado en el jks
            ks.setCertificateEntry(aliasCrt, cert);

            // Guardar el jks actualizado
            FileOutputStream fos = new FileOutputStream(ubicacionJks);
            ks.store(fos, pwd.toCharArray());
            fos.close();
        } catch (FileNotFoundException e) {
            utilLogs.logApiError("No se ha encontrado el archivo: "+e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            utilLogs.logApiError("IoException: "+e);
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            utilLogs.logApiError("Excepcion del certificado: "+e);
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            utilLogs.logApiError("KeystoreException: "+e);
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            utilLogs.logApiError("NoSuchAlgorithmException: "+e);
            throw new RuntimeException(e);
        }
    }

    public boolean eliminarCertificadoTemporal(String rutaCrt){
        File certificado = new File(rutaCrt);
        if(certificado.exists()){
            if(certificado.delete()){
                return true;
            }else{
                return false;
            }
        }else {
            return false;
        }
    }

    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}