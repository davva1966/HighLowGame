// NOTE! Don't forget to change the version number in "load-top-scripts.tagx" when changing this file.

rootFolder = location.pathname.substr(0, 15) === '/SpeedSolutions' ? '/SpeedSolutions/'
		: '/';

jQuery(document)
		.ready(
				function() {

					// userLanguage is set in "load-scripts.tagx" and is first
					// the language from the browser locale and second or the
					// user selected language
					var currentLanguage = 'en';
					// if (userLanguage)
					// currentLanguage = userLanguage;

					// Create single selects
					var singleselectboxes = jQuery('.singleselect');
					singleselectboxes.each(function(index) {

						// allowUserInput is set in the jsp page
						var allowUserInputInSelect = false;
						if (singleselectboxes.length == 1) {
							if (typeof allowUserInput != "undefined")
								allowUserInputInSelect = allowUserInput;
							else if (typeof allowUserInput1 != "undefined")
								allowUserInputInSelect = allowUserInput1;
						} else if (index == 0) {
							if (typeof allowUserInput1 != "undefined")
								allowUserInputInSelect = allowUserInput1;
						} else if (index == 1) {
							if (typeof allowUserInput2 != "undefined")
								allowUserInputInSelect = allowUserInput2;
						} else if (index == 2) {
							if (typeof allowUserInput3 != "undefined")
								allowUserInputInSelect = allowUserInput3;
						} else if (index == 3) {
							if (typeof allowUserInput4 != "undefined")
								allowUserInputInSelect = allowUserInput4;
						} else if (index == 4) {
							if (typeof allowUserInput5 != "undefined")
								allowUserInputInSelect = allowUserInput5;
						}

						$(this).select2({
							language : currentLanguage,
							tags : allowUserInputInSelect,
						});

					});

					// Create multi selects
					var multiselectboxes = jQuery('.multiselect');
					multiselectboxes.each(function(index) {

						$(this).select2({
							language : currentLanguage,
						});

					});

					// Create single/multi selects with remote data
					var singleselectremotedataboxes = jQuery('.singleselectRemote, .multiselectRemote');
					singleselectremotedataboxes
							.each(function(index) {

								// remoteDataUrl is set in the jsp page
								var url = "nourlfound";
								if (singleselectremotedataboxes.length == 1) {
									url = rootFolder + remoteDataUrl;
								} else if (index == 0) {
									url = rootFolder + remoteDataUrl1;
								} else if (index == 1) {
									url = rootFolder + remoteDataUrl2;
								} else if (index == 2) {
									url = rootFolder + remoteDataUrl3;
								} else if (index == 3) {
									url = rootFolder + remoteDataUrl4;
								} else if (index == 4) {
									url = rootFolder + remoteDataUrl5;
								}

								$(this)
										.select2(
												{
													language : currentLanguage,
													ajax : {
														url : url,
														dataType : 'json',
														delay : 400,
														data : function(params) {
															return {
																query : params.term, // search
																// term
																page : params.page
															};
														},
														processResults : function(
																data, params) {
															// parse the results
															// into the format
															// expected by
															// Select2
															// since we are
															// using custom
															// formatting
															// functions we do
															// not need to
															// alter the remote
															// JSON data, except
															// to indicate that
															// infinite
															// scrolling can be
															// used
															params.page = params.page || 1;

															return {
																results : data.items,
																pagination : {
																	more : (params.page * 30) < data.totalCount
																}
															};
														},
														cache : true
													},
													// minimumInputLength: 3,
													templateResult : function(
															result) {
														if (result.loading)
															return result.text;

														var markup = '<div class="dropdownwrapper"><div style="float:left; display:table">';
														if (result.imageUrl) {
															markup += '<div style="vertical-align:middle; display:table-cell">';
															markup += '<img src="'
																	+ result.imageUrl
																	+ '" style="max-height:35px;max-width:35px;" />';
															markup += '</div>';
														}

														markup += '<div style="float:left';
														if (result.imageUrl) {
															markup += '; margin-left:10px';
														}
														markup += '">';

														markup += '<div style="font-weight:bold">'
																+ result.text
																+ '</div>';

														if (result.extraText) {
															markup += '<div style="margin:0px; padding:0px; clear:both; height:6px">&nbsp;</div>';
															markup += '<div style="font-size:80%">'
																	+ result.extraText
																	+ '</div>';
														}

														markup += '</div></div></div>';

														var $jQueryObject = $($
																.parseHTML(markup));
														return $jQueryObject;

													},
													templateSelection : function(
															result) {
														return result.description
																|| result.text;
													}
												});
							});

				});

function globalAjaxCursorChange() {
	$("html").bind("ajaxStart", function() {
		$(this).addClass('busy');
	}).bind("ajaxStop", function() {
		$(this).removeClass('busy');
	});
}

jQuery(document)
		.ready(
				function() {
					var inputs = document.querySelectorAll('.inputfile');
					Array.prototype.forEach
							.call(
									inputs,
									function(input) {
										var label = input.nextElementSibling, labelVal = label.innerHTML;

										input
												.addEventListener(
														'change',
														function(e) {
															var fileName = '';
															if (this.files
																	&& this.files.length > 1)
																fileName = (this
																		.getAttribute('data-multiple-caption') || '')
																		.replace(
																				'{count}',
																				this.files.length);
															else
																fileName = e.target.value
																		.split(
																				'\\')
																		.pop();

															if (fileName)
																label
																		.querySelector('span').innerHTML = fileName;
															else
																label.innerHTML = labelVal;
														});
									});
				});

function updateSlaveSelectValues(url, masterSelectId, slaveSelectId) {
	updateSlaveSelectValues(url, masterSelectId, slaveSelectId, null, false);
}
function updateSlaveSelectValues(url, masterSelectId, slaveSelectId, urlParms, runWithoutSelection) {
	var selectedId = $("#" + masterSelectId).val();
	if (selectedId && runWithoutSelection == false)
		return;
	if (!selectedId)
		selectedId = "*undefined*";
	url = rootFolder + url + encodeURIComponent(selectedId);
	if (urlParms != null) {
		url = url + "?" + $.param(urlParms);
	}
	$.ajax({
		url : url,
		cache: false,
		dataType : "json",
		success : function(data) {
			var options, index, select, option, currentSelection;

			// Save current selection
			currentSelection = $("#" + slaveSelectId).val();

			select = document.getElementById(slaveSelectId);

			// Clear the old options
			if (select != null)
				select.options.length = 0;

			// Load the new options
			options = data.items;
			for (index = 0; index < options.length; ++index) {
				option = options[index];
				if (option.id == -999) {
					ssAlert(option.text);
				} else {
					// Restore current selection if present
					if (currentSelection != null
							&& option.id == currentSelection)
						select.options.add(new Option(option.text, option.id,
								false, true));
					else
						select.options.add(new Option(option.text, option.id));
				}
			}

			$("#" + slaveSelectId).trigger('change');

		},
		error : function(request, errorText, exceptionObj) {
			select = document.getElementById(slaveSelectId);
			select.options.length = 0;
			$("#" + slaveSelectId).select2();
			if (exceptionObj != "Not Found") {
				var msg = errorText + " - " + exceptionObj;
				ssAlert(msg);
			} else {
				ajaxError(request, errorText, exceptionObj);
			}

		}
	});
};

function confirmSubmit(formId, title, message, yesText, noText) {
	var buttons = {};
	buttons[yesText] = true;
	buttons[noText] = false;

	$.prompt(message, {
		title : title,
		buttons : buttons,
		submit : function(e, v, m, f) {
			if (v) {
				form = $('#' + formId);
				form.off('submit');
				form.submit();
			}
		}
	});
};

function confirmCancelSubscription(link, title, message, yesAtPeriodEndText, yesImmediatelyText, noCancelText) {
	var buttons = {};
	if (yesAtPeriodEndText)
		buttons[yesAtPeriodEndText] = 1;
	if (yesImmediatelyText)
		buttons[yesImmediatelyText] = 2;
	buttons[noCancelText] = 3;

	$.prompt(message, {
		title : title,
		buttons : buttons,
		submit : function(e, v, m, f) {
			if (v!=3) {
				if (v==1)
					link = link + "?cancelType=PeriodEnd";
				else
					link = link + "?cancelType=Immediately";
				window.location.href = link;
			}
		}
	});
};

function confirmDelete(formId, title, message, yesText, noText) {
	var buttons = {};
	buttons[yesText] = true;
	buttons[noText] = false;

	$.prompt(message, {
		title : title,
		buttons : buttons,
		submit : function(e, v, m, f) {
			if (v) {
				form = $('#' + formId);
				form.submit();
			}
		}
	});
};

function confirmLink(link, title, message, yesText, noText) {
	var buttons = {};
	buttons[yesText] = true;
	buttons[noText] = false;

	$.prompt(message, {
		title : title,
		buttons : buttons,
		submit : function(e, v, m, f) {
			if (v) {
				window.location.href = link;
			}
		}
	});
};

function ssAlert(message) {
	label = getOKButtonLabel();

	var buttons = {};
	buttons[label] = true;

	$.prompt(message, {
		buttons : buttons,
	});
};

function showToastMessage(message, stayTime, skipReloadOnClose) {
	if (stayTime == null || stayTime == 0)
		stayTime = 2000;

	if (skipReloadOnClose == null)
		skipReloadOnClose = true;

	label = getOKButtonLabel();

	var buttons = {};
	buttons[label] = true;

	if (message) {
		$.prompt(message, {
			timeout : stayTime,
			buttons : buttons,
			close : function() {
				if (!skipReloadOnClose) {
					location.reload();
				}
			}
		});
	}
};

function getOKButtonLabel() {
	url = rootFolder + "ui/getOKButtonLabel";
	var label = null;
	$.ajax({
		url : url,
		dataType : "text",
		success : function(okButtonLabel) {
			label = okButtonLabel;
		},
		error : function(request, errorText, exceptionObj) {
			label = "OK";
		},
		async : false
	});

	return label;
};

function stringToBoolean(string) {
	if (string == null || string.trim().length == 0)
		return false;
	switch (string.toLowerCase().trim()) {
	case "true":
	case "yes":
	case "1":
		return true;
	case "false":
	case "no":
	case "0":
	case null:
		return false;
	default:
		return Boolean(string);
	}
};

jQuery(document).ready(function() {
	var message = getUrlParameter('ssdisplaymessage');
	if (message) {
		message = message.replace(/\+/g, ' ');
		showToastMessage(decodeURIComponent(message), null, true);
	}
});

var getUrlParameter = function getUrlParameter(sParam) {
	var sPageURL = decodeURIComponent(window.location.search.substring(1)), sURLVariables = sPageURL
			.split('&'), sParameterName, i;

	for (i = 0; i < sURLVariables.length; i++) {
		sParameterName = sURLVariables[i].split('=');

		if (sParameterName[0] === sParam) {
			return sParameterName[1] === undefined ? true : sParameterName[1];
		}
	}
};

function transparencyToOpacity(transparency) {
	if (transparency != null) {
		return (100 - transparency) / 100;
	}

	return 1;

}
