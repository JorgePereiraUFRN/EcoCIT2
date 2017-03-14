package br.com.ecodif.security;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

import br.com.ecodif.domain.User;
import br.com.ecodif.service.UserService;

/**
 * Classe responsável por verificar as permissões de acesso aos recursos.
 * 
 * @author Andreza Lima
 * */
@Provider
@ServerInterceptor
public class SecurityInterceptor implements PreProcessInterceptor, ContainerRequestFilter{

	/**
	 * Referência para a classe de serviço de um usuário.
	 * 
	 * @see br.com.ecodif.service.UserService
	 * @see br.com.ecodif.domain.User
	 */
	@Inject
	private UserService userService;

	private static final ServerResponse ACCESS_DENIED = new ServerResponse(
			"Access denied for this resource", 401, new Headers<Object>());
	private static final ServerResponse ACCESS_FORBIDDEN = new ServerResponse(
			"Nobody can access this resource", 403, new Headers<Object>());


	private static Logger logger = Logger.getLogger(SecurityInterceptor.class);

	/**
	 * Método responsável por interceptar as requisições ao recursos para
	 * verificar se o usuário tem a permissão.
	 */
	
	@Override
	public ServerResponse preProcess(HttpRequest request,
			ResourceMethodInvoker methodInvoked) throws Failure,
			WebApplicationException {
		
		Method method = methodInvoked.getMethod();

		
		if (method.isAnnotationPresent(PermitAll.class)) {
			return null;
		}
		if (method.isAnnotationPresent(DenyAll.class)) {
			return ACCESS_FORBIDDEN;
		}
		

		String user = (String) request.getAttribute("user");
		
		System.out.println(user);

		if (user == null || user.isEmpty()) {
			
			return ACCESS_DENIED;
		}

		if (method.isAnnotationPresent(RolesAllowed.class)) {
			RolesAllowed rolesAnnotation = method
					.getAnnotation(RolesAllowed.class);
			Set<String> rolesSet = new HashSet<String>(
					Arrays.asList(rolesAnnotation.value()));

			if (!isUserAllowed(user, rolesSet)) {
				return ACCESS_DENIED;
			}
		}

		return null;
	}
	

	/**
	 * Método responsável por verificar se o usuário passado tem a permissão
	 * exigida.
	 * 
	 * @param username
	 *            - o usuário a ser verificado.
	 * @param rolesSet
	 *            - permissões possíveis.
	 * @return
	 */
	private boolean isUserAllowed(final String username,
			final Set<String> rolesSet) {
		boolean isAllowed = false;
		String userRole = null;

		User user = userService.findUserByLogin(username);
		userRole = user.getUserType().getRole();

		if (rolesSet.contains(userRole)) {
			isAllowed = true;
		}
		return isAllowed;
	}

	
	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		
		
		
		ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) requestContext
				.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
		Method method = methodInvoker.getMethod();

		if (!method.isAnnotationPresent(PermitAll.class)) {

			if (method.isAnnotationPresent(DenyAll.class)) {
				requestContext.abortWith(ACCESS_FORBIDDEN);
				return;
			}
			
			final MultivaluedMap<String, String> headers = requestContext.getHeaders();
	          
	        for(String header: headers.keySet()){
	     	   
	     	   System.out.println(header+": "+headers.get(header));
	        }
		
         
			
		}
		
		 //Get request headers
        
	}

}
