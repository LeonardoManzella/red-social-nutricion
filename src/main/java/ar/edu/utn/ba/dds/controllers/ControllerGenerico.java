package ar.edu.utn.ba.dds.controllers;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

import ar.edu.utn.ba.dds.entidades.Usuario;
import ar.edu.utn.ba.dds.model.GreenFoodService;
import ar.edu.utn.ba.dds.model.RecetaService;
import ar.edu.utn.ba.dds.model.UsuarioGrupoService;

/**
 * Contiene Cosas Comunes a Todos los Controllers. Sino creaba esta clase se hacia un caos tremendo repitiendo cosas
 */
@Controller
//No tiene URL Asociada
public abstract class ControllerGenerico {
	// Les puse Protected a los atributos para que solo puedan ser accedidos desde este Package o desde una Subclase
	
	@Autowired
	protected GreenFoodService modelService;
	
	@Autowired
	protected UsuarioGrupoService usuarioGrupoService;
	
	@Autowired
	protected RecetaService recetaService;
	
	@Autowired
	protected SesionDeUsuario sesionActual;
	
	
	
	/*************** NOMBRES DE PAGINAS WEB **********************/
	// IMPORTANTE: Los Nombres de las Paginas Distinguen Mayusculas de Minusculas, ojo con eso
	//Recordar que se autocompleta solo a ".html"
	protected static final String PAGINA_INICIO = "home";
	protected static final String PAGINA_FEEDBACK = "feedback";
	protected static final String PAGINA_LOGIN = "login";
	protected static final String PAGINA_REGISTRAR = "registrarUsuario";
	protected static final String PAGINA_PERFIL_USUARIO = "perfilUsuario";
	protected static final String PAGINA_COMIDAS_PLANIFICADAS = "comidasPlanificadas";
	protected static final String PAGINA_VER_RECETAS = "verRecetas";
	protected static final String PAGINA_CREAR_RECETA = "crearReceta";
	protected static final String PAGINA_MODIFICAR_RECETA = "modificarReceta";
	protected static final String PAGINA_DUPLICAR_RECETA = "duplicarReceta";
	protected static final String PAGINA_PLANIFICAR_COMIDA = "planificarReceta";
	protected static final String PAGINA_VER_GRUPOS = "verGrupos";
	protected static final String PAGINA_VER_GRUPO_ESPECIFICO = "verGrupoEspecifico";
	protected static final String PAGINA_ELEGIR_RECOMENDACION_DIA = "elegirRecomendacion";
	protected static final String PAGINA_ELEGIR_PIRAMIDE = "elegirPiramide";
	protected static final String PAGINA_RECOMENDACIONES = "recomendaciones";
	protected static final String PAGINA_ELEGIR_ESTADISTICAS = "elegirEstadistica";
	protected static final String PAGINA_MOSTRAR_ESTADISTICAS = "mostrarEstadistica";
	protected static final String PAGINA_ELEGIR_REPORTES = "elegirReportes";
	protected static final String PAGINA_MOSTRAR_REPORTES = "mostrarReportes";
	
	
	
	/*********** ATRIBUTOS QUE SE PASAN A TEMPLATES **************/
	//NOTA: No recomiendo cambiar los nombres de estos atributos, porque impactaria en Varios Templates
	protected static final String HUBO_EXITO = "huboExito";
	protected static final String MENSAJE_FEEDBACK = "mensajeFeedback";
	protected static final String USUARIO_NOMBRE = "usuarioNombre";
	protected static final String USUARIO_ESTA_LOGUEADO = "usuarioEstaLogueado";
	protected static final String USUARIO_ES_ADMIN = "usuarioEsAdmin";
	protected static final String OBJETO_USUARIO = "objetoUsuario";
	protected static final String LISTA_INGREDIENTES_DISPONIBLES = "ingredientesDisponibles";
	protected static final String HORARIOS_DISPONIBLES = "horariosDisponibles";
	protected static final String TEMPORADAS_DISPONIBLES = "temporadasDisponibles";
	protected static final String LISTA_GRUPOS_ALIMENTICIOS_DISPONIBLES = "gruposAlimenticiosDisponibles";
	
	
	
	/********** Otras Constantes Internas ***********/
	protected static final Boolean OK = true;
	protected static final Boolean ERROR = false;
	
	
	/****************** METODOS **************************/
	
	
	
	/**
	 * Para Cargar los Atributos necesarios que se le pasaran a los Templates para mostrar el menu
	 */
	public void cargarAtributosMenu(ModelMap model){
		//NOTA: Esta Armado asi "medio raro" para facilitar el Debug ya que no se si anda 100% bien las sesiones. Por eso esta hecho paso por paso.
		String usuarioNombre = null;
		
		if(this.sesionActual.estaLogueadoUsuario() == true){
			usuarioNombre = sesionActual.getUsuarioActual().getNombre();
			
			model.addAttribute(OBJETO_USUARIO, sesionActual.getUsuarioActual());
			
			if( this.usuarioGrupoService.usuarioEsAdmin(this.sesionActual.getUsuarioActual()) == true ){
				model.addAttribute(USUARIO_ES_ADMIN, true);
			}else{
				model.addAttribute(USUARIO_ES_ADMIN, false);
			}
			
			
			model.addAttribute(USUARIO_ESTA_LOGUEADO, true);
			model.addAttribute(USUARIO_NOMBRE, usuarioNombre);
		}else{
			model.addAttribute(USUARIO_ESTA_LOGUEADO, false);
			model.addAttribute(USUARIO_NOMBRE, null);			//Atributo USUARIO_NOMBRE sera NULL. Si llega a joder que le pase null directo, eliminar la linea dejando solamente el comentario
		}
	}
	
	
	
	/**
	 * Para Cargar los Atributos para Mostrar la Pagina de Feedback
	 */
	public void cargarAtributosFeedBack(ModelMap model, Boolean huboExito, String mensajePorMostrar){
		model.addAttribute(HUBO_EXITO, huboExito);
		model.addAttribute(MENSAJE_FEEDBACK, mensajePorMostrar);
	}
	
	
	
	public Usuario buscarUsuarioPorNombre(String nombre){
		return this.usuarioGrupoService.buscarUsuarioPorNombre(nombre);
	}
	
	
	
	/**
	 * Para Actualizar el Usuario cargado en la sesion con los cambios realizados en el BackEnd
	 */
	public void actualizarUsuario(){
		String usuarioNombre = this.sesionActual.getUsuarioActual().getNombre();
		Usuario usuarioObtenido = buscarUsuarioPorNombre(usuarioNombre);
		this.sesionActual.setUsuarioActual(usuarioObtenido);
	}
	
	public Calendar DateToCalendar(Date date){ 
		  Calendar cal = Calendar.getInstance();
		  cal.setTime(date);
		  return cal;
	}
}
