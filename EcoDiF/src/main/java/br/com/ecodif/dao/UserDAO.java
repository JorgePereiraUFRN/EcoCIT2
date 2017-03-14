package br.com.ecodif.dao;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import br.com.ecodif.domain.User;

/**
 * Classe de acesso a dados (DAO) da entidade de domínio User que abstrai as operações sobre a respectiva 
 * tabela de aplicações no banco de dados
 * @author Bruno Costa
 *
 */
@Stateless
public class UserDAO extends GenericDAO<User> {
	
	public UserDAO(){
		super(User.class);
	}
	
	/**
	 * Verifica se um login já é utilizado
	 * @param login 
	 * @return <true> se a string enviada já é utilizada, <false> se não.
	 */
	public boolean isUsedLogin(String login){
		String strQuery = "Select u From User u WHERE u.login = :login";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("login", login);		
		return !super.findManyResults(strQuery, parameters).isEmpty();
		
	}
	
	/**
	 * Verifica se um e-mail já é utilizado
	 * @param email
	 * @return <true> se o e-mail já é utilizado, <false> se não for utilizado.
	 */
	public boolean isUsedEmail(String email){
		String strQuery = "Select u From User u WHERE u.email = :email";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("email", email);		
		return !super.findManyResults(strQuery, parameters).isEmpty();
		
	}
	
	/**
	 * Recupera um usuário segundo o login
	 * @param login
	 * @return objeto do tipo User
	 */
	public User findByLogin(String login){
		String strQuery = "Select u From User u WHERE u.login = :login";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("login", login);		
		return super.findOneResult(strQuery, parameters);
		
	}
	
	/**
	 * Recupera uma lista de usuários com base no tipo de usuário
	 * @param role
	 * @return lista de usuários
	 */
	public List<User> findByUserType(String role){
		String strQuery = "Select u From User u inner join u.userType t WHERE t.role = :role";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("role", role);		
		return super.findManyResults(strQuery, parameters);
		
	}
	
	/**
	 * Recupera uma lista de usuários com base no nome e tipo.
	 * @param name
	 * @param role
	 * @param loginCreator os usuários retornados não deverão ter o login igual ao argumento enviado no loginCreator 
	 * @return lista de usuários
	 */
	public List<User> findByLoginAndRole(String name, String role, String loginCreator){
		String strQuery = "Select u From User u inner join u.userType t WHERE t.role = :role AND u.name LIKE :name AND u.login != :loginCreator";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("role", role);
		parameters.put("name", "%" + name + "%");
		parameters.put("loginCreator", loginCreator);
		return super.findManyResults(strQuery, parameters);
		
	}
	
	/**
	 * Recupera uma lista de usuários com base no nome do grupo associado
	 * @param userGroupName
	 * @param loginCreator os usuários retornados não deverão ter o login igual ao argumento enviado no loginCreator
	 * @return
	 */
	public List<User> findByUserGroup(String userGroupName, String loginCreator){
		String strQuery = "Select u From UserGroup ug inner join ug.users u WHERE ug.name = :userGroupName AND u.login != :loginCreator ";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userGroupName", userGroupName);
		parameters.put("loginCreator", loginCreator);
		return super.findManyResults(strQuery, parameters);
	}

}

