package ar.edu.utn.ba.dds.testsVarios;

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
import org.springframework.test.context.web.WebAppConfiguration;

import ar.edu.utn.ba.dds.config.AppConfig;
import ar.edu.utn.ba.dds.entidades.Comida;
import ar.edu.utn.ba.dds.entidades.Grupo;
import ar.edu.utn.ba.dds.entidades.Ingrediente;
import ar.edu.utn.ba.dds.entidades.Receta;
import ar.edu.utn.ba.dds.entidades.Usuario;
import ar.edu.utn.ba.dds.excepciones.GrupoExcepcion;
import ar.edu.utn.ba.dds.excepciones.RecetaExcepcion;
import ar.edu.utn.ba.dds.implement.GrupoAlimenticio;
import ar.edu.utn.ba.dds.model.RecetaService;
import ar.edu.utn.ba.dds.model.ReportesService;
import ar.edu.utn.ba.dds.model.UsuarioGrupoService;
import ar.edu.utn.ba.dds.enums.TipoGrupoAlimenticio;
import ar.edu.utn.ba.dds.enums.HorarioComida;
import ar.edu.utn.ba.dds.enums.Temporada;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class}, loader = AnnotationConfigContextLoader.class)
@WebAppConfiguration
public class TestsReportes {
	
	@Autowired 
	protected UsuarioGrupoService usuarioGrupoService;
	
	@Autowired 
	protected RecetaService recetaService;
	
	private Grupo grupoVegetarianos;	
	private Usuario vegano;
	private Usuario carnivora;
	private Usuario omnivoro;
	private Receta pollo;
	private Receta polloPapas;
	private Receta asado;
	private Receta ensalada;
	private Receta porotos;
	private Receta manzana;
	private Receta banana;	
	private Calendar fecha;
    
	private static SessionFactory sessionFactory;
	
	@Rule
    public ExpectedException thrown= ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		
		Configuration config = new Configuration().addResource("hibernate.cfg.xml").configure();
		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(config.getProperties());
		sessionFactory = config.buildSessionFactory(ssrb.build());
		
		grupoVegetarianos = usuarioGrupoService.crearGrupo("Vegatariano");
		vegano = usuarioGrupoService.crearUsuario("Vegano", "password", 19, 'M');
		carnivora = usuarioGrupoService.crearUsuario("Carnivoro1", "password", 19, 'F');
		omnivoro = usuarioGrupoService.crearUsuario("Omin", "password", 19, 'M');
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
		fecha = Calendar.getInstance();
		fecha.set(2015, 11, 3);
		
	}

	@After
	public void tearDownClass() {
		sessionFactory.close();
	}
	
	@Test
	public void reporteRecetasNuevas() {
		usuarioGrupoService.agregarRecetaUsuario(vegano, ensalada);
		assertEquals(true, ReportesService.recetasNuevas(vegano).contains(ensalada));
	}
	
	@Test
	public void reporteComidasPorNombre() {
		usuarioGrupoService.planificarComida(carnivora, fecha, polloPapas, HorarioComida.ALMUERZO);
		Comida comidaPlanificada = usuarioGrupoService.consultarComidaPorFecha(carnivora, fecha);
		assertEquals(true, ReportesService.comidasPorNombre(carnivora, "Pollo").contains(comidaPlanificada));
	}
	
	@Test
	public void reporteComidasPorPeriodo(){
		Calendar fechaInicio = Calendar.getInstance();
		fechaInicio.set(2015, 10, 1);

		Calendar fechaFin = Calendar.getInstance();
		fechaFin.set(2015, 12, 4);

		usuarioGrupoService.planificarComida(carnivora, fecha, polloPapas, HorarioComida.ALMUERZO);
		Comida comidaPlanificada = usuarioGrupoService.consultarComidaPorFecha(carnivora, fecha);
		assertEquals(true, ReportesService.comidasPorPeriodo(carnivora, fechaInicio, fechaFin).contains(comidaPlanificada));
	}
	
	@Test
	public void reporteComidasPorIngrediente(){
		usuarioGrupoService.planificarComida(omnivoro, fecha, manzana, HorarioComida.DESAYUNO);
		Comida comidaPlanificada = usuarioGrupoService.consultarComidaPorFecha(omnivoro, fecha);
		assertEquals(true, ReportesService.comidasPorIngrediente(omnivoro, new Ingrediente("fruta",TipoGrupoAlimenticio.FRUTAS)).contains(comidaPlanificada));
	}
	
	@Test
	public void reporteComidasPorCalorias(){
		usuarioGrupoService.planificarComida(vegano, fecha, ensalada, HorarioComida.CENA);
		Comida comidaPlanificada = usuarioGrupoService.consultarComidaPorFecha(vegano, fecha);
		assertEquals(true, ReportesService.comidasPorCalorias(vegano, 80, 150).contains(comidaPlanificada));
	}
	
	@Test
	public void reporteComidasPorGrupoAlimenticio(){
		usuarioGrupoService.planificarComida(omnivoro, fecha, manzana, HorarioComida.DESAYUNO);
		Comida comidaPlanificada = usuarioGrupoService.consultarComidaPorFecha(omnivoro, fecha);
		assertEquals(true, ReportesService.comidasPorGrupoAlimenticio(omnivoro, new GrupoAlimenticio(TipoGrupoAlimenticio.FRUTAS)).contains(comidaPlanificada));
	}

	@Test
	public void reporteComidasCompartidas() throws GrupoExcepcion, RecetaExcepcion {
		usuarioGrupoService.agregarUsuarioAGrupo(vegano, grupoVegetarianos);
		usuarioGrupoService.agregarUsuarioAGrupo(omnivoro, grupoVegetarianos);
		usuarioGrupoService.agregarRecetaUsuario(vegano, manzana);
		usuarioGrupoService.compartirReceta(vegano, manzana, grupoVegetarianos);
		usuarioGrupoService.planificarComida(omnivoro, fecha, manzana, HorarioComida.DESAYUNO);
	    Comida comidaPlanificada = usuarioGrupoService.consultarComidaPorFecha(omnivoro, fecha);
	    assertEquals(true, ReportesService.comidasCompartidas(omnivoro).contains(comidaPlanificada));
	}
	
}
