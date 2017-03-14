package br.com.ecodif.service;

import javax.inject.Inject;

import br.com.ecodif.dao.UserDAO;
import br.com.ecodif.dao.UserTypeDAO;
import br.com.ecodif.domain.User;
import br.com.ecodif.domain.UserType;

public class UserService {

	
	@Inject
	private UserDAO userDAO = new UserDAO();

	@Inject
	private UserTypeDAO usertypeDAO = new UserTypeDAO();
	
	
	
	public boolean isUsedLogin(String login){
		return userDAO.isUsedLogin(login);
	}
	
	public boolean isUsedEmail(String email) {
		return userDAO.isUsedEmail(email);
	}
	
	public UserType findTypeById(int id) {
		return usertypeDAO.find(id);
	}
	
	public void saveUser(User user) {
		userDAO.save(user);
	}
	
	public User findUserByLogin(String login) {
		return userDAO.findByLogin(login);
	}
	
	public String findUserToken(String login) {
		User user = userDAO.findByLogin(login);
		return user.getToken();
	}
}
