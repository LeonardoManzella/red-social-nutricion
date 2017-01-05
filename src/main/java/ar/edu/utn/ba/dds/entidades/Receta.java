package ar.edu.utn.ba.dds.entidades;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import ar.edu.utn.ba.dds.enums.HorarioComida;
import ar.edu.utn.ba.dds.enums.Temporada;
import ar.edu.utn.ba.dds.implement.GrupoAlimenticio;


@Entity
public class Receta {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id_receta")
	private int id;		//Id para Mappear con la Base de Datos
	
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "receta_ingredientes", joinColumns = @JoinColumn(name = "id_receta"), inverseJoinColumns = @JoinColumn(name = "id_ingrediente"))
	private Set<Ingrediente> listaIngredientes;
	
	private String nombre;
	
	private String descripcion;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "receta_calificaciones", joinColumns = @JoinColumn(name = "id_receta"), inverseJoinColumns = @JoinColumn(name = "id_calificacion"))
	private Set<Calificacion> calificaciones;
	
	private Integer calorias;
	
	private Temporada temporada;
	
	private HorarioComida horario;
	
	//PUEDE ser NULL si no esta basado en ninguna otra receta. No hay que poder nada porque por defecto Hibernate te deja NULL
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Receta recetaBase;
	
	public Receta(){
		super();
	}

	public Set<Ingrediente> getListaIngredientes() {
		return listaIngredientes;
	}
	
	public void setlistaIngredientes(Set<Ingrediente> listaIngredientes) {
		this.listaIngredientes = listaIngredientes;
	}


	public void agregarIngrediente(Ingrediente ingrediente) {
		this.listaIngredientes.add(ingrediente);
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Set<Calificacion> getCalificaciones() {
		return calificaciones;
	}

	public void setCalificaciones(Set<Calificacion> calificaciones) {
		this.calificaciones = calificaciones;
	}

	public Integer getCalorias() {
		return calorias;
	}

	public void setCalorias(Integer calorias) {
		this.calorias = calorias;
	}

	public Temporada getTemporada() {
		return temporada;
	}

	public void setTemporada(Temporada temporada) {
		this.temporada = temporada;
	}

	public Receta getRecetaBase() {
		return recetaBase;
	}

	public void setRecetaBase(Receta recetaBase) {
		this.recetaBase = recetaBase;
	}

	public HorarioComida getHorario() {
		return horario;
	}

	public void setHorario(HorarioComida horario) {
		this.horario = horario;
	}
	
	public Float calcularPromedio() {
		
		float promedio = 0.0f;
		float sumaCalificaciones = 0.0f; 
		float cantidadCalificaciones = (float)this.getCalificaciones().size();
		
		for(Calificacion calificacion : this.getCalificaciones())
			sumaCalificaciones += calificacion.getCalificacion();
		
		promedio = sumaCalificaciones/cantidadCalificaciones;	
		return promedio;
	}

	public Set<GrupoAlimenticio> getGruposAlimenticio() {
		Set<GrupoAlimenticio> grupoAlimentos = new HashSet<GrupoAlimenticio>();
		this.listaIngredientes.forEach(ingrediente-> grupoAlimentos.add(ingrediente.getGrupoAlimenticio()));
		return grupoAlimentos;
	}

	public Integer obtenerIngredientesPorGrupo(GrupoAlimenticio grupo) {
		Long valor = this.getListaIngredientes().stream().filter(ingrediente-> ingrediente.getGrupoAlimenticio().getTipoAlimentacion().equals(grupo.getTipoAlimentacion())).count();
		return valor.intValue();
	}

	/**
	 * <b>Solo usar</b> si estan Seguros que este objeto fue <b>Persistido Primero</b>
	 */
	public int getId() {
		return id;
	}
	
	
	public boolean equals(Object object) {
		if(object == null) return false;
		
		Receta receta = (Receta) object;
		
		if( !this.getNombre().equalsIgnoreCase(receta.getNombre()) )	return false;
		
		return true;
	}
	
	public int hashCode() {
	        int result;
	        if( getNombre()!=null ) result = getNombre().hashCode();
	        else result = super.hashCode();
	        return result;
	}
}


