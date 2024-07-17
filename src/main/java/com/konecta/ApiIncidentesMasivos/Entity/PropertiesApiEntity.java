package com.konecta.ApiIncidentesMasivos.Entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;

@Entity
@Table(name= "PROPERTIES_API", schema = "AUDIOADM")
@Data
public class PropertiesApiEntity {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name="IDPROPERTY")
	private Integer idProperty;

	@Column(name="PROPERTYNAME")
	private String propertyName;

	@Column(name="ENCRYPTED")
	private String encrypted;

	@Column(name="APPNAME")
	private String appName;

	public PropertiesApiEntity() {
		super();
	}

	@Override
	public String toString() {
		return "PropertiesApiEntity{" +
				"idProperty=" + idProperty +
				", propertyName='" + propertyName + '\'' +
				", encrypted='" + encrypted + '\'' +
				", appName='" + appName + '\'' +
				'}';
	}
}
