package com.konecta.ApiIncidentesMasivos.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AES256 {
    private final Utils utilLogs = new Utils();

    private SecretKeySpec getKeySpec(int tipoAes, String perfilAes) {
        SecretKeySpec spec = null;
        byte[] bytes = new byte[32];
        File dir = new File(System.getProperties().getProperty("catalina.home") + File.separator + "webapps"+ File.separator +"ApiIncidentesMasivos"+ File.separator +"WEB-INF"+ File.separator +"classes");
        if (!dir.exists()) { dir.mkdir(); }
        File f = new File(dir + File.separator + nombreAes(tipoAes, perfilAes));
        try (InputStream input = Files.newInputStream(f.toPath())) {
            int read = input.read(bytes);
            if (read > 0) {
                spec = new SecretKeySpec(bytes, "AES");
            }
        } catch (Exception ex) {
            Logger.getLogger(AES256.class.getName()).log(Level.SEVERE, null, ex);
            utilLogs.logApiError("Error obteniendo la llave de cifrado: " + ex);
        }
        return spec;
    }

    public String cifrar(final String plaintext, int tipoAes, String perfilAes){
        String cifrado = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec secretKey = getKeySpec(tipoAes, perfilAes);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            cifrado = encodeBase64(concatenateArrays(cipher.getIV(), cipher.doFinal(plaintext.getBytes())));
        }catch(Exception ex){
            Logger.getLogger(AES256.class.getName()).log(Level.SEVERE, null, ex);
            utilLogs.logApiError("Error cifrado ----> "+ex);
        }
        return cifrado;
    }

    public String descifrar(final String cipherTextBase64, int tipoAes, String perfilAes) {
        String descifrado = null;
        try {
            byte[] cipherText = decodeBase64(cipherTextBase64);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec secretKey = getKeySpec(tipoAes, perfilAes);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, cipherText, 0, 12);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
            byte[] decryptedText = cipher.doFinal(cipherText, 12, cipherText.length - 12);
            descifrado = new String(decryptedText);
        }catch(Exception ex){
            Logger.getLogger(AES256.class.getName()).log(Level.SEVERE, null, ex);
            utilLogs.logApiError("Error descifrado ----> "+ex);
        }
        return descifrado;
    }

    public static byte[] concatenateArrays(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private String encodeBase64(byte[] datos){ return java.util.Base64.getEncoder().encodeToString(datos);}
    private byte[] decodeBase64(String datos){ return Base64.getDecoder().decode(datos);}

    public String nombreAes(int tipoAes, String perfilAes){
        if (tipoAes==1)
            return "FACTORY_AES256_LOG";
        else
            return "FACTORY_AES256_TOKEN_"+perfilAes;
    }

}