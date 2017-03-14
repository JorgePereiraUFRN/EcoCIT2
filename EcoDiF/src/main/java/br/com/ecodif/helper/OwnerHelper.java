package br.com.ecodif.helper;

import java.util.Enumeration;


import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import br.com.ecodif.domain.Application;
import br.com.ecodif.domain.ConnectedDevice;
import br.com.ecodif.domain.Device;
import br.com.ecodif.domain.Environment;

/**
 * Classe responsável por verificar o dono dos recursos.
 * 
 * @author Andreza Lima
 *
 */
@RequestScoped
public class OwnerHelper {

	@Context
	protected HttpServletRequest httpRequest;

	/**
	 * Dono do recurso acessado.
	 */
	private String owner;

	/**
	 * Classe do recurso sendo acessado.
	 */
	private String entityClass;

	/**
	 * Método responsável por buscar o usuário na requisição passada.
	 * 
	 * @return o usuário.
	 */
	public String getUser() {

		
		String user = (String) httpRequest.getAttribute("user");
		return user;
	}

	public OwnerHelper() {
	}

	/**
	 * Construtor recebendo a classe do recurso sendo acessado.
	 */
	public OwnerHelper(String entity) {
		this.entityClass = entity;
	}

	/**
	 * Método responsável por verificar o dono do recurso que esta sendo
	 * acessado.
	 * 
	 * @param obj - recurso a ser verificado.
	 * @return O usuário dono do recurso.
	 */
	public String getOwner(Object obj) {
		switch (entityClass) {
		case "Environment":
			Environment e = (Environment) obj;
			owner = e.getCreator();
			return owner;
		case "Application":
			Application app = (Application) obj;
			owner = app.getUser().getLogin();
			return owner;
		case "Device":
			Device device = (Device) obj;
			owner = device.getCompany().getLogin();
			return owner;
		case "ConnectedDevice":
			ConnectedDevice conndev = (ConnectedDevice) obj;
			owner = conndev.getUser().getLogin();
			return owner;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Método auxiliar para verificar o dono do recurso.
	 * @param obj - recurso a ser verificado.
	 * @return
	 */
	protected boolean isowner(Object obj) {
		this.getOwner(obj);
		if (owner.compareTo(getUser()) == 0) {
			return true;
		}
		return false;
	}

}
