package br.com.ecodif.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * Modelo de um sensor
 * @author Bruno Costa
 */
@XmlRootElement(name = "sensor")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Entity
@Table(name = "Sensor")
public class Sensor {
	
	/** 
	 * Identificador da aplica��o, gerado automaticamente quando o sensor � 
	 * persistido em banco de dados
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	/** Nome do sensor */
	private String name;
	
	/** Descri��o do sensor */
	private String description;
	
	/** Descri��o da precis�o do sensor */
	@Column(name="precisionSensor")
	private String precision;
	
	/** Especifica��es t�cnicas do sensor */
	private String technicalSpecifications;
	
	/**
	 * Lista de dispositivos dotados do sensor em quest�o
	 * @see br.com.ecodif.domain.Device
	 */
	@XmlTransient
	@ManyToMany(mappedBy="sensors")
	private List<Device> devices;
	
	/**
	 * <em>Datastreams</em> fornecidos pelo sensor em quest�o
	 * @see br.com.ecodif.domain.Data
	 */
	@OneToMany
	@JoinColumn(name="sensor_id")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Data> datas;
	
	
	/**
	 * Retorna o identificador do sensor 
	 * @return Identificador do sensor
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Modifica o identificador do sensor
	 * @param id Identificador para altera��o
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Retorna o nome do sensor
	 * @return Nome do sensor
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Modifica o nome do sensor
	 * @param name Nome para altera��o
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Retorna a descri��o do sensor
	 * @return Descri��o do sensor
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Modifica a descri��o do sensor
	 * @param description Descri��o para altera��o
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Retorna a descri��o da precis�o do sensor
	 * @return Descri��o da precis�o do sensor
	 */
	public String getPrecision() {
		return precision;
	}
	
	/**
	 * Modifica a descri��o da precis�o do sensor
	 * @param precision Descri��o para altera��o
	 */
	public void setPrecision(String precision) {
		this.precision = precision;
	}
	
	/**
	 * Retorna as especifica��es t�cnicas do sensor
	 * @return Especifica��es t�cnicas do sensor
	 */
	public String getTechnicalSpecifications() {
		return technicalSpecifications;
	}
	
	/**
	 * Modifica as especifica��es t�cnicas do sensor
	 * @param technicalSpecifications Especifica��es t�cnicas para altera��o
	 */
	public void setTechnicalSpecifications(String technicalSpecifications) {
		this.technicalSpecifications = technicalSpecifications;
	}
	
	/**
	 * Retorna a lista de dispositivos dotados do sensor em quest�o
	 * @return Lista de dispositivos dotados do sensor em quest�o
	 */
	@JsonIgnore
	public List<Device> getDevices() {
		return devices;
	}
	
	/**
	 * Modifica a lista de dispositivos dotados do sensor em quest�o
	 * @param devices Lista de dispositivos para altera��o
	 */
	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}
	
	/**
	 * Retorna a lista de <em>datastreams</em> fornecidos pelo sensor em quest�o
	 * @return Lista de <em>datastreams</em> fornecidos pelo sensor em quest�o
	 */
	public List<Data> getDatas() {
		if(datas == null) {
			datas = new ArrayList<Data>();
		}
		return datas;
	}
	
	/**
	 * Modifica a lista de <em>datastreams</em> fornecidos pelo sensor em quest�o
	 * @param datas Lista de <em>datastreams</em> para altera��o
	 */
	public void setDatas(List<Data> datas) {
		this.datas = datas;
	}
	
	/**
	 * Verifica se dois sensores s�o iguais com base nos seus identificadores
	 * @param other Sensor a ser comparado com o sensor em quest�o
	 * @return <code>true</code> (verdadeiro) se os identificadores dos
	 * 			sensores s�o iguais, e <code>falso</code> (falso) em caso 
	 * 			contr�rio
	 */
	@Override
	public boolean equals(Object other) {
		return other instanceof Sensor && (id != 0) ?
				id == (((Sensor) other).id) : (other == this);
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
