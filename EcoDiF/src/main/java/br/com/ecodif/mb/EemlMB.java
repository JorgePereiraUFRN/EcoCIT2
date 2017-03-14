package br.com.ecodif.mb;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import com.sun.faces.context.flash.ELFlash;
import br.com.ecodif.dao.ConnectedDeviceDAO;
import br.com.ecodif.dao.DeviceDAO;
import br.com.ecodif.dao.SensorDAO;
import br.com.ecodif.dao.TriggerDAO;
import br.com.ecodif.dao.UserGroupDAO;
import br.com.ecodif.domain.ConnectedDevice;
import br.com.ecodif.domain.Data;
import br.com.ecodif.domain.Device;
import br.com.ecodif.domain.Eeml;
import br.com.ecodif.domain.Environment;
import br.com.ecodif.domain.Sensor;
import br.com.ecodif.domain.Trigger;
import br.com.ecodif.domain.Unit;
import br.com.ecodif.domain.UserGroup;
import br.com.ecodif.framework.EemlManager;

@ManagedBean
@ViewScoped
public class EemlMB {

	private Eeml eeml;
	private Environment environment;
	private Data data;
	private ConnectedDevice connectedDevice;
	private Sensor sensorSelected;
	private List<Sensor> sensorsByDevice;
	private Device deviceOfEnvironment;
	private Sensor sensorOfDatastream;
	private String valueSensorTest;
	private List<Environment> allEnvironments;
	private String environmentToFind;
	private List<Unit> allUnits;
	private Unit unitSelected;
	private String dataActualValue;
	private UserGroup userGroup;
	private Trigger trigger;
	private String triggerCondition;
	private String email;
	private Unit unit;

	@EJB
	private EemlManager eemlManager;

	@EJB
	private SensorDAO sensorDAO;

	@EJB
	private ConnectedDeviceDAO connectedDeviceDAO;

	@EJB
	private DeviceDAO deviceDAO;

	@EJB
	private UserGroupDAO userGroupDAO;

	@EJB
	private TriggerDAO triggerDAO;

	@ManagedProperty("#{userMB}")
	private UserMB userMB;

	@PostConstruct
	public void init() {
		try {
			HttpServletRequest request = (HttpServletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();
			HttpSession session = (HttpSession) request.getSession();

			if (session.getAttribute("environmentIddb") != null) {

				setEnvironment(eemlManager
						.findEnvironmentById(((Integer) session
								.getAttribute("environmentIddb"))));
				Trigger triggerEnv = triggerDAO

				.findByEnvironmentIddb((Integer) session
						.getAttribute("environmentIddb"));
				if (triggerEnv != null) {
					setTrigger(triggerEnv);
				}
			} else if (session.getAttribute("environmentedit") != null) {

				setEnvironment((Environment) session
						.getAttribute("environmentedit"));

				setUnitSelected(getEnvironment().getData().get(0).getUnit());
				setUserGroup(userGroupDAO.findByEnvironmentId(
						getEnvironment().getIddb()).get(0));
			}
		}

		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void setUserMB(UserMB userMB) {
		this.userMB = userMB;
	}

	public Eeml getEeml() {
		if (eeml == null)
			eeml = new Eeml();
		return eeml;
	}

	public void setEeml(Eeml eeml) {
		this.eeml = eeml;
	}

	public String getEnvironmentToFind() {
		return environmentToFind;
	}

	public void setEnvironmentToFind(String environmentToFind) {
		this.environmentToFind = environmentToFind;
	}

	public Device getDeviceOfEnvironment() {
		try {
			deviceOfEnvironment = deviceDAO.findDeviceByEnvironmentId(this
					.getEnvironment().getIddb());
		} catch (NoResultException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return deviceOfEnvironment;
	}

	public Sensor getSensorOfDatastream() {
		try {
			sensorOfDatastream = sensorDAO.findSensorByDatastream(this
					.getEnvironment().getData().get(0).getIddb());
		} catch (NoResultException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return sensorOfDatastream;
	}

	public List<Unit> getAllUnits() {
		if (allUnits == null)
			allUnits = eemlManager.findAllUnits();

		return allUnits;
	}

	public Unit getUnitSelected() {
		return unitSelected;
	}

	public void setUnitSelected(Unit unitSelected) {
		this.unitSelected = unitSelected;
	}

	public Environment getEnvironment() {
		if (environment == null)
			environment = new Environment();
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public Data getData() {
		if (data == null)
			data = new Data();
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public String getAddressPortal() {
		return FacesContext.getCurrentInstance().getExternalContext()
				.getInitParameter("AddressPortal");
	}

	public String getDataActualValue() {
		try {
			if (getEnvironment().getData().get(0).getCurrentValue() != null) {
				dataActualValue = getEnvironment().getData().get(0)
						.getCurrentValue().getValue();
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			dataActualValue = "Erro ocorrido na recupera��o do valor atual";
		}

		return dataActualValue;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void saveEeml() {
		try {
			String creator = userMB.getUserName();
			this.getEnvironment().setCreator(creator);
			getEnvironment().set_private("N");
			String addressPortal = FacesContext.getCurrentInstance()
					.getExternalContext().getInitParameter("AddressPortal");

			if ((getEeml().getIddb() == 0) && (getEnvironment().getIddb() == 0)) {
				Data data = new Data();
				data.setUnit(unitSelected);

				getEnvironment().getData().add(data);
				getEeml().getEnvironment().add(environment);
				eemlManager.saveEeml(eeml);

				Sensor sensorManaged = sensorDAO
						.findSensorWithDatas(getSensorSelected().getId());
				sensorManaged.getDatas().add(data);
				sensorDAO.update(sensorManaged);

				getEnvironment().setWebsite(
						"/EcodifAPI/api/feeds/"
								+ this.getEnvironment().getIddb()
								+ "/"
								+ "datastreams/"
								+ this.getEnvironment().getData().get(0)
										.getIddb());

				getEnvironment().set_private(userGroup != null ? "Y" : "");

				this.setConnectedDevice(connectedDeviceDAO
						.findConnectedDeviceWithEnvironments(connectedDevice
								.getId()));

				this.connectedDevice.getEnvironments().add(
						this.getEnvironment());
				connectedDeviceDAO.update(this.getConnectedDevice());

			} else {
				getEnvironment().set_private(userGroup != null ? "Y" : "");
				getEnvironment().getData().get(0).setUnit(unitSelected);
				eemlManager.updateEnvironment(getEnvironment());
			}

			if (userGroup != null) {
				userGroup.setEnvironment(getEnvironment());
				userGroupDAO.update(userGroup);
			} else {
				List<UserGroup> userGroups = userGroupDAO
						.findByEnvironmentId(getEnvironment().getIddb());
				if (userGroups.size() > 0) {
					userGroups.get(0).setEnvironment(null);
					userGroupDAO.update(userGroups.get(0));
					setUserGroup(null);
				}
			}

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							"Feed cadastrado com sucesso!"));
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Feed: "
							+ this.getEnvironment().getIddb()));
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							"Datastream: "
									+ this.getEnvironment().getData().get(0)
											.getIddb()));
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							"Para enviar dados para o Feed, realize um HTTP PUT para o endere�o: "
									+ addressPortal
									+ "/EcodifAPI/api/feeds/"
									+ this.getEnvironment().getIddb()
									+ "/"
									+ "datastreams/"
									+ this.getEnvironment().getData().get(0)
											.getIddb()));
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
									"Desculpe, erro ocorrido ao cadastrar o Feed. Tente novamente por favor!"));
		}
	}

	public String getIsPrivateFeed() {
		return getEnvironment().get_private().equals("Y") ? "Privado"
				: "P�blico";
	}

	public List<Environment> getEnvironmentByUser() {
		return eemlManager.findEnvironmentsByUserLogin(userMB.getUserName());
	}

	public ConnectedDevice getConnectedDevice() {
		if (this.connectedDevice == null)
			connectedDevice = new ConnectedDevice();
		return connectedDevice;
	}

	public void setConnectedDevice(ConnectedDevice connectedDevice) {
		this.connectedDevice = connectedDevice;
	}

	public List<ConnectedDevice> getConnectedDevicesByUser() {
		List<ConnectedDevice> connDevices = new ArrayList<ConnectedDevice>();
		try {
			connDevices = connectedDeviceDAO.findConnectedDevicesByUser(userMB
					.getUserName());

			// alimentar o SelectOne de sensores, apenas na primeira vez
			if ((connDevices.size() != 0)
					&& (this.getSensorsByDevice().size() == 0))
				this.setSensorsByDevice(sensorDAO
						.findSensorsByDevice(connDevices.get(0).getDevice()
								.getId()));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return connDevices;
	}

	public Sensor getSensorSelected() {
		if (sensorSelected == null)
			sensorSelected = new Sensor();
		return sensorSelected;
	}

	public void setSensorSelected(Sensor sensorSelected) {
		this.sensorSelected = sensorSelected;
	}

	public List<Sensor> getSensorsByDevice() {

		if (sensorsByDevice == null)
			sensorsByDevice = new ArrayList<Sensor>();
		return sensorsByDevice;
	}

	public void setSensorsByDevice(List<Sensor> sensorsByDevice) {
		this.sensorsByDevice = sensorsByDevice;
	}

	public void deviceChanged(ValueChangeEvent e) {
		ConnectedDevice conDevice = (ConnectedDevice) e.getNewValue();
		this.setSensorsByDevice(sensorDAO.findSensorsByDevice(conDevice
				.getDevice().getId()));
	}

	public List<Environment> getAllEnvironments() {
		if (allEnvironments == null) {
			allEnvironments = eemlManager.findAllEnvironments();

			if (!userMB.getUserName().equals("")) {
				List<Environment> feedsPrivateGroup = eemlManager
						.findAllEnvironmentsByUser(userMB.getUserName());

				for (Environment e : feedsPrivateGroup) {
					allEnvironments.add(e);
				}
			}

		}

		return allEnvironments;
	}

	public void setAllEnvironments(List<Environment> allEnvironments) {
		this.allEnvironments = allEnvironments;
	}

	public void findAllEnvironmentByName() {
		setAllEnvironments(eemlManager
				.findEnvironmentsByTitle(getEnvironmentToFind()));
	}

	public String viewFeed(Environment selectedEnvironment) {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(
				true);
		session.setAttribute("environmentIddb", null);
		session.setAttribute("environmentedit", null);
		session.setAttribute("environmentIddb", selectedEnvironment.getIddb());
		return "feedvisualizationprovdados";
	}
	
	public String viewFeedFromregfeed() {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(
				true);
		session.setAttribute("environmentIddb", null);
		session.setAttribute("environmentedit", null);
		session.setAttribute("environmentIddb", getEnvironment().getIddb());
		return "feedvisualizationprovdados";
	}

	public String viewFeedPublic(Environment selectedEnvironment) {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(
				true);
		session.setAttribute("environmentIddb", null);
		session.setAttribute("environmentedit", null);
		session.setAttribute("environmentIddb", selectedEnvironment.getIddb());
		return "feedvisualization";
	}

	public String viewFeedDevApp(Environment selectedEnvironment) {
		ELFlash.getFlash().put("environment", selectedEnvironment);
		return "feedvisualizationdevapp";
	}

	public String editFeed(Environment selectedEnvironment) {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(
				true);
		session.setAttribute("environmentIddb", null);
		session.setAttribute("environmentedit", null);
		session.setAttribute("environmentedit", selectedEnvironment);
		return "cadfeed";
	}

	public void deleteFeed(Environment feed) {
		try {

			// Check if UserGroup is associated with the Environment
			List<UserGroup> userGroups = userGroupDAO.findByEnvironmentId(feed
					.getIddb());
			if (userGroups.size() > 0) {
				for (UserGroup ug : userGroups) {
					if (ug.getEnvironment().getIddb() == feed.getIddb()) {
						ug.setEnvironment(null);
						userGroupDAO.update(ug);
						break;
					}
				}
			}

			eemlManager.deleteEnvironment(feed);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							"Feed exclu�do com sucesso!"));
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String exceptionAsString = sw.toString();
			System.out.println("Erro: " + e.getMessage() + exceptionAsString);
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									"Desculpe, erro ocorrido ao exluir o Feed. Tente novamente por favor!"));
		}
	}

	public boolean isFeedListEmpty() {
		return getEnvironmentByUser().isEmpty();
	}
	
	public boolean isEnvironmentNull(){
		return getEnvironment().getIddb() == 0; 
	}

	public boolean isEditingForm() {
		return (getEnvironment().getIddb() == 0);
	}

	public String getValueSensorTest() {
		return valueSensorTest;
	}

	public void setValueSensorTest(String valueSensorTest) {
		this.valueSensorTest = valueSensorTest;
	}

	public String sendData() {

		try {

			HttpClient httpClient = new DefaultHttpClient();

			String addressPortal = FacesContext.getCurrentInstance()
					.getExternalContext().getInitParameter("AddressPortal");

			String addressPut = addressPortal + getEnvironment().getWebsite();

			HttpPut put = new HttpPut(addressPut);
			put.addHeader("content-type", "application/xml");

			StringEntity xml = new StringEntity(
					" <eeml xmlns=\"http://www.eeml.org/xsd/0.5.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"0.5.1\" xsi:schemaLocation=\"http://www.eeml.org/xsd/0.5.1 http://www.eeml.org/xsd/0.5.1/0.5.1.xsd\"> "
							+ "<environment> "
							+ "<data id=\""
							+ getEnvironment().getData().get(0).getIddb()
							+ "\"> "
							+ "<current_value> "
							+ getValueSensorTest()
							+ "</current_value> "
							+ "</data>" + "</environment>" + "</eeml>");

			put.setEntity(xml);

			httpClient.execute(put);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		ELFlash.getFlash().put("environment", getEnvironment());
		return "feedvisualizationprovdados";
	}

	public boolean isEmptyEnvironments() {
		return getAllEnvironments().isEmpty();
	}

	public String linkNewFeed() {
		HttpServletRequest request = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		HttpSession session = (HttpSession) request.getSession();
		session.setAttribute("environmentIddb", null);
		session.setAttribute("environmentedit", null);
		return "cadfeed";
	}

	public UserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	public String getTriggerCondition() {
		return triggerCondition;
	}

	public void setTriggerCondition(String triggerCondition) {
		this.triggerCondition = triggerCondition;
	}

	public Trigger getTrigger() {
		if (trigger == null)
			trigger = new Trigger();
		return trigger;
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}

	public void createTrigger() {
		try {
			if (getTrigger().getId() == 0) {
				getTrigger().setEnvironment(getEnvironment());
				triggerDAO.save(getTrigger());

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								"Trigger criada com sucesso!"));
			} else {
				if (getTrigger().getCondition().contains("- Sem Trigger -")) {
					triggerDAO.delete(getTrigger());
					setTrigger(new Trigger());

					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null,
									"Trigger exclu�da com sucesso!"));
				} else {
					triggerDAO.update(getTrigger());
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null,
									"Trigger atualizada com sucesso!"));
				}
			}

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String exceptionAsString = sw.toString();
			System.out.println("Erro: " + e.getMessage() + exceptionAsString);

			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									"Desculpe, erro ocorrido ao criar a Trigger. Tente novamente por favor!"));
		}
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Unit getUnit() {
		if (unit == null)
			unit = new Unit();
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public void saveUnit() {
		try {
			eemlManager.saveUnit(getUnit());
			allUnits = eemlManager.findAllUnits();
		} catch (Exception e) {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									"Desculpe, erro ocorrido ao adicionar a unidade. Tente novamente por favor!"));
		}
	}

}
