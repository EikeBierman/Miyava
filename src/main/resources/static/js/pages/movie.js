$(document).ready(function() {
	var table = $('table#movie-data-table').DataTable({
		'ajax' : '/movie/data/movies',
		'serverSide' : true,
		"language" : {
			"url" : "//cdn.datatables.net/plug-ins/1.10.11/i18n/German.json"
		},
		columns : [ {
			data : 'id',
			render : function(data,
					type, row) {
				if (row.id) {
					return '<a href="/movie/'
							+ row.id
							+ ' ">'
							+ row.id
							+ '</a>';
				}
				return '';
			}
		}, {
			data : 'title'
		},
		{
			data : 'overview'
		}]
	});
	
});