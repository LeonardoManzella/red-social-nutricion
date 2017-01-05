package ar.edu.utn.ba.dds.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.ba.dds.daos.ComidaDAO;
import ar.edu.utn.ba.dds.daos.GrupoDAO;
import ar.edu.utn.ba.dds.daos.IngredienteDAO;
import ar.edu.utn.ba.dds.daos.RecetaDAO;
import ar.edu.utn.ba.dds.daos.UsuarioDAO;
import ar.edu.utn.ba.dds.entidades.Calificacion;
import ar.edu.utn.ba.dds.entidades.Comida;
import ar.edu.utn.ba.dds.entidades.Grupo;
import ar.edu.utn.ba.dds.entidades.Ingrediente;
import ar.edu.utn.ba.dds.entidades.Receta;
import ar.edu.utn.ba.dds.entidades.RestriccionAlimentaria;
import ar.edu.utn.ba.dds.entidades.RestriccionTipoAlimento;
import ar.edu.utn.ba.dds.entidades.Usuario;
import ar.edu.utn.ba.dds.enums.HorarioComida;
import ar.edu.utn.ba.dds.enums.TipoGrupoAlimenticio;
import ar.edu.utn.ba.dds.excepciones.GrupoExcepcion;
import ar.edu.utn.ba.dds.excepciones.RecetaExcepcion;
import ar.edu.utn.ba.dds.excepciones.UsuarioExcepcion;

@Service
public class UsuarioGrupoService {
	
	@Autowired
	private UsuarioDAO 		daoUsuario;
	
	@Autowired
	private GrupoDAO 		daoGrupo;
	
	@Autowired
	private RecetaDAO 		daoReceta;
	
	@Autowired
	private ComidaDAO		daoComida;
	
	@Autowired
	private IngredienteDAO	daoIngrediente;
	
	
	
	public static final String ADMIN_GREENFOOD = "greenfood";
	
	
	
	
	public Usuario crearUsuario(String nombre, String password, Integer edad, char sexo) throws UsuarioExcepcion {
		validarDatosUsuario(nombre, edad);
		Usuario usuario = new Usuario();
		usuario.setMail(nombre + "@greenFood.com.ar");
		usuario.setPassword(password);
		usuario.setEdad(edad);
		usuario.setNombre(nombre);
		usuario.setSexo(sexo);
		usuario.setPreferencias(new HashSet<Ingrediente>());
		usuario.setGrupos(new HashSet<Grupo>());
		usuario.setRecetas(new HashSet<Receta>());
		usuario.setComidasPlanificadas(new HashSet<Comida>());
		agregarUsuario(usuario);
		return usuario;
	}
	
	
	
	public Grupo crearGrupo(String descripcion) throws GrupoExcepcion {
		Grupo grupo = new Grupo();
		grupo.setUsuarios(new HashSet<Usuario>());
		grupo.setRecetasCompartidas(new HashSet<Receta>());
		grupo.setDescripcion(descripcion);
		agregarGrupo(grupo);
		return grupo;
	}
	
	
	
	public Boolean usuarioEsAdmin(Usuario usuario) {
		if( usuario.getNombre().equalsIgnoreCase(ADMIN_GREENFOOD) ) 
			return true;
		else
			return false;
	}
	
	
	
	public void eliminarMiembroDelGrupo(Usuario usuario, Grupo grupo) throws GrupoExcepcion{
		validarEliminarUsuario(grupo, usuario);
		
		quitarCalificaciones(grupo.getRecetasCompartidas(), usuario);
		
		quitarComidasPlanificadas(grupo.getRecetasCompartidas(), usuario);
		
		//Elimino del Grupo las Recetas Compartidas del Usuario
		eliminarRecetasDe(grupo, usuario);
		
		quitarUsuarioDeGrupo(usuario, grupo);
		quitarGrupoDeUsuario(grupo, usuario);
		
		daoUsuario.actualizarObjetoPorID(usuario.getId(), usuario);
		daoGrupo.actualizarObjetoPorID(grupo.getId(), grupo);
	}
	
	
	
	private void quitarUsuarioDeGrupo(Usuario usuarioPorBorrar, Grupo grupo){
		//Para evitar excepcion de Generics
		Usuario usuarioPorEliminar = null;
		
		for(Usuario usuario : grupo.getUsuarios() ){
			if(usuarioPorBorrar.getNombre().equalsIgnoreCase(  usuario.getNombre() )){
				usuarioPorEliminar = usuario;
			}
		}
		
		grupo.getUsuarios().remove(usuarioPorEliminar);
	}
	
	
	
	private void quitarGrupoDeUsuario(Grupo grupoPorBorrar, Usuario usuario){
		//Para evitar excepcion de Generics
		Grupo grupoPorEliminar = null;
		
		for(Grupo grupo : usuario.getGrupos() ){
			if(  coincideGrupo(grupo,grupoPorBorrar)==true  ){
				grupoPorEliminar = grupo;
			}
		}
		usuario.getGrupos().remove(grupoPorEliminar);
	}
	
	
	
	private boolean coincideGrupo(Grupo grupo, Grupo grupoPorComparar){
		if(  grupo.getDescripcion().equalsIgnoreCase(grupoPorComparar.getDescripcion())  ){
			return true;
		}else{
			return false;
		}
	}
	
	
	
	/**
	 * Quita todas las calificaciones que el usuario Hizo a las recetas dadas
	 */
	private void quitarCalificaciones(Set<Receta> recetas, Usuario usuario) {
		for(Receta receta: recetas){
			quitarCalificacionReceta(receta, usuario);
		}
		
	}
	
	
	
	private void quitarCalificacionReceta(Receta receta, Usuario usuario) {
		//Para evitar excepcion de Generics
		Set<Calificacion> calificacionesPorBorrar = new HashSet<Calificacion>();
		
		//Revisamos todas las calificaciones de la receta
		for(Calificacion calificacion: receta.getCalificaciones()){
			//Si coincide que el usuario califico la receta, borramos la calificacion
			if(calificacion.getUsuario().getNombre().equalsIgnoreCase(usuario.getNombre())){
				calificacionesPorBorrar.add(calificacion);
			}
		}
		
		receta.getCalificaciones().removeAll(calificacionesPorBorrar);
	}
	
	
	
	/**
	 * Quita comidas planificadas por el usuario que la receta coincida con las recetas a filtrar
	 */
	private void quitarComidasPlanificadas(Set<Receta> recetasPorFiltrar, Usuario usuario) {
		//Para evitar excepcion de Generics
		Set<Comida> comidasPorBorrar = new HashSet<Comida>();
		
		for(Comida comida: usuario.getComidasPlanificadas()){
			
			//Si la receta de la comida concide con algua de la recetas por filtrar, la eliminamos
			if(recetasPorFiltrar.contains(comida.getRecetaAsociada())){
				comidasPorBorrar.add(comida);
			}
		}
		usuario.getComidasPlanificadas().removeAll(comidasPorBorrar);
	}
	
	
	
	public void compartirReceta(Usuario usuario, Receta receta, Grupo grupo) throws GrupoExcepcion, RecetaExcepcion {
		validarGrupo(usuario, grupo);
		grupo.getRecetasCompartidas().add(receta);
		daoGrupo.actualizarObjetoPorID(grupo.getId(), grupo);
	}
	
	
	
	public void agregarUsuarioAGrupo(Usuario usuario, Grupo grupo) throws GrupoExcepcion {
		estaUsuarioEnGrupo(grupo, usuario.getNombre());
		usuario.getGrupos().add(grupo);
		grupo.getUsuarios().add(usuario);
		daoUsuario.actualizarObjetoPorID(usuario.getId(), usuario);
		daoGrupo.actualizarObjetoPorID(grupo.getId(), grupo);
	}
	
	
	public void agregarUsuarioAGrupoFixMerge(Usuario usuario, Grupo grupo) throws GrupoExcepcion {
		estaUsuarioEnGrupo(grupo, usuario.getNombre());
		usuario.getGrupos().add(grupo);
		grupo.getUsuarios().add(usuario);
		daoUsuario.actualizarObjetoPorIDFixMerge(usuario.getId(), usuario);
		daoGrupo.actualizarObjetoPorIDFixMerge(grupo.getId(), grupo);
	}
	
	
	public Grupo obtenerGrupo(int idGrupo) {
		Grupo grupoObtenido = this.daoGrupo.obtenerObjetoPorId(idGrupo, Grupo.class);
		return grupoObtenido;
	}
	
	
	
	public boolean usuarioPuedeComer(Usuario usuario, Receta receta) {
		//En caso que el usuario no Tenga una Restriccion Asignada, consideramos que puede comer todo
		if(usuario.getRestriccion() != null){
			return !(usuario.getRestriccion().getTipoAlimentoRestringidos().stream().anyMatch(tipoAlimento->tipoAlimento.rechazaGruposAlimenticios(receta.getGruposAlimenticio())));
		}else{
			return true;
		}
	}
	
	
	
	public void salirGrupo(int idGrupo, Usuario usuarioActual) throws GrupoExcepcion {
		Grupo grupoObtenido = this.obtenerGrupo(idGrupo);
		this.eliminarMiembroDelGrupo(usuarioActual, grupoObtenido);
		
	}
	
	public void agregarPreferenciaUsuario(Usuario usuario, Ingrediente preferencia){
		usuario.getPreferencias().add(preferencia);
		daoUsuario.actualizarObjetoPorID(usuario.getId(), usuario);
	}
	
	public Set<Receta> recetasDisponiblesPorUsuario(Usuario usuario) {
		Set<Receta> recetasPorDevolver = new HashSet<Receta>();
		agregarRecetasSinRepetir(recetasPorDevolver, usuario.getRecetas());
		agregarRecetasSinRepetir(recetasPorDevolver, getRecetasDefault());
		
		// Juntamos todas las recetas de los grupos del usuario
		for (Grupo grupo: usuario.getGrupos()){
			agregarRecetasSinRepetir(recetasPorDevolver, grupo.getRecetas() );
		}
		return recetasPorDevolver;
	}
	
	public void agregarRecetasSinRepetir(Collection<Receta> coleccionDondeAgregar, Collection<Receta> coleccionRecetasPorAgregar){
		for(Receta receta : coleccionRecetasPorAgregar){
			if( coleccionContieneReceta(coleccionDondeAgregar, receta)==false ){
				coleccionDondeAgregar.add(receta);
			}
		}
	}
	
	private Set<Receta> getRecetasDefault() {
		Usuario usuarioDefault = this.daoUsuario.buscarUsuarioPorNombre(ADMIN_GREENFOOD);
		return usuarioDefault.getRecetas();
	}
	
	public void agregarRecetaUsuario(Usuario usuario, Receta receta){
		usuario.getRecetas().add(receta);
		daoUsuario.actualizarObjetoPorID(usuario.getId(), usuario);
	}
	
	public void agregarRecetaUsuarioFixMerge(Usuario usuario, Receta receta){
		usuario.getRecetas().add(receta);
		daoUsuario.actualizarObjetoPorIDFixMerge(usuario.getId(), usuario);
	}
	
	public boolean puedeUsuarioCalificarReceta(Usuario usuario, Receta receta) {
		//Puede calificar la Receta si Pertenece a las Recetas Por Default
		if( coleccionContieneReceta(getRecetasDefault(), receta)==true ){
			return true;
			
			
		//Tambien puede si la receta es propia de el
		}else if(usuario.contieneReceta(receta)){
			return true;
		}
		else {
			//Puede calificarla si esta compartida en alguno de los grupos que es miembro
			for(Grupo grupo : usuario.getGrupos()) {
				if(estaRecetaGrupo(grupo, receta))
					return true;
			}
			return false;	
		}
	}
	
	public boolean coleccionContieneReceta(Collection<Receta> coleccion, Receta recetaPorComparar){
		for(Receta recetaDefault : coleccion){
			if(recetaDefault.getNombre().equalsIgnoreCase(recetaPorComparar.getNombre())){
				return true;
			}
		}
		
		return false;
	}
	
	public Comida consultarComidaPorFecha(Usuario usuario, Calendar fecha){
		for(Comida comida : usuario.getComidasPlanificadas()) {
			if(comida.getFecha().equals(fecha))
				return comida;
		}
		return null;
	}
	
	public void planificarComida(Usuario usuario, Calendar fecha, Receta recetaAsociada, HorarioComida horario){
		Comida comidaPlanificada = new Comida(fecha, recetaAsociada, horario);
		usuario.getComidasPlanificadas().add(comidaPlanificada);
		daoUsuario.actualizarObjetoPorID(usuario.getId(), usuario);
	}
	
	
	public void planificarComidaFixMerge(Usuario usuario, Calendar fecha, Receta recetaAsociada, HorarioComida horario){
		Comida comidaPlanificada = new Comida(fecha, recetaAsociada, horario);
		usuario.getComidasPlanificadas().add(comidaPlanificada);
		daoUsuario.actualizarObjetoPorIDFixMerge(usuario.getId(), usuario);
	}
	
	public void planificarComida(Usuario usuario, Calendar fecha, Integer idReceta, String horario){
		
		Receta receta = this.daoReceta.getRecetaPorId(idReceta);
		HorarioComida horarioTransformado = HorarioComida.matchearHorarios(horario);
		
		planificarComidaFixMerge(usuario, fecha, receta, horarioTransformado);
	}
	
	@SuppressWarnings("unchecked")
	public Set<Comida> getComidasPlanificadasPorFecha(Usuario usuario, Calendar fechaBusqueda) {
		return (Set<Comida>)usuario.getComidasPlanificadas().stream().filter(comida-> comida.getFecha().before(fechaBusqueda));	
	}

	public void unirGrupo(int idGrupo, Usuario usuarioActual) throws GrupoExcepcion {
		Grupo grupoObtenido = this.obtenerGrupo(idGrupo);
		estaUsuarioEnGrupo(grupoObtenido, usuarioActual.getNombre() );
		agregarUsuarioAGrupoFixMerge(usuarioActual, grupoObtenido);
	}
	
	public boolean estaRecetaGrupo(Grupo grupo, Receta receta) {
		return this.coleccionContieneReceta(grupo.getRecetasCompartidas(), receta);
	}
	
	private void validarGrupo(Usuario usuario, Grupo grupo) throws GrupoExcepcion {
		if(!grupo.getUsuarios().contains(usuario))
			throw new GrupoExcepcion("El usuario no pertenece al grupo");		
	}
	
	private void estaUsuarioEnGrupo(Grupo grupo, String nombreUsuario) throws GrupoExcepcion {
		for(Usuario usuario : grupo.getUsuarios()){
			if(  usuario.getNombre().equalsIgnoreCase(nombreUsuario)  )		throw new GrupoExcepcion("El usuario ya se encuentra en el grupo");
		}
	}
	
	
	/**
	 * Elimino del Grupo las Recetas Compartidas del Usuario
	 */
	private void eliminarRecetasDe(Grupo grupo, Usuario usuario) {
		Set<Receta> intersection = new HashSet<Receta>(usuario.getRecetas());
		intersection.retainAll(grupo.getRecetasCompartidas());
		
		for (Iterator<Receta> iterator = intersection.iterator(); iterator.hasNext();) {
			grupo.getRecetasCompartidas().remove((Receta) iterator.next());
		}
	}
	
	private void validarEliminarUsuario(Grupo grupo, Usuario usuario) throws GrupoExcepcion {
		for(Usuario usuarioGrupo :grupo.getUsuarios()){
			if( usuarioGrupo.getNombre().equalsIgnoreCase(usuario.getNombre()) )	return;	//Si se contiene el usuario hago un return asi no tira la excepcion
		}
			
		throw new GrupoExcepcion("El usuario no pertenece al grupo");
	}
	
	private void agregarGrupo(Grupo grupo) throws GrupoExcepcion {
		if (!estaGrupo(grupo)) {
			daoGrupo.agregarObjeto(grupo);
		}
	}
	
	private void agregarUsuario(Usuario usuario) throws UsuarioExcepcion {
		if ( !this.estaUsuario(usuario) ) {
			daoUsuario.agregarObjeto(usuario);
		}
	}
	
	private boolean estaUsuario(Usuario usuario) throws UsuarioExcepcion {

		boolean existe = daoUsuario.buscarUsuario(usuario);

		if (existe) throw new UsuarioExcepcion("Ya existe un usuario con ese nombre");
		return existe;
	}
	
	private boolean estaGrupo(Grupo grupo) throws GrupoExcepcion {

		boolean existe = daoGrupo.buscarGrupo(grupo);

		if(existe) throw new GrupoExcepcion("Ya existe un grupo con ese nombre");
		return existe;
	}
	
	private void validarDatosUsuario(String nombre, Integer edad) throws UsuarioExcepcion {
		if(nombre == null || nombre.isEmpty())throw new UsuarioExcepcion("Nombre Obligatorio");
		if (edad <= 17) throw new UsuarioExcepcion("El usuario debe ser mayor de 18 años");
	}

	public Usuario buscarUsuarioPorNombre(String nombre) {
		return this.daoUsuario.buscarUsuarioPorNombre(nombre);
	}
	
	public boolean existeUsuario(String nombreUsuario){
		return daoUsuario.existeUsuario(nombreUsuario);
	}
	
	public List<Grupo> obtenerTodosGrupos(){
		return this.daoGrupo.getGrupos();
	}
	
	public List<Usuario> obtenerTodosusuarios(){
		//NOTA: Uso un Set para eliminar los usuarios repetidos que me trae la consulta (si los hubiera)
		Set<Usuario> listaTemporal = new HashSet<Usuario>();
		listaTemporal.addAll(this.daoUsuario.getUsuarios());
		
		List<Usuario> listaPorDevolver = new ArrayList<Usuario>();
		listaPorDevolver.addAll(listaTemporal);	
		
		return listaPorDevolver;
	}
	
	public Boolean validarPasswordUsuario(String nombreUsuario, String password){
		Usuario usuario = this.buscarUsuarioPorNombre(nombreUsuario);
		
		if(usuario.getPassword().equals(password)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Se encarga de Eliminar un Grupo en la Base de Datos, con todas las implicaciones, incosistencias y requerimientos de Hibenernate que eso conlleva (¡Una Paja!)
	 */
	public void eliminarGrupoBaseDatos(Integer grupoID) throws GrupoExcepcion {
		Grupo grupoObtenido = obtenerGrupo(grupoID);
		
		//Elimino todos los usuarios del grupo, y con ellos sus recetas y comidas compartidas. Asi no quedan inconsistencias en la base de datos
		for(Usuario usuario: grupoObtenido.getUsuarios()){
			eliminarMiembroDelGrupo(usuario, grupoObtenido);		//El metodo hace las actualizaciones necesarias en la base de Datos
		}
		
		//Por ultimo, elimino el Grupo de la Base
		daoGrupo.eliminarObjetoPorId(grupoID, Grupo.class);
	}

	/**
	 * Se encarga de Eliminar un Usuario en la Base de Datos, con todas las implicaciones, incosistencias y requerimientos de Hibenernate que eso conlleva (¡Una Paja!)
	 */
	public void eliminarUsuarioBaseDatos(Integer usuarioID) throws GrupoExcepcion {
		Usuario usuarioObtenido = this.daoUsuario.getUsuarioPorId(usuarioID);
		
		//Elimino todos los grupo del usuarios, y con ellos sus recetas y comidas compartidas. Asi no quedan inconsistencias en la base de datos
		for(Grupo grupo: usuarioObtenido.getGrupos()){
			eliminarMiembroDelGrupo(usuarioObtenido, grupo);		//El metodo hace las actualizaciones necesarias en la base de Datos
		}
		
		
		daoGrupo.eliminarObjetoPorId(usuarioID, Usuario.class);
	}

	public void crearUsuario(String nombreUsuario, String passwordUsuario, Integer edad, char sexo, List<Integer> preferenciasList, List<String> restriccionesList, String nombreRestriccion) throws UsuarioExcepcion {
		//Convierto las Listas
		Set<Ingrediente> preferenciasConvertidas = new HashSet<Ingrediente>();
		Set<TipoGrupoAlimenticio> restriccionesConvertidas = new HashSet<TipoGrupoAlimenticio>();
		
		for(Integer preferencia : preferenciasList){
			preferenciasConvertidas.add(this.daoIngrediente.getIngredientePorId(preferencia));
		}
		
		for(String restriccion : restriccionesList){
			restriccionesConvertidas.add(TipoGrupoAlimenticio.matchearGrupo(restriccion));
		}
		

		Usuario usuarioCreado = crearUsuario(nombreUsuario, passwordUsuario, edad, sexo);
		
		//Agrego las preferencias
		for(Ingrediente preferencia : preferenciasConvertidas){
			agregarPreferenciaUsuario(usuarioCreado, preferencia);
		}
		
		
		//Si la Lista no es Vacia y El Nombre no es vacio, asignamos las restricciones al usuario
		if( nombreRestriccion!=null && !nombreRestriccion.equalsIgnoreCase("") && restriccionesConvertidas.isEmpty() == false){
			agregarRestriccionesUsuario(usuarioCreado, restriccionesConvertidas, nombreRestriccion);
		}
		
	}

	private void agregarRestriccionesUsuario(Usuario usuario, Set<TipoGrupoAlimenticio> restricciones, String nombreRestriccion) {
		//Creo la Restriccion
		RestriccionAlimentaria nuevaRestriccion = new RestriccionAlimentaria(nombreRestriccion);
		
		//Asigno los Grupos ALimenticios que estan Restringidos (no puede comer el usuario)
		for(TipoGrupoAlimenticio restriccion : restricciones){
			nuevaRestriccion.getTipoAlimentoRestringidos().add(  new RestriccionTipoAlimento(restriccion)  );
		}
		
		//Asigno al usuario y Actualizo
		usuario.setRestriccion(nuevaRestriccion);
		daoUsuario.actualizarObjetoPorID(usuario.getId(), usuario);
	}
	
	public void agregarRestriccionUsuario(Usuario usuario, RestriccionAlimentaria restriccion) {
		//Asigno al usuario y Actualizo
		usuario.setRestriccion(restriccion);
		daoUsuario.actualizarObjetoPorID(usuario.getId(), usuario);
	}

}
