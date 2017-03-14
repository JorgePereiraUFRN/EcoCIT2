package br.com.ecodif.domain;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
 * Modelo de um <em>feed</em>, que representa os dados do ambiente sensoriado.<br/>
 * Classe gerada com base no XML Schema da linguagem EEML: {@link http
 * ://eeml.org/xsd/0.5.1/0.5.1.xsd}
 * 
 * @see {@link http://eeml.org/xml/0.5.1/complete.xml}
 * 
 * @author Bruno Costa
 */
@XmlRootElement(name = "environment")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "Environment")
public class Environment {

	/**
	 * Identificador do <em>feed</em>, gerado automaticamente quando o
	 * <em>feed</em> � persistido em banco de dados
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int iddb;

	@Transient
	private int sensor;

	@Transient
	private int connectedDevice;

	/** Nome do feed */
	private String title;

	/** URL para o <em>feed</em> */
	private String feed;

	/** <em>Status</em> do <em>feed</em> */
	private String status;

	/**
	 * Indicador se o <em>feed</em> � de visualiza��o p�blica (N) ou privada (S)
	 */
	private String _private;

	/** Descri��o do <em>feed</em> */
	private String description;

	/** URL de arquivo de �cone (imagem) associado ao <em>feed</em> (opcional) */
	@XmlTransient
	private String icon;

	/** URI de acesso ao <em>feed</em> */
	@XmlTransient
	private String website;

	/** Endere�o de <em>e-mail</em> do criador do <em>feed</em> */
	@XmlTransient
	private String email;

	/** Lista de palavras-chave (<em>tags</em>) descritoras do <em>feed</em> */
	@XmlTransient
	@ElementCollection
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<String> tag;

	/**
	 * Refer�ncia para a localiza��o do <em>feed</em>.
	 * 
	 * @see br.com.ecodif.domain.Location
	 */
	@OneToOne(cascade = CascadeType.ALL)
	private Location location;

	/**
	 * Lista de <em>datastreams</em> associados ao <em>feed</em>.<br/>
	 * 
	 * @see br.com.ecodif.domain.Data
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "environment_iddb")
	private List<Data> data;

	/**
	 * Lista de aplica��es �s quais o <em>feed</em> est� vinculado<br/>
	 * 
	 * @see br.com.ecodif.domain.Application
	 */
	@XmlTransient
	@ManyToMany(mappedBy = "feeds", fetch = FetchType.EAGER)
	private Set<Application> applications;

	/** Data/hora de atualiza��o do <em>feed</em> */
	private GregorianCalendar updated;

	/** Nome do criador do <em>feed</em> */
	private String creator;

	/** Identificador do <em>feed</em> */
	private BigInteger id;
	
	@Transient
	private int unit;

	@JsonIgnore
	public int getSensor() {
		return sensor;
	}

	public void setSensor(int sensor) {
		this.sensor = sensor;
	}

	@JsonIgnore
	public int getConnectedDevice() {
		return connectedDevice;
	}

	public void setConnectedDevice(int connectedDevice) {
		this.connectedDevice = connectedDevice;
	}

	@JsonIgnore
	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	/**
	 * Retorna o identificador do <em>feed</em>
	 * 
	 * @return Identificador do <em>feed</em>
	 */
	public int getIddb() {
		return iddb;
	}

	/**
	 * Modifica o identificador do <em>feed</em>
	 * 
	 * @param iddb
	 *            Identificador para altera��o
	 */
	public void setIddb(int iddb) {
		this.iddb = iddb;
	}

	/**
	 * Retorna o nome do <em>feed</em>
	 * 
	 * @return Nome do feed
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Modifica o nome do <em>feed</em>
	 * 
	 * @param title
	 *            Nome para altera��o
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Retorna a URL para o <em>feed</em>
	 * 
	 * @return URL para o <em>feed</em>
	 */
	@JsonIgnore
	public String getFeed() {
		return feed;
	}

	/**
	 * Modifica a URL para o <em>feed</em>
	 * 
	 * @param feed
	 *            URL para altera��o
	 */
	public void setFeed(String feed) {
		this.feed = feed;
	}

	/**
	 * Retorna o <em>status</em> do <em>feed</em>
	 * 
	 * @return <em>Status</em> do <em>feed</em>
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Modifica o <em>status</em> do <em>feed</em>
	 * 
	 * @param status
	 *            <em>Status</em> para altera��o
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Informa se o <em>feed</em> � de visualiza��o p�blica ou privada
	 * 
	 * @return <code>N</code>, caso o <em>feed</em> seja de visualiza��o
	 *         p�blica, ou <code>S</code>, caso o <em>feed</em> seja de
	 *         visualiza��o privada
	 */
	public String get_private() {
		return _private;
	}

	/**
	 * Modifica o <em>status</em> de visualiza��o do <em>feed</em>
	 * 
	 * @param _private
	 *            <em>Status</em> para altera��o
	 */
	public void set_private(String _private) {
		this._private = _private;
	}

	/**
	 * Retona a descri��o do <em>feed</em>
	 * 
	 * @return Descri��o do <em>feed</em>
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Modifica a descri��o do <em>feed</em>
	 * 
	 * @param description
	 *            Descri��o para altera��o
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Retorna URL de arquivo de �cone (imagem) associado ao <em>feed</em>
	 * 
	 * @return URL de arquivo de �cone (imagem) associado ao <em>feed</em>
	 */
	@JsonIgnore
	public String getIcon() {
		return icon;
	}

	/**
	 * Modifica a URL de arquivo de �cone (imagem) associado ao <em>feed</em>
	 * 
	 * @param icon
	 *            URL de arquivo de �cone (imagem) para altera��o
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * Retorna a URI de acesso ao <em>feed</em>
	 * 
	 * @return URI de acesso ao <em>feed</em>
	 */
	@JsonIgnore
	public String getWebsite() {
		return website;
	}

	/**
	 * Modifica a URI de acesso ao <em>feed</em>
	 * 
	 * @param website
	 *            URI para altera��o
	 */
	public void setWebsite(String website) {
		this.website = website;
	}

	/**
	 * Retorna o endere�o de <em>e-mail</em> do criador do <em>feed</em>
	 * 
	 * @return Endere�o de <em>e-mail</em> do criador do <em>feed</em>
	 */
	@JsonIgnore
	public String getEmail() {
		return email;
	}

	/**
	 * Modifica o endere�o de <em>e-mail</em> do criador do <em>feed</em>
	 * 
	 * @param email
	 *            Endere�o de <em>e-mail</em> para altera��o
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Retorna a lista de palavras-chave (<em>tags</em>) descritoras do
	 * <em>feed</em>
	 * 
	 * @return Lista de palavras-chave (<em>tags</em>) descritoras do
	 *         <em>feed</em>
	 */
	@JsonIgnore
	public List<String> getTag() {
		if (tag == null) {
			tag = new ArrayList<String>();
		}
		return this.tag;
	}

	/**
	 * Modifica a lista de palavras-chave (<em>tags</em>) descritoras do
	 * <em>feed</em>
	 * 
	 * @param tag
	 *            Lista de palavras-chave para altera��o
	 */
	public void setTag(List<String> tag) {
		this.tag = tag;
	}

	/**
	 * Retorna a refer�ncia para a localiza��o do <em>feed</em>
	 * 
	 * @return Refer�ncia para a localiza��o do <em>feed</em>
	 */
	public Location getLocation() {
		if (location == null)
			location = new Location();
		return location;
	}

	/**
	 * Modifica a refer�ncia para a localiza��o do <em>feed</em>
	 * 
	 * @param location
	 *            Refer�ncia para altera��o
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * Retorna a lista de <em>datastreams</em> associados ao <em>feed</em>
	 * 
	 * @return Lista de <em>datastreams</em> associados ao <em>feed</em>
	 */
	public List<Data> getData() {
		if (data == null) {
			data = new ArrayList<Data>();
		}
		return this.data;
	}

	/**
	 * Modifica a lista de <em>datastreams</em> associados ao <em>feed</em>
	 * 
	 * @param data
	 *            Lista de <em>datastreams</em> para altera��o
	 */
	public void setData(List<Data> data) {
		this.data = data;
	}

	/**
	 * Retorna a lista de aplica��es �s quais o <em>feed</em> est� vinculado
	 * 
	 * @return Lista de aplica��es �s quais o <em>feed</em> est� vinculado
	 */
	@JsonIgnore
	public Set<Application> getApplications() {
		if (applications == null) {
			applications = new HashSet<Application>();
		}
		return this.applications;
	}

	/**
	 * Modifica a lista de aplica��es �s quais o <em>feed</em> est� vinculado<br/>
	 * 
	 * @param applications
	 *            Lista de aplica��es para altera��o
	 */
	public void setApplications(Set<Application> applications) {
		this.applications = applications;
	}

	/**
	 * Retorna a data/hora de atualiza��o do <em>feed</em>
	 * 
	 * @return Data/hora de atualiza��o do <em>feed</em>
	 */
	public GregorianCalendar getUpdated() {
		return updated;
	}

	/**
	 * Modifica a data/hora de atualiza��o do <em>feed</em>
	 * 
	 * @param updated
	 *            Data/hora para altera��o
	 */
	public void setUpdated(GregorianCalendar updated) {
		this.updated = updated;
	}

	/**
	 * Retorna o nome do criador do <em>feed</em>
	 * 
	 * @return Nome do criador do <em>feed</em>
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * Modifica o nome do criador do <em>feed</em>
	 * 
	 * @param creator
	 *            Nome para altera��o
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * Retorna o identificador do <em>feed</em>
	 * 
	 * @return Identificador do <em>feed</em>
	 */
	public BigInteger getId() {
		return id;
	}

	/**
	 * Modifica o identificador do <em>feed</em>
	 * 
	 * @param id
	 *            Identificador para altera��o
	 */
	public void setId(BigInteger id) {
		this.id = id;
	}

	/**
	 * Verifica se dois <em>feeds</em> s�o iguais com base nos seus
	 * identificadores
	 * 
	 * @param other
	 *            <em>Feed</em> a ser comparado com o <em>feed</em> em quest�o
	 * @return <code>true</code> (verdadeiro) se os identificadores dos
	 *         <em>feeds</em> s�o iguais, e <code>falso</code> (falso) em caso
	 *         contr�rio
	 */
	@Override
	public boolean equals(Object other) {
		return other instanceof Environment && (iddb != 0) ? iddb == (((Environment) other).iddb)
				: (other == this);
	}

	/**
	 * Retorna o c�digo <em>hash</em> do objeto
	 * 
	 * @return C�digo <em>hash</em> do objeto
	 */
	@Override
	public int hashCode() {
		return getIddb() * 8;
	}
}
