package ar.edu.utn.ba.dds.entidades;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.persistence.*;

import ar.edu.utn.ba.dds.enums.*;


@Entity
public class Comida {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id_comida")
	private int id;		//Id para Mappear con la Base de Datos
	
	private Calendar fecha;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Receta recetaAsociada;
	
	private HorarioComida horario;
	
	protected Comida(){
		super();
	}
	
	public Comida(Calendar fecha2, Receta recetaAsociada, HorarioComida horario){
		this.setFecha(fecha2);
		this.setRecetaAsociada(recetaAsociada);
		this.setHorario(horario);
	}

	public Calendar getFecha() {
		return fecha;
	}
	
	/**
	 * Este metodo se usa en el FrontEnt en el template "comidasPlanificadas"
	 */
	public String obtenerStringFecha(){
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");  
		String currentDate = formatter.format( getFecha().getTime() );
		return currentDate;
	}

	public void setFecha(Calendar fecha2) {
		this.fecha = fecha2;
	}

	public Receta getRecetaAsociada() {
		return recetaAsociada;
	}

	public void setRecetaAsociada(Receta recetaAsociada) {
		this.recetaAsociada = recetaAsociada;
	}

	public HorarioComida getHorario() {
		return horario;
	}

	public void setHorario(HorarioComida horario) {
		this.horario = horario;
	}

	public Integer getId() {
		return this.id;
	}
	
	/**
	 * Metodo para el FrontEnd
	 */
	public String obtenerStringHorario(){
		return HorarioComida.devolverString( getHorario() );
	}
}
