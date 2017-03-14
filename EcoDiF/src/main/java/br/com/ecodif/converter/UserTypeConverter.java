package br.com.ecodif.converter;

import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import br.com.ecodif.dao.UserTypeDAO;
import br.com.ecodif.domain.UserType;

@FacesConverter(value="userTypeConverter")
public class UserTypeConverter implements Converter {

	@EJB
	UserTypeDAO userTypeDao;

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
		return userTypeDao.find(Integer.valueOf(value));
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object value) {
		return String.valueOf(((UserType) value).getId());
	}
}
