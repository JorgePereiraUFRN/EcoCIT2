package br.com.ecodif.dao;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;

import br.com.ecodif.domain.Device;

/**
 * Classe de acesso a dados (DAO) da entidade de domínio Device que abstrai as operações sobre a respectiva 
 * tabela de aplicações no banco de dados
 * @author Bruno Costa
 *
 */
@Stateless
public class DeviceDAO extends GenericDAO<Device>{

	public DeviceDAO() {
		super(Device.class);
	}
	
	/**
	 * Recupera dispositivos pelo login do usuário
	 * @param login
	 * @return lista de dispositivos cadastrados pelo usuário
	 */
	public List<Device> findDevicesByUser(String login){
		String strQuery = "Select d From Device d left join fetch d.company c WHERE c.login = :login";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("login", login);		
		return super.findManyResults(strQuery, parameters);
	}
	
	/**
	 * Recupera dispositivos (objetos do tipo <i>Device</i>) de acordo com o login e nome do usuário responsável 
	 * pelos cadastro
	 * @param login login de usuário
	 * @param name nome do usuário
	 * @return lista de dispositivos
	 */
	public List<Device> findDevicesByUserAndName(String login, String name){
		String strQuery = "Select d From Device d left join d.company c WHERE c.login = :login AND d.name LIKE :name";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("login", login);
		parameters.put("name", "%" + name + "%");
		return super.findManyResults(strQuery, parameters);
	}
	
	/**
	 * Recupera dispositivos segundo o identificador do <i>Environment</i> associado.
	 * @param idEnvironment identificador do <i>Environment</i>
	 * @return <i>Device</i>
	 */
	public Device findDeviceByEnvironmentId(int idEnvironment){
		String strQuery = "Select c.device From ConnectedDevice c join c.environments e WHERE e.iddb = :idEnvironment";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idEnvironment", idEnvironment);		
		return super.findOneResult(strQuery, parameters);
	}
	
	/**
	 * Recupera um dispositivo segundo o seu nome 
	 * @param nameDevice
	 * @return Device
	 */
	public Device findDeviceByName(String nameDevice){
		String strQuery = "Select d From Device d WHERE d.name LIKE :nameDevice";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("nameDevice", "%" + nameDevice + "%");		
		return super.findOneResult(strQuery, parameters);
	}
	
	/**
	 * Recupera uma lista de dispositivos segundo o nome
	 * @param nameDevice
	 * @return Lista de dispositivos
	 */
	public List<Device> findAllDeviceByName(String nameDevice){
		String strQuery = "Select d From Device d WHERE d.name LIKE :nameDevice";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("nameDevice", "%" + nameDevice + "%");		
		return super.findManyResults(strQuery, parameters);
	}
}