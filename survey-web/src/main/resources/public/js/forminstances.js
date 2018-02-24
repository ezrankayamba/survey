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
				this.innerHTML = "Show";
			}
		});
		btn.classList.add("hidden");
		btn.innerHTML = "Show";
		var uuid = btn.dataset.uuid;
		var topUI = document.getElementById(uuid);
		topUI.style.display = "none";
		var jsonText = document.getElementById("json-" + uuid).value;
		var json = JSON.parse(jsonText);
		var groups = json.groups;
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
				var inpLi = document.createElement("li");
				var inpLabel = null;
				if (input.hasOwnProperty('label') === true) {
					inpLabel = document.createTextNode(input.label + ": "
							+ input.value);
				} else {
					inpLabel = document.createTextNode(input.name + ": "
							+ input.value);
				}
				inpLi.appendChild(inpLabel);
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
