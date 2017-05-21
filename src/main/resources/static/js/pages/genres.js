$(document).ready(function() {
  var table = $('table#genre-data-table').DataTable({
    'ajax' : '/genres/data/genres',
    'serverSide' : true,
    "language" : {
      "url" : "//cdn.datatables.net/plug-ins/1.10.11/i18n/German.json"
    },
    columns : [ {
      data : 'id'
    }, {
      data : 'name'
    } ]
  });

});