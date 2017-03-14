package br.com.ecodif.api;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.ecodif.domain.Platform;
import br.com.ecodif.helper.OwnerHelper;
import br.com.ecodif.service.PlatformService;

/**
 * Classe responsável pela API da EcoDiF com recursos relacionados aos
 * dispositivos
 *
 * @author Andreza Lima (andrezapbl1@gmail.com)
 *
 */
@Path("platform")
public class PlatformResource extends OwnerHelper {

	@Inject
	private PlatformService platformService = new PlatformService();

	/**
	 * Método responsável por criar uma nova plataforma para dispositivos
	 * 
	 * @return <i>status 200(ok)</i> caso o upload do driver tenha sido feito
	 *         com sucesso. </br> <i> status 404(not found) </i> caso não exista
	 *         dispostivo com o id passado. </br> <i> status 500(server error)
	 *         </i> caso seja lançado alguma exceção.
	 */
	@POST
	@RolesAllowed("FAB_DISP")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response registerPlatform(Platform platform) {
		try {
			if (platform == null)
				return Response.status(400).build();

			platformService.savePlatform(platform);
			return Response.ok().build();
		} catch (Exception e) {
			return Response.serverError().build();
		}
	}

	@GET
	@PermitAll
	@Path("/{platformid}/devices")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response findDevicesbyPlatform(@PathParam("platformid") int platform_id) {
		try {
			Platform platform = platformService
					.findDevicesWithPlatform(platform_id);
			if (platform == null)
				return Response.status(404).build();
			return Response.ok(platform).build();
		} catch (Exception e) {
			System.out.println(e);
			return Response.serverError().build();
		}
	}

}