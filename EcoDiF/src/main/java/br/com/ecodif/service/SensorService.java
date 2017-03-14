package br.com.ecodif.service;

import java.util.List;

import javax.inject.Inject;

import br.com.ecodif.dao.SensorDAO;
import br.com.ecodif.domain.Sensor;
import br.com.ecodif.framework.EemlManager;

public class SensorService {


	@Inject
	private EemlManager eemlMng = new EemlManager();
	
	@Inject
	private SensorDAO sensorDAO = new SensorDAO();

	public void saveSensor(Sensor sensor) {
		eemlMng.saveSensor(sensor);
	}
	
	public List<Sensor> getAllSensors(){
		return sensorDAO.findAll();
	}

}
