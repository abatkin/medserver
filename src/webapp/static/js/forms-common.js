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
	$("#group-" + id).addClass("error");
	$("#group-" + id + " span.help-inline").text(error).removeClass('hidden');
}

function successValidation(id) {
	$("#group-" + id).removeClass("error");
	$("#group-" + id + " span.help-inline").addClass('hidden');
}

all_validators = [];

function addRule(selector, error, validator) {
	all_validators.push([selector, error, validator]);
	selector.blur(function() {
		var id = $(this).attr('id');
		if (validator(this)) {
			successValidation(id);
		} else {
			failValidation(id, error);
		}
	});
}

function addRegexRule(fieldType, regex, error) {
	addRule($(".validatable.field-" + fieldType), error, function(element) {
		return $(element).val().search(regex) >= 0;
	});
}

function setupValidation() {
	addRegexRule("integer", /^\d*$/, "must be a whole number");
	addRule($(".validatable.required"), "required", function(element) {
		return $.trim($(element).val()).length > 0;
	});
}

function stringMinLength(fieldName, length) {
	addRule($("#" + fieldName), "must be at least " + length + " characters", function(element) {
		return $.trim($(element).val()).length >= length;
	});
}

function stringMaxLength(fieldName, length) {
	addRule($("#" + fieldName), "must be no more than " + length + " characters", function(element) {
		return $.trim($(element).val()).length <= length;
	});
}

function setupForm() {
	$('form.main-form').submit(function() {
		var failures = 0;
		for (var i = 0; i < all_validators.length; i++) {
			[selector, error, validator] = all_validators[i];
			selector.each(function() {
				var id = $(this).attr('id');
				if (validator(this)) {
					successValidation(id);
				} else {
					failValidation(id, error);
					failures++;
				}
			});
		}
	
		if (failures > 0) {
			$('#error-box').removeClass('hidden');
			$(document).scrollTop($('#error-box').offset().top - 70);
			return false;
		} else {
			return true;
		}
	});
}

$(document).ready(function() {
	setupTabs();
	setupValidation();
	setupForm();
});
