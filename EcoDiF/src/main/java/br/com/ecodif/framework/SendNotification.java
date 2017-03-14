package br.com.ecodif.framework;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * Classe responsável por enviar e-mails de notificação
 * @author Bruno Costa
 *
 */
public class SendNotification {

	public static String post(String apiKey, String deviceId,
			Map<String, String> params) throws IOException {

		StringBuilder postBody = new StringBuilder();
		postBody.append("registration_id").append("=").append(deviceId);

		Set<String> keys = params.keySet();

		for (String key : keys) {
			String value = params.get(key);
			postBody.append("&").append("data.").append(key).append("=")
					.append(URLEncoder.encode(value, "UTF-8"));
		}

		// Criando a mensagem
		byte[] postData = postBody.toString().getBytes("UTF-8");

		URL url = new URL("https://android.googleapis.com/gcm/send");

		HttpsURLConnection
				.setDefaultHostnameVerifier(new CustomizedHostnameVerifier());

		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded;charset=UTF-8");
		conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
		conn.setRequestProperty("Authorization", "key="+apiKey);

		OutputStream out = conn.getOutputStream();
		out.write(postData);
		out.close();
		return defineStatus(conn); 
			
	}

	public static String defineStatus(HttpsURLConnection conn) throws IOException{
		if (conn.getResponseCode()==200){
			return conn.getResponseMessage();
		}
		else{
			System.err.println(conn.getResponseCode() + ": " + conn.getResponseMessage());
		}
		return null;
	}
	
	private static class CustomizedHostnameVerifier implements HostnameVerifier {

		@Override
		public boolean verify(String hostname, SSLSession session) {
			// TODO Auto-generated method stub
			return true;
		}

	}
	
}
