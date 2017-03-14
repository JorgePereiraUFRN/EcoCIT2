package br.com.ecodif.domain;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

/**
 * Modelo de um grupo de usuários na EcoDiF
 * @author Bruno Costa
 */
@Entity
public class UserGroup {

	/** 
	 * Identificador do grupo de usuários, gerado automaticamente quando o 
	 * grupo é persistido em banco de dados
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	/** Nome do grupo de usuários */
	private String name;
	
	/** Nome do criador do grupo de usuários */
	private String creator;
	
	/** 
	 * Referência para o <em>feed</em> que pode ser acessado pelos usuários
	 * pertencentes ao grupo em questão
	 * @see br.com.ecodif.domain.Environment
	 */
	@ManyToOne
	private Environment environment;
	
	/**
	 * Conjunto de usuários pertencentes ao grupo em questão.<br/>
	 * @see br.com.ecodif.domain.User
	 */
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="User_UserGroup", 
	   joinColumns={@JoinColumn(name="userGroup_id", referencedColumnName="id")}, 
	   inverseJoinColumns={@JoinColumn(name="user_id", referencedColumnName="id")})
	private Set<User> users;

	
	/**
	 * Retorna o identificador do grupo de usuários
	 * @return Identificador do grupo de usuários
	 */
	public int getId() {
		return id;
	}

	/**
	 * Modifica o identificador do grupo de usuários
	 * @param id Identificador para alteração
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Retorna o nome do grupo de usuários
	 * @return Nome do grupo de usuários
	 */
	public String getName() {
		return name;
	}

	/**
	 * Modifica o nome do grupo de usuários
	 * @param name Nome para alteração
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retorna a referência para o <em>feed</em> que pode ser acessado pelos 
	 * usuários pertencentes ao grupo em questão
	 * @return Referência para o <em>feed</em>
	 */
	public Environment getEnvironment() {
		return environment;
	}

	/**
	 * Modifica a referência para o <em>feed</em> que pode ser acessado pelos 
	 * usuários pertencentes ao grupo em questão
	 * @param environment Referência para alteração
	 */
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	/**
	 * Retorna o nome do criador do grupo de usuários
	 * @return Nome do criador do grupo de usuários
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * Modifica o nome do criador do grupo de usuários
	 * @param creator Nome para alteração
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * Retorna a lista de usuários pertencentes ao grupo de usuários em questão
	 * @return Lista de usuários pertencentes ao grupo de usuários em questão
	 */
	public Set<User> getUsers() {
		if (users == null) {
			users = new HashSet<User>();
		}
		return users;
	}

	/**
	 * Modifica a lista de usuários pertencentes ao grupo de usuários em questão
	 * @param users Lista de usuários para alteração
	 */
	public void setUsers(Set<User> users) {
		this.users = users;
	}

	/**
	 * Verifica se dois grupos de usuários são iguais com base nos seus 
	 * identificadores
	 * @param other Grupo de usuários a ser comparado com o grupo de usuários 
	 * 			em questão
	 * @return <code>true</code> (verdadeiro) se os identificadores dos grupos 
	 * 		   	de usuários são iguais, e <code>falso</code> (falso) em caso 
	 * 			contrário
	 */
	@Override
	public boolean equals(Object other) {
		return other instanceof UserGroup && (id != 0) ? 
				id == (((UserGroup) other).id) : (other == this);
	}
	
	/**
	 * Retorna um código <em>hash</em> para o objeto
	 * @return Código <em>hash</em> para o objeto
	 */
	@Override
	public int hashCode() {
		return getId() * 8;
	}
}
