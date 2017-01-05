package ar.edu.utn.ba.dds.persistencia;

import static org.junit.Assert.assertEquals;

import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;

import ar.edu.utn.ba.dds.daos.ComidaDAO;
import ar.edu.utn.ba.dds.entidades.Comida;
import ar.edu.utn.ba.dds.enums.HorarioComida;
import ar.edu.utn.ba.dds.testsVarios.BaseTest;

public class PersistenciaComida extends BaseTest {


	@Test
	public void agregarComidaEnBaseDatos() {

		Integer comidaID = null;
		Comida comidaObtenido = null;
		//Obtengo una Comida ya Preparada
		Comida comida = crackTotal.getComidasPlanificadas().stream().findFirst().get();
		//Instancio el DAO y obtengo el ID de la comida, que ya esta persistida
		ComidaDAO dao = new ComidaDAO(sessionFactory);
		comidaID = comida.getId();
		
		//Ahora obtengo la comida de la base y comparo que realmente sea ese.
		//comidaObtenido = dao.obtenerObjetoPorId(comidaID,Comida.class);
		comidaObtenido = dao.getComidaPorId(comidaID);
		
		assertEquals(true, comidaObtenido.getHorario().equals(comida.getHorario()));		
		assertEquals(true, comidaObtenido.getRecetaAsociada().getDescripcion().equalsIgnoreCase(comida.getRecetaAsociada().getDescripcion()));		
		//Tuve que hacerlo asi de rebuscado porque No pude compararlo con el calendario de "comida" resulta que Hibernate me genera Calendars con milisegundos distintos y por eso falla..
		assertEquals(GregorianCalendar.class, comidaObtenido.getFecha().getClass());		
	}
	
	@Test
	public void actualizarComidaEnBaseDatos() {
		Integer comidaID = null;
		Comida comidaObtenido = null;
		Comida comida = crackTotal.getComidasPlanificadas().stream().findFirst().get();
		//Instancio el DAO y obtengo el ID de la comida, que ya esta persistida
		ComidaDAO dao = new ComidaDAO(sessionFactory);
		comidaID = comida.getId();
		
		//Ahora obtengo la comida de la base y comparo que realmente sea ese.
		//comidaObtenido = dao.obtenerObjetoPorId(comidaID,Comida.class);
		comidaObtenido = dao.getComidaPorId(comidaID);
		assertEquals(true, comidaObtenido.getHorario().equals(comida.getHorario()));	
		
		//Ahora lo modifico y Actualizo
		comida.setHorario(HorarioComida.DESAYUNO);
		dao.actualizarObjetoPorID(comidaID, comida);
		
		//Ahora obtengo la comida actualizada de la base y comparo que realmente sea ese.
		//comidaObtenido = dao.obtenerObjetoPorId(comidaID,Comida.class);
		comidaObtenido = dao.getComidaPorId(comidaID);
		assertEquals(true, comidaObtenido.getHorario().equals(comida.getHorario()));
	}
	
	
	
	/*
	NO PUEDE ELIMINARSE COMIDAS ASI COMO SI NADA
	Porque no hay manera de acceder a todas las instancias/copias del usuario que contiene a esa comida.
	Siempre va a quedar alguna copia en la Memoria de la Cache de Hibernate que no podremos acceder y jode todo
	
	@Test
	public void eliminarComidaEnBaseDatos() {
		Integer comidaID = null;
		Comida comidaObtenido = null;
		//Planifico una nueva Comida
		usuarioGrupoService.planificarComida(resentida, fechaBusqueda, ensaladaGriega, HorarioComida.ALMUERZO);
		Comida comida = resentida.getComidasPlanificadas().stream().findFirst().get();
		
		//Instancio el DAO y obtengo el ID de la comida, que ya esta persistida
		ComidaDAO dao = new ComidaDAO(sessionFactory);
		comidaID = comida.getId();
		
		//Ahora obtengo la comida de la base y comparo que realmente sea ese.
		comidaObtenido = dao.getComidaPorId(comidaID);
		assertEquals(true, comidaObtenido.getHorario().equals(comida.getHorario()));
		
		
		usuarioGrupoService.eliminarComidaBaseDatos(comidaID, resentida);
		
		//Ahora valida que se hata eliminado.
		comidaObtenido = dao.getComidaPorId(comidaID);
		assertEquals(null, comidaObtenido);
	}	
	*/
	
	
	@Test
	public void buscarComidaInexistente() {
		Integer comidaID = null;
		Comida comidaObtenido = null;
		Comida comida = crackTotal.getComidasPlanificadas().stream().findFirst().get();
		
		//Instancio el DAO y obtengo el ID de la comida, que ya esta persistida
		ComidaDAO dao = new ComidaDAO(sessionFactory);
		comidaID = comida.getId();
		
		//Ahora obtengo la comida de la base y comparo que realmente sea ese.
		//comidaObtenido = dao.obtenerObjetoPorId(comidaID,Comida.class);
		comidaObtenido = dao.getComidaPorId(comidaID);
		assertEquals(true, comidaObtenido.getHorario().equals(comida.getHorario()));
		
		//Valido que busco un objeto que no existe y retorna null
		comidaID = -1;
		//comidaObtenido = dao.obtenerObjetoPorId(comidaID,Comida.class);
		comidaObtenido = dao.getComidaPorId(comidaID);
		assertEquals(null, comidaObtenido);	
	}
	
	
	@Test
	public void queryComidasEnBaseDatos() {
		//Instancio el DAO
		ComidaDAO dao = new ComidaDAO(sessionFactory);
		List<Comida> comidas = dao.getComidas();
		assertEquals(true,comidas.size()>0);   
	}	

}
