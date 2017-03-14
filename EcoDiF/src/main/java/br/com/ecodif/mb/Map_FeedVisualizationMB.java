package br.com.ecodif.mb;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import br.com.ecodif.domain.Environment;
import br.com.ecodif.framework.EemlManager;

@ManagedBean
@ViewScoped
public class Map_FeedVisualizationMB {
	
	private MapModel simpleModel;
	private Environment environment;
	
	@EJB
	EemlManager eemlManager;

	@PostConstruct
	public void init() {
		simpleModel = new DefaultMapModel();

		try {

			HttpServletRequest request = (HttpServletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();
			HttpSession session = (HttpSession) request.getSession();

			if (session.getAttribute("environmentIddb") != null) {
				environment = (eemlManager.findEnvironmentById(((Integer) session.getAttribute("environmentIddb"))));
			} else if (session.getAttribute("environmentedit") != null) {
				environment = ((Environment) session.getAttribute("environmentedit"));
			}
			
			LatLng coord1 = new LatLng(environment.getLocation().getLat(), environment
					.getLocation().getLon());

			// Basic marker
			simpleModel.addOverlay(new Marker(coord1, environment.getLocation().getName()));

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public MapModel getSimpleModel() {
		return simpleModel;
	}

}
