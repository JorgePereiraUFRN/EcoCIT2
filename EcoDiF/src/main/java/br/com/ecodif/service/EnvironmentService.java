package br.com.ecodif.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.datatype.DatatypeConfigurationException;

import br.com.ecodif.dao.SensorDAO;
import br.com.ecodif.dao.TriggerDAO;
import br.com.ecodif.domain.Data;
import br.com.ecodif.domain.Eeml;
import br.com.ecodif.domain.Environment;
import br.com.ecodif.domain.Sensor;
import br.com.ecodif.domain.Trigger;
import br.com.ecodif.eeml_contract.Eeml_Contract;
import br.com.ecodif.framework.EemlManager;
import br.com.ecodif.searchcriteria.EnvironmentSearchCriteria;

@Stateless
public class EnvironmentService {

	@Inject
	private SensorDAO sensorDAO = new SensorDAO();

	@Inject
	private TriggerDAO triggerDAO = new TriggerDAO();

	@Inject
	private EemlManager eemlMng = new EemlManager();

	public Environment findEnvironmentById(int id) {
		return eemlMng.findEnvironmentById(id);
	}

	public Eeml findEemlByIddbEnvironment(int id) {
		return eemlMng.findEemlByIddbEnvironment(id);
	}

	public Eeml_Contract eemlDomainToContract(Eeml domain)
			throws DatatypeConfigurationException {
		return eemlMng.eemlDomainToContract(domain);
	}

	public Data findDataWithDatapoints(int id) {
		return eemlMng.findDataWithDatapoints(id);
	}

	public void deleteEnvironment(Environment environment) {
		eemlMng.deleteEnvironment(environment);
	}

	public void saveEeml(Eeml eeml) {
		eemlMng.saveEeml(eeml);
	}

	public Sensor findSensorWithDatas(int idSensor) {
		return sensorDAO.findSensorWithDatas(idSensor);
	}

	public void updateSensor(Sensor sensor) {
		sensorDAO.update(sensor);
	}

	public void updateEnvironment(Environment environment) {
		eemlMng.updateEnvironment(environment);
	}

	public List<Environment> findEnvironmentsByUserLogin(String login) {
		return eemlMng.findEnvironmentsByUserLogin(login);
	}

	public List<Eeml_Contract> findEemlEnvByUser(String login) throws DatatypeConfigurationException {		
		List<Eeml> environments = eemlMng.findEemlByUserName(login);
		List<Eeml_Contract> eemls = new ArrayList<Eeml_Contract>();
		for (Eeml eeml : environments) {
			eemls.add(eemlDomainToContract(eeml));
		}
		return eemls;
	}
	public List<Eeml_Contract> findEemlEnvironmentsByCriteria(
			EnvironmentSearchCriteria criteria) {
		return eemlMng.findEemlEnvironmentsByCriteria(criteria);
	}

	public Eeml eemlContractToDomain(Eeml_Contract eeml_Contract) {
		return eemlMng.eemlContractToDomain(eeml_Contract);

	}

	public void updateData(Data data) {
		eemlMng.updateData(data);
	}

	public Trigger findTriggerByEnvironmentIddb(int id) {
		return triggerDAO.findByEnvironmentIddb(id);
	}

	public String findEnvironmentNameById(int id) {
		return eemlMng.findEnvironmentNameById(id);
	}

	public Eeml findEemlByIddbEnvironmentIdData(int idEnvironment, int idData ){
		return eemlMng.findEemlByIddbEnvironmentIdData(idEnvironment, idData);
	}

}
