package br.com.ecodif.mb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
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
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.rowset.serial.SerialBlob;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import br.com.ecodif.dao.ConnectedDeviceDAO;
import br.com.ecodif.dao.DeviceDAO;
import br.com.ecodif.dao.DriverDAO;
import br.com.ecodif.dao.PlatformDAO;
import br.com.ecodif.dao.SensorDAO;
import br.com.ecodif.dao.UserDAO;
import br.com.ecodif.domain.ConnectedDevice;
import br.com.ecodif.domain.Device;
import br.com.ecodif.domain.Driver;
import br.com.ecodif.domain.Platform;
import br.com.ecodif.domain.Sensor;
import br.com.ecodif.domain.User;

@ManagedBean
@ViewScoped
public class DeviceMB {

	/***************************** EJBs *****************************/

	@EJB
	private SensorDAO sensorDAO;
	@EJB
	private PlatformDAO platformDAO;
	@EJB
	private DeviceDAO deviceDAO;
	@EJB
	private UserDAO userDAO;
	@EJB
	private DriverDAO driverDAO;
	@EJB
	private ConnectedDeviceDAO connectedDeviceDAO;
	@ManagedProperty("#{userMB}")
	private UserMB userMB;

	/***************************** EJBs *****************************/

	/***************************** Variables *****************************/

	private Sensor sensorSelected;
	private Platform platformSelected;
	private Device device;
	private Device deviceConnectSelected;
	private ConnectedDevice connectedDevice;
	private Driver driver;
	private UploadedFile driverFile;
	private String deviceNameToFind;
	private List<Device> devicesByUser;
	private String connectedDeviceNameToFind;
	private List<ConnectedDevice> connectedDevicesByUser;
	private List<Device> allDevices;

	/***************************** Constructor *****************************/
	@PostConstruct
	public void init() {

		try {
			HttpServletRequest request = (HttpServletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();
			HttpSession session = (HttpSession) request.getSession();

			if (session.getAttribute("device") != null) {
				this.setDevice((Device) session.getAttribute("device"));
				this.setPlatformSelected(platformDAO.findPlatformByDevice(this
						.getDevice().getId()));
				this.setDriver(driverDAO.findDriverByPlatform(this
						.getPlatformSelected().getId()));
			}

		} catch (NoResultException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/***************************** Constructor *****************************/

	/***************************** Getters and Setters *****************************/

	public void setUserMB(UserMB userMB) {
		this.userMB = userMB;
	}

	public String getConnectedDeviceNameToFind() {
		return connectedDeviceNameToFind;
	}

	public void setConnectedDeviceNameToFind(String connectedDeviceNameToFind) {
		this.connectedDeviceNameToFind = connectedDeviceNameToFind;
	}

	public List<Device> getDevicesByUser() {
		if (devicesByUser == null)
			findDevicesByUser();
		return devicesByUser;
	}

	public void setDevicesByUser(List<Device> devicesByUser) {
		this.devicesByUser = devicesByUser;
	}

	public String getDeviceNameToFind() {
		return deviceNameToFind;
	}

	public void setDeviceNameToFind(String deviceNameToFind) {
		this.deviceNameToFind = deviceNameToFind;
	}

	public Sensor getSensorSelected() {
		if (sensorSelected == null)
			sensorSelected = new Sensor();
		return sensorSelected;
	}

	public ConnectedDevice getConnectedDevice() {
		if (connectedDevice == null)
			connectedDevice = new ConnectedDevice();
		return connectedDevice;
	}

	public void setConnectedDevice(ConnectedDevice connectedDevice) {
		this.connectedDevice = connectedDevice;
	}

	public Device getDeviceConnectSelected() {
		return deviceConnectSelected;
	}

	public void setDeviceConnectSelected(Device deviceConnectSelected) {
		this.deviceConnectSelected = deviceConnectSelected;
	}

	public void setSensorSelected(Sensor sensorSelected) {
		this.sensorSelected = sensorSelected;
	}

	public List<Sensor> getSensorList() {
		return sensorDAO.findAll();
	}

	public List<Platform> getPlatformList() {
		return platformDAO.findAll();
	}

	public Device getDevice() {
		if (device == null)
			device = new Device();
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Platform getPlatformSelected() {
		return platformSelected;
	}

	public void setPlatformSelected(Platform platformSelected) {
		this.platformSelected = platformSelected;
	}

	public List<Sensor> getSensorsDeviceSelected() {
		return getDevice().getSensors();
	}

	public List<Device> getAllDevices() {
		if (allDevices == null)
			allDevices = deviceDAO.findAll();
		return allDevices;
	}

	public void setAllDevices(List<Device> allDevices) {
		this.allDevices = allDevices;
	}

	public Driver getDriver() {
		if (driver == null)
			driver = new Driver();
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public UploadedFile getDriverFile() {
		return driverFile;
	}

	public void setDriverFile(UploadedFile driverFile) {
		this.driverFile = driverFile;
	}

	public List<ConnectedDevice> getConnectedDevicesByUser() {
		if (connectedDevicesByUser == null)
			findConnectedDevicesByUser();
		return connectedDevicesByUser;
	}

	public void setConnectedDevicesByUser(
			List<ConnectedDevice> connectedDevicesByUser) {
		this.connectedDevicesByUser = connectedDevicesByUser;
	}

	public StreamedContent getFile() {
		StreamedContent file = null;

		if (this.getDriver().getId() != 0) {

			try {

				/*File fileDownload = new File(this.getDriver()
						.getLocationInDirectory());
				FileInputStream stream = new FileInputStream(fileDownload); */
				
				InputStream stream = this.getDriver().getDriverFile().getBinaryStream(1, this.getDriver().getDriverFile().length());

				file = new DefaultStreamedContent(stream, "",
						this.getDriver().getFileName());

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		return file;
	}

	public StreamedContent getFileToProvedor() {
		Driver driverUser = null;

		try {
			driverUser = driverDAO.findDriverByDevice(this
					.getDeviceConnectSelected().getId());
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							"Desculpe, erro ocorrido ao localizar o driver."));
		}
		StreamedContent file = null;

		if (driverUser == null || driverUser.getDescription() == null) {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									"O driver para o dispositivo ainda n�o foi cadastrado pelo fabricante"));
		} else {
			try {

				
				
				InputStream stream = driverUser.getDriverFile().getBinaryStream(1, driverUser.getDriverFile().length());

				file = new DefaultStreamedContent(stream,
						"application/vnd.android.package-archive",
						driverUser.getFileName());

			} catch (Exception e) {
				System.out.println(e.getMessage());
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR,
										null,
										"Desculpe, erro ocorrido ao localizar o driver. Tente novamente por favor."));
			}
		}

		return file;
	}

	/***************************** Getters and Setters *****************************/

	/***************************** Operations *****************************/

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void saveDevice() {
		try {
			User user = userDAO.findByLogin(userMB.getUserName());
			device.setCompany(user);
			/** Device **/
			if (device.getId() == 0)
				deviceDAO.save(device);
			else
				deviceDAO.update(device);

			/** Platform **/
			Platform platformManaged = platformDAO
					.findPlatformWithDevices(platformSelected.getId());
			platformManaged.getDevices().add(device);
			platformDAO.update(platformManaged);

			this.setDriver(driverDAO.findDriverByPlatform(this
					.getPlatformSelected().getId()));

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							"Dispositivo cadastrado com sucesso!"));

		} catch (Exception e) {
			System.out.println(e.getMessage());
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									"Desculpe, erro ocorrido ao incluir o dispositivo. Tente novamente por favor!"));
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void saveDriver() {

		/** Driver File **/
		if (driverFile != null) {

			try {

			
				
				Blob blob = new SerialBlob(driverFile.getContents());

				this.getDriver().setDriverFile(blob);
				this.getDriver().getPlatforms().add(platformSelected);
				this.getDriver().setFileName(driverFile.getFileName());

				

				this.getDriver().setLastUpdate(new java.sql.Date(System.currentTimeMillis()));
				if (this.getDriver().getId() == 0) {
					driverDAO.save(this.getDriver());
				} else {
					driverDAO.update(this.getDriver());
				}

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								"Driver atualizado com sucesso!"));

			} catch ( SQLException e) {
				System.out.println(e.getMessage());
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR,
										null,
										"Desculpe, erro ocorrido ao incluir o driver. Tente novamente por favor!"));
			}
		} else {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							"Selecione um driver para upload!"));
		}

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void connectDevice() {

		try {
			User user = userDAO.findByLogin(userMB.getUserName());
			connectedDevice.setUser(user);
			connectedDevice.setDevice(deviceConnectSelected);

			if (connectedDevice.getId() == 0)
				connectedDeviceDAO.save(connectedDevice);
			else
				connectedDeviceDAO.update(connectedDevice);

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							"Dispositivo conectado com sucesso!"));

		} catch (Exception e) {
			System.out.println(e.getMessage());
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									"Desculpe, erro ocorrido ao conectar o dispositivo. Tente novamente por favor!"));
		}
	}

	public void copyFile(String fileName, InputStream in) {
		try {

			// write the inputStream to a FileOutputStream
			OutputStream out = new FileOutputStream(new File(fileName));

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = in.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}

			in.close();
			out.flush();
			out.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void disconnectDevice(ConnectedDevice p_connectedDevice) {
		try {

			if (connectedDeviceDAO.hasEnvironments(p_connectedDevice.getId())) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										null,
										"Existem feeds criados para este dispositivo. Para desconect�-lo, primeiro exclua os feeds."));
			} else {
				connectedDeviceDAO.delete(p_connectedDevice);

				this.setConnectedDevicesByUser(connectedDeviceDAO
						.findConnectedDevicesByUser(userMB.getUserName()));

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								"Dispositivo desconectado com sucesso!"));
			}
		} catch (Exception e) {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									null,
									"Desculpe, erro ocorrido ao tentar desconectar o dispositivo. Tente novamente por favor"));
		}
	}

	public void findConnectedDevicesByUser() {
		this.connectedDevicesByUser = connectedDeviceDAO
				.findConnectedDevicesByUser(userMB.getUserName());
	}

	public void newDevice() {
		this.setDevice(new Device());
		this.setDriver(null);
	}

	public void findDevicesByUserAndDeviceName() {
		if (deviceNameToFind != "")
			this.setDevicesByUser(this.deviceDAO.findDevicesByUserAndName(
					userMB.getUserName(), deviceNameToFind));
		else
			this.setDevicesByUser(this.deviceDAO.findDevicesByUser(userMB
					.getUserName()));
	}

	public void findAllDevicesDeviceName() {
		if (deviceNameToFind != "")
			this.setAllDevices(deviceDAO.findAllDeviceByName(deviceNameToFind));
		else
			this.setAllDevices(deviceDAO.findAll());
	}

	public void removeDevice(Device dev) {
		try {
			deviceDAO.delete(dev);
			this.getDevicesByUser().remove(dev);
			this.setDevice(new Device());
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							"Dispositivo removido com sucesso!"));
		} catch (Exception e) {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									"Desculpe, erro ocorrido ao remover o dispositivo!"));
		}
	}

	public void addSensorDevice() {
		if (!getSensorsDeviceSelected().contains(sensorSelected))
			getSensorsDeviceSelected().add(sensorSelected);
	}

	public void removeSensorDevice(Sensor sensor) {
		getSensorsDeviceSelected().remove(sensor);
		if (getSensorsDeviceSelected().isEmpty())
			device.setSensors(null);
	}

	public String editDevice(Device selectedDevice) {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(
				true);
		session.setAttribute("device", selectedDevice);
		return "editDevice";
	}

	public void removeDriver() {
		try {
			driverDAO.delete(driver);
			/*File file = new File(this.getDriver().getLocationInDirectory());
			file.delete();*/
			this.setDriver(null);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void findConnectedDevicesByName() {
		if (this.getDeviceNameToFind() != "") {
			this.setConnectedDevicesByUser(connectedDeviceDAO
					.findConnectedDevicesByNameAndUser(
							connectedDeviceNameToFind, userMB.getUserName()));
		} else {
			this.setConnectedDevicesByUser(connectedDeviceDAO
					.findConnectedDevicesByUser(userMB.getUserName()));
		}
	}

	/**
	 * Limpa a sess�o contendo um dispositivo. Utilizado quando a p�gina �
	 * aberta para a inclus�o de um novo dispositivo
	 * 
	 * @return caddevice
	 */
	public String linkNewDevice() {
		HttpServletRequest request = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		HttpSession session = (HttpSession) request.getSession();
		session.setAttribute("device", null);

		return "caddevice";
	}

	/***************************** Operations *****************************/

	/***************************** Boolean Verifications *****************************/

	public boolean isSensorsSelectedEmpty() {
		return getSensorsDeviceSelected().isEmpty();
	}

	public boolean isDeviceListEmpty() {
		return deviceDAO.findDevicesByUser(userMB.getUserName()).isEmpty();
	}

	public boolean isDeviceListPublicEmpty() {
		return getAllDevices().isEmpty();
	}

	public void findDevicesByUser() {
		this.setDevicesByUser(deviceDAO.findDevicesByUser(userMB.getUserName()));
	}

	public boolean isDeviceRegistered() {
		return !(this.getDevice().getId() == 0);
	}

	public boolean isDriverFileUploaded() {
		if (this.getDriver().getId() == 0) {
			return false;
		} else {

			return !(this.driverDAO.find(this.getDriver().getId())
					.getDriverFile() == null);
		}
	}

	public boolean isDeviceAlreadyConnected() {
		if (this.getConnectedDevice().getId() == 0)
			return false;
		else
			return true;
	}

	public boolean isConnectedDevicesByUserEmpty() {
		return !connectedDeviceDAO.findConnectedDevicesByUser(
				userMB.getUserName()).isEmpty();
	}

	/***************************** Boolean Verifications *****************************/

}
