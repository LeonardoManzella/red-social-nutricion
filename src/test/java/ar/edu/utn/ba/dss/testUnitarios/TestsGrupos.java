package ar.edu.utn.ba.dss.testUnitarios;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import ar.edu.utn.ba.dds.config.AppConfig;
import ar.edu.utn.ba.dds.entidades.Grupo;
import ar.edu.utn.ba.dds.entidades.Ingrediente;
import ar.edu.utn.ba.dds.entidades.Receta;
import ar.edu.utn.ba.dds.entidades.Usuario;
import ar.edu.utn.ba.dds.excepciones.GrupoExcepcion;
import ar.edu.utn.ba.dds.excepciones.RecetaExcepcion;
import ar.edu.utn.ba.dds.excepciones.UsuarioExcepcion;
import ar.edu.utn.ba.dds.enums.TipoGrupoAlimenticio;
import ar.edu.utn.ba.dds.enums.HorarioComida;
import ar.edu.utn.ba.dds.enums.Temporada;
import ar.edu.utn.ba.dds.model.RecetaService;
import ar.edu.utn.ba.dds.model.UsuarioGrupoService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class}, loader = AnnotationConfigContextLoader.class)
public class TestsGrupos {
	
	private static SessionFactory sessionFactory;
	
	@Autowired
	UsuarioGrupoService usuarioGrupoService;
	
	@Autowired
	protected RecetaService recetaService;
	
	
	private Grupo grupoVegetarianos;	
	
	private Usuario vegano;
	private Usuario vegano1;
	@SuppressWarnings("unused")		//Si bien JAVA detecta que no se usa, en realidad es Necesario que este este Usuario
	private Usuario usuarioAdmin;
	
	private Receta pollo;
	private Receta polloPapas;
	private Receta asado;
	private Receta ensalada;
	private Receta porotos;
	private Receta manzana;
	private Receta banana;	
	
	private Calendar fecha;
    
	@Rule
    public ExpectedException thrown= ExpectedException.none();

    
	@Before
	public void setUp() throws Exception {
		
		Configuration config = new Configuration().addResource("hibernate.cfg.xml").configure();
		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(config.getProperties());
		sessionFactory = config.buildSessionFactory(ssrb.build());
		
		grupoVegetarianos = usuarioGrupoService.crearGrupo("Vegatariano");
		
		vegano = usuarioGrupoService.crearUsuario("Vegano", "password", 19, 'M');
		vegano1 = usuarioGrupoService.crearUsuario("Vegano1", "password", 19, 'F');
		
		pollo = recetaService.crearRecetaNueva("pollo", "al horno", 500, Temporada.INVIERNO, HorarioComida.ALMUERZO);
		pollo.agregarIngrediente(new Ingrediente("Carne", TipoGrupoAlimenticio.PESCADOS_HUEVOS_CARNES_MAGRAS));
		
		polloPapas = recetaService.crearRecetaNueva("Pollo al Horno Con Papas", "al horno con Papas", 600, Temporada.OTONIO, HorarioComida.CENA);
		polloPapas.agregarIngrediente(new Ingrediente("Carne", TipoGrupoAlimenticio.PESCADOS_HUEVOS_CARNES_MAGRAS));
		
		asado = recetaService.crearRecetaNueva( "asado", "a la Parrilla", 1000, Temporada.VERANO, HorarioComida.CENA);
		asado.agregarIngrediente(new Ingrediente("Carne", TipoGrupoAlimenticio.CARNES_GRASAS));
		
		ensalada = recetaService.crearRecetaNueva("ensalada", null, 100, Temporada.VERANO, HorarioComida.ALMUERZO);
		ensalada.agregarIngrediente(new Ingrediente("Verdura", TipoGrupoAlimenticio.VEGETALES));
		
		porotos = recetaService.crearRecetaNueva( "porotos", null, 400, Temporada.PRIMAVERA, HorarioComida.ALMUERZO);
		porotos.agregarIngrediente(new Ingrediente("Legumbres", TipoGrupoAlimenticio.VEGETALES));
		
		manzana = recetaService.crearRecetaNueva("manzana", null, 100, Temporada.PRIMAVERA, HorarioComida.MERIENDA);
		manzana.agregarIngrediente(new Ingrediente("Fruta", TipoGrupoAlimenticio.FRUTAS));
		
		banana = recetaService.crearRecetaNueva("banana", null, 180, Temporada.VERANO, HorarioComida.MERIENDA);
		banana.agregarIngrediente(new Ingrediente("Fruta", TipoGrupoAlimenticio.FRUTAS));
		
		//Usuario Administrador, es tambien el Por Default
		usuarioAdmin = usuarioGrupoService.crearUsuario(UsuarioGrupoService.ADMIN_GREENFOOD, "password", 24, 'M');
		
		fecha = Calendar.getInstance();
		fecha.set(2015, 11, 3);
	}

	@After
	public void tearDown() throws Exception {
		sessionFactory.close();
	}

	@Test
	public void agregarUsuarioAGrupo() throws UsuarioExcepcion, GrupoExcepcion {
		usuarioGrupoService.agregarUsuarioAGrupo(vegano, grupoVegetarianos);
		assertEquals(true, grupoVegetarianos.getUsuarios().contains(vegano));
	}	
	
	@Test
	public void agregarUsuarioRepetidoAGrupo() throws UsuarioExcepcion, GrupoExcepcion {
		thrown.expect(GrupoExcepcion.class);
		usuarioGrupoService.agregarUsuarioAGrupo(vegano, grupoVegetarianos);
		assertEquals(true, grupoVegetarianos.getUsuarios().contains(vegano));
		usuarioGrupoService.agregarUsuarioAGrupo(vegano, grupoVegetarianos);
	}	
	
	@Test
	public void compartirRecetaAGrupo() throws UsuarioExcepcion, GrupoExcepcion, RecetaExcepcion {
		usuarioGrupoService.agregarUsuarioAGrupo(vegano,grupoVegetarianos);
		usuarioGrupoService.agregarRecetaUsuario(vegano, ensalada);
		usuarioGrupoService.compartirReceta(vegano, ensalada, grupoVegetarianos);
		assertEquals(true,grupoVegetarianos.getRecetas().contains(ensalada));
	}
	
	@Test
	public void compartirRecetaAGrupoSinPertenecerlo() throws UsuarioExcepcion, GrupoExcepcion, RecetaExcepcion {
		//Esperamos que explote
		thrown.expect(GrupoExcepcion.class);
		usuarioGrupoService.agregarRecetaUsuario(vegano, ensalada);
		usuarioGrupoService.compartirReceta(vegano, ensalada, grupoVegetarianos);
	}
	
	
	@Test
	public void eliminarMiembro() throws UsuarioExcepcion, GrupoExcepcion {
		//Agrego un Miembro
		usuarioGrupoService.agregarUsuarioAGrupo(vegano, grupoVegetarianos);
		//Lo elimino
		usuarioGrupoService.eliminarMiembroDelGrupo(vegano, grupoVegetarianos);
		//Reviso que no contenga al miembro
		assertEquals(false,grupoVegetarianos.getUsuarios().contains(vegano));
	}
	
	@Test
	public void eliminarMiembroSinGrupo() throws UsuarioExcepcion, GrupoExcepcion {
		thrown.expect(GrupoExcepcion.class);
		usuarioGrupoService.eliminarMiembroDelGrupo(vegano, grupoVegetarianos);
	}
	
	@Test
	public void eliminarRecetaDeUnGrupo() throws UsuarioExcepcion, GrupoExcepcion, RecetaExcepcion {
		usuarioGrupoService.agregarRecetaUsuario(vegano, ensalada);
		usuarioGrupoService.agregarRecetaUsuario(vegano1, porotos);
		usuarioGrupoService.agregarUsuarioAGrupo(vegano, grupoVegetarianos);
		usuarioGrupoService.agregarUsuarioAGrupo(vegano1, grupoVegetarianos);
		usuarioGrupoService.compartirReceta(vegano, ensalada, grupoVegetarianos);
		usuarioGrupoService.compartirReceta(vegano1, porotos, grupoVegetarianos);
		assertEquals(true, grupoVegetarianos.getRecetas().contains(ensalada));
		usuarioGrupoService.eliminarMiembroDelGrupo(vegano, grupoVegetarianos);
		assertEquals(true, grupoVegetarianos.getRecetas().contains(porotos));
		assertEquals(false, grupoVegetarianos.getRecetas().contains(ensalada));
	}	
}
