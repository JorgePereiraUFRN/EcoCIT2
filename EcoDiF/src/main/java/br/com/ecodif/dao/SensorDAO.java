package br.com.ecodif.dao;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;

import br.com.ecodif.domain.Sensor;

/**
 * Classe de acesso a dados (DAO) da entidade de domínio Sensor que abstrai as operações sobre a respectiva 
 * tabela de aplicações no banco de dados
 * @author Bruno Costa
 *
 */
@Stateless
public class SensorDAO extends GenericDAO<Sensor>{
	
	public SensorDAO(){
		super(Sensor.class);
	}
	
	/**
	 * Recupera uma lista de sensores segundo o identificador do dispositivo associado
	 * @param idDevice
	 * @return lista de sensores
	 */
	public List<Sensor> findSensorsByDevice(int idDevice){
		String strQuery = "Select distinct d.sensors From Device d join d.sensors s WHERE d.id = :id";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", idDevice);		
		return super.findManyResults(strQuery, parameters);
	}
	
	/**
	 * Recupera um sensor com objetos do tipo Data associados
	 * @param idSensor
	 * @return objeto do tipo Sensor
	 */
	public Sensor findSensorWithDatas(int idSensor){
		String strQuery = "Select s From Sensor s left join fetch s.datas d WHERE s.id = :id";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", idSensor);		
		return super.findOneResult(strQuery, parameters);
	}
	
	/**
	 * Recupera um sensor com objetos do tipo Data segundo a descrição do sensor
	 * @param description
	 * @return objeto do tipo Sensor
	 */
	public Sensor findSensorWithDatasByName(String description){
		String strQuery = "Select s From Sensor s left join fetch s.datas d WHERE s.description LIKE :description";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("description", "%" + description + "%");		
		return super.findOneResult(strQuery, parameters);
	}
	
	/**
	 * Recupera um sensor com base no identificador do objeto do tipo Data associado
	 * @param idDataStream
	 * @return objeto do tipo Sensor
	 */
	public Sensor findSensorByDatastream(int idDataStream){
		String strQuery = "Select s From Sensor s join s.datas d WHERE d.iddb = :idDataStream";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idDataStream", idDataStream);		
		return super.findOneResult(strQuery, parameters);
	}
	
	/**
	 * Recupera um sensor com objetos do tipo Data associados segundo o identificador do objeto Data
	 * @param idDataStream
	 * @return objeto do tipo Sensor
	 */
	public Sensor findSensorByDatastreamWithData(int idDataStream){
		String strQuery = "Select s From Sensor s join fetch s.datas d WHERE d.iddb = :idDataStream";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idDataStream", idDataStream);		
		return super.findOneResult(strQuery, parameters);
	}
	

}
