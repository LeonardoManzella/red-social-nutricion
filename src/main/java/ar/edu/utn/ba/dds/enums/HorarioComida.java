package ar.edu.utn.ba.dds.enums;

import java.util.ArrayList;
import java.util.List;

public enum HorarioComida {
	DESAYUNO,
	ALMUERZO,
	MERIENDA,
	CENA;
	
	
	
	
	public static List<String> obtenerHorariosDisponibles(){
		List<String> listaHorarios = new ArrayList<String>();
		listaHorarios.add("Desayuno");
		listaHorarios.add("Almuerzo");
		listaHorarios.add("Merienda");
		listaHorarios.add("Cena");
		
		return listaHorarios;
	}

	public static HorarioComida matchearHorarios(String horario) {
		if(horario.equalsIgnoreCase("Desayuno")){
			return DESAYUNO;
			
		}else if(horario.equalsIgnoreCase("Almuerzo")){
			return ALMUERZO;
			
		}else if(horario.equalsIgnoreCase("Merienda")){
			return MERIENDA;
			
		}else if(horario.equalsIgnoreCase("Cena")){
			return CENA;
		}
		
		return null;
	}
	
	public static String devolverString(HorarioComida horario){
		switch (horario) {
			case DESAYUNO: 	return "Desayuno";
			case ALMUERZO: return "Almuerzo";
			case MERIENDA:	return "Merienda";
			case CENA: 	return "Cena";
			
			default: 		return null;
		}
	}
}
