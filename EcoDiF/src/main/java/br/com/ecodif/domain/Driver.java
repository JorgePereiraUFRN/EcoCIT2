package br.com.ecodif.domain;

import java.sql.Blob;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Modelo de um <em>driver</em>
 * @author Bruno Costa
 */
@Entity
@Table(name = "Driver")
public class Driver {

	/**
	 * Identificador do <em>driver</em>, gerado automaticamente quando o 
	 * <em>driver</em> � persistido em banco de dados
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	/** Nome do <em>driver</em> */
	private String name;
	
	/** Descri��o do <em>driver</em> */
	private String description;
	
	/** Vers�o do <em>driver</em> */
	private String version;
	

	private Blob driverFile;
	
	private String fileName;
	
	/** Data da �ltima atualiza��o do <em>driver</em> */
	private Date lastUpdate;
	
	/** 
	 * Lista de plataformas �s quais o driver oferece suporte
	 * @see br.com.ecodif.domain.Platform
	 */
	@OneToMany
	@JoinColumn(name="driver_id")
	private List<Platform> platforms;
	
	
	/**
	 * Retorna o identificador do <em>driver</em>
	 * @return Identificador do <em>driver</em>
	 */
	public int getId() {
		return id;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Modifica o identificador do <em>driver</em>
	 * @param id Identificador para altera��o
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Retorna o nome do <em>driver</em>
	 * @return Nome do <em>driver</em>
	 */
	public String getName() {
		return name;
	}
	
	/** 
	 * Modifica o nome do <em>driver</em>
	 * @param name Nome para altera��o
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Retorna a descri��o do <em>driver</em>
	 * @return Descri��o do <em>driver</em>
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Modifica a descri��o do <em>driver</em>
	 * @param description Descri��o para altera��o
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Retorna a vers�o do <em>driver</em>
	 * @return Vers�o do <em>driver</em>
	 */
	public String getVersion() {
		return version;
	}
	
	/**
	 * Modifica a vers�o do <em>driver</em>
	 * @param version Vers�o para altera��o
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	


	public Blob getDriverFile() {
		return driverFile;
	}

	public void setDriverFile(Blob driverFile) {
		this.driverFile = driverFile;
	}

	/**
	 * Retorna a data da �ltima atualiza��o do <em>driver</em>
	 * @return Data da �ltima atualiza��o do <em>driver</em>
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}
	
	/**
	 * Modifica��o da data da �ltima atualiza��o do <em>driver</em>
	 * @param lastUpdate Data para altera��o
	 */
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	/**
	 * Retorna a lista de plataformas �s quais o <em>driver</em> oferece
	 * suporte
	 * @return Lista de plataformas �s quais o <em>driver</em> oferece suporte
	 */
	public List<Platform> getPlatforms() {
		if (platforms == null) {
			platforms = new ArrayList<Platform>();
		}
		return platforms;
	}
	
	/**
	 * Modifica a lista de plataformas �s quais o <em>driver</em> oferece
	 * suporte
	 * @param platforms Lista de plataformas para altera��o
	 */
	public void setPlatforms(List<Platform> platforms) {
		this.platforms = platforms;
	}
}
