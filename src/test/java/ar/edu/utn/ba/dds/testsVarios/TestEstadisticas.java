package ar.edu.utn.ba.dds.testsVarios;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import ar.edu.utn.ba.dds.entidades.Receta;
import ar.edu.utn.ba.dds.enums.HorarioComida;
import ar.edu.utn.ba.dds.enums.Temporada;
import ar.edu.utn.ba.dds.excepciones.RecetaExcepcion;

public class TestEstadisticas extends BaseTest {
	
	public Integer buscarIntegerDentroMap(Map<Receta,Integer> estadistica, String nombreReceta){
		//Recorro todas las entradas del Map
		for(Entry<Receta, Integer> entry : estadistica.entrySet()){
			//Si es la Receta que busco, retorno el Integer Asociado
			if(entry.getKey().getNombre().equalsIgnoreCase(nombreReceta)){
				return entry.getValue();
			}
		}
		
		return null;
	}
	
	
	@Test
	public void recetasMasProgramadaPorEstacion() {
		
		Calendar fechaPlanificado = Calendar.getInstance();
		Calendar fechaInicio = Calendar.getInstance();
		Calendar fechaFin = Calendar.getInstance();
		
		ensaladaGriega.setTemporada(Temporada.VERANO);
		ensaladaRusa.setTemporada(Temporada.VERANO);
		tortilla.setTemporada(Temporada.VERANO);
		tostadas.setTemporada(Temporada.VERANO);
		sopa.setTemporada(Temporada.VERANO);
		milanesaNapolitana.setTemporada(Temporada.VERANO);
		cereales.setTemporada(Temporada.VERANO);
		
		fechaPlanificado.set(2015, 0, 2);
		usuarioGrupoService.planificarComida(resentida, fechaPlanificado, ensaladaRusa, HorarioComida.ALMUERZO);
		
		fechaPlanificado.add(Calendar.DATE, 3);
		usuarioGrupoService.planificarComida(resentida, fechaPlanificado, tostadas, HorarioComida.DESAYUNO);
		
		fechaPlanificado.add(Calendar.DATE, 6);
		usuarioGrupoService.planificarComida(resentida, fechaPlanificado, ensaladaRusa, HorarioComida.ALMUERZO);
		
		fechaPlanificado.set(2015, 0, 3);
		usuarioGrupoService.planificarComida(campeon, fechaPlanificado, sopa, HorarioComida.CENA);
		
		fechaPlanificado.add(Calendar.DATE, 3);
		usuarioGrupoService.planificarComida(campeon, fechaPlanificado, ensaladaRusa, HorarioComida.ALMUERZO);
		
		fechaPlanificado.add(Calendar.DATE, 6);
		usuarioGrupoService.planificarComida(campeon, fechaPlanificado, tostadas, HorarioComida.DESAYUNO);
		
		fechaPlanificado.set(2015, 0, 4);
		usuarioGrupoService.planificarComida(looser, fechaPlanificado, ensaladaGriega, HorarioComida.CENA);
		
		fechaPlanificado.add(Calendar.DATE, 3);
		usuarioGrupoService.planificarComida(looser, fechaPlanificado, milanesaNapolitana, HorarioComida.ALMUERZO);
		
		fechaPlanificado.add(Calendar.DATE, 6);
		usuarioGrupoService.planificarComida(looser, fechaPlanificado, tostadas, HorarioComida.DESAYUNO);
		
		fechaPlanificado.set(2015, 0, 5);
		usuarioGrupoService.planificarComida(crackTotal, fechaPlanificado, milanesaNapolitana, HorarioComida.CENA);
		
		fechaPlanificado.add(Calendar.DATE, 3);
		usuarioGrupoService.planificarComida(crackTotal, fechaPlanificado, tortilla, HorarioComida.ALMUERZO);
		
		fechaPlanificado.add(Calendar.DATE, 6);
		usuarioGrupoService.planificarComida(crackTotal, fechaPlanificado, tostadas, HorarioComida.DESAYUNO);
		
		fechaInicio.set(2015, 0, 1);
		fechaFin.set(2015, 0, 30);
		
		Map<Receta,Integer> estadistica = estadisticasService.recetasMasProgramadaPorEstacion(Temporada.VERANO, fechaInicio, fechaFin);
		
		
		assertEquals(4, (int) buscarIntegerDentroMap( estadistica,tostadas.getNombre() ));
		assertEquals(3, (int) buscarIntegerDentroMap( estadistica,ensaladaRusa.getNombre() ));
		assertEquals(1, (int) buscarIntegerDentroMap( estadistica,sopa.getNombre() ));
		assertEquals(1, (int) buscarIntegerDentroMap( estadistica,ensaladaGriega.getNombre() ));
		assertEquals(2, (int) buscarIntegerDentroMap( estadistica,milanesaNapolitana.getNombre() ));
		assertEquals(1, (int) buscarIntegerDentroMap( estadistica,tortilla.getNombre() ));
	
	}
	
	@Test
	public void recetasMasCopiadas() throws RecetaExcepcion {
		
		Calendar fechaInicio = Calendar.getInstance();
		Calendar fechaFin = Calendar.getInstance();
		
		fechaInicio.set(2015, 0, 1);
		fechaFin.set(2015, 0, 30);
		
		recetaService.generarRecetaPartiendoDe(crackTotal, ensaladaGriega, "Ensalada Super Griega");
		recetaService.generarRecetaPartiendoDe(crackTotal, ensaladaGriega, "Otra Ensalada Griega");
		
		Map<Receta,Integer> estadistica = estadisticasService.recetasMasCopiadas(fechaInicio, fechaFin);
		
		assertEquals(2, (int) buscarIntegerDentroMap( estadistica,ensaladaGriega.getNombre() ));
	}
	
}