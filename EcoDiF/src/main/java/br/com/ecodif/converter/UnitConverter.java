package br.com.ecodif.converter;

import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import br.com.ecodif.framework.EemlManager;
import br.com.ecodif.domain.Unit;;

@FacesConverter(value = "unitConverter")
public class UnitConverter implements Converter {

	@EJB
	EemlManager manager;

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
		return manager.findUnitById(Integer.valueOf(value));
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object value) {
		return String.valueOf(((Unit) value).getIddb());
	}
}
