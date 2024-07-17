package com.konecta.ApiIncidentesMasivos.Service.Impl;

import com.konecta.ApiIncidentesMasivos.Entitymysql.ImpactadosEntity;
import com.konecta.ApiIncidentesMasivos.Repositorymsql.ImpactadosRepository;
import com.konecta.ApiIncidentesMasivos.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;



@Service
public class AsyncServices {

    @Autowired
    private ImpactadosRepository clientesImpactados;

    @Autowired
    private Utils utils;
    private static final Logger logger = LoggerFactory.getLogger(AsyncServices.class);


    @Async
    @Transactional(rollbackFor = Exception.class)
    public String guardarImpactadosAsync(List<ImpactadosEntity> listClienteImpactados ,int consecutivo, String Email_user ,int opeCorreo) {
        //AJUSTAR LOS LOGS DEL CARGUE

        String respuesta = "";
        int IdeventoMasivo = listClienteImpactados.get(0).getEvento_masivo_id();

        //Poner un Log de incio de proceso de cargue
        try {

            List<ImpactadosEntity> listainsertada = clientesImpactados.saveAll(listClienteImpactados);
            if (listainsertada.size() > 0){
                int iDatoscargados = listainsertada.size();
                respuesta = "Exitoso";
                if (opeCorreo == 1){
                utils.enviarCorreo(respuesta,1, Email_user, IdeventoMasivo,iDatoscargados);

                } if (opeCorreo == 2) {

                    utils.enviarCorreo(respuesta,2, Email_user, IdeventoMasivo,iDatoscargados);
                }else{
                    respuesta = "opeCorreo diferente a 1 y 2 ";
                    utils.enviarCorreo(respuesta,5, Email_user, IdeventoMasivo,0);
                    utils.logApiError(respuesta);
                }
            }else{
                respuesta = "Lista insertada retornada menor que cero";
                utils.enviarCorreo(respuesta,5, Email_user, IdeventoMasivo,0);
                utils.logApiError(respuesta);
            }
        }catch (Exception e){
            respuesta = "Error en el insert de clientes impactados exepcion";
            utils.enviarCorreo(respuesta,5, Email_user, IdeventoMasivo,0);
            utils.logApiError(respuesta);
            logger.warn(respuesta);
            throw new RuntimeException(e);

        }
        return respuesta;
    }


}
