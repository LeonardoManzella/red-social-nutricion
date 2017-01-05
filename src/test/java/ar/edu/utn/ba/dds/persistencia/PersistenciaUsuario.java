package ar.edu.utn.ba.dds.persistencia;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import ar.edu.utn.ba.dds.daos.UsuarioDAO;
import ar.edu.utn.ba.dds.entidades.Usuario;
import ar.edu.utn.ba.dds.excepciones.GrupoExcepcion;
import ar.edu.utn.ba.dds.excepciones.UsuarioExcepcion;
import ar.edu.utn.ba.dds.testsVarios.BaseTest;

public class PersistenciaUsuario extends BaseTest{
	
	@Test
	public void agregarUsuarioEnBaseDatos() {
		Integer usuarioID = null;
		Usuario usuarioObtenido = null;
		
		//Instancio el DAO y agrego el usuario
		UsuarioDAO dao = new UsuarioDAO(sessionFactory);
		usuarioID = crackTotal.getId();
		
		//Ahora obtengo el grupo de la base y comparo que realmente sea ese.
		//usuarioObtenido = dao.obtenerObjetoPorId(usuarioID,Usuario.class);
		usuarioObtenido = dao.getUsuarioPorId(usuarioID);
		
		assertEquals(true, usuarioObtenido.getNombre().equalsIgnoreCase(crackTotal.getNombre()));		
		assertEquals(true, usuarioObtenido.getRecetas().size() == crackTotal.getRecetas().size());		
		assertEquals(true, usuarioObtenido.getPreferencias().size() == crackTotal.getPreferencias().size());		
		assertEquals(true, usuarioObtenido.getGrupos().size() == crackTotal.getGrupos().size());		
	}
	
	@Test
	public void actualizarUsuarioEnBaseDatos() {
		Integer usuarioID = null;
		Usuario usuarioObtenido = null;
		
		//Instancio el DAO y agrego el usuario
		UsuarioDAO dao = new UsuarioDAO(sessionFactory);
		usuarioID = crackTotal.getId();
		
		//Ahora obtengo el usuario de la base y comparo que realmente sea ese.
		//usuarioObtenido = dao.obtenerObjetoPorId(usuarioID,Usuario.class);
		usuarioObtenido = dao.getUsuarioPorId(usuarioID);
		assertEquals(true, usuarioObtenido.getNombre().equalsIgnoreCase(crackTotal.getNombre()));
		
		//Ahora lo modifico y Actualizo
		crackTotal.setEdad(50);
		dao.actualizarObjetoPorID(usuarioID, crackTotal);
		
		//Ahora obtengo el usuario actualizado de la base y comparo que realmente sea ese.
		usuarioObtenido = dao.obtenerObjetoPorId(usuarioID,Usuario.class);
		assertEquals(true, usuarioObtenido.getEdad().equals(crackTotal.getEdad()));
	}
	
	@Test
	public void eliminarUsuarioEnBaseDatos() throws UsuarioExcepcion, GrupoExcepcion {
		Integer usuarioID = null;
		Usuario usuarioObtenido = null;
		Usuario usuarioNuevo = usuarioGrupoService.crearUsuario("Usuario Por Eliminar", "password", 30, 'M');
		
		//Instancio el DAO y agrego el usuario
		UsuarioDAO dao = new UsuarioDAO(sessionFactory);
		usuarioID = dao.agregarObjeto(usuarioNuevo);
		
		this.usuarioGrupoService.eliminarUsuarioBaseDatos(usuarioID);;
		
		//Ahora valida que se haya eliminado.
		usuarioObtenido = dao.obtenerObjetoPorId(usuarioID,Usuario.class);
		assertEquals(null, usuarioObtenido);
	}	
	
	@Test
	public void buscarUsuarioInexistente() {
		Integer usuarioID = null;
		Usuario usuarioObtenido = null;
		
		//Instancio el DAO y agrego el usuario
		UsuarioDAO dao = new UsuarioDAO(sessionFactory);
		usuarioID = crackTotal.getId();
		
		//Ahora obtengo el grupo de la base y comparo que realmente sea ese.
		//usuarioObtenido = dao.obtenerObjetoPorId(usuarioID,Usuario.class);
		usuarioObtenido = dao.getUsuarioPorId(usuarioID);
		assertEquals(true, usuarioObtenido.getNombre().equalsIgnoreCase(crackTotal.getNombre()));
		
		//Valido que busco un objeto que no existe y retorna null
		usuarioID = -1;
		usuarioObtenido = dao.obtenerObjetoPorId(usuarioID,Usuario.class);
		assertEquals(null, usuarioObtenido);	
	}	

	@Test
	public void queryUsuariosEnBaseDatos() {
		//Instancio el DAO y agrego el usuario
		UsuarioDAO dao = new UsuarioDAO(sessionFactory);
		List<Usuario> Usuarios = dao.getUsuarios();
		assertEquals(true, Usuarios.size()>0);   
	}       
	
}
