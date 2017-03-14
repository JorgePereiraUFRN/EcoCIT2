package br.com.ecodif.dao;

import java.util.HashMap;
import java.util.List;
import javax.ejb.Stateless;
import br.com.ecodif.domain.UserGroup;

/**
 * Classe de acesso a dados (DAO) da entidade de domínio UserGroup que abstrai as operações sobre a respectiva 
 * tabela de aplicações no banco de dados
 * @author Bruno Costa
 *
 */
@Stateless
public class UserGroupDAO extends GenericDAO<UserGroup>{
	
	public UserGroupDAO(){
		super(UserGroup.class);
	}
	
	/**
	 * Recupera uma lista de objetos do tipo UserGroup segundo o login do usuário
	 * @param userLogin
	 * @return lista de objetos do tipo UserGroup
	 */
	public List<UserGroup> findByUser(String userLogin){
		String strQuery = "SELECT userGroup FROM UserGroup userGroup WHERE userGroup.creator = :userLogin ";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userLogin", userLogin);		
		return super.findManyResults(strQuery, parameters);
	}
	
	/**
	 * Recupear uma lista objetos do tipo UserGroup segundo o identificador do objeto 
	 * do tipo Environment associado
	 * @param environmentIddb
	 * @return lista de objetos do tipo UserGroup
	 */
	public List<UserGroup> findByEnvironmentId(int environmentIddb){
		String strQuery = "SELECT userGroup FROM UserGroup userGroup WHERE userGroup.environment.iddb = :environmentIddb ";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("environmentIddb", environmentIddb);		
		return super.findManyResults(strQuery, parameters);
	}

}
