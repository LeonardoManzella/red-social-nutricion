package ar.edu.utn.ba.dds.persistencia;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import ar.edu.utn.ba.dds.daos.PiramideDAO;
import ar.edu.utn.ba.dds.entidades.Piramide;
import ar.edu.utn.ba.dds.testsVarios.BaseTest;

public class PersistenciaPiramide extends BaseTest {
	
	@Test
	public void agregarPiramideEnBaseDatos() {

		Integer piramideID = null;
		Piramide piramideObtenido = null;
		//Instancio el DAO y agrego la piramide
		PiramideDAO dao = new PiramideDAO(sessionFactory);
		piramideID = dao.agregarObjeto(piramideComun);
		
		//Ahora obtengo la piramide de la base y comparo que realmente sea ese.
		//piramideObtenido = dao.obtenerObjetoPorId(piramideID,Piramide.class);
		piramideObtenido = dao.getPiramidePorId(piramideID);
		
		assertEquals(true, piramideObtenido.getProporciones().size()== piramideComun.getProporciones().size());		
	}
	

	@Test
	public void eliminarComidaEnBaseDatos() {
		Integer piramideID = null;
		Piramide piramideObtenido = null;
		//Instancio el DAO y agrego la piramide
		PiramideDAO dao = new PiramideDAO(sessionFactory);
		piramideID = dao.agregarObjeto(piramideComun);
		
		//Ahora obtengo la comida de la base y comparo que realmente sea ese.
		//piramideObtenido = dao.obtenerObjetoPorId(piramideID,Piramide.class);
		piramideObtenido = dao.getPiramidePorId(piramideID);
		assertEquals(true, piramideObtenido.getProporciones().size() == piramideComun.getProporciones().size());
		
		dao.eliminarObjetoPorId(piramideID, Piramide.class);
		
		//Ahora valida que se hata eliminado.
		//piramideObtenido = dao.obtenerObjetoPorId(piramideID,Piramide.class);
		piramideObtenido = dao.getPiramidePorId(piramideID);
		assertEquals(null, piramideObtenido);
	}	
	
	@Test
	public void buscarPiramideInexistente() {
		Integer piramideID = null;
		Piramide piramideObtenido = null;
		//Instancio el DAO y agrego la piramide
		PiramideDAO dao = new PiramideDAO(sessionFactory);
		piramideID = dao.agregarObjeto(piramideComun);
		
		//Ahora obtengo la piramide y valido que las proporciones sean las mismas
		//piramideObtenido = dao.obtenerObjetoPorId(piramideID,Piramide.class);
		piramideObtenido = dao.getPiramidePorId(piramideID);
		assertEquals(true, piramideObtenido.getProporciones().size() == piramideComun.getProporciones().size());
		
		//Valido que busco un objeto que no existe y retorna null
		piramideID = -1;
		//piramideObtenido = dao.obtenerObjetoPorId(piramideID,Piramide.class);
		piramideObtenido = dao.getPiramidePorId(piramideID);
		assertEquals(null, piramideObtenido);	
	}
	
	@Test
	public void queryPiramidesEnBaseDatos() {
		//Instancio el DAO
		PiramideDAO dao = new PiramideDAO(sessionFactory);
		dao.agregarObjeto(piramideComun);
		
		List<Piramide> piramides = dao.getPiramides();
		assertEquals(true, piramides.size() > 0);   
	}
	
}
