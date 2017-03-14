package br.com.ecodif.api;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.List;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.sql.rowset.serial.SerialBlob;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import br.com.ecodif.domain.ConnectedDevice;
import br.com.ecodif.domain.Device;
import br.com.ecodif.domain.Driver;
import br.com.ecodif.domain.Platform;
import br.com.ecodif.domain.User;
import br.com.ecodif.helper.FileHelper;
import br.com.ecodif.helper.OwnerHelper;
import br.com.ecodif.service.DeviceService;
import br.com.ecodif.service.DriverService;
import br.com.ecodif.service.PlatformService;
import br.com.ecodif.service.UserService;

/**
 * Classe responsável pela API da EcoDiF com recursos relacionados aos
 * dispositivos
 *
 * @author Andreza Lima (andrezapbl1@gmail.com)
 *
 */
@Path("device")
public class DeviceResource extends OwnerHelper {

	/**
	 * Referência para a classe de serviço de um usuário.
	 *
	 * @see br.com.ecodif.service.UserService
	 * @see br.com.ecodif.domain.User
	 */
	@Inject
	private DeviceService deviceService = new DeviceService();

	/**
	 * Referência para a classe de serviço de um dispositivo
	 *
	 * @see br.com.ecodif.service.DeviceService
	 * @see br.com.ecodif.domain.Device
	 */
	@Inject
	private UserService userService = new UserService();

	@Inject
	private FileHelper fileHelper = new FileHelper();

	@Context
	protected ServletContext context;

	@Inject
	private PlatformService platformService = new PlatformService();

	@Inject
	private DriverService driverService = new DriverService();

	/**
	 * @Context protected ServletContext context;
	 * 
	 * 
	 *          public DeviceResource() { super("Device"); }
	 * 
	 *          /** Método responsável por buscar todos os dispostivos.
	 *
	 * @return status 200 (ok) contendo os dispositivos caso a busca tenha sido
	 *         feita com sucesso <br/>
	 *         <i> status 404 (not found) </i> caso não seja encontrado nenhum
	 *         dispositivo. <br/>
	 *         <i> status 500 (server error) </i> caso o servidor lance alguma
	 *         exceção
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Wrapped(element = "devices")
	@PermitAll
	public Response getAllDevices() {
		List<Device> devices = null;
		try {
			devices = deviceService.findAllDevices();
			if (devices.isEmpty())
				return Response.status(404).build();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return Response.status(500).build();
		}
		GenericEntity<List<Device>> entity = new GenericEntity<List<Device>>(
				devices) {
		};

		return Response.ok(entity).build();
	}

	/**
	 * Método responsável por registar um novo dispositivo.
	 *
	 * @param device
	 *            - dispositivo a ser registrado.
	 * @return <i>status 200(ok)</i> contendo o seu id, caso o dispositivo seja
	 *         registrado com sucesso. </br> <i> status 500(server error) </i>
	 *         caso seja lançado alguma exceção.
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@RolesAllowed("FAB_DISP")
	public Response registerDevice(Device device) {
		try {
			User user = userService.findUserByLogin(getUser());
			device.setCompany(user);
			deviceService.saveDevice(device);
			Platform platformManaged = deviceService
					.findPlatformWithDevices(device.getPlatform());
			platformManaged.getDevices().add(device);
			deviceService.updatePlatform(platformManaged);
			return Response.ok(device.getId()).build();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return Response.serverError().build();
		}
	}

	/**
	 * Método responsável por buscar todos os dispositivos de um usuário.
	 *
	 * @param username
	 *            - usuário que servira para a busca.
	 * @return status 200 (ok) contendo os dispositivos caso a busca tenha sido
	 *         feita com sucesso <br/>
	 *         <i> status 404 (not found) </i> caso não seja encontrado nenhum
	 *         dispositivo. <br/>
	 *         <i> status 500 (server error) </i> caso o servidor lance alguma
	 *         exceção
	 */
	@GET
	@Path("/user")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@PermitAll
	public Response getAllDevicesByUser() {
		List<Device> devices = null;
		try {
			String username = this.getUser();
			devices = deviceService.findDevicesByUser(username);
			if (devices.isEmpty())
				return Response.status(404).build();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return Response.status(500).build();
		}

		GenericEntity<List<Device>> entity = new GenericEntity<List<Device>>(
				devices) {
		};
		return Response.ok(entity).build();

	}

	/**
	 * Métoodo responsável por deletar um dispositivo.
	 *
	 * @param deviceid
	 *            - id do dispositivo.
	 * @return <i>status 200(ok)</i> caso o dispositivo seja deletado com
	 *         sucesso. </br> <i> status 401(not authorized) </i> caso o usuário
	 *         que está tentando acessar o recurso não seja dono do dispositivo.
	 *         </br> <i> status 404(not found) </i> caso não exista dispositivo
	 *         com o id passado. </br> <i> status 500(server error) </i> caso
	 *         seja lançada alguma exceção.
	 */
	@DELETE
	@Path("/{deviceid : \\d+}")
	@RolesAllowed("FAB_DISP")
	public Response deleteDevice(@PathParam("deviceid") int deviceid) {
		try {
			Device device = deviceService.findDevice(deviceid);
			if (device == null)
				return Response.status(404).build();
			if (isowner(device)) {
				deviceService.deleteDevice(device);
				return Response.ok().build();
			}
			return Response.status(401).build();

		} catch (Exception e) {
			return Response.serverError().build();
		}
	}

	/**
	 * Método reponsável por buscar um dispositivo pelo id.
	 *
	 * @param deviceid
	 *            - id que servirá para a busca.
	 * @return <i>status 200(ok)</i> caso o dispositivo seja encontrado com
	 *         sucesso. </br> <i> status 404(not found) </i> caso não exista
	 *         dispositivo com o id passado.
	 */
	@GET
	@PermitAll
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/{deviceid : \\d+}")
	public Response getDeviceById(@PathParam("deviceid") int deviceid) {
		Device device = deviceService.findDevice(deviceid);
		if (device == null)
			Response.status(404).build();

		return Response.ok(device).build();

	}

	/**
	 * Método responsável por conectar um dispositivo.
	 * 
	 * @param deviceid
	 *            - id do dispositivo
	 * @param connectedDevice
	 *            - objeto contendo o nome e a descrição da conexão.
	 * @return <i>status 200(ok)</i> caso o dispositivo seja conectado com
	 *         sucesso. </br> <i> status 404(not found) </i> caso não exista
	 *         dispostivo com o id passado. </br> <i> status 500(server error)
	 *         </i> caso seja lançado alguma exceção.
	 */
	@POST
	@Path("/connect/{deviceid : \\d+}")
	@RolesAllowed("PROV_DADOS")
	public Response connectDevice(@PathParam("deviceid") int deviceid,
			ConnectedDevice connectedDevice) {

		try {
			Device device = deviceService.findDevice(deviceid);

			if (device == null)
				return Response.status(404).build();

			connectedDevice.setUser(userService.findUserByLogin(getUser()));
			connectedDevice.setDevice(device);

			deviceService.saveCnnDevice(connectedDevice);
			return Response.ok().build();

		} catch (Exception e) {
			System.out.println(e.getMessage());
			return Response.serverError().build();
		}
	}

	/**
	 * Método responsável por fazer upload do driver de uma plataforma de
	 * dispositivo
	 * 
	 * @param deviceid
	 *            - id do dispositivo
	 * @return <i>status 200(ok)</i> caso o upload do driver tenha sido feito
	 *         com sucesso. </br> <i> status 404(not found) </i> caso não exista
	 *         dispostivo com o id passado. </br> <i> status 500(server error)
	 *         </i> caso seja lançado alguma exceção.
	 */
	@POST
	@Path("/{deviceid : \\d+}/driver")
	@RolesAllowed("FAB_DISP")
	@Consumes("multipart/form-data")
	public Response uploadDriver(MultipartFormDataInput driverFile,
			@PathParam("deviceid") int deviceid) {

		try {
			Device device = deviceService.findDevice(deviceid);
			String username = this.getUser();

			if (device == null || driverFile == null)
				return Response.status(404).build();

			Map<String, List<InputPart>> uploadForm = driverFile
					.getFormDataMap();

			String driverFileName = uploadForm.get("fileName").get(0)
					.getBodyAsString();
			String driverFileDescription = uploadForm.get("description").get(0)
					.getBodyAsString();
			String driverFileVersion = uploadForm.get("version").get(0)
					.getBodyAsString();
			List<InputPart> inputParts = uploadForm.get("file");

			String fileName = context.getInitParameter("DriversDirectory")
					+ username + "_" + driverFileName;

			int platformId = device.getPlatform();
			Platform platform = platformService.findById(platformId);
			Driver driver = new Driver();
			
			
			
			//driver.setLocationInDirectory(fileName);
			driver.setFileName(fileName);
			driver.setName(driverFileName);
			driver.setDescription(driverFileDescription);
			driver.setVersion(driverFileVersion);
			driver.getPlatforms().add(platform);
			

			for (InputPart inputPart : inputParts) {
				InputStream inputStream = inputPart.getBody(InputStream.class,
						null);
				
				fileHelper.copyFile(inputStream, fileName);
			}

			driverService.save(driver);

			return Response.ok().build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	}

}