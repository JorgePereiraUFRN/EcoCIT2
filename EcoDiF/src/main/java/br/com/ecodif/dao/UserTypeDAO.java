package br.com.ecodif.dao;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import br.com.ecodif.domain.UserType;

/**
 * Classe de acesso a dados (DAO) da entidade de domínio UserType que abstrai as operações sobre a respectiva 
 * tabela de aplicações no banco de dados
 * @author Bruno Costa
 *
 */
@Stateless
public class UserTypeDAO extends GenericDAO<UserType>{
	
	public UserTypeDAO(){
		super(UserType.class);
	}
	
	/**
	 * Recupera um objeto do tipo UserType segundo o seu tipo
	 * @param role
	 * @return objeto do tipo UserType
	 */
	public UserType findByRole(String role){
		String strQuery = "Select u From UserType u WHERE u.role = :role";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("role", role);		
		return super.findOneResult(strQuery, parameters);
		
	}
	
	/**
	 * Recupera uma lista de objetos do tipo UserType que não sejam de um tipo específico
	 * @param userType tipo de usuários que não serão retornados
	 * @return lista de ojetos do tipo UserType
	 */
	public List<UserType> findWithoutSpecUserType(String userType){
		String strQuery = "Select u From UserType u WHERE u.role != :userType";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userType", userType);		
		return super.findManyResults(strQuery, parameters);
		
	}

}
