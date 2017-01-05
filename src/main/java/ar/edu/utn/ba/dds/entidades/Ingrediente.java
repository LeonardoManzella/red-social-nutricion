package ar.edu.utn.ba.dds.entidades;

import ar.edu.utn.ba.dds.implement.GrupoAlimenticio;

import javax.persistence.*;

import ar.edu.utn.ba.dds.enums.*;


@Entity
public class Ingrediente {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id_ingrediente")
	private int id;		//Id para Mappear con la Base de Datos
	
	private String nombre;
	
	//@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)		Deberia ir esto, pero por alguna razon no hace falta.... Raro
	@ManyToOne(cascade = CascadeType.ALL)
	private GrupoAlimenticio grupoAlimenticio;
	
	protected Ingrediente(){
		super();
	}
	
	public Ingrediente(String nombre, TipoGrupoAlimenticio tipo){
		this.nombre = nombre;
		this.grupoAlimenticio = new GrupoAlimenticio(tipo);
	}

	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public GrupoAlimenticio getGrupoAlimenticio() {
		return grupoAlimenticio;
	}

	public void setGrupoAlimenticio(GrupoAlimenticio grupoAlimenticio) {
		this.grupoAlimenticio = grupoAlimenticio;
	}
	
	/**
	 * Redefino los metodos equal y hashCode para que el HashSet no tenga duplicados
	 */
	
	public boolean equals(Object object) {
		if(object == null) return false;
		
		Ingrediente ingrediente = (Ingrediente) object;
		
		if( this.getNombre().equals(ingrediente.getNombre()) &&
				this.getGrupoAlimenticio().equals(ingrediente.getGrupoAlimenticio()) ) {
			return true;
		}
		return false;
	}
	
	//Parcheado porque Hibernate no llega a llenarle las cosas
	public int hashCode() {
		if(this.getNombre()==null && this.getGrupoAlimenticio()==null){
			return super.hashCode();
			
		}else if(this.getNombre()==null && this.getGrupoAlimenticio()!=null){
			return this.getGrupoAlimenticio().hashCode();
			
		}else if(this.getNombre()!=null && this.getGrupoAlimenticio()==null){
			return this.getNombre().hashCode();
		}
		
		return (this.getNombre().hashCode() + this.getGrupoAlimenticio().hashCode());
	}

	public int getId() {
		return id;
	}

}
