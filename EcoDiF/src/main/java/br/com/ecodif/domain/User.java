package br.com.ecodif.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
 * Modelo de um usu�rio da EcoDiF
 * @author Bruno Costa
 */
@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@Table(name = "User")
public class User {

	/** 
	 * Identificador do usu�rio, gerado automaticamente quando o usu�rio � 
	 * persistido em banco de dados
	 */
	@XmlTransient
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	/** Nome do usu�rio */
	private String name;

	/** <em>Login</em> do usu�rio */
	@XmlTransient
	@Column(unique = true)
	private String login;
	
	/** Senha do usu�rio */
	@XmlTransient
	private String password;

	/** <em>E-mail</em> do usu�rio */
	@Column(unique = true)
	private String email;
	
	/** Nome da companhia do usu�rio */
	private String companyName;

	/** 
	 * Tipo de usu�rio
	 * @see br.com.ecodif.domain.UserType
	 */
	@XmlTransient
	@ManyToOne
	@JoinColumn(name = "userTypeId")
	private UserType userType;
	
	/**
	 * Grupos de usu�rio aos quais o usu�rio em quest�o est� associado
	 * @see br.com.ecodif.domain.UserGroup
	 */
	@XmlTransient
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(mappedBy="users", fetch=FetchType.EAGER)
	private Set<UserGroup> userGroups;

	@XmlTransient
	private String token;
	
	/**
	 * Retorna o identificador do usu�rio
	 * @return Identificado do usu�rio
	 */
	public int getId() {
		return id;
	}

	/**
	 * Modifica o identificador do usu�rio
	 * @param id Identificador para altera��o
	 */
	public void setId(int id) {
		this.id = id;
	}

	@JsonIgnore
	public String getToken() {
		return this.token;
	}
	
	public void setToken(String token){
		this.token = token;
	}
	
	/**
	 * Retorna o nome do usu�rio
	 * @return Nome do usu�rio
	 */
	public String getName() {
		return name;
	}

	/**
	 * Modifica o nome do usu�rio
	 * @param name Nome para altera��o
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retorna o <em>login</em> do usu�rio
	 * @return <em>Login</em> do usu�rio
	 */
	@JsonIgnore
	public String getLogin() {
		return login;
	}

	/**
	 * Modifica o <em>login</em> do usu�rio
	 * @param login <em>Login</em> para altera��o
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * Retorna a senha do usu�rio
	 * @return Senha do usu�rio
	 */
	@JsonIgnore
	public String getPassword() {
		return password;
	}

	/**
	 * Modifica a senha do usu�rio
	 * @param password Senha para altera��o
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Retorna o <em>e-mail</em> do usu�rio
	 * @return <em>E-mail</em> do usu�rio
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Modifica o <em>e-mail</em> do usu�rio
	 * @param email <em>E-mail</em> para altera��o
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Retorna a companhia do usu�rio
	 * @return Companhia do usu�rio
	 */
	@JsonIgnore
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * Modifica a companhia do usu�rio
	 * @param companyName Companhia para altera��o
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * Retorna o tipo do usu�rio em quest�o
	 * @return Tipo do usu�rio
	 */
	@JsonIgnore
	public UserType getUserType() {
		return userType;
	}

	/**
	 * Modifica o tipo do usu�rio em quest�o
	 * @param userType Tipo de usu�rio para altera��o
	 */
	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	/**
	 * Retorna os grupos de usu�rio aos quais o usu�rio em quest�o est� associado
	 * @return Conjunto de grupos de usu�rio aos quais o usu�rio est� associado
	 */
	@JsonIgnore
	public Set<UserGroup> getUserGroups() {
		if (userGroups == null) {
			userGroups = new HashSet<UserGroup>();
		}
		return userGroups;
	}

	/**
	 * Modifica o conjunto de grupos de usu�rio aos quais o usu�rio em quest�o
	 * est� associado
	 * @param userGroups Conjunto de grupos de usu�rio para altera��o
	 */
	public void setUserGroups(Set<UserGroup> userGroups) {
		this.userGroups = userGroups;
	}	
	
	/**
	 * Verifica se dois usu�rios s�o iguais com base nos seus identificadores
	 * @param other Usu�rio a ser comparado com o usu�rio em quest�o
	 * @return <code>true</code> (verdadeiro) se os identificadores dos 
	 * 		   	usu�rios s�o iguais, e <code>falso</code> (falso) em caso 
	 * 			contr�rio
	 */
	@Override
	public boolean equals(Object other) {
		return other instanceof User && (id != 0) ?
				id == (((User) other).id) : (other == this);
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
