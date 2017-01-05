package ar.edu.utn.ba.dds.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ar.edu.utn.ba.dds.config.MainClass;
import ar.edu.utn.ba.dds.entidades.Comida;
import ar.edu.utn.ba.dds.entidades.Ingrediente;
import ar.edu.utn.ba.dds.entidades.Receta;
import ar.edu.utn.ba.dds.entidades.Usuario;
import ar.edu.utn.ba.dds.enums.TipoGrupoAlimenticio;
import ar.edu.utn.ba.dds.excepciones.UsuarioExcepcion;

@Controller
@RequestMapping("/")
public class ControllerDeInicio extends ControllerGenerico {
	//Recordar que Hereda Atributos y Metodos de su Clase Padre
	
	@Autowired
	protected MainClass mainClass;		//Para Cargar Juego de Datos
	
	
	/*********** ATRIBUTOS QUE SE PASAN A TEMPLATES **************/
	protected static final String DIEZ_MEJORES_RECETAS = "diezMejoresRecetas";
	protected static final String SET_COMIDAS_PLANIFICADAS = "comidasPlanificadas";
	
	
	
	/****************** METODOS **************************/
	//Tener en cuenta que el parametro "ModelMap" se encarga de Injectarlo Spring MVC automaticamente
	
	
	/**
	 * Para ver Pagina Home
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getHome(ModelMap model) {	
		//Si no estan Cargados los Datos PreCargados, los cargamos ahora. Si ya estaban, no hacemnos nada.
		mainClass.cargarDatosPreExistentes();
		
		
		List<Receta> diezMejoresRecetas = null;
		cargarAtributosMenu(model);
		
		
		if(this.sesionActual.estaLogueadoUsuario() == true)
			diezMejoresRecetas = this.recetaService.obtenerDiezMejoresRecetas( this.sesionActual.getUsuarioActual() );
		else
			diezMejoresRecetas = this.recetaService.obtenerDiezMejoresRecetasGlobales();
		
		if(diezMejoresRecetas.size() > 5) {
			List<Receta> diezMejoresRecetasPrimeros = new ArrayList<Receta>();
			List<Receta> diezMejoresRecetasSegundos = new ArrayList<Receta>();
			for(int i = 0; i < diezMejoresRecetas.size() / 2; i++)
				diezMejoresRecetasPrimeros.add(diezMejoresRecetas.get(i));
			for(int j = 0; j < diezMejoresRecetas.size() - diezMejoresRecetasPrimeros.size(); j++) {
				diezMejoresRecetasSegundos.add(diezMejoresRecetas.get(j + diezMejoresRecetasPrimeros.size()));
			}
			model.addAttribute("diezMejoresRecetas_1", diezMejoresRecetasPrimeros);
			model.addAttribute("diezMejoresRecetas_2", diezMejoresRecetasSegundos);
		}
		else
			model.addAttribute("diezMejoresRecetas_1", diezMejoresRecetas);

		return PAGINA_INICIO;
	}
	
	
	/**
	 * Para ver Pagina de Login
	 */
	@RequestMapping(value = "login", method = RequestMethod.GET)
	public String getLogin(ModelMap model) {	
		cargarAtributosMenu(model);
		
		return PAGINA_LOGIN;
	}
	
	
	/**
	 * Para loguearse
	 */
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public String postLogin(ModelMap model, String nombreUsuario, String passwordUsuario) {	
		cargarAtributosMenu(model);
		
		Usuario usuarioEncontrado = null;
		
		//Veo si llegan bien los Parametros al Controllers o NO
		if( (nombreUsuario == null) || (passwordUsuario == null) ){
			cargarAtributosFeedBack(model, ERROR, "NO estan llegando los parametros al controller, llegan con NULL. Revisar nombres.");
			return PAGINA_FEEDBACK;
		}
		
		//Veo si el Usuario Existe o NO.
		if(this.usuarioGrupoService.existeUsuario(nombreUsuario)==false){
			cargarAtributosFeedBack(model, ERROR, "No Existe un Usuario con ese Nombre");
			return PAGINA_FEEDBACK;
		}
		
		//Valido Contraseña del usuario.
		if(this.usuarioGrupoService.validarPasswordUsuario(nombreUsuario, passwordUsuario)==false){
			cargarAtributosFeedBack(model, ERROR, "Password Incorrecta");
			return PAGINA_FEEDBACK;
		}
		
		usuarioEncontrado = buscarUsuarioPorNombre(nombreUsuario);
		sesionActual.setUsuarioActual(usuarioEncontrado);
		
		cargarAtributosMenu(model);		//Porque sino no se cargan bien los atributos
		cargarAtributosFeedBack(model, OK, "Logueado con Exito. Bienvenido");
		return PAGINA_FEEDBACK;
	}
	
	/**
	 * Para ver Pagina de Registrarse
	 */
	@RequestMapping(value = "registrar", method = RequestMethod.GET)
	public String getRegistrar(ModelMap model) {	
		cargarAtributosMenu(model);
		
		List<Ingrediente> listaIngredientesDisponibles = this.modelService.obtenerIngredientesDisponibles();
		List<String> listaGruposAlimenticiosDisponibles = TipoGrupoAlimenticio.obtenerGruposAlimenticiosDisponibles();
		
		model.addAttribute(LISTA_INGREDIENTES_DISPONIBLES, listaIngredientesDisponibles);
		model.addAttribute(LISTA_GRUPOS_ALIMENTICIOS_DISPONIBLES, listaGruposAlimenticiosDisponibles);
		
		return PAGINA_REGISTRAR;
	}
	
	
	/**
	 * Para Registarse
	 * NOTA: La lista de Integer de preferencias es una lista de IDs de Ingredientes
	 */
	@RequestMapping(value = "registrar", method = RequestMethod.POST)
	public String postRegistrar(ModelMap model, String nombreUsuario, String passwordUsuario, Integer edad, char sexo, String preferencias, String restricciones, String nombreRestriccion) {
		cargarAtributosMenu(model);
		
		//Veo si llegan bien los Parametros al Controllers o NO
		if( (nombreUsuario == null) || (passwordUsuario == null) || (preferencias == null) || (restricciones == null) ){
			cargarAtributosFeedBack(model, ERROR, "NO estan llegando los parametros al controller, llegan con NULL. Revisar nombres.");
			return PAGINA_FEEDBACK;
		}
		
		List<String> preferenciasList = Arrays.asList(preferencias.split("\\s*,\\s*"));
		List<Integer> preferenciasIds = new ArrayList<Integer>();
		for(String ingrediente : preferenciasList)
			preferenciasIds.add(Integer.valueOf(ingrediente));
		List<String> restriccionesList = Arrays.asList(restricciones.split("\\s*,\\s*"));
		
		//Se Crea al usuario
		try{
			usuarioGrupoService.crearUsuario(nombreUsuario, passwordUsuario, edad, sexo, preferenciasIds, restriccionesList, nombreRestriccion);
			cargarAtributosFeedBack(model, OK, "El Usuario ha sido Registrado");
		}catch (UsuarioExcepcion excepcion){
			cargarAtributosFeedBack(model, false, excepcion.getMessage());
		}
			
		return PAGINA_FEEDBACK;
	}
	
	
	/**
	 * Para ver el Perfil
	 */
	@RequestMapping(value = "perfil", method = RequestMethod.GET)
	public String getPerfil(ModelMap model) {
		cargarAtributosMenu(model);
		
		//Controlo que el usuario este realmente Logueado
		if( this.sesionActual.estaLogueadoUsuario()== false){
			cargarAtributosFeedBack(model, ERROR, "Debe estar Logueado para acceder a esta Pagina");
			return PAGINA_FEEDBACK;
		}
		
		//Para que pueda mostrar los datos del usuario le mando el objeto entero.
		//model.addAttribute(OBJETO_USUARIO,sesionActual.getUsuarioActual() );
		
		return PAGINA_PERFIL_USUARIO;
	}
	
	
	/**
	 * Para ver las Comidas Planificadas del Usuario
	 */
	@RequestMapping(value = "comidas", method = RequestMethod.GET)
	public String getComidas(ModelMap model) {
		cargarAtributosMenu(model);
		
		//Controlo que el usuario este realmente Logueado
		if( this.sesionActual.estaLogueadoUsuario()== false){
			cargarAtributosFeedBack(model, ERROR, "Debe estar Logueado para acceder a esta Pagina");
			return PAGINA_FEEDBACK;
		}
		
		Set<Comida> setComidas = this.sesionActual.getUsuarioActual().getComidasPlanificadas();
		
		//Para que pueda mostrar las Comidas del Usuario
		model.addAttribute(SET_COMIDAS_PLANIFICADAS, setComidas);
		
		return PAGINA_COMIDAS_PLANIFICADAS;
	}
	
	@RequestMapping(value = "logout", method = RequestMethod.GET)
	public String getLogOut(ModelMap model) {
		cargarAtributosMenu(model);
		
		this.sesionActual.usuarioLogOut();
		
		cargarAtributosMenu(model);		//Porque sino no se cargan bien los atributos
		cargarAtributosFeedBack(model, OK, "Log Out exitoso. Adios");
		return PAGINA_FEEDBACK;
	}
}
