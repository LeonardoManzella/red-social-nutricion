package ar.edu.utn.ba.dds.entidades;

import javax.persistence.*;

import ar.edu.utn.ba.dds.implement.GrupoAlimenticio;

@Entity
public class Proporcion {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id_proporcion")
	private int id;
	
	private Integer valor;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private GrupoAlimenticio grupoAlimenticio;
	
	protected Proporcion(){
		super();
	}

	public Proporcion(GrupoAlimenticio grupo, Integer proporcion) {
		this.grupoAlimenticio = grupo;
		this.valor = proporcion;
	}

	public Integer getValor() {
		return valor;
	}

	public void setValor(Integer valor) {
		this.valor = valor;
	}

	public GrupoAlimenticio getGrupoAlimenticio() {
		return grupoAlimenticio;
	}

	public void setGrupoAlimenticio(GrupoAlimenticio grupoAlimenticio) {
		this.grupoAlimenticio = grupoAlimenticio;
	}
	
}
