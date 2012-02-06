function setupTabs() {
	$("div.navbar ul.nav li a").each(function() {
		var id = $(this).attr("id");
		if (id == null) {
			return;
		}
		var result = id.match(/^link-(.+)$/);
		if (result == null) {
			return;
		}
		var linkName = result[1];
		if (linkName != 'main') {
			$("#data-" + linkName).hide();
		}
	
		$(this).click(function() {
			// Set link styling
			$("div.navbar ul.nav li").removeClass("active");
			$("div.navbar ul.nav li").has("a#link-" + linkName).addClass("active");
	
			// Make content appear/disappear
			$('div[id^="data-"]').hide();
			$("#data-" + linkName).show();
		});
	  });
}

function failValidation(id, error) {
	console.log("fail: " + id);
	$("#group-" + id).addClass("error");
	$("#group-" + id + " span.help-inline").text(error).show();
}

function successValidation(id) {
	console.log("success");
	$("#group-" + id).removeClass("error");
	$("#group-" + id + " span.help-inline").hide();
}

function addRegexRule(fieldType, regex, error) {
	console.log("add rule");
	$(".validatable.field-" + fieldType).blur(function() {
		console.log("blur");
		var id = $(this).attr('id');
		if ($(this).val().search(regex) < 0) {
			failValidation(id, error);
		} else {
			successValidation(id);
		}
	});
}

function setupValidation() {
	addRegexRule("integer", /^\d*$/, "must be a whole number");
}

$(document).ready(function() {
	setupTabs();
	setupValidation();
});
