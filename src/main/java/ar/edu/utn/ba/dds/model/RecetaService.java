package ar.edu.utn.ba.dds.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.ba.dds.commons.MapsCommons;
import ar.edu.utn.ba.dds.daos.GrupoDAO;
import ar.edu.utn.ba.dds.daos.IngredienteDAO;
import ar.edu.utn.ba.dds.daos.PiramideDAO;
import ar.edu.utn.ba.dds.daos.RecetaDAO;
import ar.edu.utn.ba.dds.daos.UsuarioDAO;
import ar.edu.utn.ba.dds.entidades.Calificacion;
import ar.edu.utn.ba.dds.entidades.Comida;
import ar.edu.utn.ba.dds.entidades.Grupo;
import ar.edu.utn.ba.dds.entidades.Ingrediente;
import ar.edu.utn.ba.dds.entidades.Piramide;
import ar.edu.utn.ba.dds.entidades.Receta;
import ar.edu.utn.ba.dds.entidades.Usuario;
import ar.edu.utn.ba.dds.enums.HorarioComida;
import ar.edu.utn.ba.dds.enums.Temporada;
import ar.edu.utn.ba.dds.excepciones.RecetaExcepcion;
import ar.edu.utn.ba.dds.excepciones.UsuarioExcepcion;
import ar.edu.utn.ba.dds.interfaces.CriterioFilterPattern;

@Service
public class RecetaService {
	
	private static final int CANTIDAD_RECETAS_POR_MOSTRAR = 10;

	
	
	@Autowired
	private UsuarioGrupoService usuarioGrupoService;
	
	@Autowired
	private RecetaDAO daoReceta;
	
	@Autowired
	private IngredienteDAO	daoIngrediente;
	
	@Autowired
	private PiramideDAO	daoPiramide;
	
	@Autowired
	private GrupoDAO daoGrupo;
	
	@Autowired
	private UsuarioDAO daoUsuario;
	
	
	
	public Receta crearRecetaNueva(final String nombreReceta, final String descripcionReceta, final Integer calorias, 
			final Temporada temporada, final HorarioComida horario) throws RecetaExcepcion {
		Receta receta = new Receta();
		receta.setNombre(nombreReceta);
		receta.setDescripcion(descripcionReceta);
		receta.setlistaIngredientes(new HashSet<Ingrediente>());
		receta.setCalificaciones(new HashSet<Calificacion>());
		receta.setCalorias(calorias);
		receta.setTemporada(temporada);
		receta.setHorario(horario);
		receta.setRecetaBase(null);
		
		if(daoReceta.buscarReceta(receta))
			throw new RecetaExcepcion("Ya existe una receta con este nombre");
		else
			daoReceta.agregarObjeto(receta);
		return receta;
	}
	
	public Receta obtenerReceta(int idReceta){
		return daoReceta.obtenerObjetoPorId(idReceta, Receta.class);
	}
	
	/**
	 * Dado una lista de IDs de Ingredientes los Obtengo de la Base y los devuelvo
	 */
	public Set<Ingrediente> obtenerIngredientesPorIDs(List<Integer> idIngredientes){
		Set<Ingrediente> ingredientesObtenidos = new HashSet<Ingrediente>();
		Ingrediente ingredienteActual = null;
		
		for(Integer idActual: idIngredientes) {
			ingredienteActual = daoIngrediente.obtenerObjetoPorId(idActual, Ingrediente.class);
			ingredientesObtenidos.add(ingredienteActual);
		}
		return ingredientesObtenidos;
	}
	
	public void modificarReceta(int idReceta, List<Integer> idIngredientes, String descripcion, Integer calorias, String temporada, String horario){
		Receta recetaObtenida = this.obtenerReceta(idReceta);
		
		//Busco los Ingredientes
		Set<Ingrediente> ingredientesObtenidos = obtenerIngredientesPorIDs(idIngredientes);
		
		recetaObtenida.setlistaIngredientes(ingredientesObtenidos);
		recetaObtenida.setDescripcion(descripcion);
		recetaObtenida.setCalorias(calorias);
		recetaObtenida.setTemporada(Temporada.matchearTemporada(temporada));
		recetaObtenida.setHorario(HorarioComida.matchearHorarios(horario));
		
		daoReceta.actualizarObjetoPorIDFixMerge(idReceta, recetaObtenida);
	}

	
	public Boolean eliminarRecetaPorId(int idReceta, Usuario usuario) {
		Receta recetaObtenida = this.obtenerReceta(idReceta);
		return eliminarReceta(recetaObtenida.getNombre(), usuario);
	}

	
	public Boolean eliminarReceta(String nombreReceta, Usuario usuarioActualLogueado) {
		if( usuarioContieneReceta(nombreReceta, usuarioActualLogueado)==false){
			return false;
		}
		
		//Nos fijamos si algun usuario planifico alguna comida con esta receta, en ese caso no puede borrarse la receta
		for(Usuario usuarioBaseDatos : this.usuarioGrupoService.obtenerTodosusuarios() ){
			for(Comida comida : usuarioBaseDatos.getComidasPlanificadas()){
				if(  comida.getRecetaAsociada().getNombre().equalsIgnoreCase(nombreReceta)  ){
					return false;
				}
			}
		}
		
		Receta recetaObtenida = usuarioObtenerReceta(nombreReceta, usuarioActualLogueado);;
		
		usuarioActualLogueado.getRecetas().remove(recetaObtenida);
		daoUsuario.actualizarObjetoPorID(usuarioActualLogueado.getId(), usuarioActualLogueado);
		
		//Si la receta Fue Compartida en algun grupo, tenemos que desCompartirla
		desCompartirReceta(recetaObtenida.getNombre());
		
		//Ahora antes de Borrar la Receta tengo que borrar sus Atributos para que no queden cabos sueltos en la Base de Datos
		quitarAtributos(recetaObtenida);
		
		daoReceta.eliminarObjetoPorId(recetaObtenida.getId(), Receta.class);	
		return true;
	}
	
	
	private void desCompartirReceta(String nombreReceta) {
		Receta recetaPorEliminar = null;
		Grupo grupoDelCualEliminar = null;
		
		for(Grupo grupo: this.usuarioGrupoService.obtenerTodosGrupos()){
			for(Receta receta : grupo.getRecetasCompartidas()){
				if(  receta.getNombre().equalsIgnoreCase(nombreReceta)  ){
					recetaPorEliminar = receta;
					grupoDelCualEliminar = grupo;
					break;
				}
			}
		}
		
		if( recetaPorEliminar!=null && grupoDelCualEliminar!=null ){
			grupoDelCualEliminar.getRecetasCompartidas().remove(recetaPorEliminar);
			daoGrupo.actualizarObjetoPorID(grupoDelCualEliminar.getId(), grupoDelCualEliminar);
			//Posible Fix para Varios Grupos: Llamar Recursivamente a esta misma funcion, va a pasar cuando se hagan Null los grupos del cual eliminar (cuando no hallan mas grupos que contengan la receta como compartida)
			//Ya se que es complicado, pero no se me ocurrio otra mejor idea para sortear el problema de Generics de borrar cosas que estas recorriendo en un For
			//desCompartirReceta(nombreReceta)
		}
	}

	private void quitarAtributos(Receta recetaObtenida) {
		recetaObtenida.setCalificaciones(  new HashSet<Calificacion>()  );
		recetaObtenida.setlistaIngredientes(  new HashSet<Ingrediente>()  );
		recetaObtenida.setRecetaBase(null);
		
		daoReceta.actualizarObjetoPorID(recetaObtenida.getId(), recetaObtenida);
	}

	private Receta usuarioObtenerReceta(String nombreReceta, Usuario usuario) {
		for(Receta receta : usuario.getRecetas()){
			if(receta.getNombre().equalsIgnoreCase(nombreReceta)==true){
				return receta;
			}
		}
		return null;
	}

	public boolean usuarioContieneReceta(String nombreReceta, Usuario usuario) {
		
		for(Receta receta : usuario.getRecetas()){
			if(receta.getNombre().equalsIgnoreCase(nombreReceta)==true){
				return true;
			}
		}
		return false;
	}

	public void duplicarReceta(Usuario usuario, int idReceta, String nuevoNombre) throws RecetaExcepcion {
		
		Receta recetaVieja = obtenerReceta(idReceta);
		Receta recetaDuplicada = crearRecetaConBase(nuevoNombre, recetaVieja);
		
		daoReceta.agregarObjeto(recetaDuplicada);
		this.usuarioGrupoService.agregarRecetaUsuarioFixMerge(usuario, recetaDuplicada);
	}
	
	/**
	 * @param receta RecetaBase la cual Duplicar
	 * @param nuevoNombre Nombre de la Receta a Crear
	 * @throws RecetaExcepcion En caso de que Ya exista una receta con ese Nombre
	 */
	public Receta generarRecetaPartiendoDe(Usuario usuario, Receta receta, String nuevoNombre) throws RecetaExcepcion {
		//Genero la nueva receta, valida por si sola que no este duplicada
		Receta nuevaReceta = crearRecetaConBase(nuevoNombre, receta);
		
		//Copio sus Atributos
		nuevaReceta.getListaIngredientes().addAll(receta.getListaIngredientes());
		
		//La agrego a la lista de Recetas del Usuario
		usuarioGrupoService.agregarRecetaUsuario(usuario, nuevaReceta);
		return nuevaReceta;	
	}
	
	public Float calcularPromedio(Receta receta) {
		
		float promedio = 0.0f;
		float sumaCalificaciones = 0.0f; 
		float cantidadCalificaciones = (float)receta.getCalificaciones().size();
		
		for(Calificacion calificacion : receta.getCalificaciones())
			sumaCalificaciones += calificacion.getCalificacion();
		
		promedio = sumaCalificaciones/cantidadCalificaciones;	
		return promedio;
	}
	
	public void calificarRecetaPorID(Usuario usuario, int idReceta, int calificacion) throws UsuarioExcepcion, RecetaExcepcion{
		
		Receta receta = daoReceta.getRecetaPorId(idReceta);
		calificarRecetaFixMerge(usuario, receta, calificacion);
	}
	
	public void calificarReceta(Usuario usuario,Receta receta, int calificacion) throws UsuarioExcepcion, RecetaExcepcion{
		if(usuarioGrupoService.puedeUsuarioCalificarReceta(usuario, receta)) {

			if(recetaYaCalificadaPorUsuario(usuario, receta)) {
				modificarCalificacion(usuario, receta, calificacion);				
			}
			else {
				calificar(usuario, receta, calificacion);
			}
			daoReceta.actualizarObjetoPorID(receta.getId(), receta);
		}
		else {
			//Genero una excepcion
			throw new UsuarioExcepcion("No puedo calificar esta Receta, no es mia ni esta compartida en algun grupo del cual soy miembro");
		}
	}
	
	
	public void calificarRecetaFixMerge(Usuario usuario,Receta receta, int calificacion) throws UsuarioExcepcion, RecetaExcepcion{
		if(usuarioGrupoService.puedeUsuarioCalificarReceta(usuario, receta)) {

			if(recetaYaCalificadaPorUsuario(usuario, receta)) {
				modificarCalificacion(usuario, receta, calificacion);				
			}
			else {
				calificar(usuario, receta, calificacion);
			}
			daoReceta.actualizarObjetoPorIDFixMerge(receta.getId(), receta);
		}
		else {
			//Genero una excepcion
			throw new UsuarioExcepcion("No puedo calificar esta Receta, no es mia ni esta compartida en algun grupo del cual soy miembro");
		}
	}
	
	
	public Collection<Integer> listarCalificacionesDe(Usuario usuario, Receta receta) throws UsuarioExcepcion{
		if(!usuarioGrupoService.recetasDisponiblesPorUsuario(usuario).contains(receta)){
			throw new UsuarioExcepcion("No se puede listar las calificaciones de una receta que no esta disponible (no la contiene el usuario ni alguno de sus grupos)");
		}
		return listarCalificaciones(receta);
	}
	
	public List<Receta> obtenerRankingRecetas(Grupo grupo) {
		List<Receta> listaPorDevolver = new ArrayList<Receta>();
		
		for (Iterator<Receta> iterator = grupo.getRecetasCompartidas().iterator(); iterator.hasNext();) {
			listaPorDevolver.add(iterator.next());
			
		}
		listaPorDevolver.sort(ComparadorPorPromedio);
		return listaPorDevolver;
	}
	
	private Collection<Integer> listarCalificaciones(Receta receta) {	
		Collection<Integer> calificaciones = new ArrayList<Integer>();
		for(Calificacion calificacion : receta.getCalificaciones())
			calificaciones.add(calificacion.getCalificacion());
		return calificaciones;
	}
	
	public boolean contieneCalificacion(Receta receta, int calificacionBuscar) {
		for(Calificacion calificacion : receta.getCalificaciones()) {
			if(calificacion.getCalificacion().intValue() == calificacionBuscar)
				return true;
		}
		return false;
	}
	
	/**
	 * Método para buscar las N recetas recomendables según el criterio elegido.
	 * Las recomendaciones se relacionan con las recetas por el horario de las mismas.
	 *
	 * @param  usuario 			el usuario que va a recibir las recomendaciones
	 * @param  cantidadRecetas  cantidad de recomendaciones a mostrar
	 * @param  horario 			horario de las recetas a recomendar
	 * @param  criterio 		criterio seleccionado para evaluar las recetas a recomendar
	 * @return Devuelve una cola de recetas a recomendar del tamaño "cantidadRecetas".
	 * 		   Para sacar las recetas de la cola utilizar el metodo poll(),
	 * 		   la primera receta removida de la cola será la de mayor rating
	 * 		   y así las demas en el orden descendiente del puntaje.
	 *
	 **/
	public Deque<Receta> recomendarPorDiaHorario(Usuario usuario, int cantidadRecetas, HorarioComida horario, CriterioFilterPattern criterio) {
		return criterio.recomendarPorCriterio(cantidadRecetas, usuario, horario);
	}
	
	/**
	 * @param usuario el usuario que va a recibir las recomendaciones
	 * @param horario horario de las recetas a recomendar
	 * @param cantidadDias cuantos dias hacia atras se tienen en cuenta para realizar el balanceo de la piramide (Impacta en que comidas planificadas se tienen en cuenta)
	 * @param piramide la piramide alimenticia que eligio el usuario para balancear sus comidas
	 * @return Devuelve una receta recomendada para balancear la piramide
	 */
	public Receta recomendarPorPiramide(Usuario usuario, HorarioComida horario, int cantidadDias, Piramide piramide){
		//Obtenemos el Historico de Comidas del Usuario
		Calendar fechaHoy = Calendar.getInstance();
		Calendar fechaBusqueda = Calendar.getInstance();
		fechaBusqueda.add(Calendar.DATE, -cantidadDias);
		Set<Comida> historico = ReportesService.comidasPorPeriodo(usuario, fechaBusqueda, fechaHoy);
		
		//Obtenemos las recetas Disponibles del Usuario y quitamos las que tenga problemas el usuario
		Set<Receta> recetasDisponibles = usuarioGrupoService.recetasDisponiblesPorUsuario(usuario);
		Set<Receta> recetasPorEliminar = new HashSet<Receta>();
		//Sacamos todas las Recetas que no coinciden con el Horario del Cual se quiere Recomendar
		
		for(Receta receta: recetasDisponibles){
			if( !receta.getHorario().equals(horario) && usuarioGrupoService.usuarioPuedeComer(usuario, receta)){
				recetasPorEliminar.add(receta);
				
			}
		}
		recetasDisponibles.removeAll(recetasPorEliminar);
		
		
		return piramide.recomendarReceta(historico, recetasDisponibles);
	}
	
	
	
	public List<Receta> recomendarPorPiramide(Usuario usuario, HorarioComida horario, int cantidadDias, String nombrePiramide) throws Exception{
		List<Receta> recetasPorDevolver = new ArrayList<Receta>();
		
		Piramide piramide = this.daoPiramide.buscarPorNombre(nombrePiramide);
		if(piramide==null){
			throw new Exception("No existe una Piramide con ese Nombre");
		}
		
		
		Receta receta = recomendarPorPiramide(usuario, horario, cantidadDias, piramide);
		if(receta!=null) recetasPorDevolver.add(receta);
		
		return recetasPorDevolver;
	}
	
	
	
	//Nota las Recetas Devueltas estaran Ordenadas por Orden en la Lista por Mejor Puntaje (Primer receta = mejor puntaje)
	public List<Receta> recomendarPorCriterio(int cantidadRecetas, String horario, Usuario usuarioActual) {
		List<Receta> recetasPorDevolver =  new ArrayList<Receta>();

		HorarioComida horarioTransformado = HorarioComida.matchearHorarios(horario);

		Deque<Receta> recetasObtenidas = recomendarPorDiaHorario(usuarioActual, cantidadRecetas, horarioTransformado, new MejorPuntaje());
		
		//Recorro Deque Agregando Recetas a la Lista para Devolver, osea estoy transformando de una lista a otra aprovechando que el Deque las devuelve ordenadas por puntuacion
		for(Receta receta: recetasObtenidas){
			recetasPorDevolver.add(receta);
		}
		
		return recetasPorDevolver;
	}
	
	private Comparator<Receta> ComparadorPorPromedio = new Comparator<Receta>() {
		public int compare(Receta primerReceta, Receta segundaReceta) {
			//Genera Orden Descendiente (de Mayor Promedio a Menor Promedio)
			if(calcularPromedio(primerReceta)<calcularPromedio(segundaReceta)){
				//La segundaReceta tiene mayor promedio
				return -1;
			}else if(calcularPromedio(primerReceta)>calcularPromedio(segundaReceta)){
				//La segundaReceta tiene menor promedio
				return 1;
			}else{
				//Son iguales
				return 0;
			}
		}
	};
	
	private void calificar(Usuario usuario, Receta receta, int calificacion) throws RecetaExcepcion {
		if(recetaYaCalificadaPorUsuario(usuario, receta)) {
			throw new RecetaExcepcion("Este usuario ya califico esta receta");
		}
		else {
			//Sino, es la primera vez que la califica
			Calificacion calificacionNueva = new Calificacion(usuario, calificacion);
			receta.getCalificaciones().add(calificacionNueva);
		}
	}

	private void reemplazarCalificacion(Usuario usuario, Receta receta, int calificacion) {
		for(Calificacion calificacionReemplazar : receta.getCalificaciones()) {
			if(calificacionReemplazar.getUsuario().equals(usuario)) {
				calificacionReemplazar.setCalificacion(calificacion);
				break;
			}	
		}
	}
	
	private void modificarCalificacion(Usuario usuario, Receta receta, int calificacion) throws RecetaExcepcion {
		//Primero me fijo de tirar la excepcion si el usuario Nunca califico la receta
		if(!recetaYaCalificadaPorUsuario(usuario, receta)) {
			throw new RecetaExcepcion("Este usuario nunca califico esta receta");
		}
		else {
			//Sino, es que ya la ha calificado anteriormente, entonces la modifico
			reemplazarCalificacion(usuario, receta, calificacion);
		}

		return;
	}
	
	private boolean recetaYaCalificadaPorUsuario(Usuario usuario, Receta receta) {
		for(Calificacion calificacion : receta.getCalificaciones()) {
			if(calificacion.getUsuario().equals(usuario))
				return true;
		}
		return false;
	}
	
	private Receta crearRecetaConBase(String nombreReceta, Receta receta) throws RecetaExcepcion {
		Receta recetaNueva = new Receta();
		recetaNueva.setNombre(nombreReceta);
		recetaNueva.setDescripcion(receta.getDescripcion());
		recetaNueva.setlistaIngredientes(new HashSet<Ingrediente>());
		recetaNueva.setCalificaciones(new HashSet<Calificacion>());
		recetaNueva.setCalorias(receta.getCalorias());
		recetaNueva.setRecetaBase(receta);
		recetaNueva.setTemporada(receta.getTemporada());
		recetaNueva.setHorario(receta.getHorario());
		
		if(daoReceta.existeReceta(nombreReceta)==true) 
			throw new RecetaExcepcion("Ya existe una receta con este nombre");
		else
			daoReceta.agregarObjeto(recetaNueva);
		return recetaNueva;
	}
	
	public CriterioFilterPattern crearCriterio(String criterio) {
		if(criterio.equalsIgnoreCase("MejorPuntaje"))
			return new MejorPuntaje();
		return null;
	}
	
	public List<Receta> obtenerDiezMejoresRecetasGlobales(){
		
		//Obtengo Recetas Disponibles
		List<Receta> recetasDisponibles = this.daoReceta.getRecetas();
		
		//Calculo en una Variable Temporal el Puntaje, para luego poder ordenarlo
		HashMap<Receta, Float> recetasPuntaje = new HashMap<Receta, Float>();
		
		for(Receta receta: recetasDisponibles){
			float puntaje = 0;
			puntaje += receta.calcularPromedio();
			recetasPuntaje.put(receta, puntaje);
		}
		
		//Ordeno por Puntaje
		Set<Receta> recetasOrdenadas = MapsCommons.sortMapByValue(recetasPuntaje).keySet();		
		
		return obtenerDiezRecetas(recetasOrdenadas);
	}
	
	
	public List<Receta> obtenerDiezMejoresRecetas(Usuario usuario){
		
		//Si el usuario tiene Recetas Creadas, devolvemos hasta '10' de ellas
		if (usuario.getRecetas().isEmpty() == false){
			return obtenerDiezRecetas(usuario.getRecetas());
			
		//Si el usuario Ha Calificado Alguna Receta
		}else if(obtenerRecetasCalificadas(usuario).isEmpty() == false){
			return obtenerDiezRecetas(obtenerRecetasCalificadas(usuario));
			
		//Sino, devuelve las '10' Mejores Recetas Globales
		}else{
			return obtenerDiezMejoresRecetasGlobales();
		}
	}
	
	public List<Receta> obtenerRecetasCalificadas(Usuario usuario){
		 Set<Receta> recetasDisponibles = this.usuarioGrupoService.recetasDisponiblesPorUsuario(usuario);
		 Set<Receta> recetasPorEliminar = new HashSet<Receta>();
		 
		 //Quito todas las Recetas No Calificadas
		 for(Receta receta: recetasDisponibles){
			 if( recetaYaCalificadaPorUsuario(usuario, receta)==false ){
				 recetasPorEliminar.add(receta);
			 }
		 }
		 recetasDisponibles.removeAll(recetasPorEliminar);
		 
		 //Transformo de Set a List
		 List<Receta> recetasPorDevolver = new ArrayList<Receta>();
		 recetasPorDevolver.addAll(recetasDisponibles);
		 
		 return recetasPorDevolver;
	}
	
	
	public List<Receta> obtenerDiezRecetas(Collection<Receta> coleccionReceta){
		List<Receta> recetasPorDevolver = new ArrayList<Receta>();

		//Solo agarro 10 recetas para devolver
		int contador = 1;
		for(Receta receta : coleccionReceta) {
			contador++;
			recetasPorDevolver.add(receta);

			if(contador == CANTIDAD_RECETAS_POR_MOSTRAR)
				break;
		}
		
		return recetasPorDevolver;
	}
	
	
	public class MejorPuntaje implements CriterioFilterPattern {
		
		/**
		 * Dado la cantidad de recetas a mostrar, el usuario a quien recomendar y el horario de las recetas deseadas
		 *  @param horario Solo se recomendaran las recetas que tengan asignado este horario, las demas se ignoraran
		 */
		public Deque<Receta> recomendarPorCriterio(int cantidadDeRecetas, Usuario usuario, HorarioComida horario) {
			
			Set<Ingrediente> ingredientesPreferidos = usuario.getPreferencias();
			Set<Receta> recetasDisponibles = usuarioGrupoService.recetasDisponiblesPorUsuario(usuario);
			
			HashMap<Receta, Float> recetasRecomendadas = new HashMap<Receta, Float>();
			
			for(Receta receta : recetasDisponibles) {
				if(receta.getHorario().equals(horario) && usuarioGrupoService.usuarioPuedeComer(usuario, receta)) {
					float puntaje = 0;
					puntaje += getPuntajePorIngredientes(receta, ingredientesPreferidos);
					puntaje += getPuntajePorCalificaciones(receta);
					recetasRecomendadas.put(receta, puntaje);
				}
			}
			
			Set<Receta> recetasKeys = MapsCommons.sortMapByValue(recetasRecomendadas).keySet();
			Deque<Receta> recetasRecomendadasFinal = new ArrayDeque<Receta>();
			
			int contador = 0;
			for(Receta receta : recetasKeys) {
				contador++;
				recetasRecomendadasFinal.offer(receta);
				if(contador == cantidadDeRecetas)
					break;
			}
			
			return recetasRecomendadasFinal;
		}
		
				
		private float getPuntajePorIngredientes(Receta receta, Set<Ingrediente> ingredientesPreferidos) {
			int puntaje = 0; 
			for(Ingrediente ingrediente : ingredientesPreferidos) {
				if(receta.getListaIngredientes().contains(ingrediente))
					puntaje++;
			}
			return (float)puntaje;
		}
		
		private float getPuntajePorCalificaciones(Receta receta) {
			return receta.calcularPromedio();
		}
	}


	/**
	 * Se encarga de Eliminar un Ingrediente en la Base de Datos, con todas las implicaciones, incosistencias y requerimientos de Hibenernate que eso conlleva (¡Una Paja!)
	 */
	public void eliminarIngredienteBaseDatos(Integer ingredienteID) {
		Ingrediente ingrediente = this.daoIngrediente.getIngredientePorId(ingredienteID);
		
		
		//Obtengo todas las Recetas, para quitarles el ingrediente
		List<Receta> recetas = this.daoReceta.getRecetas();
		for(Receta receta : recetas){
			eliminarIngredienteDe(ingrediente, receta.getListaIngredientes());
			this.daoReceta.actualizarObjetoPorID(receta.getId(), receta);
		}
		
		//Obtengo todos los usuarios, para quitarles el Ingrediente de las preferencias
		List<Usuario> usuarios = this.daoUsuario.getUsuarios();
		for(Usuario usuario : usuarios){
			eliminarIngredienteDe(ingrediente, usuario.getPreferencias() );
			this.daoUsuario.actualizarObjetoPorID(usuario.getId(), usuario);
		}
		
		//Por ultimo, elimino el Ingrediente de la Base
		this.daoIngrediente.eliminarObjetoPorId(ingredienteID, Ingrediente.class);
	}

	private void eliminarIngredienteDe(Ingrediente ingredientePorEliminar, Collection<Ingrediente> listaIngredientes) {
		//Como no puedo eliminar objetos de las colecciones a medida que las recorro, lo que hago es "guardarme" los objetos a borrar y los borro al final todos de una.
		Set<Ingrediente> ingredientesPorBorrar = new HashSet<Ingrediente>();
		
		//Recorro la lista de ingredientes quitando el ingredientePorEliminar
		for(Ingrediente ingredienteActual : listaIngredientes){
			if(ingredienteActual.getNombre().equalsIgnoreCase(ingredientePorEliminar.getNombre())){
				ingredientesPorBorrar.add(ingredienteActual);
			}
		}
		
		//Ahora borro de una todos los ingredientes
		listaIngredientes.removeAll(ingredientesPorBorrar);
	}

	public void crearRecetaNueva(Usuario usuario, String nombreReceta, String descripcion, Integer calorias, String temporada, String horario, List<Integer> idIngredientes) throws RecetaExcepcion {
		//Convierto los Ingredientes
		Set<Ingrediente> ingredientes = obtenerIngredientesPorIDs(idIngredientes);
		
		Receta recetaNueva = crearRecetaNueva(nombreReceta, descripcion, calorias, Temporada.matchearTemporada(temporada), HorarioComida.matchearHorarios(horario));
		
		agregarIngredientesReceta(recetaNueva, ingredientes);
		
		compartirRecetaTodosGrupos(usuario, recetaNueva);
		
		this.usuarioGrupoService.agregarRecetaUsuarioFixMerge(usuario, recetaNueva);
	}

	private void compartirRecetaTodosGrupos(Usuario usuario, Receta receta) {
		for(Grupo grupo : usuario.getGrupos()){
			grupo.getRecetasCompartidas().add(receta);
		}
		
	}

	public void agregarIngredientesReceta(Receta recetaNueva, Set<Ingrediente> ingredientes) {
		recetaNueva.setlistaIngredientes(ingredientes);
		this.daoUsuario.actualizarObjetoPorID(recetaNueva.getId(), recetaNueva);		
	}
	
	public void agregarIngredienteReceta(Receta recetaNueva,Ingrediente ingrediente) {
		recetaNueva.getListaIngredientes().add(ingrediente);
		this.daoUsuario.actualizarObjetoPorID(recetaNueva.getId(), recetaNueva);		
	}
	
}
