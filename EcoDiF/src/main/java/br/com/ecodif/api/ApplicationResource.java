package br.com.ecodif.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.datatype.DatatypeConfigurationException;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import br.com.ecodif.domain.Application;
import br.com.ecodif.domain.Eeml;
import br.com.ecodif.domain.Environment;
import br.com.ecodif.domain.User;
import br.com.ecodif.eeml_contract.Eeml_Contract;
import br.com.ecodif.helper.ApplicationHelper;
import br.com.ecodif.helper.FileHelper;
import br.com.ecodif.searchcriteria.ApplicationSearchCriteria;
import br.com.ecodif.service.EnvironmentService;
import br.com.ecodif.service.UserService;
import br.com.ecodif.util.Converter;

/**
 * Classe responsável pela API da EcoDiF com recursos relacionados as
 * aplicações.
 *
 * @author Andreza Lima (andrezapbl1@gmail.com)
 *
 */
@Path("application")
public class ApplicationResource extends ApplicationHelper {

	/**
	 * Referência para a classe de critérios de busca de uma aplicação.
	 *
	 * @see br.com.ecodif.searchcriteria.ApplicationSearchCriteria
	 */
	private ApplicationSearchCriteria appcriteria;

	/**
	 * Referência para a classe de serviço de um usuário.
	 *
	 * @see br.com.ecodif.service.UserService
	 * @see br.com.ecodif.domain.User
	 */
	@Inject
	private UserService userService = new UserService();

	@Inject
	private FileHelper fileHelper = new FileHelper();
	/**
	 * Referência para a classe de conversão
	 *
	 * @see br.com.ecodif.util.Converter
	 */
	@Inject
	private Converter converterDate = new Converter();

	/**
	 * Referência para a classe de serviço de um Environment
	 *
	 * @see br.com.ecodif.service.EnvironmentService
	 * @see br.com.ecodif.domain.Envrionment
	 */
	@Inject
	private EnvironmentService environmentService = new EnvironmentService();

	/**
	 * Método responsável por buscar todas as aplicações públicas.
	 *
	 * @return <i>status 200(ok)</i> contendo os aplicações caso a busca seja
	 *         realizada com sucesso. </br> <i> status 404(not found) </i> caso
	 *         não exista aplicações públicas. </br> <i>status 500(server error)
	 *         </i> caso seja lançado alguma exceção.
	 */
	@GET
	@PermitAll
	@Wrapped(element = "applications")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getPublicApplications() {
		List<Application> applications = null;

		try {
			applications = applicationService.findPublicApplications(true);

			if (applications.isEmpty())
				return Response.status(404).build();

			GenericEntity<List<Application>> entity = new GenericEntity<List<Application>>(
					applications) {
			};
			return Response.ok(entity).build();

		} catch (Exception e) {
			return Response.status(500).build();

		}
	}

	
	@GET
	@PermitAll
	@Path("/execute/{appid}")
	public Response executeApp(@PathParam("appid") int appid) {
		Application app = applicationService.findById(appid);
		
		if(app == null)
			Response.status(404).entity(null).build();
		
		String emmlreference = app.getEmmlReference();
		String appname = emmlreference.substring(
				emmlreference.lastIndexOf(File.separator) + 1,
				emmlreference.lastIndexOf("."));

		StringBuilder sb = new StringBuilder();
		try {

			URL url = new URL(context.getInitParameter("EMMLEngineURL")
					+ appname);

			InputStream execution = url.openStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					execution, "ISO-8859-1"));

			String str = "";
			while ((str = in.readLine()) != null) {
				sb.append(str + "\n");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String resultExecution = sb.toString();
		
		return Response.ok(resultExecution).build();
	}
	
	/**
	 * Método responsável por buscar todas as aplicações do usuário que está
	 * acessando o recurso.
	 *
	 * @param username
	 *            - login do usuário.
	 * @return <i>status 200(ok)</i> contendo os aplicações caso a busca seja
	 *         realizada com sucesso. </br> <i> status 404(not found) </i> caso
	 *         não exista aplicações. </br> <i>status 500(server error) </i>
	 *         caso seja lançado alguma exceção.
	 */
	@GET
	@Path("/user")
	@PermitAll
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Wrapped(element = "applications")
	public Response getApplicationsByUser(@HeaderParam("user") String user) {

		List<Application> applications = null;

		try {
			String username = user;
			applications = applicationService.findApplicationsByUser(username);

			if (applications.isEmpty())
				return Response.status(404).build();

			GenericEntity<List<Application>> entity = new GenericEntity<List<Application>>(
					applications) {
			};
			return Response.ok(entity).build();

		} catch (Exception e) {
			return Response.status(500).build();

		}
	}

	/**
	 * Método responsável por vincular um feed a uma aplicação.
	 *
	 * @param feedid
	 *            - id do feed que deverá ser vinculado.
	 * @param applicationid
	 *            - id da aplicação.
	 * @return <i>status 200(ok)</i> caso o feed seja vinculado com sucesso.
	 *         </br> <i> status 401(not authorized) </i> caso o usuário que está
	 *         tentando acessar o recurso não seja dono da aplicação. </br> <i>
	 *         status 404(not found) </i> caso não exista aplicação ou feed com
	 *         o id passado. </br> <i> status 500(server error) </i> caso seja
	 *         lançado alguma exceção.
	 */
	@PUT
	@Path("{applicationid}/feed/{feedid}")
	@RolesAllowed("DEV_APP")
	public Response includeFeed(@PathParam("feedid") int feedid,
			@PathParam("applicationid") int applicationid, @HeaderParam("user") String user) {

		Application app = applicationService.findById(applicationid);

		if (app == null)
			return Response.status(404).build();

		if (isowner(app, user)) {
			try {
				Environment feed = environmentService
						.findEnvironmentById(feedid);
				if (feed == null)
					return Response.status(404).build();
				app.getFeeds().add(feed);
				app.setUpdateDate((GregorianCalendar) GregorianCalendar
						.getInstance());
				applicationService.updateApplication(app);
				buildEMMLInputFeeds(app);
				return Response.ok().build();
			} catch (IOException e) {
				e.printStackTrace();
				return Response.serverError().build();
			}
		}
		return Response.status(401).build();
	}

	/**
	 * Método responsável por desvincular um feed de uma aplicação.
	 *
	 * @param applicationid
	 *            - id da aplicação.
	 * @param feedid
	 *            - id do feed a ser deletado.
	 * @return <i>status 200(ok)</i> caso o feed seja deletado da aplicação com
	 *         sucesso. </br> <i> status 401(not authorized) </i> caso o usuário
	 *         que está tentando acessar o recurso não seja dono da aplicação.
	 *         </br> <i> status 404(not found) </i> caso não exista aplicação
	 *         com o id passado. </br> <i> status 500(server error) </i> caso
	 *         seja lançado alguma exceção.
	 */
	@DELETE
	@RolesAllowed("DEV_APP")
	@Path("/{applicationid}/feed/{feedid}")
	public Response unbindAppFeedById(
			@PathParam("applicationid") int applicationid,
			@PathParam("feedid") String feedid, @HeaderParam("user") String user) {

		Application app = applicationService.findById(applicationid);

		if (app == null)
			return Response.status(404).build();
		else {
			if (isowner(app, user)) {
				try {
					if (unbindFeed(feedid, app))
						return Response.ok().build();
				} catch (Exception e) {
					System.out.println(e.getMessage());
					return Response.serverError().build();
				}
			}
		}

		return Response.status(401).build();
	}

	/**
	 * Método responsável por buscar aplicações de acordo com os critérios
	 * passados.
	 *
	 * @param info
	 *            - critérios para a busca.
	 * @return Lista de aplicações filtradas pelos critérios passados, se
	 *         houver.
	 */
	@GET
	@Path("/query")
	@PermitAll
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Wrapped(element = "applications")
	public List<Application> getApplicationsByCriteria(@Context UriInfo info, @HeaderParam("user") String httpUser) {
		String name = info.getQueryParameters().getFirst("name");
		String tags = info.getQueryParameters().getFirst("tags");
		String user = info.getQueryParameters().getFirst("user");
		String creationDate = info.getQueryParameters()
				.getFirst("creationDate");
		String updateDate = info.getQueryParameters().getFirst("updateDate");
		List<Application> apps = null;
		try {
			appcriteria = new ApplicationSearchCriteria();

			if (creationDate != null) {
				Date startDate = converterDate
						.transformStringToDate(creationDate);
				appcriteria.setStartDate(startDate);
			}

			if (updateDate != null) {
				Date upDate = converterDate.transformStringToDate(updateDate);
				appcriteria.setStartDate(upDate);
			}

			if (name != null)
				appcriteria.setName(name);

			if (user != null)
				appcriteria.setUser(user);

			if (tags != null)
				appcriteria.setTags(tags);

			apps = new ArrayList<Application>();
			Iterator<Application> it = applicationService
					.findApplicationsByCriteria(appcriteria).iterator();
			while (it.hasNext()) {
				if (it.next().get_private()) {
					if (isowner(it.next(), httpUser))
						apps.add(0, it.next());
				}
				apps.add(0, it.next());
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return apps;
	}

	/**
	 * Método para buscar os feeds vinculados a uma aplicação.
	 *
	 * @param id
	 *            - id da aplicação.
	 * @return <i>status 200(ok)</i> contendo os feeds caso a busca seja
	 *         realizada com sucesso. </br> <i> status 401(not authorized) </i>
	 *         caso o usuário que está tentando acessar o recurso não seja dono
	 *         da aplicação e a aplicação seja privada. </br> <i> status 404(not
	 *         found) </i> caso não exista aplicação com o id passado ou não
	 *         haja feeds vinculados a aplicação. </br> <i>status 500(server
	 *         error) </i> caso seja lançado alguma exceção.
	 */
	@GET
	@PermitAll
	@Wrapped(element = "environments")
	@Path("/{applicationid}/feeds")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getApplicationFeeds(@PathParam("applicationid") int id, @HeaderParam("user") String user) {
		Application app = applicationService.findById(id);
		if (app == null)
			return Response.status(404).build();

		try {
			if (!app.get_private() || isowner(app, user)) {
				Set<Environment> feeds = app.getFeeds();

				if (feeds.isEmpty())
					return Response.status(404).build();

				List<Eeml_Contract> eemls = new ArrayList<Eeml_Contract>();
				for (Environment e : feeds) {
					Eeml eeml = environmentService.findEemlByIddbEnvironment(e
							.getIddb());
					Eeml_Contract eemlContract = environmentService
							.eemlDomainToContract(eeml);
					eemls.add(eemlContract);
				}
				return Response.ok(eemls).build();
			}
		} catch (DatatypeConfigurationException e) {
			return Response.serverError().build();
		}
		return Response.status(401).build();
	}

	/**
	 * Método responsável por deleter uma aplicação.
	 *
	 * @param appplicationid
	 *            id da aplicação a ser deletada.
	 * @return <i>status 200(ok)</i> caso a aplicação seja deletada com sucesso.
	 *         </br> <i> status 401(not authorized) </i> caso o usuário que está
	 *         tentando acessar o recurso não seja dono da aplicação. </br> <i>
	 *         status 404(not found) </i> caso não exista aplicação com o id
	 *         passado. </br> <i>status 500(server error) </i> caso seja lançado
	 *         alguma exceção.
	 */
	@DELETE
	@Path("/{appid}")
	public Response deleteApplication(@PathParam("appid") int appplicationid, @HeaderParam("user") String httpUser) {
		try {
			Application app = applicationService.findById(appplicationid);
			if (app == null)
				return Response.status(404).build();
			else {
				if (isowner(app, httpUser)) {
					User user = userService.findUserByLogin(httpUser);
					File fileRep = new File(app.getEmmlReference());
					fileRep.delete();
					String fileNameEng = context
							.getInitParameter("EMMLEnginePath")
							+ File.separator
							+ user.getLogin()
							+ "_"
							+ app.getName() + ".emml";

					File fileEng = new File(fileNameEng);
					fileEng.delete();
					applicationService.deleteApplication(app);
					return Response.ok().build();
				} else {
					return Response.status(401).build();
				}
			}
		} catch (Exception e) {
			return Response.serverError().build();
		}

	}

	/**
	 * Método responsável por editar uma aplicação. É possível editar o nome,
	 * tags, descrição e privacidade.
	 *
	 * @param appplicationid
	 *            - id da aplicação a ser editada.
	 * @param app_update
	 *            - novos dados da aplicação.
	 * @return <i>status 200(ok)</i> caso a aplicação seja editada com sucesso.
	 *         </br> <i> status 404(not found) </i> caso não exista aplicação
	 *         com o id passado.
	 */
	@PUT
	@Path("/{appid}")
	@RolesAllowed("DEV_APP")
	public Response updateApplication(
			@PathParam("appid") Integer appplicationid, Application app_update, @HeaderParam("user") String user) {
		Application app = applicationService.findById(appplicationid);

		if (!(app_update != null && isowner(app, user))) {
			return Response.status(404).build();
		} else {

			if (app_update.getName() != null) {
				if (applicationService.existAppName(app_update)) {
					return Response.status(401).build();
				}
			}
			app.setUpdateDate((GregorianCalendar) GregorianCalendar
					.getInstance());
			if (app_update.getDescription() != null)
				app.setDescription(app_update.getDescription());

			app.setName(app_update.getName());

			if (app_update.getTags() != null)
				app.setTags(app_update.getTags());

			if (app_update.get_private() != app.get_private())
				app.set_private(app_update.get_private());

			applicationService.updateApplication(app);
			return Response.ok().build();
		}
	}

	/**
	 * Métodor responsável por editar o EMML de uma aplicação.
	 *
	 * @param input
	 *            - o novo arquivo EMML
	 * @param appplicationid
	 *            - id da aplicação.
	 * @return <i>status 200(ok)</i> caso o arquivo EMML seja editado com
	 *         suceso. </br> <i> status 401(not authorized) </i> caso o usuário
	 *         que está tentando acessar o recurso não seja dono da aplicação.
	 *         </br> <i> status 404(not found) </i> caso não exista aplicação
	 *         com o id passado. </br> <i> status 500(server error) </i> caso
	 *         seja lançado alguma exceção.
	 */
	@PUT
	@RolesAllowed("DEV_APP")
	@Path("/{appid}/emml")
	@Consumes("multipart/form-data")
	public Response updateApplicationEMML(MultipartFormDataInput input,
			@PathParam("appid") int appplicationid, @HeaderParam("user") String user) {

		Application app = applicationService.findById(appplicationid);
		if (app == null)
			return Response.status(404).build();

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

		List<InputPart> inputParts = uploadForm.get("file");

		try {
			createEMMLFile(app);
		} catch (IOException e1) {
			e1.printStackTrace();
			return Response.serverError().build();
		}

		try {
			InputStream inputStream = null;

			for (InputPart inputPart : inputParts) {

				inputStream = inputPart.getBody(InputStream.class, null);
			}

			String fileNameRep = context
					.getInitParameter("ApplicationsDirectory")
					+ user
					+ "_" + app.getName() + ".emml";
			
			app.setEmmlReference(fileNameRep);

			fileHelper.copyFile(inputStream, fileNameRep);
			
			for (InputPart inputPart : inputParts) {

				inputStream = inputPart.getBody(InputStream.class, null);
			}

			
			String fileNameEng = context.getInitParameter("EMMLEnginePath")
					+ File.separator + user + "_" + app.getName()
					+ ".emml";

			fileHelper.copyFile(inputStream, fileNameEng);
			

			if (!isValidEMML(fileNameRep)) {
				File fileDelete = new File(fileNameRep);
				fileDelete.delete();

				return Response.notModified().entity("Arquivo inválido")
						.build();
			}
			
			app.setUpdateDate((GregorianCalendar) GregorianCalendar
					.getInstance());

			applicationService.updateApplication(app);

			System.out.println("Success");
			
			return Response.ok().build();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().build();
		}

	}

	/**
	 * Método responsável por registrar uma nova aplicação.
	 *
	 * @param app
	 *            - a aplicação a ser registrada.
	 * @return <i>status 200(ok)</i> contendo o id da nova aplicação, caso a
	 *         aplicação seja registrada com sucesso. </br> <i> status
	 *         400(invalid) </i> caso o usuário que está tentando acessar o
	 *         recurso não tenha inserido o nome da aplicação ou já exista
	 *         aplicação com o nome solicitado. </br> <i> status 500(server
	 *         error) </i> caso seja lançado alguma exceção.
	 */
	@POST
	@RolesAllowed("DEV_APP")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response registerapplication(Application app, @HeaderParam("user") String _user) {

		if (app.getName().isEmpty() || applicationService.existAppName(app))
			return Response.status(400).build();
		try {
			User user = userService.findUserByLogin(_user);
			app.setUser(user);
			String fileNameRep = context
					.getInitParameter("ApplicationsDirectory")
					+ _user
					+ "_" + app.getName() + ".emml";
			app.setEmmlReference(fileNameRep);

			createEMMLFile(app);

			GregorianCalendar gc = (GregorianCalendar) GregorianCalendar
					.getInstance();
			app.setCreationDate(gc);

			applicationService.saveApplication(app);
			return Response.ok().entity(app.getId()).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	}

	/**
	 * Método responsável por buscar o EMML de uma aplicação
	 *
	 * @param appid
	 *            id da aplicação.
	 * @return <i>status 200(ok)</i> contendo o EMML, caso a busca seja
	 *         realizada com sucesso. com sucesso. </br> <i> status 401(not
	 *         authorized) </i> caso o usuário que está tentando acessar o
	 *         recurso não seja dono da aplicação. </br> <i> status 404(not
	 *         found) </i> caso não exista aplicação com o id passado. </br> <i>
	 *         status 500(server error) </i> caso seja lançado alguma exceção.
	 */
	@GET
	@Path("/{appid}/emml")
	@Produces({ MediaType.APPLICATION_XML })
	@PermitAll
	public Response getEMML(@PathParam("appid") int appid, @HeaderParam("user") String user) {

		String contents = "";
		Application application = applicationService.findById(appid);

		if (application == null)
			return Response.status(404).build();

		try {
			if (isowner(application, user)) {
				BufferedReader in = new BufferedReader(new FileReader(
						application.getEmmlReference()));
				while (in.ready()) {
					contents += in.readLine() + "\n";
				}

				in.close();

				return Response.ok(contents).build();
			}
			return Response.status(401).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	}

	/**
	 * Método responsável por retornar uma aplicação por seu id.
	 *
	 * @param appid
	 *            - id da aplicação
	 * @return <i>status 200(ok)</i> contendo a aplicação caso a busca seja
	 *         realizada com sucesso. </br> <i> status 404(not found) </i> caso
	 *         não exista aplicação com o id passado. </br> <i>status 401(not
	 *         authorized)</i> caso a aplicação seja privada e o usuário que
	 *         esta acessando o recurso não é o dono dela. </br> <i>status
	 *         500(server error) </i> caso seja lançado alguma exceção.
	 */
	@GET
	@Path("/{appid}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@PermitAll
	public Response getApplicationById(@PathParam("appid") int appid, @HeaderParam("user") String user) {
		Application application = null;
		try {
			application = applicationService.findById(appid);
			if (application == null)
				return Response.status(404).build();
			if (isowner(application, user) || !application.get_private())
				return Response.ok(application).build();

		} catch (Exception e) {
			System.out.println(e.getMessage());
			return Response.status(500).build();
		}
		return Response.status(401).build();
	}
}
