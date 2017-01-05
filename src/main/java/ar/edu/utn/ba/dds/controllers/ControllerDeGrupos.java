package ar.edu.utn.ba.dds.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ar.edu.utn.ba.dds.entidades.Grupo;
import ar.edu.utn.ba.dds.excepciones.GrupoExcepcion;

@Controller
@RequestMapping("/grupos")
public class ControllerDeGrupos extends ControllerGenerico{
	//Recordar que Hereda Atributos y Metodos de su Clase Padre
	
	
	
	/*********** ATRIBUTOS QUE SE PASAN A TEMPLATES **************/
	protected static final String GRUPO_ESPECIFICO = "grupoEspecifico";
	protected static final String TODOS_GRUPOS = "grupos";
	
	
	
	/****************** METODOS **************************/
	//Tener en cuenta que el parametro "ModelMap" se encarga de Injectarlo Spring MVC automaticamente

	

	/**
	 * Para ver los Grupos
	 * La URL es "/grupos" (la misma que la base del controller)
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getTodosGrupos(ModelMap model){
		cargarAtributosMenu(model);
		
		//Controlo que el usuario este realmente Logueado
		if( this.sesionActual.estaLogueadoUsuario()== false){
			cargarAtributosFeedBack(model, ERROR, "Debe estar Logueado para acceder a esta Pagina");
			return PAGINA_FEEDBACK;
		}
		
		List<Grupo> gruposQueHay = this.usuarioGrupoService.obtenerTodosGrupos();
		
		model.addAttribute(TODOS_GRUPOS, gruposQueHay);
		//model.addAttribute(OBJETO_USUARIO, this.sesionActual.getUsuarioActual());
		return PAGINA_VER_GRUPOS;	
	}
		
	/**
	 * Para ver los datos de un Determinado Grupo
	 */
	@RequestMapping(value = "/{idGrupo}", method = RequestMethod.GET)
	public String getGrupoEspecifico(ModelMap model, @PathVariable("idGrupo") int idGrupo){
		cargarAtributosMenu(model);
		
		//Controlo que el usuario este realmente Logueado
		if( this.sesionActual.estaLogueadoUsuario()== false){
			cargarAtributosFeedBack(model, ERROR, "Debe estar Logueado para acceder a esta Pagina");
			return PAGINA_FEEDBACK;
		}
		
		Grupo grupoObtenido = this.usuarioGrupoService.obtenerGrupo(idGrupo);
		
		model.addAttribute(GRUPO_ESPECIFICO,grupoObtenido);
		
		return PAGINA_VER_GRUPO_ESPECIFICO;
	}
	
	/**
	 * Para Salir de un Grupo
	 */
	@RequestMapping(value = "/salir/{idGrupo}", method = RequestMethod.POST)
	public String postSalirGrupo(ModelMap model, @PathVariable("idGrupo") int idGrupo){
		cargarAtributosMenu(model);
		
		try {
			this.usuarioGrupoService.salirGrupo(idGrupo, this.sesionActual.getUsuarioActual());
			actualizarUsuario();
			cargarAtributosFeedBack(model, OK, "Has salido del Grupo");
			
		} catch (GrupoExcepcion excepcion) {
			cargarAtributosFeedBack(model, ERROR, excepcion.getMessage());
		}
		return PAGINA_FEEDBACK;
	}
	
	
	/**
	 * Para unirse a un Grupo
	 */
	@RequestMapping(value = "/unir/{idGrupo}", method = RequestMethod.POST)
	public String postUnirseGrupo(ModelMap model, @PathVariable("idGrupo") int idGrupo){
		cargarAtributosMenu(model);
		
		try {
			this.usuarioGrupoService.unirGrupo(idGrupo, this.sesionActual.getUsuarioActual());
			actualizarUsuario();
		} catch (GrupoExcepcion excepcion) {
			cargarAtributosFeedBack(model, ERROR, excepcion.getMessage());
			return PAGINA_FEEDBACK;
		}
		
		
		cargarAtributosFeedBack(model, OK, "Te Has Unido al Grupo");
		return PAGINA_FEEDBACK;
	}
}
