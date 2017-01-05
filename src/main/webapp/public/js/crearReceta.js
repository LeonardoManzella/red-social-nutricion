$(document).ready(function(){

	$(".nav a").on("click", function(){
		$(".nav").find(".active").removeClass("active");
		$(this).parent().addClass("active");
	});

	crearReceta.init();

});

var crearReceta = {
	
	ingredientes : null,	
	
	init : function() {
		this.ingredientes = new Array();
		
		$("#ingredientesDisponibles tr").unbind("click").click(function(event) {
			if(!$(event.target).hasClass("selected")) {
				$(event.target).addClass("selected");
				crearReceta.ingredientes[crearReceta.ingredientes.length] = $(event.target).attr("id").substring(12);
			}
			else {
				$(event.target).removeClass("selected");
				var indexInArray = crearReceta.ingredientes.indexOf(crearReceta.getIndex($("#ingredientesDisponibles tr").toArray(), $(event.target)));
				crearReceta.ingredientes.splice(indexInArray, 1);
			}
			$("#ingredientes").val(crearReceta.ingredientes);
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