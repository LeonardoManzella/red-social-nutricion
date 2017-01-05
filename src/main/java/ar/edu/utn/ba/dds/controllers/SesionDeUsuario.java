package ar.edu.utn.ba.dds.controllers;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import ar.edu.utn.ba.dds.entidades.Usuario;

@Component
@Scope(value="session",  proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SesionDeUsuario {

	//Para guardar el Usuario Actual, se carga al loguearse el usuario
	private Usuario usuarioActual;		
	
	public SesionDeUsuario(){
		super();
		this.setUsuarioActual(null);
	}

	public Usuario getUsuarioActual() {
		return usuarioActual;
	}

	public void setUsuarioActual(Usuario usuarioActual) {
		this.usuarioActual = usuarioActual;
	}
	
	public boolean estaLogueadoUsuario(){
		if(this.getUsuarioActual() == null){
			return false;
		}else{
			return true;
		}
	}
	
	public void usuarioLogOut(){
		this.usuarioActual = null;
	}
}
