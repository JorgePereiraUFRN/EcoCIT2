package br.com.ecodif.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Modelo de um <em>trigger</em>, elemento que dispara mensagens de notificação
 * com base em eventos
 * @author Bruno Costa
 */
@Entity
@Table(name = "TriggerFeed")
public class Trigger {

	/** 
	 * Identificador do <em>trigger</em>, gerado automaticamente quando o 
	 * <em>trigger</em> é persistido em banco de dados
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	/** Condição de disparo do <em>trigger</em> */
	@Column(name = "conditiontrigger")
	private String condition;
	
	private String numberCondition;
	
	/** 
	 * <em>E-mail</em> de notificação cadastrado pelo criador do 
	 * <em>trigger</em>
	 */
	@Column(name = "emailCreator")
	private String email;
	
	@Column(nullable=true)
	private String gcmId;

	/**
	 * <em>Feed</em> ao qual o <em>trigger</em> está vinculado
	 * @see br.com.ecodif.domain.Environment
	 */
	@ManyToOne
	@JoinColumn(name = "environmentId")
	private Environment environment;

	
	/**
	 * Retorna o identificador do <em>trigger</em>
	 * @return Identificador do <em>trigger</em>
	 */
	public int getId() {
		return id;
	}

	/**
	 * Modifica o identificador do <em>trigger</em>
	 * @param id Identificador para alteração
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Retorna a condição de disparo do <em>trigger</em>
	 * @return Condição de disparo do <em>trigger</em>
	 */
	public String getCondition() {
		return condition;
	}

	/**
	 * Modifica a condição de disparo do <em>trigger</em>
	 * @param condition Condição de disparo para alteração
	 */
	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getNumberCondition() {
		return numberCondition;
	}

	public void setNumberCondition(String numberCondition) {
		this.numberCondition = numberCondition;
	}

	/**
	 * Retorna o <em>e-mail</em> de notificação cadastrado pelo criador do
	 * <em>trigger</em>
	 * @return <em>E-mail</em> de notificação cadastrado pelo criador do
	 * 			<em>trigger</em>
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Modifica o <em>e-mail</em> de notificação cadastrado pelo criador do
	 * <em>trigger</em>
	 * @param email <em>E-mail</em> para alteração
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getGCMId() {
		return gcmId;
	}

	public void setGCMId(String gcmId) {
		this.gcmId = gcmId;
	}

	/**
	 * Retorna o <em>feed</em> ao qual o <em>trigger</em> está vinculado
	 * @return <em>Feed</em> ao qual o <em>trigger</em> está vinculado
	 */
	public Environment getEnvironment() {
		if (environment == null) {
			environment = new Environment();
		}
		return environment;
	}

	/**
	 * Modifica o <em>feed</em> ao qual o <em>trigger</em> está vinculado
	 * @param environment <em>Feed</em> para alteração
	 */
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
}
