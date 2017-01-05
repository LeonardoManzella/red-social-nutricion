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

import ar.edu.utn.ba.dds.entidades.Receta;

/**
 * Clase para Encapsular y Manejar todas las operaciones de "Receta" con la Base de Datos
 */
@Repository
public class RecetaDAO extends GenericDAO {

	@Autowired
	private SessionFactory factory;
	
	public RecetaDAO(){
		super();
	}
	
	
	/**
	 * @param newFactory Necesita una Factory para poder abrir y serrar las sesiones y transacciones de Hibernate
	 */
	public RecetaDAO(SessionFactory newFactory) {
		super(newFactory);
	}

	@SuppressWarnings("unchecked")
	public List<Receta>  getRecetas(Receta criterioBusqueda){
		List<Criterion> criterios = new ArrayList<>();
		if(!StringUtils.isEmpty(criterioBusqueda.getDescripcion())){
			Criterion criterio = Restrictions.eq("descripcion",criterioBusqueda.getDescripcion());
			criterios.add(criterio);
		}
		if(!StringUtils.isEmpty(criterioBusqueda.getCalorias())){
			Criterion criterio = Restrictions.eq("calorias",criterioBusqueda.getCalorias());
			criterios.add(criterio);		
		}
		if(!StringUtils.isEmpty(criterioBusqueda.getHorario())){
			Criterion criterio = Restrictions.eq("horario",criterioBusqueda.getHorario());
			criterios.add(criterio);		
		}
		if(!StringUtils.isEmpty(criterioBusqueda.getNombre())){
			Criterion criterio = Restrictions.eq("nombre",criterioBusqueda.getNombre());
			criterios.add(criterio);		
		}
		if(!StringUtils.isEmpty(criterioBusqueda.getTemporada())){
			Criterion criterio = Restrictions.eq("temporada",criterioBusqueda.getTemporada());
			criterios.add(criterio);		
		}
		if(criterioBusqueda.getRecetaBase() != null){
			Criterion criterio = Restrictions.eq("recetaBase.id",criterioBusqueda.getRecetaBase().getId());
			criterios.add(criterio);		
		}		
		Criteria criteria = this.createCriteria(Receta.class);
		return this.queryObjects(criteria,criterios);
	}
	
	public Receta getRecetaPorId(Integer id){
		return (Receta)this.queryObjectForId(id, Receta.class);
	}       

	@SuppressWarnings("unchecked")
	public List<Receta> getRecetas(){
		Criteria criteria = this.createCriteria(Receta.class);
		return this.queryObjects(criteria,Receta.class);
	}
	

	public Receta buscarPorNombre(String nombre) {
		List<Criterion> criterios = new ArrayList<>();
		Criterion criterio = Restrictions.eq("nombre",nombre);
		criterios.add(criterio);
		return (Receta) this.queryObject(criterios, Receta.class);
	}

	public boolean existeReceta(String nombreReceta) {
		//Si encontro alguna Receta da True, sino False
		if(buscarPorNombre(nombreReceta)!=null){
			return true;
		}else{		
			return false;
		}
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public void generateFetchMode(Criteria criteria) {
		criteria.setFetchMode(Receta.class.getName(), FetchMode.JOIN);
		criteria.setFetchMode("listaIngredientes", FetchMode.LAZY);
		criteria.setFetchMode("calificaciones", FetchMode.LAZY);	
		criteria.setFetchMode("recetaBase", FetchMode.LAZY);
	}

	@SuppressWarnings("unchecked")
	public boolean buscarReceta(Receta receta) {
		List<Criterion> criterios = new ArrayList<>();
		Criterion criterio = Restrictions.eq("nombre", receta.getNombre());
		criterios.add(criterio);
		Criteria criteria = this.createCriteria(Receta.class);
		List<Receta> recetas = this.queryObjects(criteria,criterios);
		return !recetas.isEmpty();
	}

}
