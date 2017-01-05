package ar.edu.utn.ba.dds.persistencia;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import ar.edu.utn.ba.dds.daos.RecetaDAO;
import ar.edu.utn.ba.dds.entidades.Receta;
import ar.edu.utn.ba.dds.enums.HorarioComida;
import ar.edu.utn.ba.dds.enums.Temporada;
import ar.edu.utn.ba.dds.excepciones.RecetaExcepcion;
import ar.edu.utn.ba.dds.testsVarios.BaseTest;

public class PersistenciaReceta extends BaseTest {

	@Test
	public void agregarRecetaEnBaseDatos() {
		//Uso una receta ya preparada por "BaseTest", "ensaladaRusa"
		Integer recetaID = null;
		Receta recetaObtenida = null;
		
		//Instancio el DAO y agrego la receta
		RecetaDAO dao = new RecetaDAO(sessionFactory);
		recetaID = ensaladaRusa.getId();
		
		//Ahora obtengo la receta de la base y comparo que realmente sea esa.
		//recetaObtenida = dao.obtenerObjetoPorId(recetaID, Receta.class);
		recetaObtenida = dao.getRecetaPorId(recetaID);
	
		assertEquals(true, recetaObtenida.getNombre().equalsIgnoreCase(ensaladaRusa.getNombre()));
		assertEquals(true, recetaObtenida.getListaIngredientes().size() == ensaladaRusa.getListaIngredientes().size());
		assertEquals(true, recetaObtenida.getDescripcion().equalsIgnoreCase(ensaladaRusa.getDescripcion()));
		assertEquals(true, recetaObtenida.getCalorias().intValue() == ensaladaRusa.getCalorias().intValue());
		assertEquals(true, recetaObtenida.getTemporada() == ensaladaRusa.getTemporada());
		assertEquals(true, recetaObtenida.getRecetaBase() == ensaladaRusa.getRecetaBase());
		assertEquals(true, recetaObtenida.getCalificaciones().size() == ensaladaRusa.getCalificaciones().size());
	}
	
	@Test
	public void actualizarRecetaEnBaseDatos() {
		//Uso una receta ya preparada por "BaseTest", "ensaladaRusa"
		Integer recetaID = null;
		Receta recetaObtenida = null;
		
		//Instancio el DAO y agrego la receta
		RecetaDAO dao = new RecetaDAO(sessionFactory);
		recetaID = ensaladaRusa.getId();
		
		//Ahora obtengo la receta de la base y comparo que realmente sea esa.
		//recetaObtenida = dao.obtenerObjetoPorId(recetaID, Receta.class);
		recetaObtenida = dao.getRecetaPorId(recetaID);
		assertEquals(true, recetaObtenida.getDescripcion().equalsIgnoreCase(ensaladaRusa.getDescripcion()));
		
		//Ahora la modifico y Actualizo
		ensaladaRusa.setDescripcion("Nueva Descripcion");
		dao.actualizarObjetoPorID(recetaID, ensaladaRusa);
		
		//Ahora obtengo la receta actualizada de la base y comparo que realmente sea esa.
		//recetaObtenida = dao.obtenerObjetoPorId(recetaID, Receta.class);
		recetaObtenida = dao.getRecetaPorId(recetaID);
		
		assertEquals(true, recetaObtenida.getDescripcion().equalsIgnoreCase(ensaladaRusa.getDescripcion()));
	}
	
	@Test
	public void eliminarRecetaEnBaseDatos() throws RecetaExcepcion {
		Integer recetaID = null;
		Receta recetaObtenida = null;
		Receta recetaNueva = recetaService.crearRecetaNueva("Receta Por Eliminar", "-", 0, Temporada.VERANO, HorarioComida.DESAYUNO);
		
		//Instancio el DAO y agrego el usuario
		RecetaDAO dao = new RecetaDAO(sessionFactory);
		recetaID = recetaNueva.getId();
		
		//Ahora obtengo el usuario de la base y comparo que realmente sea ese.
		//recetaObtenida = dao.obtenerObjetoPorId(recetaID,Receta.class);
		recetaObtenida = dao.getRecetaPorId(recetaID);
		assertEquals(true, recetaObtenida.getNombre().equalsIgnoreCase(recetaNueva.getNombre()));
		
		dao.eliminarObjetoPorId(recetaID, Receta.class);
		
		//Ahora valida que se haya eliminado.
		//recetaObtenida = dao.obtenerObjetoPorId(recetaID,Receta.class);
		recetaObtenida = dao.getRecetaPorId(recetaID);
		assertEquals(null, recetaObtenida);
	}
	
	@Test
	public void queryRecetasEnBaseDatos() {
		//Instancio el DAO
		RecetaDAO dao = new RecetaDAO(sessionFactory);
		List<Receta>  recetas = dao.getRecetas();
		assertEquals(true,recetas.size()>0);   
	}
	
	
}
