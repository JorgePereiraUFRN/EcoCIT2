package br.com.ecodif.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
/*import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;*/

/**
 * Modelo de um tipo de usu�rio na EcoDiF
 * @author Bruno Costa
 */
/*@XmlRootElement(name = "UserType")
@XmlAccessorType(XmlAccessType.FIELD)*/
@Entity
@Table(name = "UserType")
public class UserType {

	/** 
	 * Identificador do tipo de usu�rio, gerado automaticamente quando o tipo
	 * de usu�rio � persistido em banco de dados
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	/** Descri��o do tipo de usu�rio */
	private String description;
	
	/** Papel do tipo de usu�rio */
	private String role;

	
	/**
	 * Retorna o identificador do tipo de usu�rio
	 * @return Identificador de tipo de usu�rio
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Modifica o identificador do tipo de usu�rio
	 * @param id Identificador para altera��o
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Retorna a descri��o do tipo de usu�rio
	 * @return Descri��o do tipo de usu�rio
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Modifica a descri��o do tipo de usu�rio
	 * @param description Descri��o para altera��o
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Retorna o papel do tipo de usu�rio
	 * @return Papel do tipo de usu�rio
	 */
	public String getRole() {
		return role;
	}
	
	/**
	 * Modifica o papel do tipo de usu�rio
	 * @param role Papel para altera��o
	 */
	public void setRole(String role) {
		this.role = role;
	}
	
	/**
	 * Verifica se dois tipos de usu�rio s�o iguais com base nos seus 
	 * identificadores
	 * @param other Tipo de usu�rio a ser comparado com o tipo de usu�rio em 
	 * 			quest�o
	 * @return <code>true</code> (verdadeiro) se os identificadores dos tipos 
	 * 		   	de usu�rio s�o iguais, e <code>falso</code> (falso) em caso 
	 * 			contr�rio
	 */
	@Override
	public boolean equals(Object other) {
		return other instanceof UserType && (id != 0) ? 
				id == (((UserType) other).id) : (other == this);
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
