package ar.edu.utn.ba.dds.entidades;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.*;

import ar.edu.utn.ba.dds.enums.TipoGrupoAlimenticio;
import ar.edu.utn.ba.dds.implement.GrupoAlimenticio;


@Entity
public class Piramide {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id_piramide")
	private int id;		//Id para Mappear con la Base de Datos
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "piramide_proporcion", joinColumns = @JoinColumn(name = "id_piramide"), inverseJoinColumns = @JoinColumn(name = "id_proporcion"))
	private Set<Proporcion> proporciones;
	
	private String nombrePiramide;
	
	
	public Piramide(){
		super();
	}
	
	/**
	 * SIEMPRE deben cargarse los 11 grupos apenas se crea la piramide, para mi deben ir los 11 argumentos en el constructor para evitar problemas.
	 *  Si ya se, es un poco grande 11 argumentos, pero es que siempre deben estar cargados sino no se puede saber como balancear
	 * @param porcionesDulces Primer Parametro
	 * @param porcionesCarnesGrasas Segundo Parametro
	 * @param porcionesFrutosSecos Tercer Parametro
	 * @param porcionesPescadosHuevosCarnesMagras Cuarto Parametro
	 * @param porcionesLacteos Quinto Parametro
	 * @param porcionesLegumbres Sexto Parametro
	 * @param porcionesFrutas Septimo Parametro
	 * @param porcionesVegetales Octavo Parametro
	 * @param porcionesAceites Noveno Parametro
	 * @param porcionesHarinas Decimo Parametro
	 * @param porcionesLiquido Decimo Primer Parametro
	*/
	public Piramide( String nombre, Integer porcionesDulces, Integer porcionesCarnesGrasas, Integer porcionesFrutosSecos, Integer porcionesPescadosHuevosCarnesMagras, Integer porcionesLacteos, Integer porcionesLegumbres, Integer porcionesFrutas, Integer porcionesVegetales, Integer porcionesAceites, Integer porcionesHarinas, Integer porcionesLiquido){
		this.setNombrePiramide(nombre);
		this.proporciones = new HashSet<Proporcion>();
		this.agregarProporcion(new GrupoAlimenticio(TipoGrupoAlimenticio.DULCES), porcionesDulces);
		this.agregarProporcion(new GrupoAlimenticio(TipoGrupoAlimenticio.CARNES_GRASAS), porcionesCarnesGrasas);
		this.agregarProporcion(new GrupoAlimenticio(TipoGrupoAlimenticio.FRUTOS_SECOS), porcionesFrutosSecos);
		this.agregarProporcion(new GrupoAlimenticio(TipoGrupoAlimenticio.PESCADOS_HUEVOS_CARNES_MAGRAS), porcionesPescadosHuevosCarnesMagras);
		this.agregarProporcion(new GrupoAlimenticio(TipoGrupoAlimenticio.LACTEOS), porcionesLacteos);
		this.agregarProporcion(new GrupoAlimenticio(TipoGrupoAlimenticio.LEGUMBRES), porcionesLegumbres);
		this.agregarProporcion(new GrupoAlimenticio(TipoGrupoAlimenticio.FRUTAS), porcionesFrutas);
		this.agregarProporcion(new GrupoAlimenticio(TipoGrupoAlimenticio.VEGETALES), porcionesVegetales);
		this.agregarProporcion(new GrupoAlimenticio(TipoGrupoAlimenticio.ACEITES), porcionesAceites);
		this.agregarProporcion(new GrupoAlimenticio(TipoGrupoAlimenticio.HARINAS), porcionesHarinas);
		this.agregarProporcion(new GrupoAlimenticio(TipoGrupoAlimenticio.LIQUIDO), porcionesLiquido);
		
	}
	
	private void agregarProporcion(GrupoAlimenticio grupo,Integer proporcion){
		
		this.proporciones.add(new Proporcion(grupo,proporcion));
	}

	/**
	 * @param historico las Comidas Planificadas Hasta el Momemento por el Usuario
	 * @param recetasDisponibles Las Recetas sobre las Cuales se puede Recomendar
	 * @return Devuelve la Receta Recomendada en Base a la Piramide
	 */
	public Receta recomendarReceta(Set<Comida> historico, Set<Receta> recetasDisponibles) {
		//Usamos una Estructura Map Interna para guardar Por Grupo Cuanto le Falta por Comer, para Balancear la Piramide
		HashMap<GrupoAlimenticio, Integer> piramideEquilibrada = new HashMap<GrupoAlimenticio, Integer>();
		
		
		//Se cargan los Datos en el Map Interno
		//FIXME Esta bien asi usar Iterator?? No deberiamos usar un FOR??
		for (Iterator iterator = this.proporciones.iterator(); iterator.hasNext();) {
			 Proporcion proporcion = (Proporcion)iterator.next();
			 GrupoAlimenticio grupo = proporcion.getGrupoAlimenticio();
			 Integer cantidadConsumida = historico.stream().mapToInt(comida-> comida.getRecetaAsociada().obtenerIngredientesPorGrupo(grupo)).sum();
			 piramideEquilibrada.put(grupo, (proporcion.getValor()-cantidadConsumida));
		}
		
		//Ahora usamos las Recetas Disponibles para Ver cual Recomendar
		Receta recetaPorDevolver = null;
		
		for(Receta receta: recetasDisponibles){
			if( recetaPorDevolver==null || (Piramide.calcularPuntuacion(receta, piramideEquilibrada) > Piramide.calcularPuntuacion(recetaPorDevolver, piramideEquilibrada)) ){
				recetaPorDevolver = receta;
			}
		}
		
		
		return recetaPorDevolver;
	}
	
	
	//Metodo Interno
	private static Integer calcularPuntuacion(Receta receta, HashMap<GrupoAlimenticio, Integer> piramideEquilibrada){
		
		Integer puntuacion = new Integer(0);
		
		//Voy sumando la Puntuacion de Acuerdo a la Piramide Equilibrada
		for(Ingrediente ingrediente: receta.getListaIngredientes()){
			puntuacion += piramideEquilibrada.get(ingrediente.getGrupoAlimenticio());
		}
		
		return puntuacion;	
	}

	public Set<Proporcion> getProporciones() {
		return proporciones;
	}

	public void setProporciones(Set<Proporcion> proporciones) {
		this.proporciones = proporciones;
	}

	public String getNombrePiramide() {
		return nombrePiramide;
	}

	public void setNombrePiramide(String nombrePiramide) {
		this.nombrePiramide = nombrePiramide;
	}

}
