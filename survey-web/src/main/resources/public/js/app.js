$(document).ready(
		function() {
			function guid() {
				function s4() {
					return Math.floor((1 + Math.random()) * 0x10000).toString(
							16).substring(1);
				}
				return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-'
						+ s4() + s4() + s4();
			}
			let currentUrl = window.location.href;
			var formsCreate = "(.?){1,}\/forms\/create$";
			var formsEdit = "(.?){1,}\/forms\/edit\/((.?){1,})$";
			if ((new RegExp(formsCreate).test(currentUrl))
					| (new RegExp(formsEdit).test(currentUrl))) {
				initFormReposTree();
			}
			var fiList = "(.?){1,}\/instances\/repos\/((.?){1,})$";
			if ((new RegExp(fiList).test(currentUrl))
					| (new RegExp(formsEdit).test(currentUrl))) {
				initFormInstancesList();
			}
			$(document).foundation();
		});
