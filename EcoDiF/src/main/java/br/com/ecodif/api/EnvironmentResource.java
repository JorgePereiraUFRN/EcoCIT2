package br.com.ecodif.api;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.datatype.DatatypeConfigurationException;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;

import br.com.ecodif.dao.ValueDao;
import br.com.ecodif.dao.ValueDao2;
import br.com.ecodif.domain.ConnectedDevice;
import br.com.ecodif.domain.CurrentValue;
import br.com.ecodif.domain.Data;
//import br.com.ecodif.domain.Datapoints;
import br.com.ecodif.domain.Device;
import br.com.ecodif.domain.Eeml;
import br.com.ecodif.domain.Environment;
import br.com.ecodif.domain.Sensor;
import br.com.ecodif.domain.Trigger;
import br.com.ecodif.domain.Unit;
import br.com.ecodif.domain.Value;
import br.com.ecodif.eeml_contract.Eeml_Contract;
import br.com.ecodif.framework.SendMail;
import br.com.ecodif.helper.OwnerHelper;
import br.com.ecodif.searchcriteria.EnvironmentSearchCriteria;
import br.com.ecodif.service.DeviceService;
import br.com.ecodif.service.EnvironmentService;
import br.com.ecodif.service.UnitService;
import br.com.ecodif.util.Converter;

/**
 * Classe responsável pela API da EcoDiF com recursos relacionados aos feeds.
 * 
 * @author Andreza Lima (andrezapbl1@gmail.com)
 *
 */

@Path("feed")
public class EnvironmentResource extends OwnerHelper {

	public EnvironmentResource() {
		super("Environment");
	}

	private String repositoryServer = "http://10.9.98.179:3030/dataset";

	/**
	 * Referência para a classe de conversão
	 * 
	 * @see br.com.ecodif.util.Converter
	 */
	@Inject
	private Converter converterDate = new Converter();
	
	/**
	 * Referência para a classe de critérios de busca de um feed.
	 * 
	 * @see br.com.ecodif.searchcriteria.EnvironmentSearchCriteria
	 */
	private EnvironmentSearchCriteria envcriteria = new EnvironmentSearchCriteria();

	/**
	 * Referência para a classe de serviço de um feed.
	 * 
	 * @see br.com.ecodif.service.EnvironmentService
	 * @see br.com.ecodif.domain.Environment
	 */
	@Inject
	private EnvironmentService environmentService = new EnvironmentService();

	/**
	 * Referência para a classe de serviço de um dispositivo.
	 * 
	 * @see br.com.ecodif.service.DeviceService
	 * @see br.com.ecodif.domain.Device
	 */
	@Inject
	private DeviceService deviceService  = new DeviceService();
	

	
	
	@Inject
	private UnitService unitService = new UnitService();
	
	@Inject
	private ValueDao2 valueDao = new ValueDao2();
	
	/**
	 * Método para retornar o eeml de um feed com base no id.
	 * 
	 * @param feedid
	 *            Id do feed.
	 * @return <i>status 200(ok)</i> contento o feed no formato emml, caso o
	 *         feed seja desconectado com sucesso. </br> <i> status 401(not
	 *         authorized) </i> caso o usuário que está tentando acessar o
	 *         recurso não seja dono do feed. </br> <i> status 404(not found)
	 *         </i> caso não exista feed com o id passado. </br> <i> status
	 *         500(server error) </i> caso seja lançado alguma exceção.
	 */
	@GET
	@PermitAll
	@Path("/{feedid}/datastreams/{dataid}")
	@Produces({ MediaType.APPLICATION_XML })
	public Response getEemlFeedById(@PathParam("feedid") int feedid, @PathParam("dataid") String dataid, @Context HttpServletRequest request) {
		
		super.httpRequest = request;
		
		try {
			Environment environment = environmentService
					.findEnvironmentById(feedid);
			if (environment == null)
				return Response.status(404).build();
			if (!environment.get_private().equals("N")) {
				if (!isowner(environment)) {
					return Response.status(401).build();
				}
			}

			Eeml eeml = environmentService.findEemlByIddbEnvironmentIdData(
					Integer.valueOf(feedid), Integer.valueOf(dataid));
			
			if (eeml == null)
				return Response.status(404).build();

			Eeml_Contract eemlContract = environmentService
					.eemlDomainToContract(eeml);
			return Response.ok().entity(eemlContract).build();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	}

	/**
	 * 
	 * @param contract_eeml
	 * @param environmentid
	 * @param dataid
	 * @return
	 */
	@PUT
	@Path("{environmentid}/datastreams/{dataid}")
	@Consumes({ "application/xml", "text/xml" })
	@PermitAll
	public Response updateEeml(Eeml_Contract contract_eeml,
			@PathParam("environmentid") String environmentid,
			@PathParam("dataid") String dataid) {

		try {
			Eeml eemlReceived = environmentService
					.eemlContractToDomain(contract_eeml);

		//	Datapoints dataPoints = new Datapoints();
			Value value = new Value();
			value.setValue(eemlReceived.getEnvironment().get(0).getData()
					.get(0).getCurrentValue().getValue());

			CurrentValue currValue = new CurrentValue();
			currValue.setValue(eemlReceived.getEnvironment().get(0).getData()
					.get(0).getCurrentValue().getValue());

			GregorianCalendar cal = new GregorianCalendar();
			cal.setTimeZone(TimeZone.getTimeZone("GMT-3"));

			value.setAt(cal.getTime());
			currValue.setAt(cal);

			//dataPoints.getValue().add(value);

			Data data = environmentService.findDataWithDatapoints(Integer
					.valueOf(dataid));

			//data.getValues().add(value);
			data.setCurrentValue(currValue);

			environmentService.updateData(data);
			
			value.setDataId(Integer
					.valueOf(dataid));
			
			valueDao.save(value);
			
//			repositoryService.insertObservation(repositoryServer, "sensor1", currValue.getValue(), "propriedade1", "2016-03-16T16:09:41.000-03:00", "criterio1", 100, "observationsensor1"+"2016-03-16T16:09:41.000-03:00");
			Trigger trigger = environmentService
					.findTriggerByEnvironmentIddb(Integer
							.parseInt(environmentid));
			if (trigger != null) {
				boolean triggerGo = false;

				if (trigger.getCondition().contains("Maior do que")
						&& (Integer.parseInt(value.getValue()) > Integer
								.parseInt(trigger.getNumberCondition()))) {
					triggerGo = true;
				} else if (trigger.getCondition().contains("Menor do que")
						&& (Integer.parseInt(value.getValue()) < Integer
								.parseInt(trigger.getNumberCondition()))) {
					triggerGo = true;
				} else if (trigger.getCondition().contains("Igual a")
						&& (Integer.parseInt(value.getValue()) == Integer
								.parseInt(trigger.getNumberCondition()))) {
					triggerGo = true;
				} else if (trigger.getCondition().contains("Maior ou igual a")
						&& (Integer.parseInt(value.getValue()) >= Integer
								.parseInt(trigger.getNumberCondition()))) {
					triggerGo = true;
				} else if (trigger.getCondition().contains("Menor ou igual a")
						&& (Integer.parseInt(value.getValue()) <= Integer
								.parseInt(trigger.getNumberCondition()))) {
					triggerGo = true;
				}

				if (triggerGo) {
					String environmentName = environmentService
							.findEnvironmentNameById(Integer.valueOf(environmentid));

					SendMail sendmail = new SendMail();
					sendmail.setSender("EcoDiF_API");
					sendmail.setReceiver(trigger.getEmail());
					sendmail.setSubject("Trigger EcoDiF - " + environmentName);
					sendmail.setMessageMail("  ---- Esta � uma mensagem autom�tica ----- \n"
							+ "Feed: "
							+ environmentName
							+ "\n"
							+ "Trigger: "
							+ trigger.getCondition()
							+ " "
							+ trigger.getNumberCondition()
							+ "\n"
							+ "-------------------------------------------- \n\n"
							+ "Valor atual: "
							+ value.getValue()
							+ "\n\n"
							+ "-------------------------------------------- \n\n"
							+ "EcoDiF API 1.0");

					System.out.println(sendmail.getMessageMail());
					sendmail.sendMailSSL();

					if (trigger.getGCMId() != null) {
						Map<String, String> params = new HashMap<String, String>();
						params.put("msg", "Trigger disparada.");
						System.out.println("CHAVE:" + trigger.getGCMId());
						System.out.println("##################");
					}
				}

			}

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String exceptionAsString = sw.toString();
			System.out.print("Erro: " + e.getMessage() + exceptionAsString);
		}
		return Response
				.created(
						URI.create("/feeds/" + environmentid + "/datastreams/"
								+ dataid)).build();

	}
	
	/**
	 * Método para retornar datapoints de um feed com base em seu id
	 * 
	 * @param feedid
	 * @return @return <i>status 200(ok)</i> contendo os datapoints de um feed,
	 *         caso o feed seja desconectado com sucesso. </br> <i> status
	 *         401(not authorized) </i> caso o usuário que está tentando acessar
	 *         o recurso não seja dono do feed. </br> <i> status 404(not found)
	 *         </i> caso não exista feed com o id passado. </br> <i> status
	 *         500(server error) </i> caso seja lançada alguma exceção.
	 */
	@GET
	@PermitAll
	@Path("/{feedid}/datapoints")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getDataPointsFeedById(@PathParam("feedid") int feedid) {
		try {
			Environment environment = environmentService
					.findEnvironmentById(feedid);
			if (environment == null)
				return Response.status(404).build();

			if (isowner(environment)) {
				Data data = environmentService
						.findDataWithDatapoints(environment.getData().get(0)
								.getIddb());
				return Response.ok(data).build();
			} else {
				return Response.status(401).build();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return Response.serverError().build();
		}
	}

	/**
	 * Método responsável por buscar um dispositivo vinculado a um feed.
	 * 
	 * @param feedid
	 *            - id do feed.
	 * @return <i>status 200(ok)</i> contendo o dispositivo, caso seja
	 *         encontrado com sucesso. </br> <i> status 401(not authorized) </i>
	 *         caso o usuário que está tentando acessar o recurso não seja dono
	 *         do feed. </br> <i> status 404(not found) </i> caso não exista
	 *         feed com o id passado. </br> <i> status 500(server error) </i>
	 *         caso seja lançado alguma exceção.
	 */
	@GET
	@PermitAll
	@Path("/{feedid}/device")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getDeviceByFeed(@PathParam("feedid") int feedid) {

		Environment environment = environmentService
				.findEnvironmentById(feedid);

		if (environment == null)
			return Response.status(404).build();

		if (isowner(environment)) {
			Device device = null;
			try {
				device = deviceService.findDeviceByEnvironmentId(feedid);
				return Response.ok(device).build();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return Response.serverError().build();
			}
		}
		return Response.status(401).build();
	}

	/**
	 * Método reponsável por deletar um feed pelo id.
	 * 
	 * @param feedid
	 *            id do feed a ser deletado.
	 * @return <i>status 200(ok)</i> caso o feed seja deletado com sucesso.
	 *         </br> <i> status 401(not authorized) </i> caso o usuário que está
	 *         tentando acessar o recurso não seja dono do feed. </br> <i>
	 *         status 404(not found) </i> caso não exista feed com o id passado.
	 *         </br> <i> status 500(server error) </i> caso seja lançada alguma
	 *         exceção.
	 * 
	 */
	@DELETE
	@RolesAllowed("PROV_DADOS")
	@Path("/{feedid}")
	public Response deleteFeedById(@PathParam("feedid") int feedid) {

		Environment environment = environmentService
				.findEnvironmentById(feedid);

		if (environment == null)
			return Response.status(404).build();

		if (isowner(environment)) {
			try {
				environmentService.deleteEnvironment(environment);
				return Response.status(201).build();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return Response.serverError().build();
			}
		}
		return Response.status(401).build();
	}

	/**
	 * Método responsável por criar um novo feed.
	 * 
	 * @param feed
	 *            - feed a ser criado.
	 * @return <i>status 200(ok)</i> contendo o endereço do feed, caso o feed
	 *         seja criado com sucesso. </br> <i> status 500(server error) </i>
	 *         caso seja lançada alguma exceção.
	 */
	@POST
	@RolesAllowed("PROV_DADOS")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response createFeed(Environment feed) {
		try {
			String creator = getUser();
			feed.setCreator(creator);
			String _private = feed.get_private();
			if (_private == null)
				_private = "N";
			feed.set_private(_private);
			Eeml eeml = new Eeml();
			Data data = new Data();
			Unit unit = unitService.findUnitById(feed.getUnit());
			data.setUnit(unit);

			feed.getData().add(data);
			eeml.getEnvironment().add(feed);
			feed.setEmail(null);
			environmentService.saveEeml(eeml);

			Sensor sensorManaged = environmentService.findSensorWithDatas(feed
					.getSensor());
			sensorManaged.getDatas().add(data);
			environmentService.updateSensor(sensorManaged);

			feed.setWebsite("/EcodifAPI/api/feeds/" + feed.getIddb() + "/"
					+ "datastreams/" + feed.getData().get(0).getIddb());

			ConnectedDevice conDevice = deviceService
					.findConnectedDeviceWithEnvironments(feed
							.getConnectedDevice());

			conDevice.getEnvironments().add(feed);
			deviceService.updateConnectedDevice(conDevice);

			return Response.ok().entity(feed.getWebsite()).build();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return Response.serverError().build();
		}

	}

	

	/**
	 * Método responsável por buscar os feeds de um usuário.
	 * 
	 * @param user
	 *            - usuário utilizado para a busca.
	 * @return Lista de feed filtrado pelo usuário passado, se houver.
	 */
	@GET
	@PermitAll
	@Path("/user")
	@Wrapped(element = "environments")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<Environment> getFeedsByUser() {
		String user = this.getUser();
		List<Environment> environments = environmentService
				.findEnvironmentsByUserLogin(user);
		return environments;
	}

	/**
	 * Método responsável por buscar os feeds no formato de Eeml de um usuário.
	 * 
	 * @param user
	 *            - usuário utilizado para a busca.
	 * @return Lista de feed filtrado pelo usuário passado, se houver.
	 */
	@GET
	@Path("/user/eeml")
	@Wrapped(element = "environments")
	@Produces({ MediaType.APPLICATION_XML })
	@PermitAll
	public List<Eeml_Contract> getEemlFeedsByUser() {
		String user = this.getUser();
		List<Eeml_Contract> environments;
		try {
			environments = environmentService.findEemlEnvByUser(user);
			return environments;
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Método responsável por editar um feed. Pode ser editado o titulo, o
	 * dispositivo conectado a ele e seu status.
	 * 
	 * @param info
	 *            - novas informações do feed.
	 * @param feedid
	 *            - id do feed a ser editado.
	 * @return <i>status 200(ok)</i> contendo o endereço do feed, caso o feed
	 *         seja editado com sucesso. </br> <i> status 401(not authorized)
	 *         </i> caso o usuário que está tentando acessar o recurso não seja
	 *         dono do feed. </br> <i> status 404(not found) </i> caso não
	 *         exista feed com o id passado.
	 */
	@PUT
	@RolesAllowed("PROV_DADOS")
	@Path("/{feedid}/query")
	public Response editFeed(@Context UriInfo info, @PathParam("feedid") int feedid) {
		
		Environment environment = environmentService
				.findEnvironmentById(feedid);

		if (environment == null)
			return Response.status(404).build();

		if (isowner(environment)) {
			String title = info.getQueryParameters().getFirst("title");
			String connectedDevice = info.getQueryParameters().getFirst(
					"connectedDevice");
			String status = info.getQueryParameters().getFirst("status");

			if (title != null) {
				environment.setTitle(title);
			}
			if (connectedDevice != null)
				environment.setConnectedDevice(Integer
						.parseInt(connectedDevice));
			if (status != null)
				environment.setStatus(status);

			environmentService.updateEnvironment(environment);
			return Response.ok().entity(environment.getWebsite()).build();
		}
		return Response.status(401).build();
	}
	
	

	/**
	 * Método responsável por buscar os feeds de acordo com critérios passados.
	 * 
	 * @param info
	 *            - critérios para a busca.
	 * @return lista de feeds no formato de eeml filtrado pelos critérios
	 *         passado.
	 */
	@GET
	@Path("/query")
	@Wrapped(element = "environments")
	@Produces({ MediaType.APPLICATION_XML })
	@PermitAll
	public List<Eeml_Contract> searchEemlFeedsByCriteria(@Context UriInfo info) {
		String description = info.getQueryParameters().getFirst("description");
		String title = info.getQueryParameters().getFirst("title");
		String creator = info.getQueryParameters().getFirst("creator");
		String updateDate = info.getQueryParameters().getFirst("updateDate");
		String sensor = info.getQueryParameters().getFirst("sensor");
		String status = info.getQueryParameters().getFirst("status");
		String _private = info.getQueryParameters().getFirst("private");
		String unit = info.getQueryParameters().getFirst("unit");
		String conndevice = info.getQueryParameters().getFirst("conndevice");

		envcriteria = new EnvironmentSearchCriteria();
		List<Eeml_Contract> eemlenvlist = null;

		if (description != null)
			envcriteria.setDescription(description);

		if (status != null)
			envcriteria.setStatus(status);

		if (creator != null)
			envcriteria.setCreator(creator);
		if (title != null)
			envcriteria.setTitle(title);
		if (sensor != null)
			envcriteria.setSensor(sensor);
		if (unit != null)
			envcriteria.setUnit(unit);
		if (conndevice != null)
			envcriteria.setConnectedDevice(Integer.parseInt(conndevice));
		if (updateDate != null) {
			envcriteria.setUpdated(converterDate
					.transformStringToDate(updateDate));
		}
		if (_private != null)
			envcriteria.set_private(_private);

		eemlenvlist = new ArrayList<Eeml_Contract>();
		Iterator<Eeml_Contract> it = environmentService
				.findEemlEnvironmentsByCriteria(envcriteria).iterator();

		while (it.hasNext()) {
			eemlenvlist.add(0, it.next());
		}

		return eemlenvlist;
	}

	/**
	 * Método responsável por buscar um feed pelo id.
	 * 
	 * @param feedid
	 * @return <i>status 200(ok)</i> contendo o feed, caso seja encontrado com
	 *         sucesso. </br> <i> status 401(not authorized) </i> caso o usuário
	 *         que está tentando acessar o recurso não seja dono do feed e o
	 *         feed seja privado. </br> <i> status 404(not found) </i> caso não
	 *         exista feed com o id passado.
	 */
	@GET
	@Path("/{feedid}")
	@PermitAll
	@Produces({MediaType.APPLICATION_JSON })
	public Response getFeedById(@PathParam("feedid") int feedid) {

		Environment environment = environmentService
				.findEnvironmentById(feedid);

		if (environment == null)
			return Response.status(404).build();

		if (!environment.get_private().equals("N")) {
			if (!isowner(environment)) {
				return Response.status(401).build();
			}
		}
		return Response.ok().entity(environment).build();

	}

}