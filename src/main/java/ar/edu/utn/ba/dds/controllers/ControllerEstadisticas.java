package ar.edu.utn.ba.dds.controllers;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ar.edu.utn.ba.dds.entidades.Receta;
import ar.edu.utn.ba.dds.enums.Temporada;
import ar.edu.utn.ba.dds.model.EstadisticasService;

@Controller
@RequestMapping("/estadisticas")
public class ControllerEstadisticas extends ControllerGenerico{
	//Recordar que Hereda Atributos y Metodos de su Clase Padre	
	
	@Autowired
	protected EstadisticasService estadisticasService;
	
	
	/*********** ATRIBUTOS QUE SE PASAN A TEMPLATES **************/
	protected static final String MAP_RECETAS_ESTADISTICA = "mapRecetasCantidadVeces";
	
	
	
	/****************** METODOS **************************/

	


	/**
	 * Para ver Pagina de Elegir Estadisticas
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getElegirEstadisticas(ModelMap model){
		cargarAtributosMenu(model);
		
		//Controlo que el usuario este realmente Logueado
		if( this.sesionActual.estaLogueadoUsuario()== false){
			cargarAtributosFeedBack(model, ERROR, "Debe estar Logueado para acceder a esta Pagina");
			return PAGINA_FEEDBACK;
		}
		
		//Controlo que el usuario sea Admin
		if( this.usuarioGrupoService.usuarioEsAdmin(this.sesionActual.getUsuarioActual())== false){
			cargarAtributosFeedBack(model, ERROR, "Debe ser ADMIN para acceder a esta Pagina");
			return PAGINA_FEEDBACK;
		}
		
		List<String> listaTemporadas = Temporada.obtenerTemporadasDisponibles();
			
		model.addAttribute(TEMPORADAS_DISPONIBLES,listaTemporadas);
		return PAGINA_ELEGIR_ESTADISTICAS;
	}
	
	
	
	
	/**
	 * Para Estadisticas de Recetas Mas Programadas por Estacion
	 */
	@RequestMapping(value = "/masProgramadas", method = RequestMethod.POST)
	public String postMasProgramadas(ModelMap model, String temporada, @DateTimeFormat(iso = ISO.DATE) Date fechaInicio, @DateTimeFormat(iso = ISO.DATE) Date fechaFin){
		cargarAtributosMenu(model);
		
		//Veo si llegan bien los Parametros al Controllers o NO
		if( (temporada == null) || (fechaInicio==null) || (fechaFin==null) ){
			cargarAtributosFeedBack(model, ERROR, "NO estan llegando los parametros al controller, llegan con NULL. Revisar nombres o el tema con los Date que tipo de dato estas mandando");
			return PAGINA_FEEDBACK;
		}
		
		Map<Receta, Integer> recetasMasProgramadasPorEstacion = this.estadisticasService.recetasMasProgramadaPorEstacion(Temporada.matchearTemporada(temporada), DateToCalendar(fechaInicio), DateToCalendar(fechaFin));
		
		model.addAttribute(MAP_RECETAS_ESTADISTICA,recetasMasProgramadasPorEstacion);
		
		return PAGINA_MOSTRAR_ESTADISTICAS;
	}
	
	
	
	/**
	 * Para Estadisticas de Recetas Mas Copiadas para Hacer otras Recetas
	 */
	@RequestMapping(value = "/masCopiadas", method = RequestMethod.POST)
	public String postMasCopiadas(ModelMap model, @DateTimeFormat(iso = ISO.DATE) Date fechaInicio, @DateTimeFormat(iso = ISO.DATE) Date fechaFin){
		cargarAtributosMenu(model);
		
		//Veo si llegan bien los Parametros al Controllers o NO
		if( (fechaInicio==null) || (fechaFin==null) ){
			cargarAtributosFeedBack(model, ERROR, "NO estan llegando los parametros al controller, llegan con NULL. Revisar nombres o el tema con los Date que tipo de dato estas mandando");
			return PAGINA_FEEDBACK;
		}
		
		Map<Receta, Integer> recetasMasCopiadas = this.estadisticasService.recetasMasCopiadas(DateToCalendar(fechaInicio), DateToCalendar(fechaFin));
		
		model.addAttribute(MAP_RECETAS_ESTADISTICA,recetasMasCopiadas);
		
		return PAGINA_MOSTRAR_ESTADISTICAS;
	}
}
