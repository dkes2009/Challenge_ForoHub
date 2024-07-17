package com.konecta.ApiIncidentesMasivos.Repository;

import com.konecta.ApiIncidentesMasivos.Entity.PropertiesApiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PropertiesApiRepository extends JpaRepository<PropertiesApiEntity, Integer> {
    Optional<PropertiesApiEntity> findByAppNameAndPropertyName(String AppName, String PropertyName);

}
