package br.com.ecodif.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
 * Modelo para a conex�o de dispositivos
 * @author Bruno Costa
 */
@XmlRootElement(name = "connectedDevice")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@Entity
@Table(name = "ConnectedDevice")
public class ConnectedDevice {
	
	/** 
	 * Identificador do dispositivo conectado, gerado automaticamente quando o 
	 * registro � persistido no banco de dados
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	/** Nome da conex�o */
	private String name;
	
	/** Descri��o da conex�o do dispositivo */
	private String description;
	
	/** Palavras-chave descritoras do dispositivo conectado */
	@XmlTransient
	private String tags;
	
	/**
	 * Usu�rio que conectou o dispositivo
	 * @see br.com.ecodif.domain.User
	 */
	@XmlTransient
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	/**
	 * Dispositico conectado
	 * @see br.com.ecodif.domain.Device
	 */
	@ManyToOne
	@JoinColumn(name="device_id")
	private Device device;
	
	/**
	 * <em>Feed</em> relacionado ao dispositivo conectado
	 * @see br.com.ecodif.domain.Environment 
	 */
	@XmlTransient
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="connecteddevice_id")
	private List<Environment> environments;
	
	/**
	 * Status da conex�o <i>Ativo</i> se o dispositivo est� ativo
	 * e <i>Inativo</i> se o dispositivo est� inativo
	 */
	private String status;

	/**
	 * Retorna o identificador do dispositivo conectado
	 * @return id Identificador do dispositivo conectado
	 */
	public int getId() {
		return id;
	}

	/**
	 * Modifica o identificador do dispositivo conectado
	 * @param id Identificador do dispositivo conectado
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Retorna o nome do dispositivo conectado
	 * @return Nome do dispositivo conectado
	 */
	public String getName() {
		return name;
	}

	/**
	 * Modifica o nome da conex�o do dispositivo
	 * @param name Nome da conex�o para altera��o
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retorna a descri��o da conex�o 
	 * @return Descri��o da conex�o
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Modifica a descri��o da conex�o
	 * @param description Descri��o da conex�o para altera��o
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Retorna as palavras-chave descritoras do dispositivo conectado
	 * @return tags Palavras-chave descritoras do dispositivo conectado
	 */
	public String getTags() {
		return tags;
	}

	/**
	 * Modifica as palavras-chave descritoras do dispositivo conectado
	 * @param tags Palavras-chave para altera��o
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}

	/**
	 * Retorna o usu�rio respons�vel pela conex�o do dispositivo
	 * @return Usu�rio que realizou a conex�o 
	 */
	@JsonIgnore
	public User getUser() {
		return user;
	}

	/**
	 * Modifica o usu�rio respons�vel pela conex�o do dispositivo
	 * @param user Usu�rio para modifica��o
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Retorna o dispositivo conectado
	 * @return Dispositivo conectado
	 */
	public Device getDevice() {
		return device;
	}

	/**
	 * Modifica o dispositivo conectado
	 * @param device Dispositivo para altera��o
	 */
	public void setDevice(Device device) {
		this.device = device;
	}

	/**
	 * Retorna o <em>status</em> da conex�o, se ativa ou inativa
	 * @return <em>Status</em> da conex�o
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * Modifica o status da conex�o
	 * @param status <em>Status</em> da conex�o para altera��o
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Retorna a lista de <em>feeds</em> associados � conex�o
	 * @return Lista de <em>feeds</em> associados � conex�o
	 */
	@JsonIgnore
	public List<Environment> getEnvironments() {
		if(this.environments == null)
			environments = new ArrayList<Environment>();
		return environments;
	}

	/**
	 * Modifica a lista de <em>feeds</em> associados � conex�o 
	 * @param environments Lista de <em>feeds</em> para altera��o
	 */
	public void setEnvironments(List<Environment> environments) {
		this.environments = environments;
	}
	
	/**
	 * Verifica se duas conex�es s�o iguais com base nos seus identificadores
	 * @param other Conex�o a ser comparada com a conex�o em quest�o
	 * @return <code>true</code> (verdadeiro) se os identificadores das 
	 * 		   	conex�es s�o iguais, e <code>falso</code> (falso) em caso 
	 * 			contr�rio
	 */
	@Override
	public boolean equals(Object other) {
		return other instanceof ConnectedDevice && (id != 0) ? 
				id == (((ConnectedDevice) other).id) : (other == this);
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
