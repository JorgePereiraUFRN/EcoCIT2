package br.com.ecodif.domain;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * Modelo de um <em>datastream</em>, que representa um dado de um sensor.<br/>
 * Classe gerada com base no XML Schema da linguagem EEML: 
 * {@link http://eeml.org/xsd/0.5.1/0.5.1.xsd}
 * 
 * @see {@link http://eeml.org/xml/0.5.1/complete.xml}
 * 
 * @author Bruno Costa
 */
@XmlRootElement(name = "data")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "DataDomain")
public class Data {

	/**
	 * Identificador do <em>datastream</em> gerado automaticamente quando o 
	 * <em>datastream</em> � persistida em banco de dados
	 */
	@Id
	@XmlTransient
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int iddb;

	/** 
	 * Palavras-chave (descritores) do <em>datastream</em>, separadas por 
	 * v�rgulas 
	 */
	@ElementCollection
	@LazyCollection(LazyCollectionOption.FALSE)
	protected List<String> tag;

	/**
	 * Valor atual para o <em>datastream</em>
	 * @see br.com.ecodif.domain.CurrentValue
	 */
	//@OneToOne(cascade = CascadeType.ALL)
	@Embedded
	protected CurrentValue currentValue;

	/** Valor m�ximo para o <em>datastream</em> em quest�o */
	@Column(name = "maxValue_")
	protected String maxValue;

	/** Valor m�nimo para o <em>datastream</em> em quest�o */
	@Column(name = "minValue_")
	protected String minValue;

	/** 
	 * Lista de <em>datapoints</em> referentes ao <em>datastream</em>
	 * @see br.com.ecodif.domain.Datapoints
	 */
	/*@OneToMany(cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinColumn(name = "data_id")*/
	@Transient
	protected List<Value> values;

	/** 
	 * Unidade do <em>datastream</em>
	 * @see br.com.ecodif.domain.Unit
	 */
	@OneToOne
	protected Unit unit;
	
	/** Identificador para o <em>datastream</em> */
	protected BigInteger id;

	
	/**
	 * Retorna o identificador do objeto na base de dados
	 * @return I
	 */
	@JsonIgnore
	public int getIddb() {
		return iddb;
	}

	/**
	 * Modifica o identificador do objeto na base de dados (OBS: o identificador
	 * � gerenciado pela base de dados)
	 * 
	 * @param iddb
	 */
	public void setIddb(int iddb) {
		this.iddb = iddb;
	}

	/**
	 * Retorna as palavras-chave descritoras do Datastream (nome para a classe
	 * Data na Interface)
	 * 
	 * @return tags Palavras-chave descritoras do Datastream
	 */
	public List<String> getTag() {
		if (tag == null) {
			tag = new ArrayList<String>();
		}
		return this.tag;
	}

	/**
	 * Modifica as palavras-chave descritoras do dispositivo conectado
	 * 
	 * @param tags
	 *            Palavras-chave para altera��o
	 */
	public void setTag(List<String> tag) {
		this.tag = tag;
	}

	/**
	 * Retorna o valor atual (<i>CurrentValue</i>) do Datastream
	 * @return currentValue
	 */
	public CurrentValue getCurrentValue() {
		if (currentValue == null)
			currentValue = new CurrentValue();
		return currentValue;
	}

	/**
	 * Modifica o valor atual (<i>CurrentValue</i>) do Datastream
	 * @param currentValue para altera��o
	 */
	public void setCurrentValue(CurrentValue currentValue) {
		if (currentValue == null)
			currentValue = new CurrentValue();
		this.currentValue = currentValue;
	}

	/**
	 * Retorna o maior valor do Datastream
	 * @return maxValue
	 */
	public String getMaxValue() {
		return maxValue;
	}

	/**
	 * Modifica o maior valor  do Datastream 
	 * @param maxValue para altera��o
	 */
	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * Retorna o menor valor lido do Datastream
	 * @return minValue
	 */
	public String getMinValue() {
		return minValue;
	}

	/**
	 * Modifica o menor valor do Datastream 
	 * @param minValue
	 */
	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	/**
	 * Retorna todos os <i>Datapoints</i> da classe Data 
	 * @return datapoints
	 */
	public List<Value> getValues() {
		if (values == null){
			values = new ArrayList<Value>();
		}
		return values;
	}

	/**
	 * Modifica os <i>Datapoints</i>
	 * @param datapoints para altera��o
	 */
	public void setValues(List<Value> datapoints) {
		this.values = datapoints;
	}

	/**
	 * Retorna a unidade de medida do Datastream
	 * @return unit
	 */
	public Unit getUnit() {
		return unit;
	}

	/**
	 * Modifica a unidade de medida do Datastream
	 * @param unit para altera��o
	 */
	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	/**
	 * Retorna o identificador do Datastream. Este identificador n�o � o mesmo do banco de dados
	 * @return id
	 */
	public BigInteger getId() {
		return id;
	}

	/**
	 * Altera o identificador do Datastream
	 * @param id 
	 */
	public void setId(BigInteger id) {
		this.id = id;
	}

	
	
	

}
