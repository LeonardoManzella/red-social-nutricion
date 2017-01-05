package ar.edu.utn.ba.dds.implement;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import ar.edu.utn.ba.dds.enums.*;
import ar.edu.utn.ba.dds.interfaces.TipoAlimentacion;

@Entity
public class GrupoAlimenticio implements TipoAlimentacion{

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id_grupo_alimenticio")	
	private int id;
	
	@Column(name = "enum_tipo_grupo")
	private TipoGrupoAlimenticio grupo;

	protected GrupoAlimenticio(){
		super();
	}
	
	public GrupoAlimenticio(TipoGrupoAlimenticio grupoAsignar) {
		this.grupo = grupoAsignar;
	}

	@Override
	public TipoGrupoAlimenticio getTipoAlimentacion() {
		return this.grupo;
	}

	@Override
	public boolean contieneA(Set<GrupoAlimenticio> grupoRecetas) {
		return grupoRecetas.stream().anyMatch(grupoReceta-> (grupoReceta.getTipoAlimentacion() == this.grupo));
	}

	//Como esta Clase se usa Dentro de HashMaps, deben Redefinirse estos 2 metodos
	@Override
	public int hashCode(){
		return getTipoAlimentacion().hashCode();
	}
	
	@Override
	public boolean equals(Object otroObjeto){
		return (this.hashCode() == otroObjeto.hashCode());
	}

	public TipoGrupoAlimenticio getGrupo() {
		return grupo;
	}

	public void setGrupo(TipoGrupoAlimenticio grupo) {
		this.grupo = grupo;
	}

}
