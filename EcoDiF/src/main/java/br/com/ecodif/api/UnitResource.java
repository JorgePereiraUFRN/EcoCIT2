package br.com.ecodif.api;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.ecodif.domain.Unit;
import br.com.ecodif.service.UnitService;

/**
 * Classe responsável pela API da EcoDiF com recursos relacionados as unidades.
 * 
 * @author Andreza Lima (andrezapbl1@gmail.com)
 *
 */
@Path("unit")
public class UnitResource {

	@Inject
	UnitService unitService = new UnitService();

	@POST
	@RolesAllowed("PROV_DADOS")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response registerUnit(Unit unit) {
		try {
			if (unit != null) {
				unitService.saveUnit(unit);
				return Response.ok().build();
			}
		} catch (Exception e) {
			return Response.serverError().build();
		}

		return Response.notModified().build();

	}

	@GET
	@PermitAll
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getAllUnits() {

		try {
			List<Unit> units = unitService.getAllUnits();
			
			if(units.isEmpty())
				return Response.status(404).build();
			
			
			GenericEntity<List<Unit>> entity = new GenericEntity<List<Unit>>(
					units) {
			};
			
			return Response.ok(entity).build();
		} catch (Exception e) {
			return Response.serverError().build();
		}
	}
}
