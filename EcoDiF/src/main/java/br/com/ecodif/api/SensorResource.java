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

import br.com.ecodif.domain.Sensor;
import br.com.ecodif.service.SensorService;

/**
 * Classe responsável pela API da EcoDiF com recursos relacionados aos drivers.
 * 
 * @author Andreza Lima (andrezapbl1@gmail.com)
 *
 */
@Path("sensor")
public class SensorResource {

	@Inject
	private SensorService sensorService = new SensorService();

	@POST
	@RolesAllowed("PROV_DADOS")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response registerSensor(Sensor sensor) {
		try {
			if (sensor != null) {
				sensorService.saveSensor(sensor);
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
	public Response getAllSensors() {

		try {	
			List<Sensor> sensors = sensorService.getAllSensors();

			if (sensors.isEmpty())
				return Response.status(404).build();

			GenericEntity<List<Sensor>> entity = new GenericEntity<List<Sensor>>(
					sensors) {
			};
			return Response.ok(entity).build();

		} catch (Exception e) {
			return Response.serverError().build();
		}
	}

}
