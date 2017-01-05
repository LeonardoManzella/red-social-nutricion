$(document).ready(function(){

	$(".nav a").on("click", function(){
		$(".nav").find(".active").removeClass("active");
		$(this).parent().addClass("active");
	});

	modificarReceta.init();

});

var modificarReceta = {
	
	ingredientes : null,	
	
	init : function() {
		this.ingredientes = new Array();
		
		$("#ingredientesDisponibles tr").unbind("click").click(function(event) {
			if(!$(event.target).hasClass("selected")) {
				$(event.target).addClass("selected");
				modificarReceta.ingredientes[modificarReceta.ingredientes.length] = $(event.target).attr("id").substring(12);
			}
			else {
				$(event.target).removeClass("selected");
				var indexInArray = modificarReceta.ingredientes.indexOf(modificarReceta.getIndex($("#ingredientesDisponibles tr").toArray(), $(event.target)));
				modificarReceta.ingredientes.splice(indexInArray, 1);
			}
			$("#ingredientes").val(modificarReceta.ingredientes);
		});
		
		$("#temporadas tr").unbind("click").click(function(event) {
			if(!$(event.target).hasClass("selected")) {
				$("#temporadas tr .selected").removeClass("selected")
				$(event.target).addClass("selected");
				$("#temporada").val($(event.target).text().trim());
			}
			else {
				$(event.target).removeClass("selected");
				$("#temporada").val("");
			}
		});
		
		$("#horariosDisponibles tr").unbind("click").click(function(event) {
			if(!$(event.target).hasClass("selected")) {
				$("#horariosDisponibles tr .selected").removeClass("selected")
				$(event.target).addClass("selected");
				$("#horario").val($(event.target).text().trim());
			}
			else {
				$(event.target).removeClass("selected");
				$("#horario").val("");
			}
		});
	}
}