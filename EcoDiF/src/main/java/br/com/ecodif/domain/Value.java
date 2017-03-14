package br.com.ecodif.domain;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;



@Entity
@Table(name = "values")
public class Value {
	
	/** Valor propriamente dito */
	private String value;
	
	/** Data/hora de mensura��o do valor */
	
	
	private Date at;
	
	@Id
	@Column(name="data_id")
	private int dataId;
	

	public int getDataId() {
		return dataId;
	}

	public void setDataId(int dataId) {
		this.dataId = dataId;
	}


	/**
	 * Retorna o valor propriamente dito
	 * @return Valor propriamente dito
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Modifica o valor propriamente dito
	 * @param value Valor para altera��o
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * Retorna a data/hora de mensura��o do valor
	 * @return Data/hora de mensura��o do valor
	 */
	public Date getAt() {
		return at;
	}
	
	/**
	 * Modifica a data/hora de mensura��o do valor
	 * @param at Data/hora para altera��o
	 */
	public void setAt(Date at) {
		this.at = at;
	}

	@Override
	public String toString() {
		return "Value [value=" + value + ", at=" + at
				+ ", dataId=" + dataId + "]";
	}

	
	
	
}
