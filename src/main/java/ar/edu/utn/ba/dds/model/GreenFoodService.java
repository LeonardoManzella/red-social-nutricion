package ar.edu.utn.ba.dds.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.ba.dds.daos.ComidaDAO;
import ar.edu.utn.ba.dds.daos.GrupoDAO;
import ar.edu.utn.ba.dds.daos.IngredienteDAO;
import ar.edu.utn.ba.dds.daos.PiramideDAO;
import ar.edu.utn.ba.dds.daos.RecetaDAO;
import ar.edu.utn.ba.dds.daos.UsuarioDAO;
import ar.edu.utn.ba.dds.entidades.Comida;
import ar.edu.utn.ba.dds.entidades.Ingrediente;
import ar.edu.utn.ba.dds.entidades.Piramide;
import ar.edu.utn.ba.dds.entidades.Usuario;
import ar.edu.utn.ba.dds.enums.TipoGrupoAlimenticio;
import ar.edu.utn.ba.dds.implement.GrupoAlimenticio;

/**	Es una de las Clases <b>"Servicio"</b> del backend, osea una clase que usan los controllers para acceder al backend.<br><br>
 * 	Basicamente es una clase wrapper que nos sirve de <i>"adaptador/modelo"</i> entre los controllers y el backend/logica de negocio. <br><br>
 * 	Ademas es un <b>Wrapper de los DAOs (Data Acess Object)</b>, por lo que aca se mete toda la <i>"logica sucia"</i> que afecta a la base de datos y que afecta a varios DAOs a la vez.<br><br>
 * 	<b><i>Particularmente se encarga de los Reportes y cosas Varias que no son de los Demas Servicios, osea es como Servicio del Resto de las cosas que no cubren los demas servicios.</i></b>
*/
@Service	
public class GreenFoodService {

	
	/************ DAOs Necesarios ***************/
	@Autowired
	private ComidaDAO		daoComida;
	@Autowired
	private GrupoDAO		daoGrupo;
	@Autowired
	private IngredienteDAO	daoIngrediente;
	@Autowired
	private PiramideDAO		daoPiramide;
	@Autowired
	private RecetaDAO		daoReceta;
	@Autowired
	private UsuarioDAO		daoUsuario;
	
	/******************** Otros Atributos **************/
	
	//Notar que no tiene Constructor, este esta Definido en la clase "AppConfig" en la definicion del BEAN
	
	
	/********************* METODOS 
	 * @throws Exception *****************************/



	public Set<Comida> reporteComidasIngrediente(Usuario usuario, String nombreIngrediente) throws Exception {
		Set<Comida> comidasPorDevolver = null;
		
		Ingrediente ingrediente = this.daoIngrediente.buscarPorNombre(nombreIngrediente);

		comidasPorDevolver = ReportesService.comidasPorIngrediente(usuario, ingrediente);
		if(comidasPorDevolver==null || comidasPorDevolver.isEmpty()==true)	throw new Exception("El Usuario No ha Planificado Comidas con el Ingrediente '" + nombreIngrediente +"'");
		
		
		return comidasPorDevolver;
	}


	
	public Set<Comida> reporteComidasGrupoAlimenticio(Usuario usuario, String nombreGrupoAlimenticio) {
		Set<Comida> comidasPorDevolver = null;

		TipoGrupoAlimenticio enumTransformado = TipoGrupoAlimenticio.matchearGrupo(nombreGrupoAlimenticio);
		
		GrupoAlimenticio grupoBuscado = new GrupoAlimenticio(enumTransformado);

		comidasPorDevolver = ReportesService.comidasPorGrupoAlimenticio(usuario, grupoBuscado);
		return comidasPorDevolver;
	}

	/*
	private boolean estaReceta(Receta receta) throws RecetaExcepcion {
		
		boolean existe = daoReceta.buscarReceta(receta);

		if (existe) throw new RecetaExcepcion("Ya existe una receta con ese nombre");
		return existe;
	}
	*/
	
	public List<Ingrediente> obtenerIngredientesDisponibles(){
		return daoIngrediente.getIngredientes();
	}
	

	public List<Piramide> obtenerTodasPiramides(){
		//NOTA: uso un Set para quitar objetos duplicados si los hubiese
		Set<Piramide> setTemporal = new HashSet<Piramide>();
		setTemporal.addAll(daoPiramide.getPiramides());
		
		List<Piramide> piramidesPorDevolver = new ArrayList<Piramide>();
		piramidesPorDevolver.addAll(setTemporal);
		
		return piramidesPorDevolver;
	}


	/**
	 * Crea y persiste un Nuevo Ingrediente
	 */
	public Ingrediente crearIngrediente(String nombre, TipoGrupoAlimenticio tipo) {
		Ingrediente nuevoIngrediente = new Ingrediente(nombre, tipo);
		
		this.daoIngrediente.agregarObjeto(nuevoIngrediente);
		return nuevoIngrediente;
	}


	/**
	 * Crea y persiste una Nueva Piramide
	 */
	public Piramide crearPiramide(String nombre, Integer proporcionGrupo1, Integer proporcionGrupo2, Integer  proporcionGrupo3, Integer  proporcionGrupo4, Integer  proporcionGrupo5, Integer  proporcionGrupo6, Integer  proporcionGrupo7, Integer  proporcionGrupo8, Integer  proporcionGrupo9, Integer  proporcionGrupo10, Integer  proporcionGrupo11) {
		Piramide piramideNueva = new Piramide( nombre, proporcionGrupo1,proporcionGrupo2,proporcionGrupo3,proporcionGrupo4,proporcionGrupo5,proporcionGrupo6,proporcionGrupo7,proporcionGrupo8,proporcionGrupo9,proporcionGrupo10,proporcionGrupo11 );
		
		this.daoPiramide.agregarObjeto(piramideNueva);
		return piramideNueva;
	}
}
