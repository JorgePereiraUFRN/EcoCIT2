package br.com.ecodif.dao;

import java.util.Date;
import java.util.List;

import br.com.ecodif.domain.Value;

public class TestValueDao {

	public static void main(String[] args) {

		ValueDao2 dao2 = new ValueDao2();

		Value value = new Value();

		value.setAt(new Date());
		value.setDataId(2);
		value.setValue("120");
		
		dao2.save(value);

		List<Value> values = dao2.findValuesByDataId(1);

		for (Value v : values) {
			System.out.println(v);
		}

		
	}

}
