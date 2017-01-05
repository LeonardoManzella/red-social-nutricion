package ar.edu.utn.ba.dds.enums;

import java.util.ArrayList;
import java.util.List;

public enum TipoGrupoAlimenticio {
	DULCES,
	CARNES_GRASAS,
	FRUTOS_SECOS,
	PESCADOS_HUEVOS_CARNES_MAGRAS,
	LACTEOS,
	LEGUMBRES,
	FRUTAS,
	VEGETALES,
	ACEITES,
	HARINAS,
	LIQUIDO;
	
	
	public static List<String> obtenerGruposAlimenticiosDisponibles(){
		List<String> listaGruposAlimenticios = new ArrayList<String>();
		listaGruposAlimenticios.add("Dulces");
		listaGruposAlimenticios.add("Carnes Grasas");
		listaGruposAlimenticios.add("Frutos Secos");
		listaGruposAlimenticios.add("Pescados Harinas y Carnes Magras");
		listaGruposAlimenticios.add("Lacteos");
		listaGruposAlimenticios.add("Legumbres");
		listaGruposAlimenticios.add("Frutas");
		listaGruposAlimenticios.add("Vegetales");
		listaGruposAlimenticios.add("Aceites");
		listaGruposAlimenticios.add("Harinas");
		listaGruposAlimenticios.add("Liquido");
		
		return listaGruposAlimenticios;
	}

	public static TipoGrupoAlimenticio matchearGrupo(String nombreGrupoAlimenticio) {
		if(nombreGrupoAlimenticio.equalsIgnoreCase("Dulces")){
			return DULCES;
			
		}else if(nombreGrupoAlimenticio.equalsIgnoreCase("Carnes Grasas")){
			return CARNES_GRASAS;
			
		}else if(nombreGrupoAlimenticio.equalsIgnoreCase("Frutos Secos")){
			return FRUTOS_SECOS;
			
		}else if(nombreGrupoAlimenticio.equalsIgnoreCase("Pescados Harinas y Carnes Magras")){
			return PESCADOS_HUEVOS_CARNES_MAGRAS;
			
		}else if(nombreGrupoAlimenticio.equalsIgnoreCase("Lacteos")){
			return LACTEOS;
			
		}else if(nombreGrupoAlimenticio.equalsIgnoreCase("Legumbres")){
			return LEGUMBRES;
			
		}else if(nombreGrupoAlimenticio.equalsIgnoreCase("Frutas")){
			return FRUTAS;
			
		}else if(nombreGrupoAlimenticio.equalsIgnoreCase("Vegetales")){
			return VEGETALES;
			
		}else if(nombreGrupoAlimenticio.equalsIgnoreCase("Aceites")){
			return ACEITES;
			
		}else if(nombreGrupoAlimenticio.equalsIgnoreCase("Harinas")){
			return HARINAS;
			
		}else if(nombreGrupoAlimenticio.equalsIgnoreCase("Liquido")){
			return LIQUIDO;
		}

		
		return null;
	}
	
	
	public static String devolverString(TipoGrupoAlimenticio grupo){
		switch (grupo) {
			case DULCES: 							return "Dulces";
			case CARNES_GRASAS: 					return "Carnes Grasas";
			case FRUTOS_SECOS: 						return "Frutos Secos";
			case PESCADOS_HUEVOS_CARNES_MAGRAS: 	return "Pescados Harinas y Carnes Magras";
			case LACTEOS: 							return "Lacteos";
			case LEGUMBRES: 						return "Legumbres";
			case FRUTAS: 							return "Frutas";
			case VEGETALES: 						return "Vegetales";
			case ACEITES: 							return "Aceites";
			case HARINAS: 							return "Harinas";
			case LIQUIDO: 							return "Liquido";
			
			
			default: 		return null;
		}
	}
}
