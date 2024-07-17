package com.konecta.ApiIncidentesMasivos.Service.Impl;

import com.konecta.ApiIncidentesMasivos.Entity.PropertiesApiEntity;
import com.konecta.ApiIncidentesMasivos.Repository.PropertiesApiRepository;
import com.konecta.ApiIncidentesMasivos.Service.PropertiesApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class PropertiesApiServiceImpl implements PropertiesApiService {

    @Autowired
    private PropertiesApiRepository propertiesApiRepository;

    @Override
    public String getPropertie(String appName, String propertyName) {
        Optional<PropertiesApiEntity> entity = propertiesApiRepository.findByAppNameAndPropertyName(appName, propertyName);
        if (entity.isEmpty()){
            return "";
        } else{
            return entity.get().getEncrypted();
        }
    }
}
