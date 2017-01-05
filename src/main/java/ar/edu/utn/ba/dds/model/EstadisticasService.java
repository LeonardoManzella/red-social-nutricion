package ar.edu.utn.ba.dds.model;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.ba.dds.commons.MapsCommons;
import ar.edu.utn.ba.dds.daos.RecetaDAO;
import ar.edu.utn.ba.dds.daos.UsuarioDAO;
import ar.edu.utn.ba.dds.entidades.Comida;
import ar.edu.utn.ba.dds.entidades.Receta;
import ar.edu.utn.ba.dds.entidades.Usuario;
import ar.edu.utn.ba.dds.enums.Temporada;

@Service
public class EstadisticasService {
	
	@Autowired
	private UsuarioDAO daoUsuario;
	
	@Autowired
	private RecetaDAO daoReceta;

	/**
	 * Método para la generación de la estadistica de las recetas más utilizadas
	 * para la programación de las comidas diarias
	 *
	 * @param  temporada   indica la estación del año de la receta para la generación de las estadisticas
	 * @param  fechaInicio fecha inicial en el rango de la busqueda
	 * @param  fechaFin    fecha final en el rango de la busqueda
	 * @return Un objeto de tipo TreeMap que tiene como llaves a las recetas
	 * 		   y como valores a la cantidad de veces que fueron utilizadas para
	 * 	 	   programar las comidas
	 */
	public Map<Receta, Integer> recetasMasProgramadaPorEstacion (Temporada temporada, Calendar fechaInicio, Calendar fechaFin) {
		List<Usuario> listaUsuarios = daoUsuario.getUsuarios();
		//Transformo el List en Set para evitar Repetidos, si los hubiese
		Set<Usuario> usuarios = new HashSet<Usuario>();
		usuarios.addAll(listaUsuarios);
		
		long fechaInicioMilisec = fechaInicio.getTimeInMillis();
		long fechaFinMilisec = fechaFin.getTimeInMillis();
		
		HashMap<Receta, Integer> estadisticaRecetas = new HashMap<Receta, Integer>();
		
		// TODO Refactorizar para no tener Codigo Espagueti
		//Recorremos todos los Usuarios
		for(Usuario usuario : usuarios) {
			Set<Comida> comidas = usuario.getComidasPlanificadas();
			//Recorremos las Comidas del usuario
			for(Comida comida : comidas) {
				Receta recetaAsociada = comida.getRecetaAsociada();
				//Si coincide la temporada con la que buscamos, vemos de Agregar la Receta ala estadistica
				if(recetaAsociada.getTemporada().equals(temporada)) {
					long fechaComidaPlanificada = comida.getFecha().getTimeInMillis();
					if( (fechaComidaPlanificada >= fechaInicioMilisec) && (fechaComidaPlanificada <= fechaFinMilisec) ) {
						estadisticaRecetas = agregarRecetaEstadistica(estadisticaRecetas, recetaAsociada);
					}
				}
			}
		}
		
		Map<Receta, Integer> estadisticaRecetasFinal = MapsCommons.sortMapByValue(estadisticaRecetas);
		
		return estadisticaRecetasFinal;
	}
	
	/**
	 * Método para la generación de la estadistica de las recetas más utilizadas
	 * para la generación de las recetas nuevas
	 *
	 * @param  fechaInicio fecha inicial en el rango de la busqueda
	 * @param  fechaFin    fecha final en el rango de la busqueda
	 * @return Un objeto de tipo TreeMap que tiene como llaves a las recetas
	 * 		   y como valores a la cantidad de veces que fueron utilizadas para
	 * 	 	   generar las recetas nuevas
	 */
	public Map<Receta, Integer> recetasMasCopiadas(Calendar fechaInicio, Calendar fechaFin) {
		//Importante que sea Set para no Repetir Recetas
		Set<Receta> recetas = new HashSet<Receta>();
		recetas.addAll(daoReceta.getRecetas());
		HashMap<Receta, Integer> estadisticaRecetas = new HashMap<Receta, Integer>();
		
		for(Receta receta : recetas) {
			if(receta.getRecetaBase() != null) {
				estadisticaRecetas = agregarRecetaEstadistica(estadisticaRecetas, receta.getRecetaBase());
			}
		}
		
		Map<Receta, Integer> estadisticaRecetasFinal = MapsCommons.sortMapByValue(estadisticaRecetas);
		
		return estadisticaRecetasFinal;
	}
	
	private static HashMap<Receta, Integer> agregarRecetaEstadistica(HashMap<Receta, Integer> estadisticaRecetas, Receta receta) {
		
		Integer count = estadisticaRecetas.get(receta);
		if (count == null) {
			estadisticaRecetas.put(receta, 1);
		}
		else {
			estadisticaRecetas.put(receta, count + 1);
		}
		
		return estadisticaRecetas;
	}
	
}
