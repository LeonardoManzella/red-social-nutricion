package ar.edu.utn.ba.dds.interfaces;

import java.util.Deque;

import ar.edu.utn.ba.dds.entidades.Receta;
import ar.edu.utn.ba.dds.entidades.Usuario;
import ar.edu.utn.ba.dds.enums.HorarioComida;

public interface CriterioFilterPattern {
	
	public Deque<Receta> recomendarPorCriterio(int cantidadDeRecetas, Usuario usuario, HorarioComida horario);
	
}
