package br.com.ecodif.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Modelo de uma especificação na linguagem EEML 
 * (<em>Extended Environments Markup Language</em>).<br/>
 * Classe gerada com base no XML Schema da linguagem EEML: 
 * {@link http://eeml.org/xsd/0.5.1/0.5.1.xsd}
 * 
 * @see {@link http://eeml.org/}
 * 
 * @author Bruno Costa
 */
@Entity
@Table(name = "Eeml")
public class Eeml {
	
	/** 
	 * Identificador da especificação, gerado automaticamente quando é 
	 * persistida em banco de dados
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int iddb;
	
	/** 
	 * Lista de <em>feeds</em> vinculados à especificação em questão.<br/>
	 * @see br.com.ecodif.domain.Environment
	 */
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="eeml_iddb")
	private List<Environment> environment;
	
	/** Versão da especificação */
	private String version;
		
	
	/**
     * Retorna o identificador da especificação
     * @return Identificador da especificação
     */
	public int getIddb() {
		return iddb;
	}
	
	/**
	 * Modifica o identificador do <em>feed</em>
	 * @param iddb Identificador para alteração
	 */
	public void setIddb(int id) {
		this.iddb = id;
	}
	
	/**
	 * Retorna a lista de <em>feeds</em> vinculados à especificação em questão
	 * @return Lista de <em>feeds</em> vinculados à especificação em questão
	 */
	public List<Environment> getEnvironment() {
		if (environment == null) {
            environment = new ArrayList<Environment>();
        }
        return this.environment;
	}
	
	/**
	 * Modifica a lista de <em>feeds</em> vinculados à especificação em questão
	 * @param Lista de <em>feeds</em> para alteração
	 */
	public void setEnvironment(List<Environment> environment) {
		this.environment = environment;
	}
	
	/**
	 * Retorna a versão da especificação
	 * @return Versão da especificação
	 */
	public String getVersion() {
		return version;
	}
	
	/**
	 * Modifica a versão da especificação
	 * @param version Versão para alteração
	 */
	public void setVersion(String version) {
		this.version = version;
	}	
}
