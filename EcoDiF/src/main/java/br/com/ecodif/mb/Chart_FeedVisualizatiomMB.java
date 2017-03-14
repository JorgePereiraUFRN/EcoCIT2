package br.com.ecodif.mb;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

import br.com.ecodif.dao.ValueDao;
import br.com.ecodif.domain.Environment;
import br.com.ecodif.domain.Value;
import br.com.ecodif.framework.EemlManager;

@ManagedBean
@ViewScoped
public class Chart_FeedVisualizatiomMB {

	private CartesianChartModel categoryModel;
	private String atualValue;
	private float minValue;
	private float maxValue;
	
	@EJB
	private ValueDao valueDao;

	@EJB
	EemlManager eemlManager;

	public CartesianChartModel getCategoryModel() {
		return categoryModel;
	}

	@PostConstruct
	public void createCategoryModel() {
		categoryModel = new CartesianChartModel();
		ChartSeries valuesChart = new ChartSeries();
		float actualValue = 0F;
		Environment env;

		HttpServletRequest request = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		HttpSession session = (HttpSession) request.getSession();

		try {
			if (session.getAttribute("environmentIddb") != null) {
				env = (eemlManager.findEnvironmentById(((Integer) session
						.getAttribute("environmentIddb"))));

				if (env.getData() != null) {

					List<Value> values = valueDao.findValuesByDataId(env
							.getData().get(0).getIddb());

					Collections.reverse(values);

					if (values.size() > 0) {

						atualValue = values.get(values.size() - 1).getValue();

						String dataAt = "";
						int dataAtN = 0;

						minValue = Float.valueOf(values.get(0).getValue());
						maxValue = Float.valueOf(values.get(0).getValue());

						for (Value v : values) {
							if (v.getAt() != null) {
								SimpleDateFormat dateFormat = new SimpleDateFormat(
										"HH:mm:ss");
								dataAt = dateFormat.format(v.getAt().getTime());

							} else {
								dataAt = String.valueOf(++dataAtN);
							}

							actualValue = Float.valueOf(v.getValue());
							valuesChart.set(dataAt, actualValue);
							dataAt = "";

							if (actualValue < minValue)
								minValue = actualValue;

							if (actualValue > maxValue)
								maxValue = actualValue;
						}

					}
				}
			}
		} catch (Exception e) {
			e.getMessage();
		}

		categoryModel.addSeries(valuesChart);
	}

	public boolean isListEmpty() {
		return getAtualValue() == null;
	}

	public String getAtualValue() {
		return atualValue;
	}

	public void setAtualValue(String atualValue) {
		this.atualValue = atualValue;
	}

	public float getMinValue() {
		return minValue;
	}

	public void setMinValue(float minValue) {
		this.minValue = minValue;
	}

	public float getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
	}

}
