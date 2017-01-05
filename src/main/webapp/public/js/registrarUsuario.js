$(document).ready(function(){

	$(".nav a").on("click", function(){
	   $(".nav").find(".active").removeClass("active");
	   $(this).parent().addClass("active");
	});
	
	registrarUsuario.init();

});

var registrarUsuario = {
		
		preferencias : null,
		
		gruposAlimenticios : null,
		
		init : function() {
			
			this.preferencias = new Array();
			this.gruposAlimenticios = new Array();
			
			$("#ingredientesDisponibles tr").unbind("click").click(function(event) {
				if(!$(event.target).hasClass("selected")) {
					$(event.target).addClass("selected");
					registrarUsuario.preferencias[registrarUsuario.preferencias.length] = $(event.target).attr("id").substring(12);
				}
				else {
					$(event.target).removeClass("selected");
					var indexInArray = registrarUsuario.preferencias.indexOf(registrarUsuario.getIndex($("#ingredientesDisponibles tr").toArray(), $(event.target)));
					registrarUsuario.preferencias.splice(indexInArray, 1);
				}
				$("#preferencias").val(registrarUsuario.preferencias);
			});
			
			$("#gruposAlimenticios tr").unbind("click").click(function(event) {
				if(!$(event.target).hasClass("selected")) {
					$(event.target).addClass("selected");
					registrarUsuario.gruposAlimenticios[registrarUsuario.gruposAlimenticios.length] = $(event.target).text().trim();
				}
				else {
					$(event.target).removeClass("selected");
					var indexInArray = registrarUsuario.gruposAlimenticios.indexOf($(event.target).text().trim());
					registrarUsuario.gruposAlimenticios.splice(indexInArray, 1);
				}
				$("#restricciones").val(registrarUsuario.gruposAlimenticios);
			});
			
//			$("#submitRegistrar").click( function() {
//				$("#preferencias").val(registrarUsuario.preferencias);
//				$("#restricciones").val(registrarUsuario.gruposAlimenticios);
//				$("#registrarUsuarioForm").submit();
//			});
		},
		
		getIndex : function(array, value) {
			for(var i = 0; i < array.length; i++) {
				if(array[i].textContent.trim() === value.text().trim())
					return i-1;
			}
			return -1;
		}
		
}