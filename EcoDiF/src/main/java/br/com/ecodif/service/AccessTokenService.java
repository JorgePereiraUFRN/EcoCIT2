package br.com.ecodif.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class AccessTokenService {

	//private String url = "http://10.9.98.151:8088/AuthServer/auth";
	private String url = "http://localhost:8080/AuthorizationServer/oauth";

	/**
	 * Requisição ao servidor de autorização para verificar o usuário que está
	 * relacionado ao token.
	 * 
	 * @param token - Token que será utilizado para a verificação.
	 * @return o usuário caso exista ou null caso não exista.
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private String getUserByToken(String token) throws IOException,
			URISyntaxException {

		HttpClient client = new DefaultHttpClient();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("token", token));
		URI uri = new URI(url + "?" + URLEncodedUtils.format(params, "utf-8"));

		HttpGet request = new HttpGet(uri);
		HttpResponse response = client.execute(request);

		String user = response.getFirstHeader("user").getValue();

		return user;
	}

	/**
	 * Valida o token passado verificando se o usuário retornando existe ou se
	 * ele foi null.
	 * 
	 * @param token - Token as ser verificado
	 * @return o usuário se o token existir e estiver relacionado a um usuário e
	 *         null caso contrário.
	 */
	public String validateToken(String token) {
		try {
			String response = this.getUserByToken(token);
			if (response != null) {
				return response;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
}