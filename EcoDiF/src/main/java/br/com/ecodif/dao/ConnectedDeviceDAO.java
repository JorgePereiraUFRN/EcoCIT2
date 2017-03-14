package br.com.ecodif.dao;

import java.util.HashMap;
import java.util.List;
import javax.ejb.Stateless;
import br.com.ecodif.domain.ConnectedDevice;

/**
 * Classe de acesso a dados (DAO) da entidade de domínio ConnectedDevice que abstrai as operações sobre a respectiva 
 * tabela de aplicações no banco de dados
 * @author Bruno Costa
 *
 */
@Stateless
public class ConnectedDeviceDAO extends GenericDAO<ConnectedDevice> {

	public ConnectedDeviceDAO() {
		super(ConnectedDevice.class);
	}
	
	/**
	 * Localizar o dispositivo conectado com base no login
	 * @param login
	 * @return Lista de dispositivos conectados relacionados ao login de usuário 
	 */
	public List<ConnectedDevice> findConnectedDevicesByUser(String login){
		String strQuery = "Select c From ConnectedDevice c left join fetch c.user u WHERE u.login = :login";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("login", login);		
		return super.findManyResults(strQuery, parameters);
	}
	
	/**
	 * Verifica se o dispositivo conectado tem <i>Environments</i> associados 
	 * @param id identificador do dispositivo conectado
	 * @return <true> caso existam <i>Environments</i> associados, <false> caso não existam <i>Environments</i>.  
	 */
	public boolean hasEnvironments(int id){
		String strQuery = "Select c From ConnectedDevice c join c.environments u WHERE c.id = :id";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", id);	
		return !super.findManyResults(strQuery, parameters).isEmpty();
	}
	
	/**
	 * Localiza dispositivos conectados e os recupera juntamente com os <i>Environments</i> associados.
	 * @param id identificador do dispositivo conectado
	 * @return objeto do tipo ConnectedDevice
	 */
	public ConnectedDevice findConnectedDeviceWithEnvironments(int id){
		String strQuery = "Select c From ConnectedDevice c left join fetch c.environments u WHERE c.id = :id";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", id);		
		return super.findOneResult(strQuery, parameters);
	}
	
	/**
	 * Recupera uma lista de dispositivos conectados por nome e login de usuário
	 * @param name
	 * @param user
	 * @return lista de dispositivos conectados
	 */
	public List<ConnectedDevice> findConnectedDevicesByNameAndUser(String name, String user){
		String strQuery = "Select c From ConnectedDevice c left join c.user u WHERE c.name LIKE :name AND u.login =:login";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", "%" + name + "%");
		parameters.put("login", user);
		return super.findManyResults(strQuery, parameters);
	}
	
	/**
	 * Recupera um ConnectedDevice relacionado a um Environment
	 * @param idEnvironment
	 * @return ConnectedDevice
	 */
	public ConnectedDevice findByEnvironment(int idEnvironment){
		String strQuery = "Select c From ConnectedDevice c left join c.environments e WHERE e.iddb = :idEnvironment";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idEnvironment", idEnvironment);		
		return super.findOneResult(strQuery, parameters);
	}
	
}
