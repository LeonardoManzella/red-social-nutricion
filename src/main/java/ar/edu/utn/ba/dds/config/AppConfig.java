package ar.edu.utn.ba.dds.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import ar.edu.utn.ba.dds.model.EstadisticasService;
import ar.edu.utn.ba.dds.model.GreenFoodService;
import ar.edu.utn.ba.dds.model.RecetaService;
import ar.edu.utn.ba.dds.model.UsuarioGrupoService;

@Configuration
@EnableWebMvc
//Le dice a la aplicacion donde busca las configuraciones y los controladores, nos ahorra tener que declararlos cada vez y nos permite definir los controladores mas facilmente mediante annotations
@ComponentScan({ "ar.edu.utn.ba.dds.config", "ar.edu.utn.ba.dds.controllers" })

//Basicamente creo esta clase para no tener que poner la configuracion en el Web.XMl
//Es para que busque las clases, los templates, los controllers.. todos los archivos para mostrar la web.
public class AppConfig extends WebMvcConfigurerAdapter {

	//Aca van los dintintos BEANS
	
	/**	Ahora vienen Clases <b>"Servicio"</b> del backend, osea clases que usan todos los controllers para acceder al backend.<br><br>
	 * 	Basicamente son clases wrapper que nos sirve de <i>"adaptador/modelo"</i> entre los controllers y el backend. <br><br>
	 * 	Ademas son <b>Wrappers de los DAOs (Data Acess Object)</b>, por lo que aca se mete toda la <i>"logica sucia"</i> que afecta a la base de datos y que afecta a varios DAOs a la vez.
	*/
	@Bean
	public GreenFoodService modelService() {
		return new GreenFoodService();
	}
	
	@Bean
	public EstadisticasService estadisticasService() {
		return new EstadisticasService();
	}
	
	@Bean UsuarioGrupoService usuarioGrupoService() {
		return new UsuarioGrupoService();
	}
	
	@Bean RecetaService recetaService() {
		return new RecetaService();
	}
	
	//Para que busque archivos extras que necesitemos: para Javascript, Ajax o lo que sea..
	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		//Aca le damos permiso a la aplicacion para busque los archivos en la carpeta "public" dentro de la carpeta "webapp" dentro de la carpeta "main"
	    registry.addResourceHandler("/public/**").addResourceLocations("/public/");
	}
}
