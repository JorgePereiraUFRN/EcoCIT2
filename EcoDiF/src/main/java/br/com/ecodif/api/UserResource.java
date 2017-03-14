package br.com.ecodif.api;

import java.io.IOException;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import br.com.ecodif.domain.User;
import br.com.ecodif.service.UserService;
import br.com.ecodif.util.UserInfo;

/**
 * Classe responsável pela API da EcoDiF com recursos relacionados aos usuários.
 * 
 * @author Andreza Lima (andrezapbl1@gmail.com)
 *
 */
@Path("user")
public class UserResource {

	/**
	 * Referência para a classe de serviço de um usuário.
	 * 
	 * @see br.com.ecodif.service.UserService
	 * @see br.com.ecodif.domain.User
	 */
	@Inject
	private UserService userService = new UserService();

	/**
	 * Método responsável por registrar um novo usário.
	 * 
	 * @param user
	 *            - usuário a ser registrado.
	 * @return <i>status 200(ok)</i> caso o usuário seja registrado com sucesso.
	 *         </br> <i> status 400(invalid) </i> caso o email ou login passado
	 *         já exista. </br> <i> status 500(server error) </i> caso seja
	 *         lançado alguma exceção.
	 */
	@POST
	@PermitAll
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response registerUser(User user) {
		String msgValidation = "";
		String msgIni = "Desculpe, ";
		UserInfo userinfo = new UserInfo();

		try {
			if (userService.isUsedLogin(user.getLogin())) {
				msgValidation = " login ";
			}
			if (userService.isUsedEmail(user.getEmail())) {
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
				userinfo.setMsg(msgIni);
			}

			else {
				int id = user.getUserType().getId();
				user.setUserType(userService.findTypeById(id));
				String[] infos = this.createTokenAuthServer(user.getLogin());
				user.setToken(infos[2]);
				userService.saveUser(user);
				userinfo.setClientId(infos[0]);
				userinfo.setClientSecret(infos[1]);
				userinfo.setToken(infos[2]);
				userinfo.setMsg("success");
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
			return Response.serverError().build();
		}

		return Response.ok(userinfo).build();
	}

	private String[] createTokenAuthServer(String user)
			throws ClientProtocolException, IOException {
		String authServerURL = "http://localhost:8080/AuthorizationServer/oauth";

		final DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(authServerURL);

		post.setHeader("user", user);

		HttpResponse response = httpClient.execute(post);
		System.out.println("Response Code : "
				+ response.getStatusLine().getStatusCode());

		Header[] headers = response.getAllHeaders();

		String infos[] = new String[3];

		for (Header header : headers) {
			switch (header.getName()) {
			case "clientId":
				infos[0] = header.getValue();
				break;
			case "clientSecret":
				infos[1] = header.getValue();
				break;
			case "token":
				infos[2] = header.getValue();
				break;
			}
		}

		return infos;
	}

}
