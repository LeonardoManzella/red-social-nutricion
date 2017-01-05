package ar.edu.utn.ba.dds.model;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import ar.edu.utn.ba.dds.entidades.Comida;
import ar.edu.utn.ba.dds.entidades.Ingrediente;
import ar.edu.utn.ba.dds.entidades.Receta;
import ar.edu.utn.ba.dds.entidades.Usuario;
import ar.edu.utn.ba.dds.implement.GrupoAlimenticio;

/**
 * Contiene Metodos que Dado un Usuario se pueden Obtener Reportes de Estos
 */
public abstract class ReportesService {

	
	public static Set<Receta> recetasNuevas(Usuario usuario) {
		return usuario.getRecetas();
	}

	/**
	 * Dado un String se buscan todas las Comidas, que en la Receta Asociada contenga al String de Nombre
	 */
	public static Set<Comida> comidasPorNombre(Usuario usuario, String subNombreRecetaBuscar){
		//Variable Temporal para Juntar Comidas
		Set<Comida> comidasPorDevolver = new HashSet<Comida>();
		
		for(Comida comida: usuario.getComidasPlanificadas()) {
			//FIXME Si hay problemas cambiar el "contains" por "startsWith"
			if( comida.getRecetaAsociada().getNombre().contains(subNombreRecetaBuscar) ){
				comidasPorDevolver.add(comida);
			}	
		}
		return comidasPorDevolver;
	}
	
	
	public static Set<Comida> comidasPorPeriodo(Usuario usuario, Calendar fechaInicio, Calendar fechaFin){
		//Variable Temporal para Juntar Comidas
		Set<Comida> comidasPorDevolver = new HashSet<Comida>();
		
		for(Comida comida: usuario.getComidasPlanificadas()) {
			if( comida.getFecha().after(fechaInicio) && comida.getFecha().before(fechaFin) ){
				comidasPorDevolver.add(comida);
			}	
		}
		return comidasPorDevolver;
	}
	
	/**
	 * Devuelve Todas las Comidas Planificas por el Usuario que Contengan al Ingrediente buscado.
	 */
	public static Set<Comida> comidasPorIngrediente(Usuario usuario, Ingrediente ingredienteBuscado){
		//Variable Temporal para Juntar Comidas
		Set<Comida> comidasPorDevolver = new HashSet<Comida>();
		
		for(Comida comida: usuario.getComidasPlanificadas()) {
			
			for(Ingrediente ingrediente: comida.getRecetaAsociada().getListaIngredientes() ) {
				if( ingrediente.getNombre().equalsIgnoreCase(ingredienteBuscado.getNombre()) ){
					comidasPorDevolver.add(comida);
					break;
				}	
			}
			
		}
		return comidasPorDevolver;
	}
	
	
	
	public static Set<Comida> comidasPorCalorias(Usuario usuario, Integer caloriasInicio, Integer caloriasFin){
		//Variable Temporal para Juntar Comidas
		Set<Comida> comidasPorDevolver = new HashSet<Comida>();
		
		for(Comida comida: usuario.getComidasPlanificadas()) {
			if( (comida.getRecetaAsociada().getCalorias() > caloriasInicio) && (comida.getRecetaAsociada().getCalorias() < caloriasFin) ){
				comidasPorDevolver.add(comida);
			}	
		}
		return comidasPorDevolver;
	}
	
	
	
	public static Set<Comida> comidasPorGrupoAlimenticio(Usuario usuario, GrupoAlimenticio grupoBuscado){
		//Variable Temporal para Juntar Comidas
		Set<Comida> comidasPorDevolver = new HashSet<Comida>();
		for(Comida comida: usuario.getComidasPlanificadas()) {
			if( grupoBuscado.contieneA(comida.getRecetaAsociada().getGruposAlimenticio())){
				comidasPorDevolver.add(comida);
			}	
				
		}

		return comidasPorDevolver;
	}
	
	
	
	/**
	 * Basicamente lo que hace es Buscar Todas las Comidas Planificadas Con Recetas (Compartidas, osea las puede ver el usuario) de Otros Usuarios (No son de El)
	 */
	public static Set<Comida> comidasCompartidas(Usuario usuario){
		//Contestaron en el FORO: este Reporte que el Enunciado de la Entrega 4 llama "comidasPorPersonaCompartio" le cambie el nombre a uno mas expresivo
		
		
		//Variable Temporal para Juntar Comidas
		Set<Comida> comidasPorDevolver = new HashSet<Comida>();
		for(Comida comida: usuario.getComidasPlanificadas()) {
			if( coleccionContieneReceta(usuario.getRecetas(),comida.getRecetaAsociada())==false ){
				comidasPorDevolver.add(comida);
			}       
		}
		return comidasPorDevolver;
	}
	
	public static boolean coleccionContieneReceta(Collection<Receta> coleccion, Receta recetaPorComparar){
		for(Receta recetaDefault : coleccion){
			if(recetaDefault.getNombre().equalsIgnoreCase(recetaPorComparar.getNombre())){
				return true;
			}
		}
		
		return false;
	}
}
