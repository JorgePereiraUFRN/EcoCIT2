package br.com.ecodif.mb;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import br.com.ecodif.dao.UserDAO;
import br.com.ecodif.dao.UserTypeDAO;
import br.com.ecodif.domain.User;
import br.com.ecodif.domain.UserType;

@ManagedBean(name="userMB")
@SessionScoped
public class UserMB {

	@EJB
	private UserTypeDAO userTypeDAO;

	@EJB
	private UserDAO userDAO;

	private User user;
	private UserType userTypeSelected;

	@PostConstruct
	public void init(){
		this.setUser(new User());
	}
	
	public String logIn() {
		HttpServletRequest request = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		try {
			request.login(user.getLogin(), user.getPassword());
			return "home";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(
					"formLogin",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro",
							"Login ou senha inv�lidos"));
			e.printStackTrace();
		}
		return "errorLogin";

	}

	public boolean isAnonymousUser() {
		boolean isAnonimous = false;

		try {
			ExternalContext context = FacesContext.getCurrentInstance()
					.getExternalContext();
			Principal p = context.getUserPrincipal();

			isAnonimous = (p == null);
		} catch (Exception e) {
			// TODO: Log and message
		}

		return isAnonimous;

	}

	private HttpServletRequest getRequest() {
		return (HttpServletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest();
	}

	public boolean isFabricanteDispositivo() {
		return getRequest().isUserInRole("FAB_DISP");

	}

	public boolean isProvedorDados() {
		return getRequest().isUserInRole("PROV_DADOS");

	}
	
	public boolean isProvedorDadosWRNP() {
		return getRequest().isUserInRole("PROV_DADOS_WRNP");

	}
	
	public boolean isDesenvolvedorAplicacoes() {
		return getRequest().isUserInRole("DEV_APP");

	}

	public String logOut() {
		getRequest().getSession().invalidate();
		return "home";
	}

	public String getUserName() {
		String username = "";

		try {
			ExternalContext context = FacesContext.getCurrentInstance()
					.getExternalContext();
			Principal p = context.getUserPrincipal();

			if (p != null)
				username = context.getUserPrincipal().getName();

		} catch (Exception e) {
			// TODO: handle exception log
		}

		return username;
	}

	public User getUser() {
		if (user == null) {
			user = new User();
		}

		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserType getUserTypeSelected() {
		if (userTypeSelected == null) {
			userTypeSelected = new UserType();
			userTypeSelected.setRole("FAB_DISP");
		}

		return userTypeSelected;
	}

	public void setUserTypeSelected(UserType userTypeSelected) {
		this.userTypeSelected = userTypeSelected;
	}

	public List<UserType> getUserTypes() {
		List<UserType> userTypes = userTypeDAO.findWithoutSpecUserType("PROV_DADOS_WRNP");

		return userTypes;
	}

	public String saveUser() {
		String ret = "";
		String msgIni = "Desculpe, ";
		String msgValidation = "";

		try {
			if (userDAO.isUsedLogin(user.getLogin())) {
				msgValidation = " login ";
			}
			if (userDAO.isUsedEmail(user.getEmail())) {
				msgValidation += msgValidation.contains("login") ? "e e-mail "
						: " e-mail";
			}

			if ((msgValidation.contains("login"))
					&& (msgValidation.contains("e-mail"))) {
				msgIni += msgValidation + " já utilizados";
			} else if ((msgValidation.contains("login"))
					|| ((msgValidation.contains("e-mail")))) {
				msgIni += msgValidation + " já utilizado";
			}

			if (!msgValidation.equals("")) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								msgIni));
			}

			else {

				user.setUserType(userTypeSelected);
				String token = this.createTokenAuthServer(user.getLogin());
				if (token == null) {
					String msg = "Ocorreu um erro ao tentar se comunicar com o servidor de autorização";
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									msg));
					throw new Exception(msg);
				}
				user.setToken(token);
				this.userDAO.save(user);
				logIn();
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								"Usuário cadastrado com sucesso!"));
				ret = "home";
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return ret;
	}
	
	private String createTokenAuthServer(String user)
			throws ClientProtocolException, IOException {
		String authServerURL = "http://localhost:8080/AuthorizationServer/oauth";

		final DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(authServerURL);

		post.setHeader("user", user);

		HttpResponse response = httpClient.execute(post);
		System.out.println("Response Code : "
				+ response.getStatusLine().getStatusCode());

		Header[] headers = response.getAllHeaders();

		for (Header header : headers) {
			if (header.getName().equals("token")) {
				String token = header.getValue();
				return token;
			}

		}

		return null;
	}
}
