package com.konecta.ApiIncidentesMasivos.Entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name= "BD_USER_APIS_BANCO", schema = "AUDIOADM")
@Data
public class UserEntity {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name="IDUSER")
	private Integer idUser;
	
	@Column(name="USERNAME")
	private String userName;
	
	@Column(name="PASSWORD")
	private String password;
	
	@Column(name="APPNAME")
	private String appName;
	
	public UserEntity() {
		super();
	}
	@Override
	public String toString() {
		return "UserEntity [idUser=" + idUser + ", userName=" + userName + ", password= [PROTECTED]" + ", appName="
				+ appName + "]";
	}
	
	

}
