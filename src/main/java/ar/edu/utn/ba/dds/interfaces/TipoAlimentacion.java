package ar.edu.utn.ba.dds.interfaces;

import java.util.Set;

import ar.edu.utn.ba.dds.enums.TipoGrupoAlimenticio;
import ar.edu.utn.ba.dds.implement.GrupoAlimenticio;

public interface TipoAlimentacion {
	
	public TipoGrupoAlimenticio getTipoAlimentacion();
	
	public boolean contieneA(Set<GrupoAlimenticio> grupoReceta);

}
