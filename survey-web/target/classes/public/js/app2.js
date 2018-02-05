$(document).ready(function() {

	$(document).foundation();

	$(function() {
		$(window).scroll(function() {
			var winTop = $(window).scrollTop();
			if (winTop >= 30) {
				$("body").addClass("sticky-shrinknav-wrapper");
			} else {
				$("body").removeClass("sticky-shrinknav-wrapper");
			}
		});
	});
	function guid() {
		  function s4() {
		    return Math.floor((1 + Math.random()) * 0x10000)
		      .toString(16)
		      .substring(1);
		  }
		  return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
		    s4() + '-' + s4() + s4() + s4();
		}

	let currentUrl = window.location.href;

	console.log("Current Url: " + currentUrl);
	
	var formsCreate = "(.?){1,}\/forms\/create$";
	var formsEdit = "(.?){1,}\/forms\/edit\/((.?){1,})$";
	

	if ((new RegExp(formsCreate).test(currentUrl)) | (new RegExp(formsEdit).test(currentUrl))) {
		$('#reposTree').jstree();

		var reposName = document.getElementById('reposName');

		reposName.onkeyup = function() {
			document.getElementById('rootNode').innerHTML = reposName.value;
		}

		//$.contextMenu('html5');
		$.contextMenu({
            selector: '.context-menu-root', 
            callback: function(key, options) {
                var m = "clicked(cb): " + key;
                window.console && console.log(m) || alert(m); 
                //$('#groupModel').foundation('reveal', 'open');
                var popup = new Foundation.Reveal($('#firstModal'));
                popup.open();
            },
            items: {
                "edit": {name: "Edit", icon: "edit"},
                "add": {name: "Add group", icon: "add"},
                "delete": {name: "Delete", icon: "delete"},
                "sep1": "---------",
                "quit": {name: "Quit", icon: function(){
                    return 'context-menu-icon context-menu-icon-quit';
                }}
            }
        });

        $('.context-menu-root').on('click', function(e){
            console.log('clicked(on): ', this);
        })  
		
		console.log("JS Tree Configured!");
		
		
		/* Manage the JSON object */
		let rps={"name": "Test"};
		rps.groups = [];
		
		let rpsText = JSON.stringify(rps, undefined, 4);
		
		document.getElementById('reposJson').getElementsByTagName("textarea")[0].innerHTML = rpsText;
	}

});
