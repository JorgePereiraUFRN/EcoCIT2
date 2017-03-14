package br.com.ecodif.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Modelo de uma localidade.<br/>
 * Classe gerada com base no XML Schema da linguagem EEML: 
 * {@link http://eeml.org/xsd/0.5.1/0.5.1.xsd}
 * 
 * @see {@link http://eeml.org/xml/0.5.1/complete.xml}
 * 
 * @author Bruno Costa
 */
@XmlRootElement(name = "location")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown=true)
@Entity
@Table(name = "Location")
public class Location {
	
	/** 
	 * Identificador da localidade, gerada automaticamente quando a localidade
	 * � persistida em banco de dados
	 */
	@XmlTransient
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int iddb;
	
	/** Nome da localidade */
	private String name;
	
	/** Latitude da localidade */
    private double lat;
    
    /** Longitude da localidade */
    private double lon;
    
    /** Altitude (eleva��o) da localidade */
    private Double ele;
    
    /** 
     * Exposi��o da localidade, se interna (<em>indoor</em>) ou 
     * externa (<em>outdoor</em>)
	 */
    private String exposure;
    
    /**
     * Dom�nio da localidade, se f�sico (<em>physical</em>) ou 
     * virtual (<em>virtual</em>)
     */
    private String domain;
    
    /**
     * Disposi��o da localidade, se fixa (<em>fixed</em>) ou 
     * m�vel (<em>mobile</em>)
     */
    private String disposition;
	
	
    /**
     * Retorna o identificador da localidade
     * @return Identificador da localidade
     */
    @JsonIgnore
	public int getIddb() {
		return iddb;
	}

	/**
	 * Modifica o identificador da localidade
	 * @param iddb Identificador para altera��o
	 */
	public void setIddb(int iddb) {
		this.iddb = iddb;
	}

	/**
	 * Retorna o nome da localidade
	 * @return Nome da localidade
	 */
	public String getName() {
		return name;
	}

	/**
	 * Modifica o nome da localidade
	 * @param name Nome para altera��o
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retorna a latitude da localidade
	 * @return Latitude da localidade
	 */
	public double getLat() {
		return lat;
	}

	/**
	 * Modifica a latitude da localidade
	 * @param lat Latitude para altera��o
	 */
	public void setLat(double lat) {
		this.lat = lat;
	}

	/**
	 * Retorna a longitude da localidade
	 * @return Longitude da localidade
	 */
	public double getLon() {
		return lon;
	}

	/**
	 * Modifica a longitude da localidade
	 * @param lon Longitude para altera��o
	 */
	public void setLon(double lon) {
		this.lon = lon;
	}

	/**
	 * Retorna a altitude (eleva��o) da localidade
	 * @return Altitude (eleva��o) da localidade
	 */
	public Double getEle() {
		return ele;
	}

	/**
	 * Modifica a altitude (eleva��o) da localidade
	 * @param ele Altitude (eleva��o) para altera��o
	 */
	public void setEle(Double ele) {
		this.ele = ele;
	}

	/**
	 * Retorna a exposi��o da localidade
	 * @return Exposi��o da localidade
	 */
	public String getExposure() {
		return exposure;
	}

	/**
	 * Modifica a exposi��o da localidade
	 * @param exposure Exposi��o para altera��o
	 */
	public void setExposure(String exposure) {
		this.exposure = exposure;
	}

	/**
	 * Retorna o dom�nio da localidade
	 * @return Dom�nio para altera��o
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * Modifica o dom�nio da localidade
	 * @param domain Dom�nio para altera��o
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * Retorna a disposi��o da localidade
	 * @return Disposi��o da localidade
	 */
	public String getDisposition() {
		return disposition;
	}

	/**
	 * Modifica a disposi��o da localidade
	 * @param disposition Disposi��o para altera��o
	 */
	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}
}
