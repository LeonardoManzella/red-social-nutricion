package ar.edu.utn.ba.dds.entidades;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import ar.edu.utn.ba.dds.enums.*;
import ar.edu.utn.ba.dds.implement.GrupoAlimenticio;

@Entity
public class RestriccionTipoAlimento {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id_restriccion_tipo_alimento")
	private int id;
	
	
	@Column(name = "enum_tipo_grupo")
	private TipoGrupoAlimenticio grupoAlimenticio;

	protected RestriccionTipoAlimento(){
		
	}
	
	public RestriccionTipoAlimento(TipoGrupoAlimenticio grupo) {
		this.grupoAlimenticio = grupo;
	}

	public TipoGrupoAlimenticio getGrupoAlimenticio() {
		return grupoAlimenticio;
	}

	public void setGrupoAlimenticio(TipoGrupoAlimenticio grupoAlimenticio) {
		this.grupoAlimenticio = grupoAlimenticio;
	}

	public boolean rechazaGruposAlimenticios(Set<GrupoAlimenticio> gruposAlimenticio) {
		return gruposAlimenticio.stream().anyMatch(grupo-> grupo.getTipoAlimentacion().equals(this.getGrupoAlimenticio()));
	}
	
	
}
