package br.com.ecodif.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Modelo de uma plataforma de dispositivo
 * @author Bruno Costa
 */
@XmlRootElement(name = "platform")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "Platform")
public class Platform {

	/** 
	 * Identificador da plataforma, gerado automaticamente quando a plataforma
	 * � persistida em banco de dados
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	/** Descri��o da plataforma */
	private String description;
	
	/** Vers�o da plataforma */
	@Column(name="platformVersion")
	private String version;
	
	/** 
	 * Lista de dispositivos 
	 * @see br.com.ecodif.domain.Device
	 */
	@OneToMany
	@JoinColumn(name="platform_id")
	private List<Device> devices;

	
	/**
	 * Retorna o identificador da plataforma
	 * @return Identificador da plataforma
	 */
	public int getId() {
		return id;
	}

	/**
	 * Modifica o identificador da plataforma
	 * @param id Identificador para altera��o
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Descri��o da plataforma
	 * @return Descri��o da plataforma
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Modifica a descri��o da plataforma
	 * @param description Descri��o para altera��o
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Retorna a vers�o da plataforma
	 * @return Vers�o da plataforma
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Modifica a vers�o da plataforma
	 * @param version Vers�o para altera��o
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Retorna a lista de dispositivos
	 * @return Lista de dispositivos
	 */
	public List<Device> getDevices() {
		if (devices == null) {
			devices = new ArrayList<Device>();
		}
		return devices;
	}

	/**
	 * Modifica a lista de dispositivos
	 * @param devices Lista de dispositivos
	 */
	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}
	
	/**
	 * Verifica se duas plataformas s�o iguais com base nos seus identificadores
	 * @param other Plataforma a ser comparada com a plataforma em quest�o
	 * @return <code>true</code> (verdadeiro) se os identificadores das 
	 * 		   	plataformas s�o iguais, e <code>falso</code> (falso) em caso 
	 * 			contr�rio
	 */
	@Override
	public boolean equals(Object other) {
		return other instanceof Platform && (id != 0) ?
				id == (((Platform) other).id) : (other == this);
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
