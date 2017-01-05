package ar.edu.utn.ba.dds.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ar.edu.utn.ba.dds.entidades.Ingrediente;
import ar.edu.utn.ba.dds.entidades.Receta;
import ar.edu.utn.ba.dds.entidades.Usuario;
import ar.edu.utn.ba.dds.enums.HorarioComida;
import ar.edu.utn.ba.dds.enums.Temporada;
import ar.edu.utn.ba.dds.excepciones.RecetaExcepcion;
import ar.edu.utn.ba.dds.excepciones.UsuarioExcepcion;
import ar.edu.utn.ba.dds.model.RecetaService;

@Controller
@RequestMapping("/recetas")
public class ControllerDeRecetas extends ControllerGenerico {
	//Recordar que Hereda Atributos y Metodos de su Clase Padre	
	
	@Autowired
	private RecetaService recetaService;
	
	/*********** ATRIBUTOS QUE SE PASAN A TEMPLATES **************/
	protected static final String RECETAS_DISPONIBLES = "recetasDisponibles";
	protected static final String RECETA = "receta";
	
	
	/****************** METODOS **************************/
	//Tener en cuenta que el parametro "ModelMap" se encarga de Injectarlo Spring MVC automaticamente
	
	
	/**
	 * Para Ver la Pagina de Crear una Receta
	 */
	@RequestMapping(value = "/crear", method = RequestMethod.GET)
	public String getCrearReceta(ModelMap model){
		cargarAtributosMenu(model);
		
		//Controlo que el usuario este realmente Logueado
		if( this.sesionActual.estaLogueadoUsuario()== false){
			cargarAtributosFeedBack(model, ERROR, "Debe estar Logueado para acceder a esta Pagina");
			return PAGINA_FEEDBACK;
		}
		
		List<String> listaHorarios = HorarioComida.obtenerHorariosDisponibles();
		List<String> listaTemporadas = Temporada.obtenerTemporadasDisponibles();
		List<Ingrediente> ingredientesDisponibles = this.modelService.obtenerIngredientesDisponibles();
		
		model.addAttribute(HORARIOS_DISPONIBLES,listaHorarios);
		model.addAttribute(TEMPORADAS_DISPONIBLES,listaTemporadas);
		model.addAttribute(LISTA_INGREDIENTES_DISPONIBLES, ingredientesDisponibles);
		
		
		return PAGINA_CREAR_RECETA;
	}

	
	/**
	 * Para Crear una Receta
	 */
	@RequestMapping(value = "/crear", method = RequestMethod.POST)
	public String postCrearReceta(ModelMap model, String ingredientes, String nombreReceta, String descripcion, Integer calorias, String temporada, String horario) {
		cargarAtributosMenu(model);
		
		//Veo si llegan bien los parametros al controllers o no
		if( (ingredientes == null) || (nombreReceta == null) || (descripcion == null) || (horario == null) || (temporada == null) ){
			cargarAtributosFeedBack(model, ERROR, "NO estan llegando los parametros al controller, llegan con NULL. Revisar nombres.");
			return PAGINA_FEEDBACK;
		}
		
		List<String> ingredientesList = Arrays.asList(ingredientes.split("\\s*,\\s*"));
		List<Integer> ingredientesIds = new ArrayList<Integer>();
		for(String ingrediente : ingredientesList)
			ingredientesIds.add(Integer.valueOf(ingrediente));
		
		try {
			 recetaService.crearRecetaNueva(sesionActual.getUsuarioActual(), nombreReceta, descripcion, calorias, temporada, horario, ingredientesIds);
			 actualizarUsuario();
			
		}
		catch (RecetaExcepcion excepcion) {
			cargarAtributosFeedBack(model, ERROR, excepcion.getMessage());
			return PAGINA_FEEDBACK;
		}

		cargarAtributosFeedBack(model, OK, "Receta Creada con Exito");
		return PAGINA_FEEDBACK;
	}
	
	
	/**
	 * Para ver las Recetas Disponibles del Usuario.
	 * La URL es "/recetas" (la misma que la base del controller)
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getRecetas(ModelMap model){
		cargarAtributosMenu(model);
		
		//Controlo que el usuario este realmente Logueado
		if( this.sesionActual.estaLogueadoUsuario()== false){
			cargarAtributosFeedBack(model, ERROR, "Debe estar Logueado para acceder a esta Pagina");
			return PAGINA_FEEDBACK;
		}
		
		Set<Receta> recetasDisponibles = usuarioGrupoService.recetasDisponiblesPorUsuario(this.sesionActual.getUsuarioActual());

		model.addAttribute(RECETAS_DISPONIBLES,recetasDisponibles);
		//model.addAttribute(OBJETO_USUARIO,this.sesionActual.getUsuarioActual());
		
		return PAGINA_VER_RECETAS;
	}
	
	
	
	/**
	 * Para Ver la Pagina de Modificar una Receta
	 * NOTA: NO debe poder el usuario modificar el nombre de una receta, para eso esta lo de duplicar recetas
	 */
	@RequestMapping(value = "/modificar/{idReceta}", method = RequestMethod.GET)
	public String getModificarReceta(ModelMap model, @PathVariable("idReceta") int idReceta){
		cargarAtributosMenu(model);
		
		//Controlo que el usuario este realmente Logueado
		if( this.sesionActual.estaLogueadoUsuario()== false){
			cargarAtributosFeedBack(model, ERROR, "Debe estar Logueado para acceder a esta Pagina");
			return PAGINA_FEEDBACK;
		}
		
		//Veo si llegan bien los Parametros al Controllers o NO
		if( idReceta == 0 ){
			cargarAtributosFeedBack(model, ERROR, "NO estan llegando los parametros al controller, ID llego 0. Revisar nombres.");
			return PAGINA_FEEDBACK;
		}
		
		List<String> listaHorarios = HorarioComida.obtenerHorariosDisponibles();
		List<String> listaTemporadas = Temporada.obtenerTemporadasDisponibles();
		List<Ingrediente> ingredientesDisponibles = this.modelService.obtenerIngredientesDisponibles();
		
		model.addAttribute(HORARIOS_DISPONIBLES,listaHorarios);
		model.addAttribute(TEMPORADAS_DISPONIBLES,listaTemporadas);
		model.addAttribute(LISTA_INGREDIENTES_DISPONIBLES, ingredientesDisponibles);
		
		Receta recetaObtenida = this.recetaService.obtenerReceta(idReceta);
		model.addAttribute(RECETA, recetaObtenida);
		
		
		return PAGINA_MODIFICAR_RECETA;
	}
	
	/**
	 * Para Modificar una Receta
	 * NOTA: NO debe poder el usuario modificar el nombre de una receta, para eso esta lo de duplicar recetas
	 */
	@RequestMapping(value = "/modificar/{idReceta}", method = RequestMethod.POST)
	public String postModificarReceta(ModelMap model, @PathVariable("idReceta") int idReceta, String ingredientes, String descripcion, Integer calorias, String temporada, String horario) {
		cargarAtributosMenu(model);
		
		//Veo si llegan bien los Parametros al Controllers o NO
		if( (ingredientes == null) || (descripcion == null)|| (temporada == null)|| (horario == null) ){
			cargarAtributosFeedBack(model, ERROR, "NO estan llegando los parametros al controller, llegan con NULL. Revisar nombres.");
			return PAGINA_FEEDBACK;
		}
		
		List<String> ingredientesList = Arrays.asList(ingredientes.split("\\s*,\\s*"));
		List<Integer> ingredientesIds = new ArrayList<Integer>();
		for(String ingrediente : ingredientesList)
			ingredientesIds.add(Integer.valueOf(ingrediente));
		
		this.recetaService.modificarReceta(idReceta, ingredientesIds, descripcion, calorias, temporada, horario);
		actualizarUsuario();
		
		cargarAtributosFeedBack(model, OK, "Receta Modificada con Exito");
		return PAGINA_FEEDBACK;
	}
	
	/**
	 * Eliminar una Receta
	 */
	@RequestMapping(value = "/eliminar/{idReceta}", method = RequestMethod.POST)
	public String postEliminarReceta(ModelMap model, @PathVariable("idReceta") int idReceta){
		cargarAtributosMenu(model);
		
		//Veo si llegan bien los Parametros al Controllers o NO
		if( idReceta == 0 ){
			cargarAtributosFeedBack(model, ERROR, "NO estan llegando los parametros al controller, ID llego 0. Revisar nombres.");
			return PAGINA_FEEDBACK;
		}
		
		Boolean pudoBorrar = this.recetaService.eliminarRecetaPorId(idReceta, this.sesionActual.getUsuarioActual());
		actualizarUsuario();
		
		if(pudoBorrar==false){
			cargarAtributosFeedBack(model, ERROR, "No pudo Eliminarse la Receta. Puede ser que no pertenesca al usuario o que este usandose en alguna Comida");
		}else{
			cargarAtributosFeedBack(model, OK, "Receta Eliminada con Exito");
		}
		return PAGINA_FEEDBACK;
	}
	
	
	/**
	 * Ver Pagina de Duplicar Receta
	 * El Usuario SOLO debe ingresar un nuevo nombre
	 */
	@RequestMapping(value = "/duplicar/{idReceta}", method = RequestMethod.GET)
	public String getDuplicarReceta(ModelMap model, @PathVariable("idReceta") int idReceta){
		cargarAtributosMenu(model);
		
		//Controlo que el usuario este realmente Logueado
		if( this.sesionActual.estaLogueadoUsuario()== false){
			cargarAtributosFeedBack(model, ERROR, "Debe estar Logueado para acceder a esta Pagina");
			return PAGINA_FEEDBACK;
		}
		
		//Veo si llegan bien los Parametros al Controllers o NO
		if( idReceta == 0 ){
			cargarAtributosFeedBack(model, ERROR, "NO estan llegando los parametros al controller, ID llego 0. Revisar nombres.");
			return PAGINA_FEEDBACK;
		}
		
		Receta recetaObtenida = this.recetaService.obtenerReceta(idReceta);
		model.addAttribute(RECETA, recetaObtenida);
		
		return PAGINA_DUPLICAR_RECETA;
	}
	
	
	
	/**
	 * Para Duplicar Receta
	 * El Usuario SOLO debe ingresar un nuevo nombre
	 */
	@RequestMapping(value = "/duplicar/{idReceta}", method = RequestMethod.POST)
	public String postDuplicarReceta(ModelMap model, @PathVariable("idReceta") int idReceta, String nuevoNombre){
		cargarAtributosMenu(model);
		
		//Veo si llegan bien los Parametros al Controllers o NO
		if( nuevoNombre == null ){
			cargarAtributosFeedBack(model, ERROR, "NO estan llegando los parametros al controller, llegan con NULL. Revisar nombres.");
			return PAGINA_FEEDBACK;
		}
		
		try {
			this.recetaService.duplicarReceta(sesionActual.getUsuarioActual(), idReceta, nuevoNombre);
			actualizarUsuario();
			cargarAtributosFeedBack(model, OK, "Receta Duplicada con Exito");
		} catch (RecetaExcepcion e) {
			cargarAtributosFeedBack(model, ERROR, e.getMessage() );
		}

		return PAGINA_FEEDBACK;
	}
	
	
	/**
	 * Para ver Pagina de Planificar Comida
	 */
	@RequestMapping(value = "/planificar/{idReceta}", method = RequestMethod.GET)
	public String getPlanificarComida(ModelMap model, @PathVariable("idReceta") int idReceta){
		cargarAtributosMenu(model);
		
		//Controlo que el usuario este realmente Logueado
		if( this.sesionActual.estaLogueadoUsuario()== false){
			cargarAtributosFeedBack(model, ERROR, "Debe estar Logueado para acceder a esta Pagina");
			return PAGINA_FEEDBACK;
		}
		
		//Veo si llegan bien los Parametros al Controllers o NO
		if( idReceta == 0 ){
			cargarAtributosFeedBack(model, ERROR, "NO estan llegando los parametros al controller, ID llego 0. Revisar nombres.");
			return PAGINA_FEEDBACK;
		}
		
		Receta recetaObtenida = this.recetaService.obtenerReceta(idReceta);
		model.addAttribute(RECETA, recetaObtenida);
		
		List<String> listaHorarios = HorarioComida.obtenerHorariosDisponibles();
		model.addAttribute(HORARIOS_DISPONIBLES,listaHorarios);
		
		return PAGINA_PLANIFICAR_COMIDA;
	}
	
	
	/**
	 * Para Planificar Comida
	 */
	@RequestMapping(value = "/planificar/{idReceta}", method = RequestMethod.POST)
	public String postPlanificarComida(ModelMap model, @PathVariable("idReceta") int idReceta,@DateTimeFormat(iso = ISO.DATE) Date fecha, String horario){
		cargarAtributosMenu(model);
		
		//Veo si llegan bien los Parametros al Controllers o NO
		if( (fecha == null) || (horario == null) ){
			cargarAtributosFeedBack(model, ERROR, "NO estan llegando los parametros al controller, llegan con NULL. Revisar nombres.");
			return PAGINA_FEEDBACK;
		}
		
		Usuario usuarioActual = this.sesionActual.getUsuarioActual();
		
		this.usuarioGrupoService.planificarComida(usuarioActual, DateToCalendar(fecha), idReceta, horario);
		actualizarUsuario();
		
		cargarAtributosFeedBack(model, OK, "Comida Planificada con Exito");
		return PAGINA_FEEDBACK;
	}
	
	
	/**
	 * Para Calificar una Receta
	 */
	@RequestMapping(value = "/{idReceta}/calificar/{puntuacion}", method = RequestMethod.GET)
	public String getCalificarReceta(ModelMap model, @PathVariable("idReceta") int idReceta, @PathVariable("puntuacion") int puntuacion){
		cargarAtributosMenu(model);
		
		//Veo si llegan bien los Parametros al Controllers o NO
		if( (idReceta == 0) || (puntuacion==0) ){
			cargarAtributosFeedBack(model, ERROR, "NO estan llegando los parametros al controller, ID llego 0 o Puntuacion llego 0. Revisar nombres.");
			return PAGINA_FEEDBACK;
		}
		
		
		Usuario usuarioActual = this.sesionActual.getUsuarioActual();
		
		try {
			this.recetaService.calificarRecetaPorID(usuarioActual, idReceta, puntuacion);
			actualizarUsuario();		//Por Si justo califico una receta que era de el mismo
			cargarAtributosFeedBack(model, OK, "Receta Calificada con Exito");
			
		} catch (UsuarioExcepcion | RecetaExcepcion excepcion) {
			cargarAtributosFeedBack(model, ERROR, excepcion.getMessage());
		}

		return PAGINA_FEEDBACK;
	}
}