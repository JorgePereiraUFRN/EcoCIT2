package br.com.ecodif.converter;

import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import br.com.ecodif.dao.PlatformDAO;
import br.com.ecodif.domain.Platform;

@FacesConverter(value = "platformConverter")
public class PlatformConverter implements Converter {

	@EJB
	PlatformDAO platformDAO;

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
		return platformDAO.find(Integer.valueOf(value));
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object value) {
		return String.valueOf(((Platform) value).getId());
	}
}
