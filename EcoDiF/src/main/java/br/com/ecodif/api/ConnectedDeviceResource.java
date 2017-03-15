package br.com.ecodif.api;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;

import br.com.ecodif.domain.ConnectedDevice;
import br.com.ecodif.helper.OwnerHelper;
import br.com.ecodif.service.DeviceService;

/**
 * Classe responsável pela API da EcoDiF com recursos relacionados aos
 * dispositivos conectados.
 * 
 * @author Andreza Lima (andrezapbl1@gmail.com)
 *
 */
@Path("conndevice")
public class ConnectedDeviceResource extends OwnerHelper {

	/**
	 * Referência para a classe de serviço de um dispositivo
	 * 
	 * @see br.com.ecodif.service.DeviceService
	 * @see br.com.ecodif.domain.Device
	 */
	@Inject
	private DeviceService deviceService = new DeviceService();

	public ConnectedDeviceResource() {
		super("ConnectedDevice");
	}

	/**
	 * Método responsável por buscar todos os dispositivos conectados de um
	 * usuário. O usuário passado deve ser o que está acessando o recurso.
	 * 
	 * @param username
	 *            - usuário que será utilizado para a busca.
	 * @return Lista de dispositivos conectados.
	 */
	@GET
	@Path("/user")
	@PermitAll
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Wrapped(element = "connectedDevices")
	public Response getAllDevicesConnectedByUser(@HeaderParam("user") String user) {
		List<ConnectedDevice> devices = null;
		try {
			String username = user;
			devices = deviceService.findConnectedDevicesByUser(username);
			if(devices == null)
				return Response.status(404).build();
			GenericEntity<List<ConnectedDevice>> entity = new GenericEntity<List<ConnectedDevice>>(
					devices) {
			};
			
			return Response.ok(entity).build();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return Response.serverError().build();
		}		
	}

	/**
	 * Método responsável por desconectar um dispositivo.
	 * 
	 * @param deviceid
	 *            - id do dispositivo.
	 * @return <i>status 200(ok)</i> caso o dispositivo seja desconectado com
	 *         sucesso. </br> <i> status 401(not authorized) </i> caso o usuário
	 *         que está tentando acessar o recurso não seja dono do dispositivo.
	 *         </br> <i> status 404(not found) </i> caso não exista dispositivo
	 *         com o id passado. </br> <i> status 500(server error) </i> caso o
	 *         dispositivo passado tenha feeds.
	 */
	@PUT
	@Path("/disconnect/{deviceid}")
	@RolesAllowed("PROV_DADOS")
	public Response disconnectDevice(@PathParam("deviceid") int deviceid, @HeaderParam("user") String user) {

		ConnectedDevice cnndevice = deviceService
				.findConnectedDeviceById(deviceid);
		if (cnndevice == null)
			return Response.status(404).build();

		try {
			if (isowner(cnndevice, user)) {
				if (deviceService.connectedDeviceHasEnvironment(deviceid)) {
					return Response
							.notModified()
							.entity("Existem feeds criados para este dispositivo. "
									+ "Para desconectá-lo, primeiro exclua os feeds.")
							.build();
				} else {
					deviceService.deleteConnectedDevice(cnndevice);
					return Response.ok().build();
				}
			}
		} catch (Exception e) {
			return Response.serverError().build();
		}
		return Response.status(401).build();
	}

}
