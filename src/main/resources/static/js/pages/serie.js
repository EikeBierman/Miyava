$(document).ready(function() {
	var table = $('table#serie-data-table').DataTable({
		'ajax' : '/serie/data/series',
		'serverSide' : true,
		"language" : {
			"url" : "//cdn.datatables.net/plug-ins/1.10.11/i18n/German.json"
		},
		columns : [ {
			data : 'id',
			render : function(data,
					type, row) {
				if (row.id) {
					return '<a href="/serie/'
					+ row.id
					+ ' ">'
					+ row.id
					+ '</a>';
				}
				return '';
			}
		}, {
			data : 'name'
		},
		{
			data : 'overview'
		}]
	});

});