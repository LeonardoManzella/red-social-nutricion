package ar.edu.utn.ba.dds.daos;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import ar.edu.utn.ba.dds.entidades.Usuario;


/**
 * Clase para Encapsular y Manejar todas las operaciones de "Usuarios" con la Base de Datos
 */
@Repository
public class UsuarioDAO extends GenericDAO {

	@Autowired
	private SessionFactory factory;
	
	public UsuarioDAO(){
		super();
	}
	
	/**
	 * @param newFactory Necesita una Factory para poder abrir y serrar las sesiones y transacciones de Hibernate
	 */
	public UsuarioDAO(SessionFactory newFactory) {
		super(newFactory);
	}

	@SuppressWarnings("unchecked")
	public List<Usuario> getUsuarios(Usuario criterioBusqueda){
		List<Criterion> criterios = new ArrayList<>();
		if(!StringUtils.isEmpty(criterioBusqueda.getEdad())){
			Criterion criterio = Restrictions.eq("edad",criterioBusqueda.getEdad());
			criterios.add(criterio);
		}
		if(!StringUtils.isEmpty(criterioBusqueda.getMail())){
			Criterion criterio = Restrictions.eq("mail",criterioBusqueda.getMail());
			criterios.add(criterio);		
		}
		if(!StringUtils.isEmpty(criterioBusqueda.getNombre())){
			Criterion criterio = Restrictions.eq("nombre",criterioBusqueda.getNombre());
			criterios.add(criterio);		
		}
		if(!StringUtils.isEmpty(criterioBusqueda.getSexo())){
			Criterion criterio = Restrictions.eq("sexo",criterioBusqueda.getSexo());
			criterios.add(criterio);		
		}		
		Criteria criteria = this.createCriteria(Usuario.class);
		return this.queryObjects(criteria,criterios);
	}

	public Usuario getUsuarioPorId(Integer id){
		return (Usuario)this.queryObjectForId(id, Usuario.class);
	}
	
	@SuppressWarnings("unchecked")
	public boolean buscarUsuario(Usuario usuario) {
		List<Criterion> criterios = new ArrayList<>();
		Criterion criterio = Restrictions.eq("mail", usuario.getMail());
		criterios.add(criterio);
		Criteria criteria = this.createCriteria(Usuario.class);
		List<Usuario> usuarios = this.queryObjects(criteria,criterios);
		return !usuarios.isEmpty();
	}

	@SuppressWarnings("unchecked")
	public List<Usuario> getUsuarios(){
		Criteria criteria = this.createCriteria(Usuario.class);
		return this.queryObjects(criteria,Usuario.class);
	}


	@SuppressWarnings("deprecation")
	@Override
	public void generateFetchMode(Criteria criteria) {
		criteria.setFetchMode(Usuario.class.getName(), FetchMode.JOIN);
		criteria.setFetchMode("recetas", FetchMode.LAZY);
		criteria.setFetchMode("preferencias", FetchMode.LAZY);
		criteria.setFetchMode("usuario_grupo", FetchMode.LAZY);
		criteria.setFetchMode("comidasPlanificadas", FetchMode.LAZY);
		criteria.setFetchMode("restriccion", FetchMode.LAZY);
	}

	public Usuario buscarUsuarioPorNombre(String nombre) {
		List<Criterion> criterios = new ArrayList<>();
		Criterion criterio = Restrictions.eq("nombre",nombre);
		criterios.add(criterio);
		return (Usuario) this.queryObject(criterios, Usuario.class);
	}

	public boolean existeUsuario(String nombreUsuario) {
		//Si encontro algun Usuario da True, sino False
		if(buscarUsuarioPorNombre(nombreUsuario)!=null){
			return true;
		}else{		
			return false;
		}
	}
}
