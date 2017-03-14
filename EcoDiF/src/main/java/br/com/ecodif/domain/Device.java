package br.com.ecodif.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * Modelo de um dispositivo
 * @author Bruno Costa
 */
@XmlRootElement(name = "device")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true) 
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "Device")
public class Device {

	/**
	 * Identificador do dispositivo, gerado automaticamente quando o dispositivo
	 * � persistido em banco de dados
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private int platform;
	
	/**
	 * Refer�ncia para o usu�rio propriet�rio do dispositivo
	 * @see br.com.ecodif.domain.User
	 */
	@ManyToOne
	@JoinColumn(name = "user_id")
	@XmlTransient
	private User company;
	
	/** Nome do dispositivo */
	private String name;
	
	/** Descri��o do dispositivo */
	private String description;
	
	/** Vers�o do dispositivo */
	@Column(name = "deviceVersion")
	private String version;
	
	/** Especifica��es t�cnicas do dispositivo */
	private String technicalSpecifications;

	/** 
	 * Lista de sensores dos quais o dispositivo � dotado
	 * @see br.com.ecodif.domain.Sensor
	 */
	@ManyToMany(fetch=FetchType.EAGER)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Sensor> sensors;

	/**
	 * Lista de conex�es
	 * @see br.com.ecodif.domain.ConnectedDevice
	 */
	@OneToMany
	@JoinColumn(name = "device_id")
	@XmlTransient
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<ConnectedDevice> connectedDevices;	

	
	public int getPlatform() {
		return platform;
	}

	public void setPlatform(int platform) {
		this.platform = platform;
	}
	
	/**
	 * Retorna o identificador do dispositivo
	 * @return Identificador do dispositivo
	 */
	public int getId() {
		return id;
	}

	/**
	 * Modifica o identificador para o dispositivo
	 * @param id Identificador para altera��o
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Retorna a descri��o do dispositivo
	 * @return Descri��o do dispositivo
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Modifica a descri��o do dispositivo
	 * @param description Descri��o para altera��o
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Retorna a refer�ncia para o usu�rio propriet�rio do dispositivo
	 * @return Refer�ncia para o usu�rio propriet�rio do dispositivo
	 */
	@JsonIgnore
	public User getCompany() {
		return company;
	}

	/**
	 * Modifica a refer�ncia para o usu�rio propriet�rio do dispositivo
	 * @param company Refer�ncia para altera��o
	 */
	public void setCompany(User company) {
		this.company = company;
	}

	/**
	 * Retorna o nome do dispositivo
	 * @return Nome do dispositivo
	 */
	public String getName() {
		return name;
	}

	/**
	 * Modifica o nome do dispositivo
	 * @param name Nome para altera��o
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retorna a vers�o do dispositivo
	 * @return Vers�o do dispositivo
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Modifica a vers�o do dispositivo
	 * @param version Vers�o para altera��o
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Retorna as especifica��es t�cnicas do dispositivo
	 * @return Especifica��es t�cnicas do dispositivo
	 */
	public String getTechnicalSpecifications() {
		return technicalSpecifications;
	}

	/** 
	 * Modifica as especifica��es t�cnicas do dispositivo
	 * @param technicalSpecifications Especifica��es t�cnicas para altera��o
	 */
	public void setTechnicalSpecifications(String technicalSpecifications) {
		this.technicalSpecifications = technicalSpecifications;
	}

	/**
	 * Retorna a lista de sensores dos quais o dispositivo em quest�o � dotado
	 * @return Lista de sensores dos quais o dispositivo � dotado
	 */
	public List<Sensor> getSensors() {
		if (sensors == null) {
			sensors = new ArrayList<Sensor>();
		}
		return sensors;
	}

	/**
	 * Modifica a lista de sensores dos quais o dispositivo em quest�o � dotado
	 * @param sensors Lista de sensores para modifica��o
	 */
	public void setSensors(List<Sensor> sensors) {
		this.sensors = sensors;
	}

	/**
	 * Retorna a lista de conex�es
	 * @return Lista de conex�es
	 */
	@JsonIgnore
	public List<ConnectedDevice> getConnectedDevices() {
		return connectedDevices;
	}

	/**
	 * Modifica a lista de conex�es
	 * @param connectedDevices Lista de conex�es para altera��o
	 */
	public void setConnectedDevices(List<ConnectedDevice> connectedDevices) {
		this.connectedDevices = connectedDevices;
	}
	
	/**
	 * Verifica se dois dispositivos s�o iguais com base nos seus identificadores
	 * @param other Dispositivo a ser comparado com o dispositivo em quest�o
	 * @return <code>true</code> (verdadeiro) se os identificadores dos 
	 * 		   	dispositivos s�o iguais, e <code>falso</code> (falso) em caso 
	 * 			contr�rio
	 */
	@Override
	public boolean equals(Object other) {
		return other instanceof Device && (id != 0) ?
				id == (((Device) other).id) : (other == this);
	}
	
	/**
	 * Retorna um c�digo <em>hash</em> para o objeto
	 * @return C�digo <em>hash</em> para o objeto
	 */
	@Override
	public int hashCode() {
		return getId() * 8;
	}
}
