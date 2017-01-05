package ar.edu.utn.ba.dds.controllers;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ar.edu.utn.ba.dds.entidades.Comida;
import ar.edu.utn.ba.dds.entidades.Ingrediente;
import ar.edu.utn.ba.dds.entidades.Receta;
import ar.edu.utn.ba.dds.entidades.Usuario;
import ar.edu.utn.ba.dds.enums.TipoGrupoAlimenticio;
import ar.edu.utn.ba.dds.model.ReportesService;

@Controller
@RequestMapping("/reportes")
public class ControllerReportes extends ControllerGenerico{
	//Recordar que Hereda Atributos y Metodos de su Clase Padre
	
	
	
	/*********** ATRIBUTOS QUE SE PASAN A TEMPLATES **************/
	protected static final String USUARIOS = "usuarios";
	protected static final String TIPO_DE_VISUALIZACION = "debeMostrarRecetas";		//Es un Booleano con ese nombre
	protected static final String SET_RECETAS = "setRecetas";
	protected static final String SET_COMIDAS = "setComidas";
	
	
	/****************** CONSTANTES INTERNAS **************************/
	protected static final Boolean MOSTRAR_COMIDAS = false;
	protected static final Boolean MOSTRAR_RECETAS = true;
	
	
	/****************** METODOS **************************/
	
	

	/**
	 * Para ver Pagina de Elegir Estadisticas
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getElegirReportes(ModelMap model){
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
		
		List<String> listaGruposAlimenticiosDisponibles = TipoGrupoAlimenticio.obtenerGruposAlimenticiosDisponibles();
		List<Usuario> usuariosQueHay = this.usuarioGrupoService.obtenerTodosusuarios();
		List<Ingrediente> listaIngredientesDisponibles = this.modelService.obtenerIngredientesDisponibles();
		
		model.addAttribute(LISTA_GRUPOS_ALIMENTICIOS_DISPONIBLES, listaGruposAlimenticiosDisponibles);
		model.addAttribute(USUARIOS, usuariosQueHay);
		model.addAttribute(LISTA_INGREDIENTES_DISPONIBLES, listaIngredientesDisponibles);
		return PAGINA_ELEGIR_REPORTES;
	}
	
	
	
	/**
	 * Reportes de Recetas nuevas
	 */
	@RequestMapping(value = "/recetasNuevas", method = RequestMethod.POST)
	public String postRecetasNuevas(ModelMap model, String nombreUsuario){
		cargarAtributosMenu(model);
		
		//Veo si llegan bien los Parametros al Controllers o NO
		if( nombreUsuario==null){
			cargarAtributosFeedBack(model, ERROR, "NO estan llegando los parametros al controller, llegan con NULL. Revisar nombres");
			return PAGINA_FEEDBACK;
		}
		
		
		Set<Receta> recetasNuevas = ReportesService.recetasNuevas(buscarUsuarioPorNombre(nombreUsuario));
		
		model.addAttribute(TIPO_DE_VISUALIZACION, MOSTRAR_RECETAS);
		model.addAttribute(SET_RECETAS, recetasNuevas);
		return PAGINA_MOSTRAR_REPORTES;
	}
	
	
	
	/**
	 * Reportes de Comidas Planificadas por SubNombre Receta
	 */
	@RequestMapping(value = "/comidasNombre", method = RequestMethod.POST)
	public String postComidasNombre(ModelMap model, String nombreUsuario, String nombreReceta){
		cargarAtributosMenu(model);
		
		//Veo si llegan bien los Parametros al Controllers o NO
		if( (nombreUsuario==null) || (nombreReceta==null)){
			cargarAtributosFeedBack(model, ERROR, "NO estan llegando los parametros al controller, llegan con NULL. Revisar nombres");
			return PAGINA_FEEDBACK;
		}
		
		 Set<Comida> comidas = ReportesService.comidasPorNombre(buscarUsuarioPorNombre(nombreUsuario), nombreReceta);
		
		model.addAttribute(TIPO_DE_VISUALIZACION, MOSTRAR_COMIDAS);
		model.addAttribute(SET_COMIDAS, comidas);
		return PAGINA_MOSTRAR_REPORTES;
	}
	
	
	
	/**
	 * Reportes de Comidas por Periodo de Tiempo
	 */
	@RequestMapping(value = "/comidasPeriodo", method = RequestMethod.POST)
	public String postComidasPeriodo(ModelMap model, String nombreUsuario, @DateTimeFormat(iso = ISO.DATE) Date fechaInicio, @DateTimeFormat(iso = ISO.DATE) Date fechaFin){
		cargarAtributosMenu(model);
		
		//Veo si llegan bien los Parametros al Controllers o NO
		if( (nombreUsuario==null) || (fechaInicio==null) || (fechaFin==null)){
			cargarAtributosFeedBack(model, ERROR, "NO estan llegando los parametros al controller, llegan con NULL. Revisar nombres");
			return PAGINA_FEEDBACK;
		}
		
		Set<Comida> comidas = ReportesService.comidasPorPeriodo(buscarUsuarioPorNombre(nombreUsuario), DateToCalendar(fechaInicio),DateToCalendar(fechaFin));
		
		model.addAttribute(TIPO_DE_VISUALIZACION, MOSTRAR_COMIDAS);
		model.addAttribute(SET_COMIDAS, comidas);
		return PAGINA_MOSTRAR_REPORTES;
	}
	
	
	
	/**
	 * Reportes de Comidas por Ingrediente
	 */
	@RequestMapping(value = "/comidasIngrediente", method = RequestMethod.POST)
	public String postComidasIngrediente(ModelMap model, String nombreUsuario, String nombreIngrediente){
		cargarAtributosMenu(model);
		
		//Veo si llegan bien los Parametros al Controllers o NO
		if( (nombreUsuario==null) || (nombreIngrediente==null) ){
			cargarAtributosFeedBack(model, ERROR, "NO estan llegando los parametros al controller, llegan con NULL. Revisar nombres");
			return PAGINA_FEEDBACK;
		}
		
		Set<Comida> comidas = null;
		try {
			comidas = this.modelService.reporteComidasIngrediente(buscarUsuarioPorNombre(nombreUsuario), nombreIngrediente);
		} catch (Exception e) {
			cargarAtributosFeedBack(model, ERROR, e.getMessage() );
			return PAGINA_FEEDBACK;
		}
		
		model.addAttribute(TIPO_DE_VISUALIZACION, MOSTRAR_COMIDAS);
		model.addAttribute(SET_COMIDAS, comidas);
		return PAGINA_MOSTRAR_REPORTES;
	}
	
	
	
	/**
	 * Reportes de Comidas por Calorias
	 */
	@RequestMapping(value = "/comidasCalorias", method = RequestMethod.POST)
	public String postComidasCalorias(ModelMap model, String nombreUsuario, Integer caloriasInicio, Integer caloriasFin){
		cargarAtributosMenu(model);
		
		//Veo si llegan bien los Parametros al Controllers o NO
		if( (nombreUsuario==null) || (caloriasInicio<=0) || (caloriasFin<=0) ){
			cargarAtributosFeedBack(model, ERROR, "No pueden ser 0 Las Calorias. Por Favor cambiarlo. Tambien revisar que hayas puesto bien el nombre de usuario");
			return PAGINA_FEEDBACK;
		}
		
		Set<Comida> comidas = ReportesService.comidasPorCalorias(buscarUsuarioPorNombre(nombreUsuario), caloriasInicio, caloriasFin);

		model.addAttribute(TIPO_DE_VISUALIZACION, MOSTRAR_COMIDAS);
		model.addAttribute(SET_COMIDAS, comidas);
		return PAGINA_MOSTRAR_REPORTES;
	}
	
	
	
	/**
	 * Reportes de Comidas por Grupo Alimenticio
	 */ 
	@RequestMapping(value = "/comidasGrupo", method = RequestMethod.POST)
	public String postComidasGrupoAlimenticio(ModelMap model, String nombreUsuario, String nombreGrupoAlimenticio){
		cargarAtributosMenu(model);
		
		//Veo si llegan bien los Parametros al Controllers o NO
		if( (nombreUsuario==null) || (nombreGrupoAlimenticio==null) ){
			cargarAtributosFeedBack(model, ERROR, "NO estan llegando los parametros al controller, llegan con NULL. Revisar nombres");
			return PAGINA_FEEDBACK;
		}
		
		Set<Comida> comidas = this.modelService.reporteComidasGrupoAlimenticio(buscarUsuarioPorNombre(nombreUsuario), nombreGrupoAlimenticio);
		
		model.addAttribute(TIPO_DE_VISUALIZACION, MOSTRAR_COMIDAS);
		model.addAttribute(SET_COMIDAS, comidas);
		return PAGINA_MOSTRAR_REPORTES;
	}
	
	
	
	
	/**
	 * Reportes de Comidas Compartidas
	 */
	@RequestMapping(value = "/comidasCompartidas", method = RequestMethod.POST)
	public String postComidasCompartida(ModelMap model, String nombreUsuario){
		cargarAtributosMenu(model);
		
		//Veo si llegan bien los Parametros al Controllers o NO
		if( nombreUsuario==null){
			cargarAtributosFeedBack(model, ERROR, "NO estan llegando los parametros al controller, llegan con NULL. Revisar nombres");
			return PAGINA_FEEDBACK;
		}
		
		Set<Comida> comidasCompartidas = ReportesService.comidasCompartidas(buscarUsuarioPorNombre(nombreUsuario));
		
		model.addAttribute(TIPO_DE_VISUALIZACION, MOSTRAR_COMIDAS);
		model.addAttribute(SET_COMIDAS, comidasCompartidas);
		return PAGINA_MOSTRAR_REPORTES;
	}
}
