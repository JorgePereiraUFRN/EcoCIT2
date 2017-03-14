package br.com.ecodif.api;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import br.com.ecodif.dao.ConnectedDeviceDAO;
import br.com.ecodif.dao.DeviceDAO;
import br.com.ecodif.dao.SensorDAO;
import br.com.ecodif.dao.TriggerDAO;
import br.com.ecodif.dao.UserDAO;
import br.com.ecodif.dao.UserTypeDAO;
import br.com.ecodif.dao.ValueDao;
import br.com.ecodif.dao.ValueDao2;
import br.com.ecodif.domain.ConnectedDevice;
import br.com.ecodif.domain.CurrentValue;
import br.com.ecodif.domain.Data;
import br.com.ecodif.domain.Device;
import br.com.ecodif.domain.Eeml;
import br.com.ecodif.domain.Environment;
import br.com.ecodif.domain.Location;
import br.com.ecodif.domain.Sensor;
import br.com.ecodif.domain.Trigger;
import br.com.ecodif.domain.User;
import br.com.ecodif.domain.Value;
import br.com.ecodif.eeml_contract.Eeml_Contract;
import br.com.ecodif.framework.EemlManager;
import br.com.ecodif.framework.SendMail;
import br.com.ecodif.framework.SendNotification;

/**
 * Classe respons�vel pela API da EcoDiF
 * @author Bruno Costa
 *
 */
@Path("/feeds")
public class EemlResource {

	@Inject
	EemlManager eemlManager = new EemlManager();

	@Inject
	UserDAO userDAO = new UserDAO();

	@Inject
	UserTypeDAO userTypeDAO = new UserTypeDAO();

	@Inject
	DeviceDAO deviceDAO = new DeviceDAO();

	@Inject
	ConnectedDeviceDAO connectedDeviceDAO = new ConnectedDeviceDAO();

	@Inject
	SensorDAO sensorDAO = new SensorDAO();

	@Inject
	TriggerDAO triggerDAO = new TriggerDAO();
	
	@Inject
	ValueDao2 valueDao = new ValueDao2();

	private static final String API_KEY = "AIzaSyALvVoCJkkqr_a8TuXPmrXAW4c7iptWmj0";
	private static final String DEVICE_ID = "APA91bEZAJGuBzvD-2RLgc-AoCfKt5MudQVxDm4Q7zoKEIduMV5-uNpq7RwtbM2m5c_muIVCIrBblIN_5isU9T0GFAJlYCa3eTLq79-JphdK4OAcKpBF3WoDNAMzk7r2O1AUx6SmEk6LpoW9OC2mkJkRuhJxIbCgYva6egu3jBG-gm-DPe3TWY0";

	/**
	 * <p>
	 * <b>Create a feed</b>
	 * </p>
	 * Minimum eeml to create a feed:
	 * 
	 * <pre>
	 * {@code
	 * <eeml xmlns="http://www.eeml.org/xsd/0.5.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="0.5.1" xsi:schemaLocation="http://www.eeml.org/xsd/0.5.1 http://www.eeml.org/xsd/0.5.1/0.5.1.xsd">
	 * 	<environment updated="2007-05-04T18:13:51.0Z" creator="ecodif/user">
	 * 		<title>A Room Somewhere</title>
	 * 		<description>This is a room somewhere</description>
	 * 		<location exposure="indoor" domain="physical" disposition="fixed">
	 * 			<name>My Room</name>
	 * 			<lat>32.4</lat>
	 * 			<lon>22.7</lon>
	 * 			<ele>0.2</ele>
	 * 		</location>
	 * 		<data id="0">
	 * 		<tag>temperature</tag>
	 * 		<current_value at="2010-11-10T20:14:23.185123Z">36.2</current_value>
	 * 	</data>	
	 * 	</environment>
	 * </eeml>
	 * }
	 * </pre>
	 * 
	 * 
	 * /** Update a Datastream
	 * 
	 * @param contract_eeml
	 * @param environmentid
	 * @param dataid
	 * @return
	 */
	@PUT
	@Path("{environmentid}/datastreams/{dataid}")
	@Consumes({ "application/xml", "text/xml" })
	public Response updateEeml(Eeml_Contract contract_eeml,
			@PathParam("environmentid") String environmentid,
			@PathParam("dataid") String dataid) {

		try {
			Eeml eemlReceived = eemlManager.eemlContractToDomain(contract_eeml);

			//Datapoints dataPoints = new Datapoints();
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


			
			Data data = eemlManager.findDataWithDatapoints(Integer
					.valueOf(dataid));


			data.setCurrentValue(currValue);

			eemlManager.updateData(data);
			//eemlManager.updateCurrentValue(data.getIddb(), data.getCurrentValue().getValue());
			
			value.setDataId(Integer
					.valueOf(dataid));
			
			valueDao.save(value);

			Trigger trigger = triggerDAO.findByEnvironmentIddb(Integer
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
					String environmentName = eemlManager.findEnironmentNameById(Integer.valueOf(environmentid));
					
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
						System.out.println("PASSEI AQUI");
						Map<String, String> params = new HashMap<String, String>();
						params.put("msg", "Trigger disparada.");
						System.out.println("API_KEY" + API_KEY);
						System.out.println("CHAVE:" + trigger.getGCMId());
						System.out.println("##################");
						SendNotification.post(API_KEY, trigger.getGCMId(),
								params);
					}
				}

			}

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String exceptionAsString = sw.toString();
			System.out.print("Erro: " + e.getMessage() + exceptionAsString);
			
			return Response.serverError().build();
			
		}
		return Response
				.created(
						URI.create("/feeds/" + environmentid + "/datastreams/"
								+ dataid)).build();

	}

	/**
	 * Retorna um XML do EEML
	 * @param environmentid
	 * @param dataid
	 * @return objeto do tipo Eeml_contract
	 */
	@GET
	@Path("{environmentid}/datastreams/{dataid}")
	@Produces({ "application/xml" })
	public Eeml_Contract getEeml(
			@PathParam("environmentid") String environmentid,
			@PathParam("dataid") String dataid) {

		Eeml_Contract eemlContract = new Eeml_Contract();

		try {


			Eeml eeml = eemlManager.findEemlByIddbEnvironmentIdData(
					Integer.valueOf(environmentid), Integer.valueOf(dataid));

			eemlContract = eemlManager.eemlDomainToContract(eeml);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

		return eemlContract;
	}

}
