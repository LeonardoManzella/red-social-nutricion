package ar.edu.utn.ba.dds.daos;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Clase para Encapsular y Manejar todas las operaciones de Objetos con la Base de Datos
 */
@Repository
public abstract class GenericDAO {
	
	@Autowired
	private SessionFactory factory;
	
	private Session session;
	
	public GenericDAO(){
		super();
	}
	
	
	/**
	 * @param newFactory  = Necesita una Factory para poder abrir y serrar las sesiones y transacciones de Hibernate
	 */
	
	public GenericDAO(SessionFactory newFactory) {
		factory = newFactory;
	}
	
	/**
	 * @param objetoPorAgregar a la Base de Datos (por Persistir)
	 * @return	ID del Objeto en la Base de Datos. Es un <i>Integer</i>.
	 * <br><strong>Devuelve NULL en caso de <i>Error</i></strong>
	 */
	public Integer agregarObjeto(Object objetoPorAgregar){

		session = factory.openSession();
		Transaction transaction = null;
		Integer idRegistro = null;
		
		try {
			transaction = session.beginTransaction();
			idRegistro = (Integer) session.save(objetoPorAgregar);
			transaction.commit();
		}
		catch (HibernateException excepcionCapturada) {
			if (transaction != null) transaction.rollback();
			excepcionCapturada.printStackTrace();
		}
		finally{
			session.close();
		}
		return idRegistro;
	}
	
	/**
	 * @param objetoId = El ID del Objeto que queremos Obtener de la Base
	 * @param clazz = La clase del Objeto que queremos obtener, necesitamos pasarla porque sino Hibernate no sabe que objeto traer si hay varios con el mismo ID
	 * @return El objteo que queremos Obtener.
	 * <br><strong>Devuelve NULL en caso de <i>Error</i></strong>
	 */
	@SuppressWarnings("unchecked")
	public <T> T obtenerObjetoPorId(Integer objetoId, Class<T> clazz){

		session = factory.openSession();
		Transaction transaction = null;
		T registroObtenido = null;
		
		try{
			transaction = session.beginTransaction();
			registroObtenido = (T)session.get(clazz, objetoId);
			transaction.commit();
		}
		catch(HibernateException excepcionCapturada) {
			if (transaction != null) transaction.rollback();
			excepcionCapturada.printStackTrace();
		}
		finally{
			session.close();
		}
		return registroObtenido;
	}
	
	/**
	 * @param objetoId = ID del Objeto que queremos actualizar en la Base
	 * @param objetoConNuevosValores = El objeto que "sobrescribira"/actualizara al guardado en la Base
	 */
	public void actualizarObjetoPorID(Integer objetoId, Object objetoConNuevosValores){
		session = factory.openSession();
		Transaction transaction = null;
		
		try{
			transaction = session.beginTransaction();
			session.update(objetoConNuevosValores);
			
			//Deberia estar en merge por esto:   http://www.stevideter.com/2008/12/07/saveorupdate-versus-merge-in-hibernate/
			//Pero Lamentablemente el mismo metodo me da error a veces si y a aveces no, no entiendo.
			//session.merge(objetoConNuevosValores);
			transaction.commit();
		}
		catch (HibernateException excepcionCapturada){
			if (transaction != null) transaction.rollback();
			excepcionCapturada.printStackTrace();
		}
		finally{
			session.close();
		}
	}
	
	
	public void actualizarObjetoPorIDFixMerge(Integer objetoId, Object objetoConNuevosValores){
		session = factory.openSession();
		Transaction transaction = null;
		
		try{
			transaction = session.beginTransaction();
			//Debe estar en merge por esto:   http://www.stevideter.com/2008/12/07/saveorupdate-versus-merge-in-hibernate/
			session.merge(objetoConNuevosValores);
			transaction.commit();
		}
		catch (HibernateException excepcionCapturada){
			if (transaction != null) transaction.rollback();
			excepcionCapturada.printStackTrace();
		}
		finally{
			session.close();
		}
	}
	
	
	
	
	/**
	 * @param objetoId = ID del Objeto que queremos eliminar en la Base
	 * @param clazz = La clase del Objeto que queremos obtener, necesitamos pasarla porque sino Hibernate no sabe que objeto eliminar si hay varios con el mismo ID
	 */
	@SuppressWarnings("unchecked")
	public <T> void eliminarObjetoPorId(Integer objetoId, Class<T> clazz){
		session = factory.openSession();
		Transaction transaction = null;
		T registroBorrar = null;
		
		try{
			transaction = session.beginTransaction();
			registroBorrar = (T)session.get(clazz, objetoId);
			session.delete(registroBorrar);
			transaction.commit();
		}
		catch(HibernateException excepcionCapturada){
			if (transaction != null) transaction.rollback();
			excepcionCapturada.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
			
		}
		finally{
			session.close();
		}
	}
	
	@SuppressWarnings("rawtypes")
	public List queryObjects(Criteria criteria,List<Criterion> criterios){
		session = getSession();
		List lista = new ArrayList<Object>();
		try{
			criterios.forEach(criterio->criteria.add(criterio));
			lista =  criteria.list();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			session.close();
		}                       
		return lista;
	}

	@SuppressWarnings("rawtypes")
	public Object queryObject(List<Criterion> criterios,Class clazz){
		session = getSession();
		Criteria cr = session.createCriteria(clazz,clazz.getName());
		Object obj = null;
		try{
			criterios.forEach(criterio->cr.add(criterio));
			obj =  cr.uniqueResult();
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			session.close();
		}               
		return obj;
	}

	@SuppressWarnings("rawtypes")
	public List queryObjects(Criteria criteria,Class clazz){
		session = getSession();
		List lista = null;
		try{
			lista =  criteria.list();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			session.close();
		}                       
		return lista;
	}

	@SuppressWarnings("unchecked")
	public <T> T queryObjectForId(Integer objetoId, Class<T> clazz){
		session = getSession();
		T objeto = null;
		try{
			Criteria cr = session.createCriteria(clazz,clazz.getName());
			Criterion criterio = Restrictions.eq("id", objetoId );
			cr.add(criterio);
			objeto =  (T)cr.uniqueResult();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			session.close();
		}                       
		return objeto;
	}   
	
	@SuppressWarnings("rawtypes")
	public Criteria createCriteria(Class clazz){
		Criteria criteria = null;
		session = getSession();
		criteria =  session.createCriteria(clazz,clazz.getName());
		this.generateFetchMode(criteria);
		return criteria;
	}
	
	public abstract void generateFetchMode(Criteria criteria);
	
	private Session getSession() {
		if(session != null){ 
			if(!session.isOpen()) return factory.openSession();
			 else return session;
		}else{
			return factory.openSession();
		}
	}
}
