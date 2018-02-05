//Global data
let FormJSON = {};
let GroupUList = null;
let GroupId = 0;
let InputId = 0;
let SelectedId = null;
function initFormReposTree() {
	var reposName = document.getElementById('reposName');
	var reposDesc = document.getElementById('reposDesc');

	var json = document.getElementById('reposJson').getElementsByTagName(
			"textarea")[0].innerHTML;
	var formId = document.getElementsByName("id")[0].value;
	try {
		if (json) {
			FormJSON = JSON.parse(json);
			FormJSON.id = formId;
			console.log("Json exist");
			console.log(FormJSON);
		} else {
			FormJSON.id = formId;
		}
	} catch (e) {
		console.log(e);
	}

	FormJSON.name = reposName.value;
	FormJSON.description = reposDesc.value;
	if (!('groups' in FormJSON)) {
		FormJSON.groups = [];
	}
	updateJsonDisplay();

	var rootNode = {
		"id" : "rootNode",
		"text" : "Root",
		"state" : {
			"opened" : true
		},
		"children" : []
	};
	var grpIdx = 0;
	var inpIdx = 0;
	for (i in FormJSON.groups) {
		var grp = FormJSON.groups[i];
		var groupId = parseInt(grp.id.split("-")[1]);
		if (groupId > grpIdx) {
			grpIdx = groupId;
		}
		var grpNode = {
			"id" : "group-" + groupId,
			"text" : grp.name,
			"children" : []
		};
		for (j in grp.inputs) {
			var inp = grp.inputs[j];
			var inputId = parseInt(inp.id.split("-")[1]);
			if (inputId > inpIdx) {
				inpIdx = inputId;
			}

			var inpNode = {
				"id" : "input-" + inputId,
				"text" : inp.name,
				"children" : []
			};
			grpNode.children.push(inpNode);
		}
		rootNode.children.push(grpNode);
	}
	// update Indexs
	GroupId = grpIdx;
	InputId = inpIdx;

	$('#reposTree').jstree({
		'plugins' : [ "state", "crrm" ],
		'core' : {
			'data' : [ rootNode ],
			'check_callback' : true,
			'error' : function(err) {
				console.log("Tree Error: " + err);
			}
		}
	});

	$('#reposTree').on("select_node.jstree", function(e, data) {
		SelectedId = data.node.id;

		if (data.node.id === 'rootNode') {
			$("#groupForm input[name=_name]").val("");
			$("#groupForm input[name=label]").val("");
			$("#groupForm input[name=type]").val("");

			rootClick();
		} else {
			$("#inputForm select[name=action]").unbind('change');
			$("#inputForm select[name=action]").off();
			$("#inputForm input[name=_name]").val("");
			$("#inputForm input[name=label]").val("");
			$("#inputForm input[name=type]").val("");

			if (data.node.id.startsWith("group-")) {
				groupClick(data.node.parent, data.node.id);
			}

			if (data.node.id.startsWith("input-")) {
				inputClick(data.node.parent, data.node.id);
			}
		}

	});

	reposName.onkeyup = function() {
		FormJSON.name = reposName.value;
		updateJsonDisplay();
	}
	reposDesc.onkeyup = function() {
		FormJSON.description = reposDesc.value;
		updateJsonDisplay();
	}
}
function replacer(key, value) {
	if (value === null || !value)
		return undefined
	return value
}
function grpId() {
	GroupId = GroupId + 1;
	return GroupId;
}
function inpId() {
	InputId = InputId + 1;
	return InputId;
}
function updateJsonDisplay() {
	var rpsText = JSON.stringify(FormJSON, replacer, 4);
	document.getElementById('reposJson').getElementsByTagName("textarea")[0].innerHTML = rpsText;
}
function createNode(parent_node, new_node_id, new_node_text, position) {
	$('#reposTree').jstree('create_node', $(parent_node), {
		"text" : new_node_text,
		"id" : new_node_id
	}, position, function() {
		// console.log("Node created successfully!");
	}, false);

	return new_node_id;
}
function deleteNode(nodeId) {
	$('#reposTree').jstree('delete_node', "#" + nodeId);
}
function renameNode(nodeId, text) {
	$('#reposTree').jstree('rename_node', "#" + nodeId, text);
}
function rootClick() {
	$("#groupForm").show();
	$("#inputForm").hide();
}
function groupClick(parentId, myId) {
	$("#inputForm").show();
	$("#groupForm").hide();

	$("#nodeIdRef").val(parentId + "," + myId);

	$('#inputForm select[name=action] option[value="manageInput"]').prop(
			'selected', true);
	$('#inputForm select[name=action]').prop('disabled', false);

	$("#inputForm select[name=action]").change(function() {
		var val = $("#inputForm select[name=action]").val();
		if (val === 'editGroup') {
			var grp = getGroupById(SelectedId);
			$("#inputForm input[name=_name]").val(grp.name);
			$("#inputForm input[name=label]").val(grp.label);
			$("#inputForm input[name=type]").val(grp.type);
		} else {
			$("#inputForm input[name=_name]").val("");
			$("#inputForm input[name=label]").val("");
			$("#inputForm input[name=type]").val("");
		}
	});
}
function inputClick(parentId, myId) {
	$("#inputForm").show();
	$("#groupForm").hide();

	$("#nodeIdRef").val(parentId + "," + myId);

	$('#inputForm select[name=action] option[value="manageInput"]').prop(
			'selected', true);
	$('#inputForm select[name=action]').prop('disabled', true);

	var inp = getInputByIdAndGrpId(parentId, myId);
	$("#inputForm input[name=_name]").val(inp.name);
	$("#inputForm input[name=label]").val(inp.label);
	$("#inputForm input[name=type]").val(inp.type);

}
function saveNewGroup(btn) {
	if (true) {
		$("#groupForm").hide();
		var grp = {};
		grp.id = "group-" + grpId();
		grp.name = $("#groupForm input[name=_name]").val();
		grp.label = $("#groupForm input[name=label]").val();
		grp.type = $("#groupForm input[name=type]").val();
		grp.inputs = [];

		FormJSON.groups.push(grp);
		updateJsonDisplay();
		createNode("#rootNode", grp.id, grp.name, "last");
	}
}
function saveNewInput() {
	if (true) {
		$("#inputForm").hide();
		var action = $("#inputForm select[name=action]").val();

		if (action === 'manageInput') {
			if (SelectedId.startsWith("group-")) {
				var inp = {};
				inp.id = "input-" + inpId();
				inp.name = $("#inputForm input[name=_name]").val();
				inp.label = $("#inputForm input[name=label]").val();
				inp.type = $("#inputForm input[name=type]").val();

				var idRefs = $("#nodeIdRef").val();
				var groupId = idRefs.split(",")[1];

				getGroupById(groupId).inputs.push(inp);
				createNode("#" + groupId, inp.id, inp.name, "last");
			} else if (SelectedId.startsWith("input-")) {// update input
				var idRefs = $("#nodeIdRef").val();
				var groupId = idRefs.split(",")[0];
				var inp = getInputByIdAndGrpId(groupId, SelectedId);
				inp.name = $("#inputForm input[name=_name]").val();
				inp.label = $("#inputForm input[name=label]").val();
				inp.type = $("#inputForm input[name=type]").val();

				// getGroupById(groupId).inputs.push(inp);
				// createNode("#" + groupId, inp.id, inp.name, "last");
				renameNode(SelectedId, inp.name);
			}
		} else if (action === 'editGroup' && SelectedId.startsWith("group-")) {
			var grp = getGroupById(SelectedId);
			grp.name = $("#inputForm input[name=_name]").val();
			grp.label = $("#inputForm input[name=label]").val();
			grp.type = $("#inputForm input[name=type]").val();
			renameNode(SelectedId, grp.name);
		}

		updateJsonDisplay();
	}
}
function getGroupById(groupId) {
	var grp = null;
	for (i in FormJSON.groups) {
		var tmp = FormJSON.groups[i];
		if (tmp.id === groupId) {
			grp = FormJSON.groups[i];
			break;
		}
	}

	return grp;
}
function getInputByIdAndGrpId(groupId, inputId) {
	var inp = null;
	for (i in FormJSON.groups) {
		var tmp = FormJSON.groups[i];
		if (tmp.id === groupId) {
			for (j in FormJSON.groups[i].inputs) {
				var tmp2 = FormJSON.groups[i].inputs[j];
				if (tmp2.id === inputId) {
					inp = tmp2;
					break;
				}
			}
			break;
		}
	}

	return inp;
}
function deleteSelected() {
	var success = false;
	if (confirm("Are you sure you want to delete this node?")) {
		$("#inputForm").hide();
		if (SelectedId.startsWith('group-')) {
			var idx = FormJSON.groups.indexOf(getGroupById(SelectedId));
			if (idx > -1) {
				FormJSON.groups.splice(idx, 1);
				success = true;
			}
		} else if (SelectedId.startsWith('input-')) {
			var idRefs = $("#nodeIdRef").val();
			var groupId = idRefs.split(",")[0];
			var grp = getGroupById(groupId);
			var idx = grp.inputs.indexOf(getInputByIdAndGrpId(groupId,
					SelectedId));
			if (idx > -1) {
				grp.inputs.splice(idx, 1);
				success = true;
			}
		}
		if (success) {
			deleteNode(SelectedId);
			updateJsonDisplay();
		}
	}
}
function formSubmitOverall() {
	$("#inputForm").remove();
	$("#groupForm").remove();
	return true;
}