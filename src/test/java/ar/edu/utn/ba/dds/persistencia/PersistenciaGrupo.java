package ar.edu.utn.ba.dds.persistencia;


import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import ar.edu.utn.ba.dds.daos.GrupoDAO;
import ar.edu.utn.ba.dds.entidades.Grupo;
import ar.edu.utn.ba.dds.excepciones.GrupoExcepcion;
import ar.edu.utn.ba.dds.excepciones.RecetaExcepcion;
import ar.edu.utn.ba.dds.testsVarios.BaseTest;

public class PersistenciaGrupo extends BaseTest {
	@Test
	public void agregarGrupoEnBaseDatos() throws GrupoExcepcion, RecetaExcepcion {
		//Uso un Grupo ya Preparado por "BaseTest", el "grupoOmnivoro"
		Grupo grupoObtenido = null;
		
		//Instancio el DAO, el grupo ya esta persistido antes
		GrupoDAO dao = new GrupoDAO(sessionFactory);

		//Ahora obtengo el grupo de la base y comparo que realmente sea ese.
		grupoObtenido = dao.getGrupoPorId(grupoOmnivoro.getId()); 
		
		assertEquals(true, grupoObtenido.getDescripcion().equalsIgnoreCase(grupoOmnivoro.getDescripcion()));
		assertEquals(true, grupoObtenido.getUsuarios().size() == grupoOmnivoro.getUsuarios().size());
		assertEquals(true, grupoObtenido.getRecetasCompartidas().size() == grupoOmnivoro.getRecetasCompartidas().size());
	}
	
	@Test
	public void actualizarGrupoEnBaseDatos() {
		//Uso un Grupo ya Preparado por "BaseTest", el "grupoOmnivoro"
		Integer grupoID = grupoOmnivoro.getId();
		Grupo grupoObtenido = null;
		
		//Instancio el DAO, el grupo ya esta persistido antes
		GrupoDAO dao = new GrupoDAO(sessionFactory);
		
		//Ahora obtengo el grupo de la base y comparo que realmente sea ese.
		grupoObtenido = dao.getGrupoPorId(grupoID);
		assertEquals(true, grupoObtenido.getDescripcion().equalsIgnoreCase(grupoOmnivoro.getDescripcion()));
		
		//Ahora lo modifico y Actualizo
		grupoOmnivoro.setDescripcion("Nueva Descripcion");
		dao.actualizarObjetoPorID(grupoID, grupoOmnivoro);
		
		//Ahora obtengo el grupo actualizado de la base y comparo que realmente sea ese.
		grupoObtenido = dao.getGrupoPorId(grupoID);
		assertEquals(true, grupoObtenido.getDescripcion().equalsIgnoreCase(grupoOmnivoro.getDescripcion()));
	}
	
	
	@Test
	public void eliminarGrupoEnBaseDatos() throws GrupoExcepcion {
		//Para no joder los demas tests, hay que crear un grupo y luego eliminarlo
		Integer grupoID = null;
		Grupo grupoObtenido = null;
		
		//Instancio el DAO y creo el grupo
		GrupoDAO dao = new GrupoDAO(sessionFactory);
		
		Grupo grupo = usuarioGrupoService.crearGrupo("Grupo por Eliminar");
		grupoID = grupo.getId();
		//Le agrego un usuario al grupo
		usuarioGrupoService.agregarUsuarioAGrupo(crackTotal, grupo);
		
		//Elimino al grupo
		usuarioGrupoService.eliminarGrupoBaseDatos(grupoID);
		
		//Ahora valida que se hata eliminado.
		grupoObtenido = dao.getGrupoPorId(grupoID);
		assertEquals(null, grupoObtenido);
	}
	
	
	@Test
	public void queryGruposEnBaseDatos() {
		//Instancio el DAO
		GrupoDAO dao = new GrupoDAO(sessionFactory);
		dao.agregarObjeto(grupoOmnivoro);
		List<Grupo> grupos = dao.getGrupos();
		assertEquals(true,grupos.size()>0);   
	}	
	
}
