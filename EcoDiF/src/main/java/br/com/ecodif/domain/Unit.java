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
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Modelo de uma unidade de medida.<br/>
 * Classe gerada com base no XML Schema da linguagem EEML: 
 * {@link http://eeml.org/xsd/0.5.1/0.5.1.xsd}
 * 
 * @see {@link http://eeml.org/xml/0.5.1/complete.xml}
 * 
 * @author Bruno Costa
 */
@Entity
@XmlRootElement(name = "unit")
@XmlAccessorType(XmlAccessType.FIELD)
@Table(name = "Unit")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Unit {

	/** 
	 * Identificador da unidade, gerada automaticamente quando a unidade � 
	 * persistida em banco de dados
	 */
	@Id
	@XmlTransient
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int iddb;
	
	/** Valor da unidade */
	private String value;
	
	/** S�mbolo da unidade */
	private String symbol;
	
	/** Tipo descritivo da unidade */
	private String type;

	
	/**
     * Retorna o identificador da unidade
     * @return Identificador da unidade
     */
	@JsonIgnore
	public int getIddb() {
		return iddb;
	}

	/**
	 * Modifica o identificador da unidade
	 * @param iddb Identificador para altera��o
	 */
	public void setIddb(int iddb) {
		this.iddb = iddb;
	}

	/**
	 * Retorna o valor da unidade
	 * @return Valor da unidade
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Modifica o valor da unidade
	 * @param value Valor para altera��o
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Retorna o s�mbolo da unidade
	 * @return S�mbolo da unidade
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * Modifica o s�mbolo da unidade
	 * @param symbol S�mbolo para altera��o
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	/**
	 * Retorna o tipo descritivo da unidade
	 * @return Tipo descritivo da unidade
	 */
	public String getType() {
		return type;
	}

	/**
	 * Modifica o tipo descritivo da unidade
	 * @param type Tipo descritivo para altera��o
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Verifica se duas unidades s�o iguais com base nos seus identificadores
	 * @param other Unidade a ser comparada com a unidade em quest�o
	 * @return <code>true</code> (verdadeiro) se os identificadores das
	 * 			unidades s�o iguais, e <code>falso</code> (falso) em caso 
	 * 			contr�rio
	 */
	@Override
	public boolean equals(Object other) {		
		return other instanceof Unit && (iddb != 0) ? 
				iddb == (((Unit) other).iddb) : (other == this);
	}
	
	/** 
	 * Retorna o c�digo <em>hash</em> do objeto
	 * @return C�digo <em>hash</em> do objeto
	 */
	@Override
	public int hashCode() {
		return getIddb() * 8;
	}
}
