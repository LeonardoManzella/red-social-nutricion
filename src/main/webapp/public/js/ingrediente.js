$(document).ready(function(){
	
	var ingredienteIndex=0;
	
	//Handler del click en el botón agregar ingrediente
	$("#crearRecetaForm")
	.on('click', '.addButton', function() {
            ingredienteIndex++;
            var $template = $('#ingredienteTemplate'),
                $clone    = $template
                                .clone()
                                .removeClass('hide')
                                .removeAttr('id')
                                .attr('data-ingrediente-index', ingredienteIndex)
                                .insertBefore($template);

            // Actualizar los atributos de name
            $clone
                .find('[name="ingredientes"]').attr('name', 'ingrediente[' + ingredienteIndex + '].ingredientes').end()
                .find('[name="cantidad"]').attr('name', 'ingrediente[' + ingredienteIndex + '].cantidad').end()
                .find('[name="unidad"]').attr('name', 'ingrediente[' + ingredienteIndex + '].unidad').end();

            // Agregar nuevos campos
        })

        // Handler del click en el botón para eliminar ingrediente
        .on('click', '.removeButton', function() {
            var $row  = $(this).parents('.form-group'),
                index = $row.attr('data-ingrediente-index');

            // Eliminar elemento que contiene los campos
            $row.remove();
        });

});