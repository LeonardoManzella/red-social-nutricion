package ar.edu.utn.ba.dds.persistencia;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import ar.edu.utn.ba.dds.daos.IngredienteDAO;
import ar.edu.utn.ba.dds.entidades.Ingrediente;
import ar.edu.utn.ba.dds.enums.TipoGrupoAlimenticio;
import ar.edu.utn.ba.dds.testsVarios.BaseTest;

public class PersistenciaIngrediente extends BaseTest {

	@Test
	public void agregarIngredienteEnBaseDatos() {
		//Uso un Grupo ya Preparado por "BaseTest", el "grupoOmnivoro"
		Integer ingredienteID = null;
		Ingrediente ingredienteObtenido = null;
		
		//Instancio el DAO y agrego el grupo
		IngredienteDAO dao = new IngredienteDAO(sessionFactory);
		ingredienteID = oliva.getId();
		
		//Ahora obtengo el grupo de la base y comparo que realmente sea ese.
		//ingredienteObtenido = dao.obtenerObjetoPorId(ingredienteID,Ingrediente.class);
		ingredienteObtenido = dao.getIngredientePorId(ingredienteID);
		
		assertEquals(true, ingredienteObtenido.getNombre().equalsIgnoreCase(oliva.getNombre()));		
		assertEquals(true, ingredienteObtenido.getGrupoAlimenticio().getTipoAlimentacion() == oliva.getGrupoAlimenticio().getTipoAlimentacion());		
	}
	
	@Test
	public void actualizarIngredienteEnBaseDatos() {
		//Uso un Grupo ya Preparado por "BaseTest", el "grupoOmnivoro"
		Integer ingredienteID = null;
		Ingrediente ingredienteObtenido = null;
		
		//Instancio el DAO y agrego el grupo
		IngredienteDAO dao = new IngredienteDAO(sessionFactory);
		ingredienteID = pan.getId();
		
		//Ahora obtengo el grupo de la base y comparo que realmente sea ese.
		//ingredienteObtenido = dao.obtenerObjetoPorId(ingredienteID,Ingrediente.class);
		ingredienteObtenido = dao.getIngredientePorId(ingredienteID);
		assertEquals(true, ingredienteObtenido.getNombre().equalsIgnoreCase(pan.getNombre()));
		
		//Ahora lo modifico y Actualizo
		pan.setNombre("Pan Frances");
		dao.actualizarObjetoPorID(ingredienteID, pan);
		
		//Ahora obtengo el grupo actualizado de la base y comparo que realmente sea ese.
		//ingredienteObtenido = dao.obtenerObjetoPorId(ingredienteID,Ingrediente.class);
		ingredienteObtenido = dao.getIngredientePorId(ingredienteID);
		assertEquals(true, ingredienteObtenido.getNombre().equalsIgnoreCase(pan.getNombre()));
	}
	
	@Test
	public void eliminarIngredienteEnBaseDatos() {
		Integer ingredienteID = null;
		Ingrediente ingredienteObtenido = null;
		Ingrediente ingredienteNuevo = new Ingrediente("Ingrediente Por Eliminar",TipoGrupoAlimenticio.DULCES);
		
		//Instancio el DAO y agrego el grupo
		IngredienteDAO dao = new IngredienteDAO(sessionFactory);
		
		ingredienteID = dao.agregarObjeto(ingredienteNuevo);
		
		
		//dao.eliminarObjetoPorId(ingredienteID, Ingrediente.class);
		recetaService.eliminarIngredienteBaseDatos(ingredienteID);
		
		//Ahora valida que se haya eliminado.
		//ingredienteObtenido = dao.obtenerObjetoPorId(ingredienteID,Ingrediente.class);
		ingredienteObtenido = dao.getIngredientePorId(ingredienteID);
		assertEquals(null, ingredienteObtenido);
	}	
	
	@Test
	public void buscarIngredienteInexistente() {
		//Uso un Ingrediente ya preparado por "BaseTest", el "pan"
		Integer ingredienteID = null;
		Ingrediente ingredienteObtenido = null;
		
		//Instancio el DAO y agrego el grupo
		IngredienteDAO dao = new IngredienteDAO(sessionFactory);
		ingredienteID = pan.getId();
		
		//Ahora obtengo el grupo de la base y comparo que realmente sea ese.
		//ingredienteObtenido = dao.obtenerObjetoPorId(ingredienteID,Ingrediente.class);
		ingredienteObtenido = dao.getIngredientePorId(ingredienteID);
		assertEquals(true, ingredienteObtenido.getNombre().equalsIgnoreCase(pan.getNombre()));
		
		//Valido que busco un objeto que no existe y retorna null
		ingredienteID = -1;
		//ingredienteObtenido = dao.obtenerObjetoPorId(ingredienteID,Ingrediente.class);
		ingredienteObtenido = dao.getIngredientePorId(ingredienteID);
		assertEquals(null, ingredienteObtenido);	
	}
	
	@Test
	public void getIngredientes(){
		IngredienteDAO dao = new IngredienteDAO(sessionFactory);

		//Agrego Ingredientes
		dao.agregarObjeto(pan);
		dao.agregarObjeto(cereal);
		
		List<Ingrediente> ingredientes = dao.getIngredientes();
		
		assertEquals(true,ingredientes.size()>0);		
	}
	
}
