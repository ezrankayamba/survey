function initFormInstancesList() {
	var buttons = document.getElementsByClassName("expclp-btn");
	var locations = [];
	for (var i = 0; i < buttons.length; i++) {
		var btn = buttons[i];
		btn.addEventListener("click", function() {
			var uuid = this.dataset.uuid;
			var x = document.getElementById(uuid);
			if (x.style.display === "none") {
				x.style.display = "block";
				this.classList.add("shown");
				this.innerHTML = "Hide";
			} else {
				x.style.display = "none";
				this.classList.add("hidden");
				this.innerHTML = "Show details";
			}
		});
		btn.classList.add("hidden");
		btn.innerHTML = "Show details";
		var uuid = btn.dataset.uuid;
		var topUI = document.getElementById(uuid);
		topUI.style.display = "none";
		var jsonText = document.getElementById("json-" + uuid).value;
		var json = JSON.parse(jsonText);
		var groups = json.groups;
		var regexMulti=/^MultiSelect\[([ a-zA-Z0-9,;()\/]+)]$/g;
		// regexMulti=escapeRegExp(regexMulti);
		console.log(regexMulti);
		var regexSingle="^Select\[([ a-zA-Z0-9,;()/]+)]$";
		// regexSingle=escapeRegExp(regexSingle);
		for (var j = 0; j < groups.length; j++) {
			var group = groups[j];
			if (group.type === 'GPSLocationCapture') {
				var loc = {};
				for (var k = 0; k < group.inputs.length; k++) {
					var input = group.inputs[k];
					if (input.name.endsWith('Latitude')) {
						loc.latitude = input.value;
					} else if (input.name.endsWith('Longitude')) {
						loc.longitude = input.value;
					}
				}
				loc.name = document.getElementById("name-" + uuid).value;
				if (loc.latitude) {
					locations.push(loc);
				}
			}
			var grpLi = document.createElement("li");
			var grpLabel = null;
			if (group.hasOwnProperty('label') === true) {
				grpLabel = document.createTextNode(group.label);
			} else {
				grpLabel = document.createTextNode(group.name);
			}
			grpLi.appendChild(grpLabel);
			topUI.appendChild(grpLi);
			var inputs = group.inputs;
			var grpUL = document.createElement("ul");
			grpLi.appendChild(grpUL);
			for (var k = 0; k < inputs.length; k++) {
				var input = inputs[k];
				var type = input.type;
				var extras=null;
				var pattern=new RegExp(regexMulti);
				var regexRes=pattern.exec(type);
				if(regexRes){
					console.log(regexRes);
					var tokens=regexRes[1].split(";");
					if(tokens.length == 2){
						extras = tokens[1].split(",");
					}
		    	}
				var inpLi = document.createElement("li");
				var inpData = null;
				var value = null;
				var label = null;
				if (input.hasOwnProperty('label') === true) {
					label = input.label + ": ";
				} else {
					label = input.name + ": ";
				}
				if(input.value !== null && typeof input.value === 'object'){
					inpLi.appendChild(document.createTextNode(label));
					if(input.value instanceof Array){
						inpData=printObjectArray(input.value, extras);
					}else {
						inpData=printObject(input.value, extras);
					}
				}else{
					inpData = document.createTextNode(label + input.value);
				}
				

				inpLi.appendChild(inpData);
				grpUL.appendChild(inpLi);
			}
		}
	}
	var totLat = 0;
	var totLon = 0;
	var cords = [];
	for (var i = 0; i < locations.length; i++) {
		var loc = locations[i];
		var lat = parseFloat(loc.latitude);
		var lon = parseFloat(loc.longitude);
		totLat = lat + totLat;
		totLon = lon + totLon;
		cords.push([ lat, lon ]);
	}
	var avgLat = totLat / locations.length;
	var avgLon = totLon / locations.length;
	var mymap = L.map('themap');
	mymap.on('load', function() {
		$('#fi-tabs').on('change.zf.tabs', function() {
			if ($('#fi-map:visible').length) {
				mymap.invalidateSize(false);
				mymap.fitBounds(cords, {
					padding : [ 10, 10 ]
				});
			}
		});
	});

	mymap.setView([ avgLat, avgLon ], 13);
	var tile1 = L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {});
	tile1.addTo(mymap);

	for (var i = 0; i < locations.length; i++) {
		var loc = locations[i];
		if (loc.latitude !== '') {
			var marker = L.marker([ loc.latitude, loc.longitude ]).addTo(mymap);
			marker.bindPopup("<b>" + loc.name + "</b>").openPopup();
		}
	}
}
function escapeRegExp(str) {
	  return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
	}
function inputLabel(text) {
    return (text.charAt(0).toUpperCase() + text.slice(1))+": ";
}
function printObject(obj, extras){
	var grpUL = document.createElement("ul");
	var name;
	var keyNames = Object.keys(obj);
	
	keyNames.sort((a, b) => {
		if(a && a == 'extras'){
			return 999999;
		}
		return -1;
	});
		
	for(var i in keyNames) {
	    name = keyNames[i]
	    var inpLi = document.createElement("li");
	    if(obj[name] instanceof Array && extras && extras.length > 0){
	    	var tmp="";
	    	Array.prototype.forEach.call(extras, (item, index) => {
		    		if(index != 0){
		    			tmp=tmp + ", ";
		    		}
		    		tmp=tmp + item+": " + obj[name][index];
	    		});
	    	inpLi.appendChild(document.createTextNode(inputLabel(name) + tmp));	
	    }else{
	    	inpLi.appendChild(document.createTextNode(inputLabel(name) + obj[name]));	
	    }
	        
	    grpUL.appendChild(inpLi);
	}
	return grpUL;
}
function printObjectArray(objArr, extras){
	var grpUL = document.createElement("ul");
	Array.prototype.forEach.call(objArr, (item, index) => {
		var inpLi = document.createElement("li");
		var label="[#"+(index+1)+"]";
		inpLi.appendChild(document.createTextNode(label));
		inpLi.appendChild(printObject(item, extras));
		grpUL.appendChild(inpLi);
		});
	return grpUL;
}
