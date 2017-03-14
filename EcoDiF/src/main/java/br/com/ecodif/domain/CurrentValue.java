package br.com.ecodif.domain;

import java.util.GregorianCalendar;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Modelo de um valor atual para um <em>datastream</em>.<br/>
 * Classe gerada com base no XML Schema da linguagem EEML: 
 * {@link http://eeml.org/xsd/0.5.1/0.5.1.xsd}
 * 
 * @see {@link http://eeml.org/xml/0.5.1/complete.xml}
 * 
 * @author Bruno Costa
 */
@Embeddable
public class CurrentValue {
	
	/** 
	 * Identificador gerado automaticamente quando o registro � persistido no 
	 * banco de dados
	 */
/*	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
	private int iddb;*/
	@Column(name="currentValue")
	private String value;
	private GregorianCalendar at;
	
	/**
	 * Retorna o identificador do objeto na base de dados
	 * @return Identificador do objeto na base de dados
	 *//*
	@JsonIgnore	
	public int getIddb() {
		return iddb;
	}

	*//**
	 * Modifica o identificador do objeto na base de dados
	 * @param iddb Identificador para altera��o
	 *//*
	public void setIddb(int iddb) {
		this.iddb = iddb;
	}
*/
	/**
	 * Retorna o valor atual do <em>datastream</em>
	 * @return value Valor atual do <em>datastream</em>
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Modifica o valor atual do Datasource
	 * @param value Valor para altera��o
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Retorna a data/hora da leitura do valor
	 * @return at Data/hora da leitura do valor
	 */
	public GregorianCalendar getAt() {
		return at;
	}

	/**
	 * Modifica a data/hora da leitura do valor
	 * @param at Data/hora para altera��o
	 */
	public void setAt(GregorianCalendar at) {
		this.at = at;
	}
}
