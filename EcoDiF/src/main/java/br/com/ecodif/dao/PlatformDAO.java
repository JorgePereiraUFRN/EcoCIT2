package br.com.ecodif.dao;

import java.util.HashMap;

import javax.ejb.Stateless;

import br.com.ecodif.domain.Platform;

/**
 * Classe de acesso a dados (DAO) da entidade de domínio Platform que abstrai as operações sobre a respectiva 
 * tabela de aplicações no banco de dados
 * @author Bruno Costa
 *
 */
@Stateless
public class PlatformDAO extends GenericDAO<Platform> {
	
	public PlatformDAO(){
		super(Platform.class);
	}
	
	/**
	 * Recupera uma plataforma e dispositivos associados segundo o identificador da plataforma
	 * @param platform_id
	 * @return objeto do tipo Platform
	 */
	public Platform findPlatformWithDevices(int platform_id){
		String strQuery = "Select p From Platform p left join fetch p.devices d WHERE p.id = :id";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", platform_id);		
		return super.findOneResult(strQuery, parameters);
	}
	
	/**
	 * Recupera uma plataforma segundo o identificador do dispositivo associado
	 * @param deviceId
	 * @return objeto do tipo Platform
	 */
	public Platform findPlatformByDevice(int deviceId){
		String strQuery = "Select p From Platform p left join p.devices d WHERE d.id = :id";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", deviceId);		
		return super.findOneResult(strQuery, parameters);
	}
}
