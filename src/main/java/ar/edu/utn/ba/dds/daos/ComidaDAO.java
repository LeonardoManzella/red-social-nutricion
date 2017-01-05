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

import ar.edu.utn.ba.dds.entidades.Comida;

/**
 * Clase para Encapsular y Manejar todas las operaciones de "Comidas" con la Base de Datos
 */
@Repository
public class ComidaDAO extends GenericDAO{
	
	@Autowired
	private SessionFactory factory;
	
	public ComidaDAO(){
		super();
	}
	
	/**
	 * @param newFactory Necesita una Factory para poder abrir y serrar las sesiones y transacciones de Hibernate
	 */
	public ComidaDAO(SessionFactory newFactory) {
		super(newFactory);
	}
	
	@SuppressWarnings("unchecked")
	public List<Comida> getComidas(Comida criterioBusqueda){
		List<Criterion> criterios = new ArrayList<>();
		/*if(!StringUtils.isEmpty(criterioBusqueda.getFecha())){
			Criterion criterio = Restrictions.eq("fecha",criterioBusqueda.getFecha());
			criterios.add(criterio);
		}*/
		if(!StringUtils.isEmpty(criterioBusqueda.getHorario())){
			Criterion criterio = Restrictions.eq("horario",criterioBusqueda.getHorario());
			criterios.add(criterio);		
		}
		if(criterioBusqueda.getRecetaAsociada() != null){
			Criterion criterio = Restrictions.eq("recetaAsociada.id",criterioBusqueda.getRecetaAsociada().getId());
			criterios.add(criterio);			
		}
		Criteria criteria = this.createCriteria(Comida.class);
		return this.queryObjects(criteria,criterios);
	}
	
	public Comida getComidaPorId(Integer id){
		return (Comida)this.queryObjectForId(id, Comida.class);
	}       

	@SuppressWarnings("unchecked")
	public List<Comida> getComidas(){
		Criteria criteria = this.createCriteria(Comida.class);
		return this.queryObjects(criteria,Comida.class);
	}	

	@SuppressWarnings("deprecation")
	@Override
	public void generateFetchMode(Criteria criteria) {
		criteria.setFetchMode(Comida.class.getName(), FetchMode.JOIN);
		criteria.setFetchMode("recetaAsociada", FetchMode.LAZY);		
	}
	
}
