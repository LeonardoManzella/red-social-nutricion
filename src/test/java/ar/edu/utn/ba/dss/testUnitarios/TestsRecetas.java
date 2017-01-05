package ar.edu.utn.ba.dss.testUnitarios;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Set;

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
import ar.edu.utn.ba.dds.entidades.RestriccionAlimentaria;
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
public class TestsRecetas {
	
	private static SessionFactory sessionFactory;
	
	@Autowired
	UsuarioGrupoService usuarioGrupoService;
	
	@Autowired
	protected RecetaService recetaService;
	
	private Grupo grupoCarnivoro;
	private Grupo grupoOmnivoro;
	
	private Usuario vegano;
	private Usuario carnivoro;
	private Usuario carnivora;
	private Usuario omnivoro;
	private Usuario omnivora;
	@SuppressWarnings("unused")		//Si bien JAVA detecta que no se usa, en realidad es Necesario que este este Usuario
	private Usuario usuarioAdmin;
	
	private Receta pollo;
	private Receta polloPapas;
	private Receta asado;
	private Receta ensalada;
	private Receta porotos;
	private Receta manzana;
	private Receta banana;	
	private Receta budinDePan;
	
	private Calendar fecha;
    
	@Rule
    public ExpectedException thrown= ExpectedException.none();
    
	@Before
	public void setUp() throws Exception {
		
		Configuration config = new Configuration().addResource("hibernate.cfg.xml").configure();
		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(config.getProperties());
		sessionFactory = config.buildSessionFactory(ssrb.build());
		
		grupoCarnivoro = usuarioGrupoService.crearGrupo("Carnivoro");	
		grupoOmnivoro = usuarioGrupoService.crearGrupo("Omnivoro");	
		
		
		vegano = usuarioGrupoService.crearUsuario("Vegano", "password", 19, 'M');
		carnivoro = usuarioGrupoService.crearUsuario("Carnivoro", "password", 19, 'M');
		carnivora = usuarioGrupoService.crearUsuario("Carnivoro1", "password", 19, 'F');
		omnivoro = usuarioGrupoService.crearUsuario("Omin", "password", 19, 'M');
		omnivora = usuarioGrupoService.crearUsuario("Omin1", "password", 19, 'F');
		
		
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
		
		budinDePan = recetaService.crearRecetaNueva("budin de pan", null, 180, Temporada.VERANO, HorarioComida.MERIENDA);
		budinDePan.agregarIngrediente(new Ingrediente("Pan", TipoGrupoAlimenticio.HARINAS));
		budinDePan.agregarIngrediente(new Ingrediente("Leche", TipoGrupoAlimenticio.LACTEOS));
		
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
	public void generarRecetaNueva() throws RecetaExcepcion{
		//Agrego una Receta
		usuarioGrupoService.agregarRecetaUsuario(carnivoro, asado);
		//Genero una Nueva Receta Partiendo de la Anterior
		recetaService.generarRecetaPartiendoDe(carnivoro, asado, "Super Asado");
		
		//Ahora Reviso si tiene alguna Receta que tenga de nombre "Super Asado"
		boolean estaReceta = false;
		
		Set<Receta> recetas = carnivoro.getRecetas();
		
		for(Receta receta: recetas) {
			if(receta.getNombre().equalsIgnoreCase("Super Asado")) {
				estaReceta = true;
				break;
			}
		}
		
		assertEquals(true, estaReceta);
	}
	
	@Test(expected=RecetaExcepcion.class)
	public void generarRecetaDuplicada() throws RecetaExcepcion{
		//Agrego una Receta
		usuarioGrupoService.agregarRecetaUsuario(carnivoro, asado);
		//Genero la misma Receta Partiendo de la Anterior, debe generar Excepcion
		recetaService.generarRecetaPartiendoDe(carnivoro, asado, "asado");
	}
	
	@Test
	public void usuarioCalificarYModificarReceta() throws UsuarioExcepcion, RecetaExcepcion, GrupoExcepcion{
		//Primero Hago que Califique una Receta Suya, luego una de un Grupo al que Pertenece
		
		//Agrego una Receta como Suya
		usuarioGrupoService.agregarRecetaUsuario(carnivora, pollo);
		//Califica Su Receta
		recetaService.calificarReceta(carnivora, pollo, 1);
		//Reviso que se haya calificado
		assertEquals(true, recetaService.contieneCalificacion(pollo, 1) );
		
		//Vuelve a Calificar(Modificar) la misma Receta
		recetaService.calificarReceta(carnivora, pollo, 2);
		//Reviso que se haya calificado, que se haya quitado el "1" y ahora tenga un "2"
		assertEquals(false, recetaService.contieneCalificacion(pollo, 1) );
		assertEquals(true, recetaService.contieneCalificacion(pollo,2) );
		
		//Agrego el Usuario a un grupo
		usuarioGrupoService.agregarUsuarioAGrupo(carnivora, grupoCarnivoro);
		//Hago que alguien ya haya compartido una receta
		usuarioGrupoService.compartirReceta(carnivora, asado, grupoCarnivoro);
		//Hago que el usuario Califique la receta del grupo
		recetaService.calificarReceta(carnivora, asado, 3);
		//Reviso que se haya calificado
		assertEquals(true, recetaService.contieneCalificacion(asado, 3));
	}
	
	@Test(expected=UsuarioExcepcion.class)
	public void usuarioNOCalificaReceta() throws UsuarioExcepcion, RecetaExcepcion{
		//Hago que Califique una receta que ni tiene ella ni tiene alguno de sus grupos
		recetaService.calificarReceta(omnivora, pollo, 5);
	}
	
	@Test
	public void recetalistaCorrectamenteCalificaciones() throws GrupoExcepcion, UsuarioExcepcion, RecetaExcepcion{
		//Hago que 2 personas califiquen una receta en un grupo
		
		//Agrego Usuarios a un grupo
		usuarioGrupoService.agregarUsuarioAGrupo(omnivoro, grupoOmnivoro);
		usuarioGrupoService.agregarUsuarioAGrupo(omnivora, grupoOmnivoro);
		//Hago que alguien ya haya compartido una receta
		usuarioGrupoService.compartirReceta(omnivoro, banana, grupoOmnivoro);
		//Hago que los usuarios califiquen la receta
		recetaService.calificarReceta(omnivoro, banana, 3);
		recetaService.calificarReceta(omnivora, banana, 1);
	
		//Verifico que esten las 2 calificaciones
		assertEquals(true, recetaService.listarCalificacionesDe(omnivoro,banana).contains(3));
		assertEquals(true, recetaService.listarCalificacionesDe(omnivoro, banana).contains(1));
	}
	
	@Test
	public void usuarioListeCorrectamenteCalificaciones() throws UsuarioExcepcion, RecetaExcepcion{
		//Agrego una Receta a un Usuario
		usuarioGrupoService.agregarRecetaUsuario(carnivora,pollo);
		//Califica Su Receta
		recetaService.calificarReceta(carnivora, pollo, 1);
		
		
		//Reviso que aparesca la calificacion en el listado
		assertEquals(true, recetaService.listarCalificacionesDe(carnivora, pollo).contains(1));
	}
	
	
	@Test(expected=UsuarioExcepcion.class)
	public void usuarioListeMalCalificaciones() throws UsuarioExcepcion{
		//Hago que Liste de Una receta que no tiene disponible
		recetaService.listarCalificacionesDe(vegano, asado);
	}
	

	@Test
	public void eliminarReceta(){
		//Agrego una Receta a un Usuario
		usuarioGrupoService.agregarRecetaUsuario(vegano, ensalada);
		//Elimino la receta
		recetaService.eliminarReceta(ensalada.getNombre(), vegano);
		
		//Reviso que no este la receta
		assertEquals(false, recetaService.usuarioContieneReceta(ensalada.getNombre(), vegano));
	}
	
	@Test
	public void puedeComer(){
		carnivora.setRestriccion(new RestriccionAlimentaria("Celiacos"));
		carnivora.getRestriccion().agregarRestriccion(TipoGrupoAlimenticio.HARINAS);
		carnivora.getRestriccion().agregarRestriccion(TipoGrupoAlimenticio.LACTEOS);
		assertEquals(true, usuarioGrupoService.usuarioPuedeComer(carnivora, banana));
		assertEquals(true, usuarioGrupoService.usuarioPuedeComer(carnivora, ensalada));
		assertEquals(false, usuarioGrupoService.usuarioPuedeComer(carnivora, budinDePan));
		
	}
}
