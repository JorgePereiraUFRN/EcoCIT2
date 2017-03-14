package br.com.ecodif.converter;

import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import br.com.ecodif.dao.UserGroupDAO;
import br.com.ecodif.domain.UserGroup;

@FacesConverter(value = "userGroupConverter")
public class UserGroupConverter implements Converter {

	@EJB
	UserGroupDAO userGroupDao;

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
		if (!value.equals(""))
			return userGroupDao.find(Integer.valueOf(value));
		else
			return null;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object value) {
		if (value != null)
			return String.valueOf(((UserGroup) value).getId());
		else
			return "";
	}
}
