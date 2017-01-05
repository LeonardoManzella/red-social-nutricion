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
import ar.edu.utn.ba.dds.entidades.Ingrediente;
import ar.edu.utn.ba.dds.entidades.Receta;
import ar.edu.utn.ba.dds.entidades.Usuario;
import ar.edu.utn.ba.dds.excepciones.UsuarioExcepcion;
import ar.edu.utn.ba.dds.enums.TipoGrupoAlimenticio;
import ar.edu.utn.ba.dds.enums.HorarioComida;
import ar.edu.utn.ba.dds.enums.Temporada;
import ar.edu.utn.ba.dds.model.RecetaService;
import ar.edu.utn.ba.dds.model.UsuarioGrupoService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class}, loader = AnnotationConfigContextLoader.class)
public class TestsUsuarios {
	
	private static SessionFactory sessionFactory;
	
	@Autowired 
	UsuarioGrupoService usuarioGrupoService;
	
	@Autowired
	protected RecetaService recetaService;
	
	private Usuario usuarioPrueba;
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
		
		usuarioPrueba = usuarioGrupoService.crearUsuario("usuarioPrueba", "password",18, 'M');
		
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

	@Test(expected=UsuarioExcepcion.class)
	public void crearUsuarioExistente() throws UsuarioExcepcion {
		@SuppressWarnings("unused")
		Usuario vegano = usuarioGrupoService.crearUsuario("Vegano", "password", 19, 'M');
		usuarioGrupoService.crearUsuario("Vegano", "password", 19, 'M');
	}
	
	@Test(expected=UsuarioExcepcion.class)
	public void validarNombreObligatorio() throws UsuarioExcepcion {
		usuarioGrupoService.crearUsuario(null, "password", 19, 'M');
	}	
	
	@Test(expected=UsuarioExcepcion.class)
	public void validarNombreObligatorioVacio() throws UsuarioExcepcion {
		usuarioGrupoService.crearUsuario("", "password", 19, 'M');
	}
	@Test(expected=UsuarioExcepcion.class)
	public void validarEdad() throws UsuarioExcepcion {
		usuarioGrupoService.crearUsuario("Prueba", "password", 17, 'M');
	}
	
	@Test
	public void agregarPreferencia() throws UsuarioExcepcion {
		usuarioGrupoService.agregarPreferenciaUsuario(usuarioPrueba, new Ingrediente("Atun", TipoGrupoAlimenticio.PESCADOS_HUEVOS_CARNES_MAGRAS));
		usuarioGrupoService.agregarPreferenciaUsuario(usuarioPrueba, new Ingrediente("Atun", TipoGrupoAlimenticio.PESCADOS_HUEVOS_CARNES_MAGRAS));
		assertEquals(1, usuarioPrueba.getPreferencias().size());
	}	
}
