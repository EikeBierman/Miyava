$(document)
		.ready(
				function() {
					var table = $('table#user-data-table')
							.DataTable(
									{
										'ajax' : '/user/data/users',
										'serverSide' : true,
										"language" : {
											"url" : "//cdn.datatables.net/plug-ins/1.10.11/i18n/German.json"
										},
										columns : [
												{
													data : 'username',
													render : function(data,
															type, row) {
														if (row.username) {
															return '<a href="/user/'
																	+ row.id
																	+ ' ">'
																	+ row.username
																	+ '</a>';
														}
														return '';
													}
												},
												{
													data : 'email'
												},
												{
													data : 'firstname'
												},
												{
													data : 'lastname'
												},
												{
													data : 'enabled',
													orderable : false,
													searchable : false,
													render : function(data,
															type, row) {
														if(!row.enabled){
															return "<i style='color:red;' class='fa fa-times' aria-hidden='true'></i>";
														} else if(row.enabled){
															return "<i style='color:green;' class='fa fa-check' aria-hidden='true'></i>";
														}
													}
												},
												{
													data : 'userRoles'
												}
										// ,
										// {
										// add another column not
										// persisted on the
										// server-side
										// data : 'anothercolumn',
										// order is not available
										// orderable : false,
										// yet filter should be
										// available through the
										// method
										// findAll(DataTablesInput
										// input, Specification<T>
										// additionalSpecification)
										// searchable : false,
										// render : function(data,
										// type, row) {
										// if (row.userRole) {
										// return row.role;
										// }
										// return '';
										// }
										// }
										]
									});

					$('select#role_selector')
							.change(
									function() {
										var filter = '';
										$(
												'select#role_selector option:selected')
												.each(
														function() {
															filter += $(this)
																	.text()
																	+ "+";
														});
										filter = filter.substring(0,
												filter.length - 1);
										table.columns(2).search(filter).draw();
									});

				});