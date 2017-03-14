package br.com.ecodif.converter;

import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import br.com.ecodif.dao.SensorDAO;
import br.com.ecodif.domain.Sensor;

@FacesConverter(value = "sensorConverter")
public class SensorConverter implements Converter {

	@EJB
	SensorDAO sensorDAO;

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
		return sensorDAO.find(Integer.valueOf(value));
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object value) {
		return String.valueOf(((Sensor) value).getId());
	}
}
