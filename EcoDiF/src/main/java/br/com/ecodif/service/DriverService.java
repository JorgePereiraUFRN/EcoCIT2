package br.com.ecodif.service;

import javax.inject.Inject;

import br.com.ecodif.dao.DriverDAO;
import br.com.ecodif.domain.Driver;

public class DriverService {

	@Inject
	private DriverDAO driverDAO = new DriverDAO();
	
	public void update(Driver driver){
		driverDAO.update(driver);
	}
	
	public void save(Driver driver){
		driverDAO.save(driver);
	}

}
