package ar.edu.utn.ba.dds.testsVarios;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

import ar.edu.utn.ba.dds.config.AppConfig;
import ar.edu.utn.ba.dds.entidades.Calificacion;
import ar.edu.utn.ba.dds.entidades.Grupo;
import ar.edu.utn.ba.dds.entidades.Ingrediente;
import ar.edu.utn.ba.dds.entidades.Piramide;
import ar.edu.utn.ba.dds.entidades.Receta;
import ar.edu.utn.ba.dds.entidades.RestriccionAlimentaria;
import ar.edu.utn.ba.dds.entidades.Usuario;
import ar.edu.utn.ba.dds.enums.HorarioComida;
import ar.edu.utn.ba.dds.enums.Temporada;
import ar.edu.utn.ba.dds.enums.TipoGrupoAlimenticio;
import ar.edu.utn.ba.dds.model.EstadisticasService;
import ar.edu.utn.ba.dds.model.GreenFoodService;
import ar.edu.utn.ba.dds.model.RecetaService;
import ar.edu.utn.ba.dds.model.UsuarioGrupoService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class}, loader = AnnotationConfigContextLoader.class)
@WebAppConfiguration
public abstract class BaseTest {
		
	@Autowired
	protected GreenFoodService modelService;
	
	@Autowired
	protected EstadisticasService estadisticasService;
	
	@Autowired 
	protected UsuarioGrupoService usuarioGrupoService;
	
	@Autowired
	protected RecetaService recetaService;
	
	protected static SessionFactory sessionFactory;
	
	//Usuarios
	protected Usuario crackTotal;
	protected Usuario campeon;
	protected Usuario looser;
	protected Usuario resentida;
	protected Usuario usuarioAdmin;
	
	//Restricciones
	protected RestriccionAlimentaria celiacos;
	
	//Recetas
	protected Receta ensaladaRusa;
	protected Receta milanesaNapolitana;
	protected Receta tortilla;
	protected Receta cereales;
	protected Receta tostadas;
	protected Receta sopa;
	protected Receta ensaladaGriega;
	
	
	//Grupos de usuarios
	protected Grupo grupoOmnivoro;
	
	
	//Piramides alimenticias
	protected Piramide piramideComun;	//La piramide comun que recomienda el enunciado
	
	
	//Otras Cosas
	protected Calendar fechaBusqueda;
	protected int cantidadDeRecetasRecomendar;
	
	//Ingredientes
	protected Ingrediente tomate = new Ingrediente("Tomate", TipoGrupoAlimenticio.VEGETALES);
	protected Ingrediente carne = new Ingrediente("Carne", TipoGrupoAlimenticio.CARNES_GRASAS);
	protected Ingrediente huevo = new Ingrediente("Huevo", TipoGrupoAlimenticio.PESCADOS_HUEVOS_CARNES_MAGRAS);
	protected Ingrediente oliva = new Ingrediente("Oliva", TipoGrupoAlimenticio.ACEITES);
	protected Ingrediente pan = new Ingrediente("Pan", TipoGrupoAlimenticio.HARINAS);
	protected Ingrediente cereal = new Ingrediente("Cereales", TipoGrupoAlimenticio.HARINAS);
	
	
	@Before
	public void setUp() throws Exception {
		
		Configuration config = new Configuration().addResource("hibernate.cfg.xml").configure();
		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(config.getProperties());
		sessionFactory = config.buildSessionFactory(ssrb.build());
				
		cantidadDeRecetasRecomendar = 5;
		
		celiacos = new RestriccionAlimentaria("Celiacos");
		celiacos.agregarRestriccion(TipoGrupoAlimenticio.HARINAS);
		celiacos.agregarRestriccion(TipoGrupoAlimenticio.LACTEOS);
		
		
		crackTotal = usuarioGrupoService.crearUsuario("German Velazquez", "password", 24, 'M');
		Set<Ingrediente> preferenciasCrackTotal = new HashSet<Ingrediente>();
		preferenciasCrackTotal.add(tomate);
		preferenciasCrackTotal.add(carne);
		preferenciasCrackTotal.add(huevo);
		preferenciasCrackTotal.add(oliva);
		preferenciasCrackTotal.add(pan);
		crackTotal.setPreferencias(preferenciasCrackTotal);
		
		campeon = usuarioGrupoService.crearUsuario("Mauro Bedoya", "password", 26, 'M');
		Set<Ingrediente> preferenciasCampeon = new HashSet<Ingrediente>();
		preferenciasCampeon.add(new Ingrediente("Tomate", TipoGrupoAlimenticio.VEGETALES));
		preferenciasCampeon.add(new Ingrediente("Carne", TipoGrupoAlimenticio.CARNES_GRASAS));
		preferenciasCampeon.add(new Ingrediente("Papa", TipoGrupoAlimenticio.VEGETALES));
		preferenciasCampeon.add(new Ingrediente("Arveja", TipoGrupoAlimenticio.LEGUMBRES));
		preferenciasCampeon.add(new Ingrediente("Pan", TipoGrupoAlimenticio.HARINAS));
		campeon.setPreferencias(preferenciasCampeon);
		
		looser = usuarioGrupoService.crearUsuario("Manco Gonzalez", "password", 30, 'M');
		Set<Ingrediente> preferenciasLooser = new HashSet<Ingrediente>();
		preferenciasLooser.add(new Ingrediente("Jamon", TipoGrupoAlimenticio.CARNES_GRASAS));
		preferenciasLooser.add(new Ingrediente("Queso", TipoGrupoAlimenticio.LACTEOS));
		preferenciasLooser.add(new Ingrediente("Mayonesa", TipoGrupoAlimenticio.LACTEOS));
		preferenciasLooser.add(new Ingrediente("Leche", TipoGrupoAlimenticio.LACTEOS));
		looser.setPreferencias(preferenciasLooser);
		looser.setRestriccion(celiacos);
		
		resentida = usuarioGrupoService.crearUsuario("Alejandra Perez", "password", 45, 'F');
		Set<Ingrediente> preferenciasResentida = new HashSet<Ingrediente>();
		preferenciasResentida.add(new Ingrediente("Tomate", TipoGrupoAlimenticio.VEGETALES));
		preferenciasResentida.add(new Ingrediente("Carne", TipoGrupoAlimenticio.CARNES_GRASAS));
		preferenciasResentida.add(new Ingrediente("Huevo", TipoGrupoAlimenticio.PESCADOS_HUEVOS_CARNES_MAGRAS));
		preferenciasResentida.add(new Ingrediente("Oliva", TipoGrupoAlimenticio.ACEITES));
		preferenciasResentida.add(new Ingrediente("Pan", TipoGrupoAlimenticio.HARINAS));
		resentida.setPreferencias(preferenciasResentida);
		
		ensaladaRusa = recetaService.crearRecetaNueva("Ensalada Rusa", "Una ensalada super rica", 850, Temporada.INVIERNO, HorarioComida.ALMUERZO);
		ensaladaRusa.agregarIngrediente(new Ingrediente("Arveja", TipoGrupoAlimenticio.VEGETALES));
		ensaladaRusa.agregarIngrediente(new Ingrediente("Pepino", TipoGrupoAlimenticio.VEGETALES));
		ensaladaRusa.agregarIngrediente(new Ingrediente("Carne", TipoGrupoAlimenticio.CARNES_GRASAS));
		ensaladaRusa.agregarIngrediente(new Ingrediente("Papa", TipoGrupoAlimenticio.VEGETALES));
		ensaladaRusa.agregarIngrediente(new Ingrediente("Mayonesa", TipoGrupoAlimenticio.VEGETALES));
		Calificacion calificacion1 = new Calificacion(crackTotal, 5);
		Calificacion calificacion2 = new Calificacion(campeon, 2);
		Calificacion calificacion3 = new Calificacion(looser, 1);
		Calificacion calificacion4 = new Calificacion(resentida, 4);
		ensaladaRusa.getCalificaciones().add(calificacion1);
		ensaladaRusa.getCalificaciones().add(calificacion2);
		ensaladaRusa.getCalificaciones().add(calificacion3);
		ensaladaRusa.getCalificaciones().add(calificacion4);
		
		milanesaNapolitana = recetaService.crearRecetaNueva("Milanesa Napolitana", "Milanesa con tomates y queso", 1000,Temporada.OTONIO, HorarioComida.ALMUERZO);
		milanesaNapolitana.agregarIngrediente(new Ingrediente("Carne", TipoGrupoAlimenticio.CARNES_GRASAS));
		milanesaNapolitana.agregarIngrediente(new Ingrediente("Pan", TipoGrupoAlimenticio.HARINAS));
		milanesaNapolitana.agregarIngrediente(new Ingrediente("Huevo", TipoGrupoAlimenticio.PESCADOS_HUEVOS_CARNES_MAGRAS));
		milanesaNapolitana.agregarIngrediente(new Ingrediente("Tomate", TipoGrupoAlimenticio.VEGETALES));
		milanesaNapolitana.agregarIngrediente(new Ingrediente("Queso", TipoGrupoAlimenticio.LACTEOS));
		Calificacion calificacion5 = new Calificacion(crackTotal, 5);
		Calificacion calificacion6 = new Calificacion(campeon, 4);
		Calificacion calificacion7 = new Calificacion(looser, 4);
		Calificacion calificacion8 = new Calificacion(resentida, 5);
		milanesaNapolitana.getCalificaciones().add(calificacion5);
		milanesaNapolitana.getCalificaciones().add(calificacion6);
		milanesaNapolitana.getCalificaciones().add(calificacion7);
		milanesaNapolitana.getCalificaciones().add(calificacion8);
		
		tortilla = recetaService.crearRecetaNueva("Tortilla", "Huevos revueltos con leche", 520, Temporada.OTONIO, HorarioComida.ALMUERZO);
		tortilla.agregarIngrediente(new Ingrediente("Huevo", TipoGrupoAlimenticio.PESCADOS_HUEVOS_CARNES_MAGRAS));
		tortilla.agregarIngrediente(new Ingrediente("Leche", TipoGrupoAlimenticio.LACTEOS));
		tortilla.agregarIngrediente(new Ingrediente("Tomate", TipoGrupoAlimenticio.VEGETALES));
		Calificacion calificacion9 = new Calificacion(crackTotal, 2);
		Calificacion calificacion10 = new Calificacion(campeon, 3);
		Calificacion calificacion11 = new Calificacion(looser, 4);
		Calificacion calificacion12 = new Calificacion(resentida, 5);
		tortilla.getCalificaciones().add(calificacion9);
		tortilla.getCalificaciones().add(calificacion10);
		tortilla.getCalificaciones().add(calificacion11);
		tortilla.getCalificaciones().add(calificacion12);
		
		cereales = recetaService.crearRecetaNueva("Cereales", "Cereales con leche y frutas", 250, Temporada.VERANO, HorarioComida.ALMUERZO);
		cereales.agregarIngrediente(cereal);
		cereales.agregarIngrediente(new Ingrediente("Leche", TipoGrupoAlimenticio.LACTEOS));
		cereales.agregarIngrediente(new Ingrediente("Pasas", TipoGrupoAlimenticio.FRUTOS_SECOS));
		cereales.agregarIngrediente(new Ingrediente("Frutilla", TipoGrupoAlimenticio.FRUTAS));
		cereales.agregarIngrediente(new Ingrediente("Manzana", TipoGrupoAlimenticio.FRUTAS));
		Calificacion calificacion13 = new Calificacion(crackTotal, 5);
		Calificacion calificacion14 = new Calificacion(campeon, 5);
		Calificacion calificacion15 = new Calificacion(looser, 5);
		Calificacion calificacion16 = new Calificacion(resentida, 5);
		cereales.getCalificaciones().add(calificacion13);
		cereales.getCalificaciones().add(calificacion14);
		cereales.getCalificaciones().add(calificacion15);
		cereales.getCalificaciones().add(calificacion16);
		
		tostadas = recetaService.crearRecetaNueva("Tostadas", "Tostadas con diferentes fiambres", 500,Temporada.PRIMAVERA, HorarioComida.ALMUERZO);
		tostadas.agregarIngrediente(new Ingrediente("Pan", TipoGrupoAlimenticio.HARINAS));
		tostadas.agregarIngrediente(new Ingrediente("Jamon", TipoGrupoAlimenticio.CARNES_GRASAS));
		tostadas.agregarIngrediente(new Ingrediente("Queso", TipoGrupoAlimenticio.LACTEOS));
		tostadas.agregarIngrediente(new Ingrediente("Miel", TipoGrupoAlimenticio.DULCES));
		tostadas.agregarIngrediente(new Ingrediente("Pate", TipoGrupoAlimenticio.CARNES_GRASAS));
		Calificacion calificacion17 = new Calificacion(crackTotal, 1);
		Calificacion calificacion18 = new Calificacion(campeon, 2);
		Calificacion calificacion19 = new Calificacion(looser, 2);
		Calificacion calificacion20 = new Calificacion(resentida, 3);
		tostadas.getCalificaciones().add(calificacion17);
		tostadas.getCalificaciones().add(calificacion18);
		tostadas.getCalificaciones().add(calificacion19);
		tostadas.getCalificaciones().add(calificacion20);
		
		sopa = recetaService.crearRecetaNueva("Sopa", "Sopa de verduras", 200, Temporada.VERANO, HorarioComida.ALMUERZO);
		sopa.agregarIngrediente(new Ingrediente("Agua", TipoGrupoAlimenticio.LIQUIDO));
		sopa.agregarIngrediente(new Ingrediente("Papa", TipoGrupoAlimenticio.VEGETALES));
		sopa.agregarIngrediente(new Ingrediente("Lentejas", TipoGrupoAlimenticio.LEGUMBRES));
		sopa.agregarIngrediente(new Ingrediente("Zanahoria", TipoGrupoAlimenticio.VEGETALES));
		sopa.agregarIngrediente(new Ingrediente("Aceite", TipoGrupoAlimenticio.ACEITES));
		Calificacion calificacion21 = new Calificacion(crackTotal, 3);
		Calificacion calificacion22 = new Calificacion(campeon, 3);
		Calificacion calificacion23 = new Calificacion(looser, 2);
		Calificacion calificacion24 = new Calificacion(resentida, 2);
		sopa.getCalificaciones().add(calificacion21);
		sopa.getCalificaciones().add(calificacion22);
		sopa.getCalificaciones().add(calificacion23);
		sopa.getCalificaciones().add(calificacion24);
		
		ensaladaGriega = recetaService.crearRecetaNueva("Ensalada Griega", "Ensalada liviana con aceitunas y queso feta", 200, Temporada.PRIMAVERA, HorarioComida.ALMUERZO);
		ensaladaGriega.agregarIngrediente(new Ingrediente("Queso", TipoGrupoAlimenticio.LACTEOS));
		ensaladaGriega.agregarIngrediente(new Ingrediente("Tomate", TipoGrupoAlimenticio.VEGETALES));
		ensaladaGriega.agregarIngrediente(new Ingrediente("Oliva", TipoGrupoAlimenticio.ACEITES));
		ensaladaGriega.agregarIngrediente(new Ingrediente("Morron", TipoGrupoAlimenticio.VEGETALES));
		ensaladaGriega.agregarIngrediente(new Ingrediente("Pepino", TipoGrupoAlimenticio.VEGETALES));
		Calificacion calificacion25 = new Calificacion(crackTotal, 4);
		Calificacion calificacion26 = new Calificacion(campeon, 4);
		Calificacion calificacion27 = new Calificacion(looser, 4);
		Calificacion calificacion28 = new Calificacion(resentida, 4);
		ensaladaGriega.getCalificaciones().add(calificacion25);
		ensaladaGriega.getCalificaciones().add(calificacion26);
		ensaladaGriega.getCalificaciones().add(calificacion27);
		ensaladaGriega.getCalificaciones().add(calificacion28);
		
		grupoOmnivoro = usuarioGrupoService.crearGrupo("Omnivoro");
		usuarioGrupoService.agregarUsuarioAGrupo(crackTotal, grupoOmnivoro);
		usuarioGrupoService.agregarRecetaUsuario(crackTotal, cereales);
		usuarioGrupoService.agregarRecetaUsuario(crackTotal, ensaladaGriega);
		usuarioGrupoService.compartirReceta(crackTotal, cereales, grupoOmnivoro);
		usuarioGrupoService.compartirReceta(crackTotal, ensaladaGriega, grupoOmnivoro);
		fechaBusqueda = Calendar.getInstance();
		fechaBusqueda.add(Calendar.DATE, -3);
		usuarioGrupoService.planificarComida(crackTotal, fechaBusqueda, cereales, HorarioComida.DESAYUNO);
		usuarioGrupoService.planificarComida(crackTotal, fechaBusqueda, ensaladaGriega, HorarioComida.ALMUERZO);
		
		//Usuario Administrador, es tambien el Por Default
		usuarioAdmin = usuarioGrupoService.crearUsuario(UsuarioGrupoService.ADMIN_GREENFOOD, "password", 24, 'M');
		usuarioGrupoService.agregarRecetaUsuario(usuarioAdmin,ensaladaRusa);
		usuarioGrupoService.agregarRecetaUsuario(usuarioAdmin,milanesaNapolitana);
		usuarioGrupoService.agregarRecetaUsuario(usuarioAdmin,tortilla);
		usuarioGrupoService.agregarRecetaUsuario(usuarioAdmin,cereales);
		usuarioGrupoService.agregarRecetaUsuario(usuarioAdmin,tostadas);
		usuarioGrupoService.agregarRecetaUsuario(usuarioAdmin,sopa);
		usuarioGrupoService.agregarRecetaUsuario(usuarioAdmin,ensaladaGriega);
		
		//Es tipo la piramide que aparece en el Enunciado, la arme pensando en esas proporciones
		piramideComun = new Piramide( "Piramide Comun", new Integer(1),new Integer(2),new Integer(3),new Integer(4),new Integer(2),new Integer(4),new Integer(4),new Integer(5),new Integer(2),new Integer(6),new Integer(6) );
	}
	
	@After
	public void tearDownClass() {
		sessionFactory.close();
	}
}
