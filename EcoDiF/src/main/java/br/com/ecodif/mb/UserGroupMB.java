package br.com.ecodif.mb;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import br.com.ecodif.dao.UserDAO;
import br.com.ecodif.dao.UserGroupDAO;
import br.com.ecodif.domain.User;
import br.com.ecodif.domain.UserGroup;

@ManagedBean
@ViewScoped
public class UserGroupMB {

	private UserGroup userGroup;
	private List<User> users;
	private String userNameSearch;
	private UserGroup userGroupSelected;
	private List<UserGroup> userGroupsByUser;
	private Boolean isNew;
	private List<User> usersSearch;

	@EJB
	private UserDAO userDao;

	@EJB
	private UserGroupDAO userGroupDao;

	@ManagedProperty("#{userMB}")
	private UserMB userMB;

	@PostConstruct
	public void init() {
		userGroupsByUser = userGroupDao.findByUser(userMB.getUserName());
		String userGroupName = "";

		if (userGroupsByUser.size() > 0) {
			userGroupName = userGroupsByUser.get(0).getName();
			userGroupSelected = userGroupsByUser.get(0);
		}

		users = userDao.findByUserGroup(userGroupName, userMB.getUserName());
		usersSearch = new ArrayList<User>();
	}

	public void setUserMB(UserMB userMB) {
		this.userMB = userMB;
	}

	public UserGroup getUserGroup() {
		if (userGroup == null)
			userGroup = new UserGroup();
		return userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	public List<User> getUsers() {
		if (users == null)
			users = userDao.findByUserGroup(userGroupSelected.getName(),
					userMB.getUserName());
		return users;
	}

	public List<User> getUsersSearch() {
		if (usersSearch == null)
			usersSearch = new ArrayList<User>();
		return usersSearch;
	}

	public boolean isUserSearchEmpty() {
		return getUsersSearch().isEmpty();
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public String getUserNameSearch() {
		return userNameSearch;
	}

	public void setUsersSearch(List<User> usersSearch) {
		this.usersSearch = usersSearch;
	}

	public void setUserNameSearch(String userNameSearch) {
		this.userNameSearch = userNameSearch;
	}

	public UserGroup getUserGroupSelected() {
		if (userGroupSelected == null)
			userGroupSelected = new UserGroup();
		return userGroupSelected;
	}

	public void setUserGroupSelected(UserGroup userGroupSelected) {
		this.userGroupSelected = userGroupSelected;
	}

	public boolean isUsersEmpty() {
		return getUsers().isEmpty();
	}

	public Boolean getIsNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}

	public void findUserByName() {
		try {
			this.setUsersSearch(userDao.findByLoginAndRole(userNameSearch,
					"PROV_DADOS", userMB.getUserName()));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public boolean isEmptyUserGroupsByUser() {
		return getUserGroupsByUser().isEmpty();
	}

	public List<UserGroup> getUserGroupsByUser() {
		if (userGroupsByUser == null)
			try {

				userGroupsByUser = userGroupDao
						.findByUser(userMB.getUserName());

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		return userGroupsByUser;
	}

	public void setUserGroupsByUser(List<UserGroup> userGroupsByUser) {
		this.userGroupsByUser = userGroupsByUser;
	}

	public void newGroup() {
		setUserGroup(new UserGroup());
		setUsers(new ArrayList<User>());
		setUsersSearch(new ArrayList<User>());
		this.isNew = true;
	}

	public void cancelNewGroup() {
		this.isNew = false;
	}

	public void deleteGroup() {
		try {
			if (getUserGroupSelected().getId() > 0) {
				userGroupDao.delete(userGroupSelected);
				userGroupsByUser = userGroupDao
						.findByUser(userMB.getUserName());

				if (getUserGroupsByUser().size() > 1) {
					users = userDao.findByUserGroup(getUserGroupsByUser()
							.get(0).getName(), userMB.getUserName());
				} else {
					users = new ArrayList<User>();
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void save() {
		try {
			User user = userDao.findByLogin(userMB.getUserName());
			userGroup.setCreator(userMB.getUserName());
			userGroup.getUsers().add(user);
			userGroupDao.save(userGroup);

			this.isNew = false;

			userGroupsByUser = userGroupDao.findByUser(userMB.getUserName());

			if (getUserGroupsByUser().size() > 1) {
				users = userDao.findByUserGroup(getUserGroupsByUser().get(0)
						.getName(), userMB.getUserName());
			} else {
				users = new ArrayList<User>();
			}

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							"Grupo de usuários cadastrado com sucesso!"));

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String exceptionAsString = sw.toString();
			System.out.println("Erro: " + e.getMessage() + "\n"
					+ exceptionAsString);

			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									"Desculpe, erro ocorrido ao cadastrar o grupo de usuários."));
		}
	}

	public void valueChanged_selectUserGroups(ValueChangeEvent e) {
		if (e.getNewValue() != null) {
			users = userDao.findByUserGroup(
					((UserGroup) e.getNewValue()).getName(),
					userMB.getUserName());
		}
	}

	public void addUserGroup(User userAdd) {
		try {
			if (!getUserGroupSelected().getName().equals("")) {

				userGroupSelected.getUsers().add(userAdd);

				userGroupDao.update(userGroupSelected);

				users = userDao.findByUserGroup(userGroupSelected.getName(),
						userMB.getUserName());
			}

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String exceptionAsString = sw.toString();
			System.out.println("Erro: " + e.getMessage() + "\n"
					+ exceptionAsString);
		}
	}

	public boolean isEmptyUsers() {
		return getUsers().isEmpty();
	}

	public void deleteUserFromGroup(User userToDelete) {
		try {

			userGroupSelected.getUsers().remove(userToDelete);

			userGroupDao.update(userGroupSelected);

			users = userDao.findByUserGroup(userGroupSelected.getName(),
					userGroupSelected.getCreator());
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String exceptionAsString = sw.toString();
			System.out.print("Erro: " + e.getMessage() + exceptionAsString);
		}

	}
}
