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

import ar.edu.utn.ba.dds.entidades.Grupo;

/**
 * Clase para Encapsular y Manejar todas las operaciones de "Grupos" con la Base de Datos
 */
@Repository
public class GrupoDAO extends GenericDAO {

	@Autowired
	private SessionFactory factory;
	
	public GrupoDAO(){
		super();
	}
	
	/**
	 * @param newFactory Necesita una Factory para poder abrir y serrar las sesiones y transacciones de Hibernate
	 */
	public GrupoDAO(SessionFactory newFactory) {
		super(newFactory);
	}
	
	@SuppressWarnings("unchecked")
	public List<Grupo> getGrupos(Grupo criterioBusqueda){
		List<Criterion> criterios = new ArrayList<>();
		if(!StringUtils.isEmpty(criterioBusqueda.getDescripcion())){
			Criterion criterio = Restrictions.eq("descripcion", criterioBusqueda.getDescripcion());
			criterios.add(criterio);
		}
		Criteria criteria = this.createCriteria(Grupo.class);
		return (List<Grupo> )this.queryObjects(criteria,criterios);
	}
	
	public Grupo getGrupoPorId(Integer id){
		return (Grupo)this.queryObjectForId(id, Grupo.class);
	} 
	
	@SuppressWarnings("unchecked")
	public List<Grupo> getGrupos(){
		Criteria criteria = this.createCriteria(Grupo.class);
		return this.queryObjects(criteria,Grupo.class);
	}	

	@SuppressWarnings("deprecation")
	@Override
	public void generateFetchMode(Criteria criteria) {
		criteria.setFetchMode(Grupo.class.getName(), FetchMode.JOIN);
		criteria.setFetchMode("usuarios", FetchMode.LAZY);
		criteria.setFetchMode("recetasCompartidas", FetchMode.LAZY);
	}

	@SuppressWarnings("unchecked")
	public boolean buscarGrupo(Grupo grupo) {
		List<Criterion> criterios = new ArrayList<>();
		Criterion criterio = Restrictions.eq("descripcion", grupo.getDescripcion());
		criterios.add(criterio);
		Criteria criteria = this.createCriteria(Grupo.class);
		List<Grupo> grupos = this.queryObjects(criteria, criterios);
		
		return !grupos.isEmpty();
	}

}
