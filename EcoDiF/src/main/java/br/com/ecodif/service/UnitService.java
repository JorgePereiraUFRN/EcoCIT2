package br.com.ecodif.service;

import java.util.List;

import javax.inject.Inject;

import br.com.ecodif.domain.Unit;
import br.com.ecodif.framework.EemlManager;

public class UnitService {


	@Inject
	private EemlManager eemlMng = new EemlManager();

	public void saveUnit(Unit unit) {
		eemlMng.saveUnit(unit);
	}

	public List<Unit> getAllUnits(){
		return eemlMng.findAllUnits();
	}
	
	public Unit findUnitById(int id) {
		return eemlMng.findUnitById(id);
	}
}
