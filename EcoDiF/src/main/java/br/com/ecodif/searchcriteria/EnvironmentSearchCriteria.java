package br.com.ecodif.searchcriteria;

import java.util.Date;

/**
 * Encapsula os critérios de busca utilizados para consulta por feeds.
 * @author Andreza Lima 
 */
public class EnvironmentSearchCriteria {
	
	
	private String unit;
	
	/** Nome do feed */
	private String title;
	
    /** <em>Status</em> do <em>feed</em> */
    private String status;
    
    /** Indicador se o <em>feed</em> é  de visualização pública (N) ou privada (S) */ 
    private String _private;
    
    /** Descri��o do <em>feed</em> */
    private String description;
    
    
    /** Data/hora de atualização do <em>feed</em> */
    private Date updated;
    
    /** Nome do criador do <em>feed</em> */
    private String creator;       
		
	private String sensor ;
	
	private Integer connectedDevice;

	
	public EnvironmentSearchCriteria() {
		setDescription(null);
		setCreator(null);
		setStatus(null);
		setUnit(null);
		setSensor(null);
		setConnectedDevice(null);
		set_private(null);
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String get_private() {
		return _private;
	}

	public void set_private(String _private) {
		this._private = _private;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getSensor() {
		return sensor;
	}

	public void setSensor(String sensor) {
		this.sensor = sensor;
	}

	public Integer getConnectedDevice() {
		return connectedDevice;
	}

	public void setConnectedDevice(Integer connectedDevice) {
		this.connectedDevice = connectedDevice;
	}
	
	

}
