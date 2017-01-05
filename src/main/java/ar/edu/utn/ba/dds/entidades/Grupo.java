package ar.edu.utn.ba.dds.entidades;

import java.util.Set;

import javax.persistence.*;

@Entity
public class Grupo {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id_grupo")
	private int id;

	//XXX IMPORTANTE: "grupos" es el nombre del atributo en la Clase Usuario. NO Cambiarlo ni aca ni en la clase Usuarios
	//XXX IMPORTANTE: Se usa EAGER porque nosotros queremos que llene las colecciones al momento de devolver el objeto
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "grupos")
	private Set<Usuario> usuarios;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "recetas_compartida", joinColumns = @JoinColumn(name = "id_grupo"), inverseJoinColumns = @JoinColumn(name = "id_receta"))
	private Set<Receta> recetasCompartidas;
	
	private String descripcion;
	
	public Grupo(){
		super();
	}
	
	public Set<Receta> getRecetas(){
		return this.recetasCompartidas;
	}
	
	public Set<Usuario> getUsuarios() {
		return this.usuarios;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public Set<Receta> getRecetasCompartidas() {
		return recetasCompartidas;
	}

	public void setRecetasCompartidas(Set<Receta> recetasCompartidas) {
		this.recetasCompartidas = recetasCompartidas;
	}

	public void setUsuarios(Set<Usuario> usuarios) {
		this.usuarios = usuarios;
	}
	
	/**
	 * <b>Solo usar</b> si estan Seguros que este objeto fue <b>Persistido Primero</b>
	 */
	public int getId() {
		return id;
	}
	
	
	
	/**
	 * Para el FrontEnd es este metodo, se usa en el Template "verGrupos.html"
	 */
	public boolean contieneUsuario(Usuario usuarioPorComparar){
		
		for(Usuario usuario: this.getUsuarios()){
			if(usuario.getNombre().equalsIgnoreCase( usuarioPorComparar.getNombre() )){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean equals(Object object) {
		if(object == null) return false;
		
		Grupo grupo = (Grupo) object;
		
		if( !this.getDescripcion().equalsIgnoreCase(grupo.getDescripcion()) )	return false;
		
		return true;
	}
	
	public int hashCode() {
	        int result;
	        if( getDescripcion()!=null ) result = getDescripcion().hashCode();
	        else result = super.hashCode();
	        return result;
	}
	
}
