package ar.edu.utn.ba.dds.testsVarios;

import java.util.Calendar;
import java.util.Deque;

import org.junit.Test;

import ar.edu.utn.ba.dds.entidades.Receta;
import ar.edu.utn.ba.dds.enums.HorarioComida;
import ar.edu.utn.ba.dds.excepciones.GrupoExcepcion;
import ar.edu.utn.ba.dds.excepciones.RecetaExcepcion;
import ar.edu.utn.ba.dds.model.ReportesService;
import static org.junit.Assert.*;


public class TestsRecomendaciones extends BaseTest {
	
	@Test
	public void recomendacionesPorHorarioMejorPuntaje() {
		Deque<Receta> recetasRecomendadas = recetaService.recomendarPorDiaHorario(resentida, 
				cantidadDeRecetasRecomendar, HorarioComida.ALMUERZO, recetaService.crearCriterio("MejorPuntaje"));
		Receta primerPuesto = recetasRecomendadas.poll();
		Receta segundoPuesto = recetasRecomendadas.poll();
		Receta tercerPuesto = recetasRecomendadas.poll();
		Receta cuartoPuesto = recetasRecomendadas.poll();
		Receta quintoPuesto = recetasRecomendadas.poll();
		
		assertEquals(true, milanesaNapolitana.getNombre().equalsIgnoreCase(primerPuesto.getNombre()));
		assertEquals(true, ensaladaGriega.getNombre().equalsIgnoreCase(segundoPuesto.getNombre()));
		assertEquals(true, tortilla.getNombre().equalsIgnoreCase(tercerPuesto.getNombre()));
		assertEquals(true, cereales.getNombre().equalsIgnoreCase(cuartoPuesto.getNombre()));
		assertEquals(true, ensaladaRusa.getNombre().equalsIgnoreCase(quintoPuesto.getNombre()));
	}
	
	@Test
	public void filtrarRecetasPorFecha() throws GrupoExcepcion, RecetaExcepcion {
		fechaBusqueda = Calendar.getInstance();
		fechaBusqueda.add(Calendar.DATE, -4);
		assertEquals(2, ReportesService.comidasPorPeriodo(crackTotal, fechaBusqueda, Calendar.getInstance()).size());
	}
	
	
	@Test
	public void obtenerRecomendacionPorPiramide() throws GrupoExcepcion, RecetaExcepcion{
		
		//Uso a usuario "crackTotal" que viene comiendo cereales y ensalada griega,que esta alto en vegetales, lacteos-carnesMagras y frutas.
		//Esas recetas no contemplan ninguna bebida, asi que el tipo como mucho viene tomando aceite y jugo de fruta; y todavia no tomo nada de agua, asi que Le debe recomendar algo con Mucha Agua, una Sopa

		Receta recetaEncontrada = recetaService.recomendarPorPiramide(crackTotal, HorarioComida.ALMUERZO, 4, piramideComun);
		assertEquals(true, sopa.getNombre().equalsIgnoreCase(recetaEncontrada.getNombre()));
	}
}
