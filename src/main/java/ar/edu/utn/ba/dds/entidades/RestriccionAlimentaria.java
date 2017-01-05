package ar.edu.utn.ba.dds.entidades;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import ar.edu.utn.ba.dds.enums.TipoGrupoAlimenticio;

@Entity
public class RestriccionAlimentaria {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id_restriccion")
	private int id;		//Id para Mappear con la Base de Datos	
	
	private String nombre;
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "restriccion_alimentaria", joinColumns = @JoinColumn(name = "id_usuario"), inverseJoinColumns = @JoinColumn(name = "id_restriccion"))
	private Set<RestriccionTipoAlimento> tipoAlimentoRestringidos;

	public RestriccionAlimentaria(){
		super();
	}
	
	public RestriccionAlimentaria(String nombre) {
		this.nombre = nombre;
		this.tipoAlimentoRestringidos = new HashSet<RestriccionTipoAlimento>();
	}

	public Set<RestriccionTipoAlimento> getTipoAlimentoRestringidos() {
		return tipoAlimentoRestringidos;
	}

	public void setTipoAlimentoRestringidos(Set<RestriccionTipoAlimento> tipoAlimentoRestringidos) {
		this.tipoAlimentoRestringidos = tipoAlimentoRestringidos;
	}

	public void agregarRestriccion(TipoGrupoAlimenticio harinas) {
		RestriccionTipoAlimento restriccion = new RestriccionTipoAlimento(harinas);
		this.tipoAlimentoRestringidos.add(restriccion);
		
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	
	/**
	 * Para el FrontEnd, se usa el template "perfilUsuario"
	 */
	public List<String> obtenerNombresRestricciones(){
		List<String> listaPorDevolver = new ArrayList<String>();
		
		for(RestriccionTipoAlimento restriccion : getTipoAlimentoRestringidos()){
			listaPorDevolver.add( TipoGrupoAlimenticio.devolverString(restriccion.getGrupoAlimenticio()) );
		}
		
		return listaPorDevolver;
	}
}
