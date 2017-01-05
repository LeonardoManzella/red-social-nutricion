package ar.edu.utn.ba.dds.enums;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public enum Temporada {
	INVIERNO,
	VERANO,
	PRIMAVERA,
	OTONIO;
	
	public static Temporada getTemporadaByFecha(Calendar fecha) {
		
		int month = fecha.get(Calendar.MONTH);
		int day = fecha.get(Calendar.DAY_OF_MONTH);
		
		SimpleDateFormat fechaFormat = new SimpleDateFormat("MM-dd");
		fechaFormat.setLenient(false);
		
		try {
			Date date = fechaFormat.parse(month + "-" + day);
			
			if(date.after(fechaFormat.parse("21-6")) && date.before(fechaFormat.parse("20-9"))) {
				return INVIERNO;
			}
			else if(date.after(fechaFormat.parse("21-9")) && date.before(fechaFormat.parse("20-12"))) {
				return PRIMAVERA;
			}
			else if(date.after(fechaFormat.parse("21-12")) && date.before(fechaFormat.parse("20-3"))) {
				return VERANO;
			}
			else if(date.after(fechaFormat.parse("21-3")) && date.before(fechaFormat.parse("20-6"))) {
				return OTONIO;
			}
			else {
				return null;
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Temporada matchearTemporada(String temporada) {
		switch (temporada) {
				case "invierno": case "Invierno" : case "INVIERNO" :
					return INVIERNO;
				case "primavera": case "Primavera" : case "PRIMAVERA" :
					return PRIMAVERA;
				case "verano": case "Verano" : case "VERANO" :
					return VERANO;
				case "otonio": case "Otonio" : case "OTONIO" :
					return OTONIO;
					
				default:
					return null;
		}
	}
	
	public static String devolverString(Temporada temporada){
		switch (temporada) {
			case INVIERNO: 	return "Invierno";
			case PRIMAVERA: return "Primavera";
			case VERANO:	return "Verano";
			case OTONIO: 	return "Otonio";
			
			default: 		return null;
		}
	}
	
	public static List<String> obtenerTemporadasDisponibles(){
		List<String> listaTemporadas = new ArrayList<String>();
		listaTemporadas.add("Invierno");
		listaTemporadas.add("Primavera");
		listaTemporadas.add("Verano");
		listaTemporadas.add("Otonio");
		
		return listaTemporadas;
	}
}
