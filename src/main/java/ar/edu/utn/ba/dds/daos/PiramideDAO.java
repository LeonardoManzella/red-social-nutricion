package ar.edu.utn.ba.dds.daos;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ar.edu.utn.ba.dds.entidades.Piramide;

/**
 * Clase para Encapsular y Manejar todas las operaciones de "Piramides" con la Base de Datos
 */
@Repository
public class PiramideDAO extends GenericDAO {

	@Autowired
	private SessionFactory factory;
	
	public PiramideDAO(){
		super();
	}
	
	/**
	 * @param newFactory Necesita una Factory para poder abrir y serrar las sesiones y transacciones de Hibernate
	 */
	public PiramideDAO(SessionFactory newFactory) {
		super(newFactory);
	}
	
	public Piramide getPiramidePorId(Integer id){
		return (Piramide)this.queryObjectForId(id, Piramide.class);
	}       

	
	public Piramide buscarPorNombre(String nombrePiramide) {
		List<Criterion> criterios = new ArrayList<>();
		Criterion criterio = Restrictions.eq("nombrePiramide",nombrePiramide);
		criterios.add(criterio);
		return (Piramide) this.queryObject(criterios, Piramide.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<Piramide> getPiramides(){
		Criteria criteria = this.createCriteria(Piramide.class);
		return this.queryObjects(criteria, Piramide.class);
	}	
	
	@Override
	public void generateFetchMode(Criteria criteria) {
		// TODO Auto-generated method stub
		
	}
}
