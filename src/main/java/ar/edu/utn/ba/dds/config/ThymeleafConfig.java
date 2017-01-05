package ar.edu.utn.ba.dds.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

@Configuration
//@PropertySource("classpath:thymeleaf.properties")
//Creo que basicamente creo esta clase para no tener que poner la configuracion en el Web.XMl y Tener que crear un archivo XMl mas para la configuracion del Servlet
public class ThymeleafConfig {

	//Aca vienen los BEANS
	
	//Para configurar ThymeLeaf asi puede detectar nuestros templates
	@Bean
	public TemplateResolver templateResolver() {
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver();
		//Donde estan ubicados los Templates
		templateResolver.setPrefix("/WEB-INF/templates/");
		//Sufijo de los templates, asi los distingue de otros archivos que pudiera haber en esa carpeta
		templateResolver.setSuffix(".html");
		//Configuro el Modo de Renderizado de ThymeLeaf
		templateResolver.setTemplateMode("HTML5");

		return templateResolver;
	}
	
	//Para Configurar nuestro Template Engine
	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());
		return templateEngine;
	}
	
	//Para Configurar el "ViewResolver" de nuestro Servlet (Se encarga de decidir a que archivo html mandar al usuario).
	@Bean
	public ViewResolver viewResolver() {
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		//Usamos un Template Engine para que pre-renderize las paginas y les cargue el contenido
		viewResolver.setTemplateEngine(templateEngine());
		viewResolver.setOrder(1);
	
		return viewResolver;
	}
	
}
