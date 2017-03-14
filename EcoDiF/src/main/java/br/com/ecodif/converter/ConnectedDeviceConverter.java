package br.com.ecodif.converter;

import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.ecodif.dao.ConnectedDeviceDAO;
import br.com.ecodif.domain.ConnectedDevice;

@FacesConverter(value = "connectedDeviceConverter")
public class ConnectedDeviceConverter implements Converter {

	@EJB
	ConnectedDeviceDAO connectedDeviceDAO;

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
		return connectedDeviceDAO.find(Integer.valueOf(value));
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object value) {
		return String.valueOf(((ConnectedDevice) value).getId());
	}
}
