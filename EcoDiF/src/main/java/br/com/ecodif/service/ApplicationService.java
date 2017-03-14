package br.com.ecodif.service;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import br.com.ecodif.dao.ApplicationDAO;
import br.com.ecodif.dao.UserDAO;
import br.com.ecodif.domain.Application;
import br.com.ecodif.domain.User;
import br.com.ecodif.searchcriteria.ApplicationSearchCriteria;

public class ApplicationService {

	@Inject
	protected UserDAO userDAO = new UserDAO();

	@Inject
	protected ApplicationDAO applicationDAO = new ApplicationDAO();

	
	public void updateApplication(Application application) {
		applicationDAO.update(application);
	}
	
	public List<Application> findPublicApplications(boolean isPrivate) {
		return applicationDAO.findPublicApplications(isPrivate);
	}
	
	public List<Application> findApplicationsByUser(String login) {
		User user = userDAO.findByLogin(login);
		return applicationDAO.findApplicationsByUserId(user.getId());
	}
	
	public Application findById(int id) {
		return applicationDAO.find(id);				
	}

	public Set<Application> findApplicationsByCriteria(ApplicationSearchCriteria criteria) {
		return applicationDAO.findApplicationsByCriteria(criteria);				
	}
	
	public void deleteApplication(Application application) {
		applicationDAO.delete(application);
	}
	

	public boolean existAppName(Application application) {
		if (applicationDAO.findApplicationByName(application.getName()) != null)
			return true;
		return false;
	}
	
	public void saveApplication(Application application) {
		applicationDAO.save(application);
	}
}
