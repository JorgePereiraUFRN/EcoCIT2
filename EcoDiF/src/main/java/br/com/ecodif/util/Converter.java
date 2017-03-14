package br.com.ecodif.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Converter {


	/**
	 * Método para converter uma string em data.
	 * 
	 * @param dateInString
	 *            - data a ser convertida
	 * @return data convertida.
	 */
	public Date transformStringToDate(String dateInString) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		Date date = null;
		try {
			date = formatter.parse(dateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

}
