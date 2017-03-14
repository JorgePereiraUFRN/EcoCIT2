package br.com.ecodif.converter;

import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.ecodif.dao.DeviceDAO;
import br.com.ecodif.domain.Device;

@FacesConverter(value = "deviceConverter")
public class DeviceConverter implements Converter {

	@EJB
	DeviceDAO deviceDAO;

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
		return deviceDAO.find(Integer.valueOf(value));
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object value) {
		return String.valueOf(((Device) value).getId());
	}
}
