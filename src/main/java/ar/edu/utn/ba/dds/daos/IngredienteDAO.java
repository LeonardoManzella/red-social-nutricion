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

import ar.edu.utn.ba.dds.entidades.Ingrediente;

/**
 * Clase para Encapsular y Manejar todas las operaciones de "Ingredientes" con la Base de Datos
 */
@Repository
public class IngredienteDAO extends GenericDAO{

	@Autowired
	private SessionFactory factory;
	
	public IngredienteDAO(){
		super();
	}
	
	/**
	 * @param newFactory Necesita una Factory para poder abrir y serrar las sesiones y transacciones de Hibernate
	 */
	public IngredienteDAO(SessionFactory newFactory) {
		super(newFactory);
	}
	
	@SuppressWarnings("unchecked")
	public List<Ingrediente> getIngredientes(Ingrediente criterioBusqueda){
		List<Criterion> criterios = new ArrayList<>();
		if(!StringUtils.isEmpty(criterioBusqueda.getNombre())){
			Criterion criterio = Restrictions.eq("nombre",criterioBusqueda.getNombre());
			criterios.add(criterio);
		}
		if(!StringUtils.isEmpty(criterioBusqueda.getGrupoAlimenticio())){
			Criterion criterio = Restrictions.eq("grupoAlimenticio.id",criterioBusqueda.getId());
			criterios.add(criterio);
		}		
		Criteria criteria = this.createCriteria(Ingrediente.class);
		return this.queryObjects(criteria,criterios);
	}
	
	public Ingrediente getIngredientePorId(Integer id){
		return (Ingrediente)this.queryObjectForId(id, Ingrediente.class);
	} 
	
	public Ingrediente buscarPorNombre(String nombre) {
		List<Criterion> criterios = new ArrayList<>();
		Criterion criterio = Restrictions.eq("nombre",nombre);
		criterios.add(criterio);
		return (Ingrediente) this.queryObject(criterios, Ingrediente.class);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Ingrediente> getIngredientes(){
		Criteria criteria = this.createCriteria(Ingrediente.class);
		return this.queryObjects(criteria,Ingrediente.class);
	}	
	

	@SuppressWarnings("deprecation")
	@Override
	public void generateFetchMode(Criteria criteria) {
		criteria.setFetchMode(Ingrediente.class.getName(), FetchMode.JOIN);
		criteria.setFetchMode("grupoAlimenticio", FetchMode.LAZY);	}
}
