package ar.edu.utn.ba.dds.config;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

import ar.edu.utn.ba.dds.entidades.Grupo;
import ar.edu.utn.ba.dds.entidades.Ingrediente;
import ar.edu.utn.ba.dds.entidades.Piramide;
import ar.edu.utn.ba.dds.entidades.Receta;
import ar.edu.utn.ba.dds.entidades.RestriccionAlimentaria;
import ar.edu.utn.ba.dds.entidades.Usuario;
import ar.edu.utn.ba.dds.enums.HorarioComida;
import ar.edu.utn.ba.dds.enums.Temporada;
import ar.edu.utn.ba.dds.enums.TipoGrupoAlimenticio;
import ar.edu.utn.ba.dds.model.GreenFoodService;
import ar.edu.utn.ba.dds.model.RecetaService;
import ar.edu.utn.ba.dds.model.UsuarioGrupoService;

@Component
public class MainClass implements ApplicationListener<ContextStartedEvent> {
	
	@Autowired 
	private UsuarioGrupoService usuarioGrupoService;
	
	@Autowired
	protected RecetaService recetaService;
	
	@Autowired
	protected GreenFoodService modelService;
		
	private static boolean firstTime = true;

	@Override
	public void onApplicationEvent(ContextStartedEvent arg0) {
		cargarDatosPreExistentes();
		//Hago que no haga nada, porque anda Mal. Si bien carga Correctamente los Datos, luego los borra y no se porque..
	}

	
	public void cargarDatosPreExistentes(){
		if(firstTime) {
			System.out.println("");
			System.out.println("");
			System.out.println("--------------------- CARGANDO DATOS ------------------------");
			
			//Creamos y Persistimos el Juego de Datos Pre-Cargado (Pedido en el Enunciado)
			try {
				/********************* INGREDIENTES ************************/
				Ingrediente arveja = modelService.crearIngrediente("Arveja", TipoGrupoAlimenticio.VEGETALES);
				Ingrediente pepino = modelService.crearIngrediente("Pepino", TipoGrupoAlimenticio.VEGETALES);
				Ingrediente carne = modelService.crearIngrediente("Carne", TipoGrupoAlimenticio.CARNES_GRASAS);
				Ingrediente papa = modelService.crearIngrediente("Papa", TipoGrupoAlimenticio.VEGETALES);
				Ingrediente mayonesa = modelService.crearIngrediente("Mayonesa", TipoGrupoAlimenticio.VEGETALES);
				Ingrediente pan = modelService.crearIngrediente("Pan", TipoGrupoAlimenticio.HARINAS);
				Ingrediente huevo = modelService.crearIngrediente("Huevo", TipoGrupoAlimenticio.PESCADOS_HUEVOS_CARNES_MAGRAS);
				Ingrediente tomate = modelService.crearIngrediente("Tomate", TipoGrupoAlimenticio.VEGETALES);
				Ingrediente queso = modelService.crearIngrediente("Queso", TipoGrupoAlimenticio.LACTEOS);
				Ingrediente leche = modelService.crearIngrediente("Leche", TipoGrupoAlimenticio.LACTEOS);
				Ingrediente cereal = modelService.crearIngrediente("Cereales", TipoGrupoAlimenticio.HARINAS);
				Ingrediente mermelada = modelService.crearIngrediente("Mermelada", TipoGrupoAlimenticio.DULCES);
				
				
				
				/********************* RECETAS ************************/
				Receta ensaladaRusa = recetaService.crearRecetaNueva("Ensalada Rusa", "Una ensalada super rica", 850, Temporada.INVIERNO, HorarioComida.ALMUERZO);
				recetaService.agregarIngredienteReceta(ensaladaRusa, arveja);
				recetaService.agregarIngredienteReceta(ensaladaRusa, pepino);
				recetaService.agregarIngredienteReceta(ensaladaRusa, carne);
				recetaService.agregarIngredienteReceta(ensaladaRusa, papa);
				recetaService.agregarIngredienteReceta(ensaladaRusa, mayonesa);
				
				Receta milanesaNapolitana = recetaService.crearRecetaNueva("Milanesa Napolitana", "Milanesa con tomates y queso", 1000,Temporada.OTONIO, HorarioComida.CENA);
				recetaService.agregarIngredienteReceta(milanesaNapolitana, carne);
				recetaService.agregarIngredienteReceta(milanesaNapolitana, pan);
				recetaService.agregarIngredienteReceta(milanesaNapolitana, huevo);
				recetaService.agregarIngredienteReceta(milanesaNapolitana, tomate);
				recetaService.agregarIngredienteReceta(milanesaNapolitana, queso);
								
				Receta tortilla = recetaService.crearRecetaNueva("Tortilla", "Huevos revueltos con leche", 520, Temporada.OTONIO, HorarioComida.ALMUERZO);
				recetaService.agregarIngredienteReceta(tortilla, huevo);
				recetaService.agregarIngredienteReceta(tortilla, leche);
				recetaService.agregarIngredienteReceta(tortilla, tomate);
				
				Receta cereales = recetaService.crearRecetaNueva("Cereales", "Cereales con leche y frutas", 250, Temporada.VERANO, HorarioComida.DESAYUNO);
				recetaService.agregarIngredienteReceta(cereales, cereal);
				recetaService.agregarIngredienteReceta(cereales, leche);
				
				Receta tostadas = recetaService.crearRecetaNueva("Tostadas", "Tostadas con diferentes fiambres", 500,Temporada.PRIMAVERA, HorarioComida.MERIENDA);
				recetaService.agregarIngredienteReceta(tostadas, pan);
				recetaService.agregarIngredienteReceta(tostadas, mermelada);
				
				
				//Creamos Usuario Administrador
				Usuario usuarioAdmin = usuarioGrupoService.crearUsuario(UsuarioGrupoService.ADMIN_GREENFOOD, "password", 24, 'M');
				usuarioGrupoService.agregarRecetaUsuario(usuarioAdmin,ensaladaRusa);
				usuarioGrupoService.agregarRecetaUsuario(usuarioAdmin,milanesaNapolitana);
				usuarioGrupoService.agregarRecetaUsuario(usuarioAdmin,tortilla);
				usuarioGrupoService.agregarRecetaUsuario(usuarioAdmin,cereales);
				usuarioGrupoService.agregarRecetaUsuario(usuarioAdmin,tostadas);
			
				
				//Creamos Condiciones Pre-Existentes
				RestriccionAlimentaria celiaco = new RestriccionAlimentaria("Celiaco");
				celiaco.agregarRestriccion(TipoGrupoAlimenticio.HARINAS);
				celiaco.agregarRestriccion(TipoGrupoAlimenticio.LACTEOS);
				RestriccionAlimentaria diabetes = new RestriccionAlimentaria("Diabetes");
				celiaco.agregarRestriccion(TipoGrupoAlimenticio.DULCES);
				celiaco.agregarRestriccion(TipoGrupoAlimenticio.HARINAS);
				
				
				//Creamos usuarios comunes (pedidos por el enunciado, como parte del juego de datos)
				Usuario primerUsuarioComun = usuarioGrupoService.crearUsuario("1er Usuario", "password", 24, 'M');
				Usuario segundoUsuarioComun = usuarioGrupoService.crearUsuario("2do Usuario", "password", 20, 'F');
				Usuario usuarioCeliaco = usuarioGrupoService.crearUsuario("Usuario Celiaco", "password", 29, 'M');
				usuarioGrupoService.agregarRestriccionUsuario(usuarioCeliaco, celiaco);
				Usuario usuarioDiabetico = usuarioGrupoService.crearUsuario("Usuario Diabetico", "password", 34, 'F');
				usuarioGrupoService.agregarRestriccionUsuario(usuarioDiabetico, diabetes);
				
				
				//Creamos 2 Piramides
				@SuppressWarnings("unused")		//Se lo que hago
				Piramide piramideComun = modelService.crearPiramide( "Piramide Comun", new Integer(1),new Integer(2),new Integer(3),new Integer(4),new Integer(2),new Integer(4),new Integer(4),new Integer(5),new Integer(2),new Integer(6),new Integer(6) );
				@SuppressWarnings("unused")		//Se lo que hago
				Piramide piramideArgentina = modelService.crearPiramide( "Piramide Argentina", new Integer(1),new Integer(7),new Integer(1),new Integer(4),new Integer(2),new Integer(2),new Integer(2),new Integer(2),new Integer(2),new Integer(6),new Integer(6) );
				
				//Hacemos que los Usuarios Planifiquen comidas
				Calendar fecha = Calendar.getInstance();
				fecha.add(Calendar.DATE, -2);
				usuarioGrupoService.planificarComida(primerUsuarioComun, fecha, tostadas, HorarioComida.DESAYUNO);
				usuarioGrupoService.planificarComida(primerUsuarioComun, fecha, milanesaNapolitana, HorarioComida.ALMUERZO);
				usuarioGrupoService.planificarComida(primerUsuarioComun, fecha, milanesaNapolitana, HorarioComida.CENA);
				usuarioGrupoService.planificarComida(segundoUsuarioComun, fecha, cereales, HorarioComida.DESAYUNO);
				usuarioGrupoService.planificarComida(segundoUsuarioComun, fecha, milanesaNapolitana, HorarioComida.ALMUERZO);
				usuarioGrupoService.planificarComida(segundoUsuarioComun, fecha, ensaladaRusa, HorarioComida.CENA);
				
				
				
				fecha.add(Calendar.DATE, -3);
				usuarioGrupoService.planificarComida(primerUsuarioComun, fecha, cereales, HorarioComida.DESAYUNO);
				usuarioGrupoService.planificarComida(primerUsuarioComun, fecha, ensaladaRusa, HorarioComida.ALMUERZO);
				usuarioGrupoService.planificarComida(primerUsuarioComun, fecha, tortilla, HorarioComida.CENA);
				usuarioGrupoService.planificarComida(segundoUsuarioComun, fecha, cereales, HorarioComida.DESAYUNO);
				usuarioGrupoService.planificarComida(segundoUsuarioComun, fecha, tortilla, HorarioComida.ALMUERZO);
				usuarioGrupoService.planificarComida(segundoUsuarioComun, fecha, ensaladaRusa, HorarioComida.CENA);
				
				
				//Creamos Grupos de Usuarios e incluimos a los Usuarios
				Grupo grupoVeganos = usuarioGrupoService.crearGrupo("Veganos Justicieros");
				Grupo grupoCarnivoros = usuarioGrupoService.crearGrupo("No vives de Ensalada");
				Grupo grupoOmnivoros = usuarioGrupoService.crearGrupo("Lo que De!");
				Grupo grupoVegetarianos = usuarioGrupoService.crearGrupo("Grupo Vegetarianos");
				
				usuarioGrupoService.agregarUsuarioAGrupo(primerUsuarioComun, grupoOmnivoros);
				usuarioGrupoService.agregarUsuarioAGrupo(segundoUsuarioComun, grupoVegetarianos);
				usuarioGrupoService.agregarUsuarioAGrupo(usuarioCeliaco, grupoCarnivoros);
				usuarioGrupoService.agregarUsuarioAGrupo(segundoUsuarioComun, grupoVeganos);
				usuarioGrupoService.agregarUsuarioAGrupo(segundoUsuarioComun, grupoOmnivoros);
				
				
				//Hago que los Usuarios Creen Nuevas Recetas
				Receta milaPower = recetaService.generarRecetaPartiendoDe(primerUsuarioComun, milanesaNapolitana, "Mila Power");
				Receta superTortilla = recetaService.generarRecetaPartiendoDe(segundoUsuarioComun, tortilla, "Super Tortilla");
				
				
				//Hacemos que los Usuarios compartan recetas y las califiquen
				usuarioGrupoService.compartirReceta(primerUsuarioComun, milaPower, grupoOmnivoros);
				usuarioGrupoService.compartirReceta(segundoUsuarioComun, superTortilla, grupoOmnivoros);
				
				recetaService.calificarReceta(primerUsuarioComun, milaPower, 5);
				recetaService.calificarReceta(primerUsuarioComun, superTortilla, 1);
				recetaService.calificarReceta(segundoUsuarioComun, superTortilla, 5);
				recetaService.calificarReceta(segundoUsuarioComun, milaPower, 1);
				
				
			//Ante cualquier excepcion imprimimos que paso
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			System.out.println("--------------------- DATOS CARGADOS ------------------------");
			System.out.println("");
			System.out.println("");
			firstTime = false;
		}	
	}

}
