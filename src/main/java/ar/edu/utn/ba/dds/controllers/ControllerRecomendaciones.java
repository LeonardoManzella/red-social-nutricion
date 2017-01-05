package ar.edu.utn.ba.dds.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ar.edu.utn.ba.dds.entidades.Piramide;
import ar.edu.utn.ba.dds.entidades.Receta;
import ar.edu.utn.ba.dds.enums.HorarioComida;

@Controller
@RequestMapping("/recomen")
public class ControllerRecomendaciones extends ControllerGenerico{
	//Recordar que Hereda Atributos y Metodos de su Clase Padre
	
	
	
	/*********** ATRIBUTOS QUE SE PASAN A TEMPLATES **************/
	protected static final String RECETAS_RECOMENDADAS = "recetasRecomendadas";
	protected static final String PIRAMIDES_DISPONIBLES = "piramidesDisponibles";
	

	
	/****************** METODOS **************************/
	//Tener en cuenta que el parametro "ModelMap" se encarga de Injectarlo Spring MVC automaticamente
	
	/**
	 * Para ver Recomendacion del Dia
	 * NOTA: Como hay un Unico Criterio no nos preocupamos de diferenciar cual elije el usuario, por el momento. Si llegamos con el tiempo para la entrega final lo mejoramos.
	 */
	@RequestMapping(value = "/dia", method = RequestMethod.GET)
	public String getRecomendacionDia(ModelMap model){
		cargarAtributosMenu(model);
		
		//Controlo que el usuario este realmente Logueado
		if( this.sesionActual.estaLogueadoUsuario()== false){
			cargarAtributosFeedBack(model, ERROR, "Debe estar Logueado para acceder a esta Pagina");
			return PAGINA_FEEDBACK;
		}
		
		List<String> listaHorarios = HorarioComida.obtenerHorariosDisponibles();
		
		model.addAttribute(HORARIOS_DISPONIBLES,listaHorarios);
		return PAGINA_ELEGIR_RECOMENDACION_DIA;
	}
	
	
	/**
	 * Para ver Resultado de Recomendacion del Dia
	 * NOTA: Como hay un Unico Criterio no nos preocupamos de diferenciar cual elije el usuario, por el momento. Si llegamos con el tiempo para la entrega final lo mejoramos.
	 */
	@RequestMapping(value = "/dia", method = RequestMethod.POST)
	public String postRecomendacionDia(ModelMap model,int cantidadRecetas, String horario){
		cargarAtributosMenu(model);
		
		//Veo si llegan bien los Parametros al Controllers o NO
		if( (cantidadRecetas <= 0) || (horario == null) ){
			cargarAtributosFeedBack(model, ERROR, "NO estan llegando los parametros al controller, llegan con NULL o 0. Revisar nombres.");
			return PAGINA_FEEDBACK;
		}
		
		List<Receta> recetasRecomendadas = this.recetaService.recomendarPorCriterio( cantidadRecetas,  horario, this.sesionActual.getUsuarioActual());
		
		model.addAttribute(RECETAS_RECOMENDADAS, recetasRecomendadas);
		return PAGINA_RECOMENDACIONES;
	}
	
	
	
	/**
	 * Para ver Recomendacion por Balanceo
	 */
	@RequestMapping(value = "/balanceo", method = RequestMethod.GET)
	public String getRecomendacionBalanceo(ModelMap model){
		cargarAtributosMenu(model);
		
		//Controlo que el usuario este realmente Logueado
		if( this.sesionActual.estaLogueadoUsuario()== false){
			cargarAtributosFeedBack(model, ERROR, "Debe estar Logueado para acceder a esta Pagina");
			return PAGINA_FEEDBACK;
		}
		
		List<String> listaHorarios = HorarioComida.obtenerHorariosDisponibles();
		List<Piramide> piramidesDisponibles = this.modelService.obtenerTodasPiramides();
		
		model.addAttribute(HORARIOS_DISPONIBLES,listaHorarios);
		model.addAttribute(PIRAMIDES_DISPONIBLES, piramidesDisponibles);
		return PAGINA_ELEGIR_PIRAMIDE;
	}
	
	
	/**
	 * Para ver Resultado de Recomendacion por Balanceo
	 */
	@RequestMapping(value = "/balanceo", method = RequestMethod.POST)
	public String postRecomendacionBalanceo(ModelMap model, String nombrePiramide, int cantidadDias, String horario){
		cargarAtributosMenu(model);
		
		//Veo si llegan bien los Parametros al Controllers o NO
		if( (nombrePiramide == null) || (cantidadDias <= 0) || (horario == null) ){
			cargarAtributosFeedBack(model, ERROR, "NO estan llegando los parametros al controller, llegan con NULL o 0. Revisar nombres.");
			return PAGINA_FEEDBACK;
		}
		
		List<Receta> recetasRecomendadas = null;
		try {
			recetasRecomendadas = this.recetaService.recomendarPorPiramide(this.sesionActual.getUsuarioActual(), HorarioComida.matchearHorarios(horario), cantidadDias, nombrePiramide);
		} catch (Exception e) {
			cargarAtributosFeedBack(model, ERROR, e.getMessage() );
			return PAGINA_FEEDBACK;
		}
		
		model.addAttribute(RECETAS_RECOMENDADAS, recetasRecomendadas);
		return PAGINA_RECOMENDACIONES;
	}
}

