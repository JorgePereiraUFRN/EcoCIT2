package br.com.ecodif.domain;

import java.sql.Blob;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Modelo de uma aplica��o
 * @author Everton Cavalcante
 */
@XmlRootElement(name = "application")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Entity
@Table
public class Application {
	
	/** 
	 * Identificador da aplica��o, gerado automaticamente quando a aplica��o � 
	 * persistida em banco de dados
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	/** Nome da aplica��o */
	@Column(unique=true, nullable=false)
	private String name;
	
	/** Descri��o da aplica��o */
	private String description;
	
	/**
	 * Usu�rio propriet�rio da aplica��o
	 * @see br.com.ecodif.domain.User
	 */
	@XmlTransient
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	/**
	 * Conjunto de <em>feeds</em> vinculados � aplica��o.
	 * @see br.com.ecodif.domain.Environment
	 */
	@XmlTransient
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="Applications_Environments", 
	   joinColumns={@JoinColumn(name="applicationId", referencedColumnName="id")}, 
	   inverseJoinColumns={@JoinColumn(name="environmentId", referencedColumnName="iddb")})
	private Set<Environment> feeds;
	
	/** Palavras-chave (descritores) da aplica��o, separadas por v�rgulas */
	private String tags;
	
	/** 
	 * Flag que indica se a aplica��o � <b>p�blica</b> (<code>false</code>), 
	 * i.e. vis�vel para qualquer usu�rio, ou <b>privada</b> (<code>true</code>), 
	 * i.e. vis�vel apenas para o usu�rio propriet�rio da mesma
	 */
	private boolean _private;
	
	/** Data/hora de cria��o da aplica��o */
	private GregorianCalendar creationDate;
	
	/** Data/hora de atualiza��o da aplica��o */
	@XmlTransient
	private GregorianCalendar updateDate;
	
	/** Caminho do arquivo EMML associado � aplica��o */
	@XmlTransient
	private String emmlReference;
	
	private Blob emmlFile;
	

	
	/**
	 * Retorna o identificador da aplica��o
	 * @return id Identificador da aplica��o
	 */
	public int getId() {
		return id;
	}
	
	public Blob getEmmlFile() {
		return emmlFile;
	}

	public void setEmmlFile(Blob emmlFile) {
		this.emmlFile = emmlFile;
	}

	/**
	 * Modifica o identificador da aplica��o
	 * @param iddb Identificador para altera��o
	 */
	public void setId(int iddb) {
		this.id = iddb;
	}
	
	/**
	 * Retorna o nome da aplica��o
	 * @return name Nome da aplica��o
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Modifica o nome da aplica��o
	 * @param name Nome da aplica��o para altera��o
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Retorna a descri��o da aplica��o
	 * @return description Descri��o da aplica��o
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Modifica a descri��o da aplica��o
	 * @param description Descri��o da aplica��o para altera��o
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Retorna uma refer�ncia para o usu�rio propriet�rio da aplica��o
	 * @return user Usu�rio propriet�rio da aplica��o
	 */
	@JsonIgnore
	public User getUser() {
		return user;
	}
	
	/**
	 * Modifica o usu�rio propriet�rio da aplica��o
	 * @param user Usu�rio para altera��o
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * Retorna o conjunto de <i>feeds</i> atualmente vinculados � aplica��o
	 * @return feeds Conjunto de <i>feeds</i> atualmente vinculados � aplica��o
	 */
	@JsonIgnore
	public Set<Environment> getFeeds() {
		if (feeds == null) {
			feeds = new HashSet<Environment>();
		}
		return feeds;
	}
	
	/**
	 * Modifica o conjunto de <i>feeds</i> atualmente vinculados � aplica��o
	 * @param feeds Conjunto de <i>feeds</i> para altera��o
	 */
	public void setFeeds(Set<Environment> feeds) {
		this.feeds = feeds;
	}
	
	/**
	 * Retorna as palavras-chave descritoras da aplica��o
	 * @return tags Palavras-chave descritoras da aplica��o
	 */
	public String getTags() {
		if (tags == null) {
			tags = "";
		}
		return tags;
	}
	
	/**
	 * Modifica as palavras-chave descritoras da aplica��o
	 * @param tags Palavras-chave para altera��o
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}
	
	/**
	 * Retorna <code>true</code> (verdadeiro) se a aplica��o � privada, 
	 * e <code>false</code> (falso) se a aplica��o � p�blica
	 * @return
	 */
	public boolean get_private() {
		return _private;
	}

	/**
	 * Modifica a flag de permiss�es da aplica��o
	 * @param _private Flag para altera��o
	 */
	public void set_private(boolean _private) {
		this._private = _private;
	}
	
	/**
	 * Retorna a data/hora de cria��o da aplica��o
	 * @return creationDate Data/hora de cria��o da aplica��o
	 */
	public GregorianCalendar getCreationDate() {
		return creationDate;
	}
	
	/**
	 * Modifica a data/hora de cria��o da aplica��o
	 * @param creationDate Data/hora para altera��o
	 */
	public void setCreationDate(GregorianCalendar creationDate) {
		this.creationDate = creationDate;
	}
	
	/**
	 * Retorna a data/hora de atualiza��o da aplica��o
	 * @return updateDate Data/hora de atualiza��o da aplica��o
	 */
	@JsonIgnore
	public GregorianCalendar getUpdateDate() {
		return updateDate;
	}
	
	/**
	 * Modifica a data/hora de atualiza��o da aplica��o
	 * @param updateDate Data/hora para altera��o
	 */
	public void setUpdateDate(GregorianCalendar updateDate) {
		this.updateDate = updateDate;
	}
	
	/**
	 * Retorna o caminho do arquivo EMML associado � aplica��o
	 * @return Caminho do arquivo EMML associado � aplica��o
	 */
	@JsonIgnore
	public String getEmmlReference() {
		return emmlReference;
	}
	
	/**
	 * Modifica o caminho do arquivo EMML associado � aplica��o
	 * @param emmlReference Caminho do arquivo EMML para altera��o
	 */
	public void setEmmlReference(String emmlReference) {
		this.emmlReference = emmlReference;
	}
	
	/**
	 * Verifica se duas aplica��es s�o iguais com base nos seus identificadores
	 * @param other Aplica��o a ser comparada com a aplica��o em quest�o
	 * @return <code>true</code> (verdadeiro) se os identificadores das 
	 * 		   	aplica��es s�o iguais, e <code>falso</code> (falso) em caso 
	 * 			contr�rio
	 */
	@Override
	public boolean equals(Object other) {
		return (other instanceof Application && (id != 0)) ? 
				id == (((Application) other).id) : (other == this);
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
