package ar.edu.utn.ba.dds.entidades;

import java.util.Set;

import javax.persistence.*;

@Entity
public class Usuario {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id_usuario")
	private int id;
	
	private String mail;
	
	private String password;
	
	private String nombre;
	
	private Integer edad;
	
	private char sexo;
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Receta> recetas;
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Ingrediente> preferencias;
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "usuario_grupo", joinColumns = @JoinColumn(name = "id_usuario"), inverseJoinColumns = @JoinColumn(name = "id_grupo"))
	private Set<Grupo> grupos;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Comida> comidasPlanificadas;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private RestriccionAlimentaria restriccion;
	
	
	public Usuario() {
		super();
	}
	
	public String getNombre(){
		return this.nombre;
	}
	
	public Set<Comida> getComidasPlanificadas(){
		return this.comidasPlanificadas;
	}
	
	public String getMail(){
		return this.mail;
	}
	
	public Set<Receta> getRecetas(){
		return this.recetas;
	}

	public Set<Ingrediente> getPreferencias(){
		return this.preferencias;
	}	

	public char getSexo() {
		return sexo;
	}
	
	public Set<Grupo> getGrupos() {
		return grupos;
	}
	
	public Integer getEdad() {
		return edad;
	}
	
	public void setPreferencias(Set<Ingrediente> preferencias) {
		this.preferencias = preferencias;
	}

	public void setEdad(Integer edad) {
		this.edad = edad;
	}


	public void setGrupos(Set<Grupo> grupos) {
		this.grupos = grupos;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setRecetas(Set<Receta> recetas) {
		this.recetas = recetas;
	}

	public void setComidasPlanificadas(Set<Comida> comidasPlanificadas) {
		this.comidasPlanificadas = comidasPlanificadas;
	}

	public void setSexo(char sexo) {
		this.sexo = sexo;
	}

	public RestriccionAlimentaria getRestriccion() {
		return restriccion;
	}

	public void setRestriccion(RestriccionAlimentaria restriccion) {
		this.restriccion = restriccion;
	}

	/**
	 * OJO con lo que hacen con esto, <b>NO lo llamen</b> a menos que esten seguro que fue <i>Persistido Primero</i>
	 * @return
	 */
	public int getId() {
		return id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Para el FrontEnd es este metodo, se usa en el Template "verRecetas.html"
	 */
	public boolean contieneReceta(Receta recetaPorComparar){
		
		for(Receta receta: this.getRecetas()){
			if(receta.getNombre().equalsIgnoreCase( recetaPorComparar.getNombre() )){
				return true;
			}
		}
		
		return false;
	}
	
	
	
	public boolean equals(Object object) {
		if(object == null) return false;
		
		Usuario usuario = (Usuario) object;
		
		if( !this.getNombre().equalsIgnoreCase(usuario.getNombre()) )	return false;
		
		return true;
	}
	
	public int hashCode() {
	        int result;
	        if( getNombre()!=null ) result = getNombre().hashCode();
	        else result = super.hashCode();
	        return result;
	}
}
