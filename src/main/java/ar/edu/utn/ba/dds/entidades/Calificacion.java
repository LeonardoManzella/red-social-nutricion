package ar.edu.utn.ba.dds.entidades;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Calificacion {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id_calificacion")
	private int id;		//Id para Mappear con la Base de Datos
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Usuario usuario;
	
	
	private Integer calificacion;
	
	protected Calificacion(){
		super();
	}

	public Calificacion(Usuario usuario, Integer calificacion) {
		this.usuario = usuario;
		this.calificacion = calificacion;
	}
	
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public Integer getCalificacion() {
		return calificacion;
	}
	public void setCalificacion(Integer calificacion) {
		this.calificacion = calificacion;
	}

}
