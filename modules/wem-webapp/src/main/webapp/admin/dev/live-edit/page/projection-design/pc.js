$(function() {

	var measurements = 'metric';
	
	$('.datepicker').datepicker({dateFormat: phrases.dateFormat});
	
	function tooltip(element) {	
		element.tipTip({defaultPosition: 'top', delay: 0, maxWidth: 'auto'});
	}
	
	tooltip($('.tooltip'));
	
	// My page
	var primaryContact = $('#primary-contact'); 
	if (primaryContact.length) {
		$.get(primaryContact.text(), function(data) {
			if (data.length) {
				primaryContact.html(data).show().prev('.with-contact').show().prev('p').hide();
				$('.primary-contact').val(primaryContact.find('h4 > a').attr('href').split('mailto:')[1]);
			}
	    });
	} 
	
	// Newsfeed
	var newsfeed = $('.newsfeed');
	if (newsfeed.length) {
	    $.get(newsfeed.prev().text(), function(data) {
	        newsfeed.html(data);
	    });
	}
	
	// Language selection
	$('#flags').find('a').click(function(){
		if ($(this).hasClass('default-language')) {
			$.cookie('language', null, { path: '/' });
		} else {
			$.cookie('language', $(this).children('img').attr('src').split('flag-')[1].substring(0, 2), { expires: 3650, path: '/' });
		}
	});
	
	$('#flags > a').click(function(e) {
		e.preventDefault();
		if ($(this).next('ul').is(':visible')) {
			topmenuPopup($(this), true, 0, 'fast');
		} else {
			topmenuPopup($(this), false, 0, 'fast');
		}
	});
	
	// SLA search
	$('.sla-search').find('form').validate({
		submitHandler: function(form) {
			$.post($(form).attr('action'), $(form).serialize(), function(data){
				$('#sla-result').html(data).show();
	    	});
		}
	});
	
	$('.sla-search').find('form').ajaxStart(function () {
		$(this).addClass('loading');
	}).ajaxStop(function () {
		$(this).removeClass('loading');
	});
	
    // World map
    $('#where-to-buy').find('area, a:not(".no-hover")').hover(
		function () {
			$('[id='+'map_' + $(this).attr('id').split('_')[1]+']').show();
		},
		function () {
			$('[id='+'map_' + $(this).attr('id').split('_')[1]+']').hide();
		}
    );
	
    // Top menu
    $('#top-menu > ul > li > a.children').each(function() {
    	var selItem = $(this);
        if (!selItem.next('ul').length) {
            $.get(selItem.attr('href'), {'pagemode': 'sub-menu'}, function(data){
                selItem.parent().append(data);
                tooltip(selItem.parent().find('.tooltip'));
            });
        }
    });

 	$('#top-menu-container').css('zIndex', 500);
 	$('#menu-splash-section').css('zIndex', 490);
    
    $('#top-menu > ul > li > a.children').hover(function() {
    	if (!$(this).parent('#flags').length || ($(this).next('ul').is(':visible') && $(this).parent('#flags').length)) {
	        topmenuPopup($(this), false, 200);
    	}
    }, function() {
        topmenuPopup($(this), true, 400);
    });
    
    function topmenuPopup(elm, hide, delay, slideSpeed) {
    	if (hide) {
	        elm.next('ul').stop(true, true).delay(delay).slideUp(slideSpeed, function(){
	        	$(this).prev().removeClass('popup');
	        });
    	} else {
	        $('#top-menu > ul > li > a.children').next('ul').stop(true, true).delay(delay).hide(0, function(){
	        	$(this).prev().removeClass('popup');
	        });
	        elm.addClass('popup');
	    	elm.next('ul').stop(true, true).delay(delay).slideDown(slideSpeed);
    	}
    }
    
	$('#top-menu > ul > li > ul').live('mouseover mouseout', function(e) {
		if (e.type == 'mouseover') {
    		$(this).stop(true, true);
		} else {
	        $(this).stop(true, true).delay(400).slideUp(function(){
	        	$(this).prev().removeClass('popup');
	        });
		}
	});
    
    // Main menu
    $('#main-menu > ul > li > a.children').each(function() {
    	var selItem = $(this);
        if (!selItem.next('.popup-container').length) {
            selItem.after('<div class="popup-container"/>');
            $.get(selItem.attr('href'), {'pagemode': 'sub-menu'}, function(data){
                selItem.next('.popup-container').append(data);
                tooltip(selItem.next('.popup-container').find('.tooltip'));
            });
        }
    });
    
    $('#main-menu > ul > li > a.children').hover(function() {
        $('#main-menu > ul > li > a.children').next('.popup-container').stop(true, true).delay(200).hide(0, function(){
        	$(this).prev().removeClass('popup');
        });
        $(this).addClass('popup');
        $(this).next('.popup-container').stop(true, true).delay(200).slideDown();
    }, function() {
        $(this).next('.popup-container').stop(true, true).delay(400).slideUp(function(){
        	$(this).prev().removeClass('popup');
        });
    });
    
	$('.popup-container').live('mouseover mouseout', function(e) {
		if (e.type == 'mouseover') {
    		$(this).stop(true, true);
		} else {
	        $(this).stop(true, true).delay(400).slideUp(function(){
	        	$(this).prev().removeClass('popup');
	        });
		}
	});
	
    // Show / hide
    $('#mi_map').hide();
    
    $('[id*="rm_"]').live('click', function(e){
        e.preventDefault();
        var trigger = $(this);
        var id = 'mi_' + $(this).attr('id').split('_')[1];
        var moreInfo = $('[id='+id+']');
        if (moreInfo.is(':visible')) {
        	if (moreInfo.is('span')) {
        		moreInfo.fadeOut('', function() {
        			trigger.text(phrases.more);
        		});
        	} else {
        		moreInfo.slideUp('', function() {
		        	if (trigger.parent('.facet').length) {
		        		trigger.children('.icon').removeClass('minus').addClass('plus');
		        	} else {
			        	trigger.text(phrases.more);
		        	}
        		});
        	}
        } else {
        	if (moreInfo.is('span')) {
        		moreInfo.fadeIn();
        	} else {
				moreInfo.slideDown();
        	}
        	if (trigger.parent('.facet').length) {
        		trigger.children('.icon').removeClass('plus').addClass('minus');
        	} else {
	        	$(this).text(phrases.less);
        	}
        }
    });
    
    // Share bar
    $('.share-bar').find('img, a').click(function(e){
    	if ($(this).hasClass('add-comment')) {
    		$('#olt_comment > a.add-comment').trigger('click');
		} else if ($(this).hasClass('comment')) {
			window.location.href = window.location.href.split('#')[0] + '#comments';
    	} else if ($(this).hasClass('print-icon')) {
    		window.print();
    	} else if ($(this).hasClass('twitter-share-icon')) {
    		e.preventDefault();
    		var newWindow = window.open($(this).attr('href'), 'twitter', 'width=600, height=450');
        	newWindow.focus();
    	}
    });
    
    // File archive
    $('.file-archive > ul').find('.left').hover(function () {
		$(this).siblings('.left').andSelf().toggleClass('highlight');
	});
    
    $('.file-archive > ul.vertical').find('.left').click(function () {
        if ($(this).parent().hasClass('folder')) {
        	if (!$(this).hasClass('teaser')) {
				if ($(this).nextAll('ul').length > 0) {
	                $(this).nextAll('ul').slideToggle('fast', function() {
	                    changeIcon($(this));
	                });
	            } else {
	                changeIcon($(this));
	            }
        	} else if ($(this).children('span.hidden').length) {
				window.location = $(this).children('span.hidden').text();
        	}
        } else {
            window.location = $(this).parent().find('a').attr('href');
        }
    });
    
	function changeIcon(elm) {
	    var icon = elm.parent().children('.name').find('img');
	    if (icon.attr('src').search(/icon-folder-open./) != -1) {
	        icon.attr('src', icon.attr('src').replace(/icon-folder-open./, 'icon-folder.'));
	    } else {
	        icon.attr('src', icon.attr('src').replace(/icon-folder./, 'icon-folder-open.'));
	    }
	}
    
    // Site search
    $('.site-search-form').find('label').removeClass('hidden');
    
	$('.site-search-form').ajaxStart(function () {
		$(this).removeClass('close').addClass('loading');
	}).ajaxStop(function () {
		$(this).removeClass('loading');
	});
    
	$('#site-search-box').autocomplete({
		minLength: 2,
		source: $('.site-search-form').find('span.hidden').text(),
		html: true,
		open: function(event, ui) {
			var widget = $('#site-search-box').autocomplete('widget');
			widget.find('li:last').not('.autocomplete-last').addClass('autocomplete-last');
			$('.autocomplete-last').position({
				my: 'left top',
				at: 'left bottom',
				of: widget.find('li:not(.autocomplete-last):last')
			});
			$('.site-search-form').css('zIndex', parseInt($(this).closest('form').css('zIndex')) + 2).addClass('close');
		},
		close: function(event, ui) {
			$('.site-search-form').css('zIndex', parseInt($(this).closest('form').css('zIndex')) - 2).removeClass('close');
		},
		appendTo: '#top-menu-container',
		position: {my: 'right top', at: 'right top', of: '.site-search-form', offset: '13 -10'},
		select: function(event, ui) {window.location = ui.item.url}
	}).focus(function(){            
        $(this).data('autocomplete').search($(this).val());
    });
    
    $('.autocomplete-icon.close').click(function() {
    	$('#site-search-box').autocomplete('close').val('').focus();
    });
    
    // Forms
    $('form.labels-inside').find('select').live('focus', (function() {
    	$(this).removeClass('inside');
    })).live('blur', (function() {
    	if ($(this).val() == $(this).children('option').first().val()) $(this).addClass('inside');
    }));
    
    function prepareInsideLabels() {
	    $('form.labels-inside').find('label:not(".radio, .outside")').addClass('inside').inFieldLabels();
	    
	    $('form.labels-inside').find('select').each(function() {
	    	if ($(this).val() == $(this).children('option').first().val()) $(this).addClass('inside');
	    });
	    
	    $('form.labels-inside').find('.required').not('#site-search-box').each(function() {
	    	var label = $('label[for = '+this.id+']');
	    	if (!label.hasClass('outside')) {
		    	if (label.children('span.tooltip').length) label = label.children('span.tooltip');
	    		label.append(' (' + phrases.required + ')');
    		}
	    });
	}
	
	prepareInsideLabels();
    
    function setRequired() {
	    $('form').find('.required').not('span').each(function () {
	    	var label = $('label[for = '+this.id+']');
	    	if (!(label.children('span.required').length || label.hasClass('inside'))) label.not('.radio, .error').append('<span class="required">*</span>');
	    });
	}
	
	setRequired();
    
    // Form dialog feedback
    $('form.dialog-feedback').append('<div class="feedback-dialog"/>');
    $('.feedback-dialog').dialog({
		modal: true,
		resizable: false,
		autoOpen: false,
		buttons: [
			{
				text: phrases.ok,
				click: function() {
					$(this).dialog('close');
				}
			}
		]
	});
	
	$('form.dialog-feedback').find('select[name="country"]').change(function() {
		var stateParagraph = $(this).parent().next('p');
		var stateSelect = stateParagraph.children('select');
		var phoneInput = $('form.dialog-feedback').find('input[name="phone"]');
		var phoneLabel = phoneInput.prev('label');
		if ($(this).find('option:selected').hasClass('phone-required')) {
			if (!phoneInput.hasClass('required')) {
				phoneInput.addClass('required');
				phoneLabel.text(phoneLabel.text().split(' (')[0] + ' (' + phrases.required + ')');
			}
			if (stateParagraph.children('select').length && $(this).val() == 'US') {
				stateSelect.addClass('required');
				stateParagraph.slideDown();
			}
		} else {
			phoneInput.removeClass('required');
			phoneLabel.text(phoneLabel.text().split(' (')[0] + ' (' + phrases.recommended + ')');
			phoneInput.removeClass('error');
			if (stateParagraph.is(':visible')) {
				stateParagraph.slideUp();
				stateSelect.removeClass('required').val(stateSelect.children('option').first().val());
			}
		}
		/*if (stateParagraph.children('select').length) {
			if ($(this).val() == 'US') {
				stateSelect.addClass('required');
				stateParagraph.slideDown();
				phoneInput.addClass('required');
				phoneLabel.append(requiredPhrase);
			} else {
				phoneInput.removeClass('required');
				phoneLabel.text(phoneLabel.text().split(' (')[0]);
				phoneInput.removeClass('error');
				stateParagraph.slideUp();
				stateSelect.removeClass('required').val(stateSelect.children('option').first().val());
			}
		}*/
	});
	
	function concatName(form) {
		var firstname = form.find('input[name="firstname"]');
		var lastname = form.find('input[name="lastname"]');
		var name = form.find('input[name="name"]');
    	if (firstname.length && lastname.length && name.length) {
    		name.val(firstname.val() + ' ' + lastname.val());
    	}
	}

	function handleSubmit() {
		$('form.dialog-feedback').validate({
			errorPlacement: function(label, element) {
				if (element.parent('p').length) {
					label.insertBefore(element.parent('p'));
				} else if (element.prev('label').length) {
					label.insertBefore(element.prev('label'));
				} else {
					label.insertBefore(element);
				}
			},
			submitHandler: function(form) {
		    	var dialog = $('.feedback-dialog');
		    	var response = '';
		    	concatName($(form));
		    	if ($(form).attr('id') == 'interest-form') {
			    	$('.picker').each(function() {
			    		var selected = '';
			    		$(this).find('li:not(".add")').each(function(i) {
			    			if (i > 0) selected += '|';
			    			selected += $(this).children('a').text() + '_' + $(this).children('a').attr('class').split('_')[1];
			    		});
			    		$(this).find('input').val(selected);
			    	});
		    	}
		    	$.post($(form).attr('action'), $(form).serialize(), function(data){
		    		if ($(data).find('.error').length) {
		    			response += $(data).find('.error').html();
						dialog.bind('dialogclose', function() {
							$(form).replaceWith($(data).find('form.dialog-feedback'));
							handleSubmit();
							if ($(form).hasClass('labels-inside')) prepareInsideLabels();
							tooltip($('form.dialog-feedback').find('.tooltip'));
							reloadCaptcha($('form.dialog-feedback').find('img.captcha-image'));
						});
					} else {
						dialog.dialog('option', 'buttons', {}).dialog('option','closeOnEscape', false);
						response += data;
						if ($(data).find('span.hidden').length) {
							$.post($(data).find('span.hidden').text(), $(form).serialize(), function(){
								dialog.dialog('option','closeOnEscape', true);
								dialog.dialog('option', 'buttons', [
									{
										text: phrases.ok,
										click: function() {
											$(this).dialog('close');
										}
									}
								]);
							});
						}
						dialog.bind('dialogclose', function() {
							window.location.href = window.location.href;
						});
					}
					dialog.html(response);
					dialog.dialog('option', 'title', $(form).find('input[name="dialog-title"]').val());
					dialog.dialog('open');
		    	});
			}
		});
	}
	
	handleSubmit();
	
	jQuery.validator.messages.required = '';
	jQuery.validator.addMethod('required-group', function(val, el) {
        var form = $(el).closest('form');
        return form.find('.required-group:checked').length;
	}, phrases.requiredgroup);
	jQuery.validator.addMethod('required-terms', function(val, el) {
        var form = $(el).closest('form');
        return form.find('#event-registration-terms:checked').length;
	}, phrases.requiredterms);
	
	// Splash
    $('#splash').hover(function(){
    	$(this).children('.counter, .arrow').stop(true, true).fadeIn();
	}, function() {
		$(this).children('.counter, .arrow').stop(true, true).delay(500).fadeOut();
    });
    
    if ($('#splash > .splash').length > 1) {
		var runSplash = setInterval(splash, 5000);
	}

	function splash() {
		clearInterval(runSplash);
    	var as = $('#splash > .splash.active');
    	as.stop(true, true).fadeOut().removeClass('active');
    	$('#splash > .counter > span.active').removeClass('active');
    	var std;
    	var id;
    	if ($(this).hasClass('left')) {
    		if (as.prev('.splash').length) {
    			std = as.prev();
    		} else {
    			std = as.nextAll('.splash').last();
    		}
    	} else if ($(this).parent('.counter').length) {
    		id = 's_' + $(this).attr('id').split('_')[1];
        	std = $('[id='+id+']');
    	} else {
    		if (as.next('.splash').length) {
    			std = as.next();
    		} else {
    			std = as.prevAll('.splash').last();
    		}
    	}
    	std.stop(true, true).fadeIn().addClass('active');
    	id = 'si_' + std.attr('id').split('_')[1];
    	$('[id='+id+']').addClass('active');
    	runSplash = setInterval(splash, 5000);
	}

    $('#splash > .arrow, #splash > .counter > span:not(.active)').live('click', splash);
    
    // Visit form
    $('#visit-form').find('.datepicker').datepicker('option', 'minDate', 0);
    
	$('#visit-form').find('.bar > a').click(function(e) {
		e.preventDefault();
		var bar = $(this).parent();
		bar.before(bar.prev().clone());
		visitFormVisitors(bar.prev());
	});
	
	$('#visit-form').find('.icon.close').live('click', function(){
		$(this).closest('.visitor').nextAll('.visitor').each(function() {
			visitFormVisitors($(this), true);
		});
		$(this).closest('.visitor').remove();
	});
	
	function visitFormVisitors(visitor, remove) {
		var id = visitor.children('label:first').attr('for'),
			newVisitorNo = Number(id.charAt(id.length - 1));
		if (remove) {
			newVisitorNo -= 1;
		} else {
			newVisitorNo += 1;
		}
		if (!visitor.find('.icon.close').length) visitor.children('label:first').prepend('<div class="icon close"/>');
		visitor.html(visitor.html().replace(/(-name|-title|Persons\[)\d+(\]\.\w*)?/g, '$1' + newVisitorNo + '$2'));
	}
	
	$('#visit-form').validate({
		errorPlacement: function(label, element) {
			return true;
		},
		submitHandler: function(form) {
			var visitors = '';
			$('.visitor').each(function(i){
				if (i > 0) visitors += '\n';
				visitors += $(this).find('input:first').val();
				if ($(this).find('input:last').val().length) visitors += ', ' + $(this).find('input:last').val();
			});
			$('input[name="visitors"]').val(visitors);
			form.submit();
		}
	});
	
	// Passport user create
	if ($('#passport-user-form').find('input[name="admin_mail_body"]').length) {
	    var roles = '';
	    $('#passport-user-form').find('.required-group').each(function() {
			if (roles.length > 0) roles += ' ';
			roles += $(this).attr('name');
	    });
		$('#passport-user-form').validate({
			ignoreTitle: true,
			groups: {
				roles: roles
			},
			errorPlacement: function(label, element) {
				if (element.prev('label').length) {
					label.insertBefore(element.prev('label'));
				} else {
					label.insertBefore(element);
				}
			},
			submitHandler: function(form) {
				$(form).find('.button').hide().nextAll('img.hidden').show();
				if ($('input[name="role-other-specification"]').val() != '') $('#passport-create-role-other').attr('checked','checked');
				var adminMailBody = $(form).find('input[name="admin_mail_body"]').val() + '\n\nRoles:';
				var crmRoles = '';
				$('#passport-create-role').find('input:checkbox:checked').each(function(){
					adminMailBody += '\n' + $(this).next().text();
					if (crmRoles.length > 0) crmRoles += ', ';
					crmRoles += $(this).next().text();
					if ($(this).attr('id') == 'passport-create-role-other') {
						var otherSpecification = $('input[name="role-other-specification"]').val();
						adminMailBody += ' ' + otherSpecification;
						crmRoles += ' ' + otherSpecification;
					}
				});
				$(form).find('input[name^="address[0]."]').each(function(){
					var inputName = $(this).attr('name');
					$(form).find('input[name="' + inputName + '"]').val($(this).val());
				});
				$(form).find('input[name="suggested-roles"]').val(crmRoles);
				$(form).find('input[name="admin_mail_body"]').val(adminMailBody);
				$(form).find('input[name="username"]').val($('#passport-create-email').val());
				$.post($(form).find('span.hidden').text(), $(form).serialize(), function(){
					form.submit();
				});
			}
		});
	}
	
    // Total cost of ownership calculator
	if ($('#tco').length) {
	    $('#tco-hours-slider').slider({
			range: 'min',
			value: 8,
			step: 1,
			min: 1,
			max: 24,
			slide: function(e, ui){
				$('#tco-hours').val(ui.value);
				$('#tco-hours-show').text(ui.value);
			}
		});
		$('#tco-hours-show').text($('#tco-hours-slider').slider('value'));
		$('#tco-hours').val($('#tco-hours-slider').slider('value'));
		
	    $('#tco-days-slider').slider({
			range: 'min',
			value: 260,
			step: 1,
			min: 1,
			max: 365,
			slide: function(e, ui){
				$('#tco-days').val(ui.value);
				$('#tco-days-show').text(ui.value);
			}
		});
		$('#tco-days-show').text($('#tco-days-slider').slider('value'));
		$('#tco-days').val($('#tco-days-slider').slider('value'));
		
	    $('#tco-years-slider').slider({
			range: 'min',
			value: 3,
			step: 1,
			min: 1,
			max: 7,
			slide: function(e, ui){
				$('#tco-years').val(ui.value);
				$('#tco-years-show').text(ui.value);
			},
			change: function(e, ui){
				tcoChangeYears();
			} 
		});
		$('#tco-years-show').text($('#tco-years-slider').slider('value'));
		$('#tco-years').val($('#tco-years-slider').slider('value'));
		
		$('[id^="tco-units-slider-"]').each(function() {
			tcoPrepareSlider($(this), 3, 10);
		});
		$('[id^="tco-lamp-units-slider-brand"]').each(function() {
			tcoPrepareSlider($(this), 1, 4);
		});
		tcoChangeYears();
		
		$('#tco-new-brand > a').click(function(e) {
			e.preventDefault();
			var bar = $(this).parent();
			bar.before(bar.prev().clone());
			tcoBrandNumber(bar.prev());
		});
		
		$('#tco-details > a').live('click', function(e){
			e.preventDefault();
			var table = $(this).parent().next('table'),
				trigger = $(this);
	        if (table.is(':visible')) {
    			trigger.text(phrases.showdetails);
	        } else {
	        	trigger.text(phrases.hidedetails);
	        }
	        table.toggle();
	        trigger.prevAll('.icon').toggle();
		});
		
		$('#tco').find('.icon.close').live('click', function(){
			$(this).closest('fieldset').nextAll('fieldset').each(function(i) {
				tcoBrandNumber($(this), true);
			});
			$(this).closest('fieldset').remove();
			tcoChange();
		});
		
		$('#tco > header').find('a').click(function(e) {
			e.preventDefault();
	        $(this).closest('.frame').children('.frame-content').toggle();
	        $(this).find('.icon').toggle();
		});
		
		var tcoForm = $('#tco').find('form');

		tcoForm.validate({
			submitHandler: function(form) {
				tcoSubmit($(form));
			},
			errorPlacement: function(label, element) {
				if (element.prev('label').length) {
					label.insertBefore(element.prev('label'));
				} else {
					label.insertBefore(element);
				}
			}
		});
		
	    tcoForm.append('<div id="tco-result-dialog"/>');
	    $('#tco-result-dialog').dialog({
	    	width: 620,
	    	dialogClass: 'dialog-shadow two-columns tco-result-dialog',
			resizable: false,
			autoOpen: false,
			buttons: [
				{
					text: phrases.close,
					click: function() {
						$(this).dialog('close');
					}
				}
			],
			open: function(event, ui) {
				tcoChange();
			}
		});
		
		$('#tco').find('input').live('change', tcoChange);
		function bindSlidechange() {
	    	$('#tco').find('.slider').not('#tco-years-slider').bind('slidechange', tcoChange);
    	}
    	bindSlidechange();
	}
	
	function tcoPrepareSlider(slider, value, max) {
		var input = slider.next();
		var label = slider.prev().children('span');
		slider.slider({
			range: 'min',
			value: value,
			step: 1,
			min: 1,
			max: max,
			slide: function(e, ui){
				input.val(ui.value);
				label.text(ui.value);
			}
		});
		input.val(slider.slider('value'));
		label.text(slider.slider('value'));
	}
	
	function tcoChange() {
		if ($('#tco-result').length && $('#tco-result-dialog').is(':visible')) tcoForm.submit();
	}
	
	function tcoSubmit(form) {
    	var dialog = $('#tco-result-dialog');
    	if ($('#tco-details-table').is(':visible')) {
    		form.find('input[name="show-details"]').val('true');
		} else {
			form.find('input[name="show-details"]').val('false');
		}
		$.post(form.attr('action'), form.serialize(), function(data){
			if ($('#tco-result').length) {
				$('#tco-result').replaceWith(data);
			} else {
				dialog.html(data);
				dialog.dialog('option', 'title', form.find('input[name="dialog-title"]').val());
	    	}
	    	dialog.dialog('open');
    	});
	}
	
	function tcoBrandNumber(brand, remove) {
		var legend = brand.children('legend');
		var legendText = legend.text();
		var newBrandNo = Number(legendText.charAt(legendText.length - 1));
		if (remove) {
			newBrandNo -= 1;
		} else {
			newBrandNo += 1;
		}
		legend.text(legendText.substring(0, legendText.length - 1) + newBrandNo).prepend('<div class="icon close"/>');
		brand.html(brand.html().replace(/-brand\d+/g, '-brand' + newBrandNo));
		if (!remove) {
			brand.find('.slider').empty();
			tcoPrepareSlider($('[id='+"tco-units-slider-brand"+newBrandNo+']'), 3, 10);
			tcoPrepareSlider($('[id='+"tco-lamp-units-slider-brand"+newBrandNo+']'), 1, 4);
			bindSlidechange();
			tcoChange();
		}
	}
	
	function tcoChangeYears() {
		var numYears = $('#tco-years').val();
		$('[id^="tco-service-y1"]').each(function() {
			$(this).nextAll('input').each(function(i) {
				if (i + 2 > numYears) $(this).prev('label').andSelf().remove();
			});
			for (i = $(this).nextAll('input').length + 2; i <= numYears; i++) {
				$(this).parent().append($(this).prev().andSelf().clone());
				var newId = $(this).attr('id').replace('-y1-', '-y' + i + '-');
				$(this).parent().children('input:last').attr('id', newId).attr('name', newId);
				var newLabel = $(this).parent().children('label:last');
				var newLabelText = phrases.Year + ' ' + i;
				newLabel.attr('for', newId).text(newLabelText);
			}
		});
		tcoChange();
		setRequired();
	}
    
    // Product selector
	function productSelector() {
    	var psq = '';
		$('#product-selector').find('input[name = "application"]:visible').each(function() {
			if (this.checked) {
				if (!psq.length) {
					psq += 'ap:';
				} else {
					psq += ';'
				}
				psq += $(this).attr('value');
			}
		});
		var rp = $('#ps-rear-projection:checked').attr('value');
		if (rp) {
			if (psq.length) psq += '|';
			psq += 'rp:' + rp;
		}
    	var iw = $('#ps-image-width').attr('value');
    	if (iw) {
			if (psq.length) psq += '|';
			psq += 'iw:' + iw;
    	}
    	var di = $('#ps-distance').attr('value');
    	if (di) {
			if (psq.length) psq += '|';
			psq += 'di:' + di;
    	}
    	if ($('#brightness-slider:visible').length){
	    	var br = $('#ps-brightness:visible').text();
	    	if (br) {
				if (psq.length) psq += '|';
				psq += 'br:' + br;
	    	}
	    }
		var re = $('#product-selector').find('input[name = "ps-resolution"]:checked:visible').attr('value');
		if (re) {
			if (psq.length) psq += '|';
			psq += 're:' + re;
		}
		var le = $('#ps-lens-replaceable:checked:visible').attr('value');
		if (le) {
			if (psq.length) psq += '|';
			psq += 'le:' + le;
		}
		var pm = $('#ps-portrait-mode:checked:visible').attr('value');
		if (pm) {
			if (psq.length) psq += '|';
			psq += 'pm:' + pm;
		}
		//var cc = $('#ps-correct-colour:checked').attr('value');
		//if (cc) {
		//	if (psq.length) psq += '|';
		//	psq += 'cc:' + cc;
		//}
		var lc = $('[name="ps-light-conditions"]:checked').attr('value');
		if (lc) {
			if (psq.length) psq += '|';
			psq += 'lc:' + lc;
		}
		if ($('#weight-slider:visible').length){
	    	var wmi = $('#ps-weight-min').attr('value');
	    	if (wmi) {
				if (psq.length) psq += '|';
				psq += 'wmi:' + wmi;
	    	}
	    	var wma = $('#ps-weight-max').attr('value');
	    	if (wma) {
				if (psq.length) psq += '|';
				psq += 'wma:' + wma;
	    	}
	    }
		$.get($('#product-selector').find('form').attr('action'), {'psq': psq}, function(data){
	    	$('.list').stop(true, true).hide().replaceWith(data).fadeIn();
		});
	}
	
	if ($('#product-selector').find('form').length) {
	    $('#image-width-slider').slider({
			range: 'min',
			value: 2,
			step: 0.1,
			min: psValues.iwMin,
			max: psValues.iwMax,
			slide: function(e, ui){
				$('#ps-image-width').val(ui.value);
				$('#ps-image-width-show').text(convert(ui.value, 'm'));
			}
		});
		
	    $('#distance-slider').slider({
			range: 'min',
			value: 3.6,
			step: 0.1,
			min: 0.1,
			max: 50,
			slide: function(e, ui){
				$('#ps-distance').val(ui.value);
				$('#ps-distance-show').text(convert(ui.value, 'm'));
			}
		});
		
	    $('#brightness-slider').slider({
			range: 'min',
			value: 2000,
			step: 100,
			min: psValues.brMin,
			max: psValues.brMax,
			slide: function(e, ui){
				$('#ps-brightness').text(ui.value);
			}
		});
		$('#ps-brightness').text($('#brightness-slider').slider('value'));
		
		$('#weight-slider').slider({
			range: true,
			min: psValues.weMin,
			max: psValues.weMax,
			step: 0.1,
			values: [psValues.weMin, psValues.weMax],
			slide: function(e, ui) {
				$('#ps-weight-min').val(ui.values[0]);
				$('#ps-weight-min-show').text(convert(ui.values[0], 'kg'));
				$('#ps-weight-max').val(ui.values[1]);
				$('#ps-weight-max-show').text(convert(ui.values[1], 'kg'));
			}
		});

		convertValues();		
		productSelector();
		
		$('#measurements > a').click(function(e) {
			e.preventDefault();
			$(this).siblings().removeClass('active');
			if (!$(this).hasClass('active')) $(this).addClass('active');
			measurements = $(this).attr('id').split('-')[1];
			convertValues();
		});
	}
	
    $('#product-selector').find('form').submit(function(e) {
    	e.preventDefault();
    }).find('input').change(productSelector);
    
    $('#product-selector').find('.slider').bind('slidechange', productSelector);
    
    function convertValues(){
		$('#ps-image-width').val($('#image-width-slider').slider('value'));
		$('#ps-image-width-show').text(convert($('#image-width-slider').slider('value'), 'm'));
		$('#ps-distance').val($('#distance-slider').slider('value'));
		$('#ps-distance-show').text(convert($('#distance-slider').slider('value'), 'm'));
		$('#ps-weight-min').val($('#weight-slider').slider('values', 0));
		$('#ps-weight-min-show').text(convert($('#weight-slider').slider('values', 0), 'kg'));
		$('#ps-weight-max').val($('#weight-slider').slider('values', 1));
		$('#ps-weight-max-show').text(convert($('#weight-slider').slider('values', 1), 'kg'));
    }
    
    function convert(num, from){
    	var result;
    	if (from == 'm'){
    		if (measurements == 'imperial'){
    			result = Math.floor(num * 3.2808399) + ' ft ' + Math.floor(((num * 3.2808399) - Math.floor(num * 3.2808399)) * 12) + ' in';
    			$('#image-width-slider').slider('option', 'step', 0.001);
    		} else {
    			result = (Math.round(num * 10) / 10) + ' m';
    			$('#image-width-slider').slider('option', 'step', 0.1);
    		}
    	} else if (from == 'kg'){
    		if (measurements == 'imperial'){
    			result = Math.floor(num * 2.20462262) + ' lb ' + Math.floor(((num * 2.20462262) - Math.floor(num * 2.20462262)) * 16) + ' oz';
    			$('#weight-slider').slider('option', 'step', 0.001);
    		} else {
    			result = (Math.round(num * 10) / 10) + ' kg';
    			$('#weight-slider').slider('option', 'step', 0.1);
    		}
    	}
    	return result;
    }
    
    // Tabs
    $('[id^="tab-"]').click(function(e) {
		e.preventDefault();
		$(this).siblings().removeClass('active');
		if (!$(this).hasClass('active')){
			$(this).addClass('active');
			$('[id^="tabcontent-"]').hide();
			$('[id='+"tabcontent-"+$(this).attr("id").substring($(this).attr("id").indexOf("-")+1)+']').show();
			if ($('#product-selector').find('form').length) productSelector();
		}
	});
	
	// Case study list facet
	$('#case-study-list > .facet').find('a').live('click', function(e) {
		e.preventDefault();
		$(this).prevAll('.text').val('');
		$(this).closest('.content').find('.checkbox:checked').attr('checked', false);
		facetFormChange();
	});
	
	$('#case-study-list > .facet').find('.checkbox').live('change', facetFormChange);
	
	$('#case-study-list > .facet').live('submit', function(e) {
		e.preventDefault();
		facetFormChange();
	});
	
	function facetFormChange() {
		var asyncUrl = $('#case-study-list > .async-url').text() + getFilters($('#case-study-list > .facet'));
		$.get(asyncUrl, function(data){
			$('#case-study-list').html(data);
			$('#case-study-list > .facet').find('ul').each(function() {
				if (!$(this).children('li:visible').length) $(this).children('li:hidden').show();
			});
        });
	}
	
	function getFilters(facet) {
		var filters = '',
			textBox = facet.find('.text');
		facet.find('.checkbox:checked').each(function() {
			if (!filters.length) {
				filters += '&sectionIds=';
			} else {
				filters += ',';
			}
			filters += $(this).val();
		});
		if (textBox.val() != '') filters += '&' + textBox.attr('id') + '=' + textBox.val();
		if (filters.length) {
			filters += '&acs=0';
			return filters;
		} else {
			return '&fv=1';
		}
	}
	
	// Get items with ajax
	$('.items-async').live('click', function(e){
		e.preventDefault();
		var selItem = $(this),
			asyncUrl = selItem.attr('href');
		if (selItem.children('.async-url').length) {
			asyncUrl = selItem.children('.async-url').text();
		}
		selItem.prevAll('.icon').toggleClass('hidden');
		$.get(asyncUrl, function(data){
			selItem.prevAll('.icon').toggleClass('hidden');
			if (selItem.hasClass('tags') || selItem.hasClass('simple')) {
				$(data).each(function() {
					if ($(this).hasClass('item')) {
						if (selItem.hasClass('tags')) {
							var cty = $(this).attr('class').split(' ')[0].split('tag-item-')[1];
							$('[id='+"tag-list-"+cty+']').show().append(this);
						} else {
							selItem.parent().prev().append(this);
						}
					} else if ($(this).hasClass('bar') || $(this).hasClass('repeat-unfiltered')) {
						selItem.parent().before(this);
					}
				});
			} else {
				selItem.parent().before(data);
			}
            selItem.parent().detach();
        });
    });
    
    // Comments
    $('#comments > div > .item').hover(function() {
    	if (!$(this).find('.overlay:visible').length) $(this).find('header').toggleClass('show-tools');
    });
    
    $('.byline > .comment-header > a.add-comment').click(function(e) {
    	e.preventDefault();
    	$('#olt_comment > a.add-comment').trigger('click');
    });
    
    $('#comments-form-dialog, #quote-form-dialog').dialog({
		modal: true,
		resizable: false,
		autoOpen: false
	});
    
    $('#olt_comment > a.add-comment, [id*="olt_comment_"] > a').click(function(e) {
    	e.preventDefault();
    	var trigger = $(this).parent();
    	var dialog = $('#comments-form-dialog');
    	if (trigger.attr('id') != 'olt_comment') {
	    	var commentId = trigger.attr('id').split('olt_comment_')[1];
	    	$('#comments-form').find('[name="comment-replied-to"]').val(commentId);
	    	$('#comments-form').find('textarea').text('[quote=' + trigger.closest('.comment').prev().children('img').attr('title') + ']\n' + normalizeSpace(trigger.closest('header').next().children('.hidden').text()) + '\n[/quote]\n');
	    	$('#comments-form').find('textarea').prev().addClass('off-left');
    	}
        dialog.dialog('option', 'title', dialog.find('input[name="dialog-title"]').val()).dialog('open');
    });
    
    $('#comments-form-dialog').find('.button.cancel').live('click', function(e) {
    	e.preventDefault();
    	$('#comments-form-dialog').dialog('close');
    });
    
	function handleCommentSubmit() {
		$('#comments-form').validate({
			errorPlacement: function(label, element) {
				if (element.parent('p').length) {
					label.insertBefore(element.parent('p'));
				} else if (element.prev('label').length) {
					label.insertBefore(element.prev('label'));
				} else {
					label.insertBefore(element);
				}
			},
			submitHandler: function(form) {
		    	var dialog = $('#comments-form-dialog');
		    	concatName($(form));
		    	$.post($(form).attr('action'), $(form).serialize(), function(data){
		    		if ($(data).find('.error').length) {
						$(form).replaceWith($(data).find('#comments-form'));
						handleCommentSubmit();
						if ($(form).hasClass('labels-inside')) prepareInsideLabels();
						tooltip($('#comments-form').find('.tooltip'));
						reloadCaptcha($('#comments-form').find('img.captcha-image'));
					} else {
						dialog.find('.button').hide().nextAll('img.hidden').show();
						$.post($(data).find('#comments-form-dialog > span.hidden').text(), $(form).serialize(), function(){
							$('#comments-form').find('input, textarea').val('');
							window.location.href = window.location.href.split('#')[0] + '#comments';
							dialog.dialog('close');
							window.location.reload();
							window.location.href = window.location.href;
						});
					}
		    	});
	    	}
    	});
	}
	
	handleCommentSubmit();
	
	// Quote
    $('#olt_quote').click(function(e) {
    	e.preventDefault();
    	var dialog = $('#quote-form-dialog');
        dialog.dialog('option', 'title', dialog.find('input[name="dialog-title"]').val()).dialog('open');
    });
    
    $('#quote-form-dialog').find('.button.cancel').live('click', function(e) {
    	e.preventDefault();
    	$('#quote-form-dialog').dialog('close');
    });
    
    $('.product-series').find('.button.buy').click(function(e) {
    	e.preventDefault();
		$(this).prev('form').submit();
    });
    
    // Picker
    $('.picker').find('li.add > a').click(function(e) {
    	e.preventDefault();
    	var trigger = $(this);
    	var triggerClass = '';
    	$.get(trigger.prev('span.hidden').text(), function(data){
    		$('.picker-list').detach();
    		trigger.closest('form').append('<div class="picker-list" id="' + trigger.attr('class') + '"/>');
    		$('.picker-list').html(data);
    		trigger.parent().prevAll('li').each(function() {
				triggerClass = $(this).children('a').attr('class');
				$('.picker-list').find('a[class='+triggerClass+']').parent().addClass('selected');
    		});
			tooltip($('.picker-list').find('.tooltip'));
		    $('.picker-list').dialog({
				modal: true,
				resizable: false,
				title: trigger.text(),
	    		buttons: [
					{
						text: phrases.ok,
						click: function() {
							$(this).dialog('close');
						}
					}
				]
			});
    	});
    });
    
    $('.picker').find('.remove').live('click', function(e) {
    	e.preventDefault();
    	$(this).parent().detach();
    });
    
    $('.picker-list > ul > li > a, .picker-list > ul > li > img').live('click', function(e) {
    	e.preventDefault();
    	var trigger = $(this);
    	openerClass = $('.picker-list').attr('id');
    	openerLI = $('a[class='+openerClass+']').parent();
    	if (trigger.parent().hasClass('selected')) {
    		if (trigger.is('img')) trigger = trigger.next();
    		var triggerClass = trigger.attr('class');
    		$('.picker').find('a[class='+triggerClass+']').parent().detach();
    	} else {
    		openerLI.before('<li></li>');
    		trigger.parent().children('a').clone().appendTo(openerLI.prev());
    		trigger.parent().children('.remove').clone().appendTo(openerLI.prev());
    	}
    	trigger.parent().toggleClass('selected');
    });
    
    // Media archive
    $('.gallery').find('a.shadow').click(function(e) {
    	e.preventDefault();
    	$(this).siblings('a.shadow.active').andSelf().toggleClass('active');
    	var id = 'img-' + $(this).attr('id').split('thumb-')[1];
    	var ai = $('[id='+id+']');
    	var pai;
    	if (ai.siblings('div.image:visible').length) {
    		pai = ai.siblings('div.image:visible');
    	} else {
    		pai = ai.siblings('a:visible');
    	}
    	pai.css('position', 'absolute').stop(true, true).fadeOut();
    	ai.children('.download').hide();
    	ai.css('position', 'relative').stop(true, true).fadeIn();
    });
    
    $('.main-image > .zoom').click(function() {
    	$(this).siblings('div:visible').children('a').not('.hidden').trigger('click');
    });
    
    $('.main-image > div > a').not('.download', '.video').click(function(e) {
    	$(this).parent().siblings('.zoom').add($(this).siblings('.download')).hide();
    });
    
    $('.main-image').hover(function(){
    	if (!$(this).children('.video:visible').length) $(this).children('.zoom').add($(this).children('div:visible').children('.download')).stop(true, true).fadeIn();
	}, function() {
		if (!$(this).children('.video:visible').length) $(this).children('.zoom').add($(this).children('div:visible').children('.download')).stop(true, true).delay(500).fadeOut();
    });
    
    // Captcha
    $('a.captcha').live('click', function(e) {
    	e.preventDefault();
		reloadCaptcha($(this).prev('img'));
    });
    
    function reloadCaptcha(img) {
    	var newSrc = img.attr('src').split('?')[0] + '?' + (new Date()).getTime();
    	img.attr('src', newSrc);    
    }
    
    // Terms
    $('.terms').click(function(e) {
		e.preventDefault();
    	$('#event-registration-terms-dialog').dialog({
    		modal: true,
    		resizable: false,
    		buttons: [
				{
					text: phrases.ok,
					click: function() {
						$(this).dialog('close');
					}
				}
			]
		});
	});
    
    // Event
    $('.event.mini-list > li > strong > a, .event.list > .item > h3 > a').live('mouseover mouseout', function() {
    	$(this).parent().prev().children('a').toggleClass('active');
    });
    
    var allSubEvents = '';
    $('#event-registration-form').find('.required-group').each(function() {
    	if (allSubEvents.length > 0) allSubEvents += ' ';
    	allSubEvents += $(this).attr('name');
    });
    
	function handleEventSubmit() {
		$('#event-registration-form').validate({
			groups: {
				subevents: allSubEvents
			},
			errorPlacement: function(label, element) {
				if (element.hasClass('required-group')) {
					label.insertBefore(element.closest('.list').children('div:first'));
				} else if (element.prev('label').length) {
					label.insertBefore(element.prev('label'));
				} else {
					label.insertBefore(element);
				}
			},
			submitHandler: function(form) {
		    	$('#event-registration-dialog').dialog({
		    		modal: true,
		    		resizable: false,
		    		autoOpen: false,
		    		buttons: [
						{
							text: phrases.ok,
							click: function() {
								$(this).dialog('close');
							}
						}
					]
				});
		    	var subEvents = '';
		    	$(form).find('input[name^="sub-event-contents"]:checked').each(function() {
		    		subEvents += $(this).closest('.item').children('.when').children('span').text() + ': ';
	    			subEvents += $(this).closest('.item').children('.event').find('.event-title').text();
		    		subEvents += '\n';
		    	});
		    	$(form).find('input[name="sub-events"]').val(subEvents);
		    	$(form).find('input[name="from_email"]').val($(form).find('input[name="email"]').val());
		    	$(form).find('input[name="from_name"]').val($(form).find('input[name="first-name"]').val() + ' ' + $(form).find('input[name="last-name"]').val());
		    	var response = '';
		    	// Create registration, send mail to event owner
				$.post($(form).attr('action'), $(form).serialize(), function(data){
					// Registration fail
					if ($(data).find('.error').length) {
						response += $(data).find('.error').html();
						$('#event-registration-dialog').html(response);
						$('#event-registration-dialog').bind('dialogclose', function() {
							$(form).replaceWith($(data).find('#event-registration-form'));
							handleEventSubmit();
							reloadCaptcha($('#event-registration-form').find('img.captcha-image'));
						});
						$('#event-registration-dialog').dialog('open');
					// Registration success
					} else {
						$('#event-registration-dialog').dialog('option', 'buttons', {}).dialog('option','closeOnEscape', false);
						// Send user receipt for academy
						if ($(form).find('input[name="sendmail-send"]').length) {
							var academyreceiptbody = $(form).find('input[name="academy-receipt-body"]').val().replace('%name%', $(form).find('input[name="from_name"]').val()).replace('%events%', $(form).find('input[name="event"]').val() + '\n\n' + $(form).find('input[name="sub-events"]').val());
							var receiptFormData = 'to=' + $(form).find('input[name="email"]').val() + '&from_email=' + $(form).find('input[name="to"]').val() + '&from_name=' + $(form).find('input[name="academy-from-name"]').val() + '&subject=' + $(form).find('input[name="academy-receipt-subject"]').val() + '&sort_order=' + phrases.registration + '&' + phrases.registration + '=' + encodeURIComponent(academyreceiptbody);
							$.post($(form).find('input[name="sendmail-send"]').val(), receiptFormData);
						}
						//response += data;
						response += $(data).find('.event-registration-sent').html();
						// Register user, send mail to webmaster and user
				    	if ($(form).find('#event-registration-register').is(':checked')) {
				    		var formData = '';
					    	$(form).find('input[name="from_email"]').val($(form).find('input[name="webmaster_from_email"]').val());
					    	$(form).find('input[name="from_name"]').val($(form).find('input[name="webmaster_from_name"]').val());
				    		var formArray = $(form).serialize().split('&');
				    		for (i = 0; i < formArray.length; i++) {
				    			if (formArray[i].split('=')[0] != '_captcha_response') formData += '&' + formArray[i];
				    		}
							$.post($(form).find('input[name="user-create"]').val(), formData, function(data){
								if ($(data).find('.error').length) {
									response += $(data).find('.error').html();
								} else {
									//response += data;
									response += $(data).find('.user-account-created').html();
								}
								$('#event-registration-dialog').html(response);
								$('#event-registration-dialog').bind('dialogclose', function() {
									window.location.href = window.location.href;
								});
								$('#event-registration-dialog').dialog('open');
							});
				    	} else {
							$('#event-registration-dialog').html(response);
							$('#event-registration-dialog').bind('dialogclose', function() {
								window.location.href = window.location.href;
							});
							$('#event-registration-dialog').dialog('open');
						}
						$.post($(data).find('.event-registration-sent > span.hidden').text(), $(form).serialize(), function(){
							$('#event-registration-dialog').dialog('option','closeOnEscape', true);
							$('#event-registration-dialog').dialog('option', 'buttons', [
								{
									text: phrases.ok,
									click: function() {
										$(this).dialog('close');
									}
								}
							]);
						});
					}
				});
			}
		});
	}
	
	handleEventSubmit();
    
    // Subscriptions NEW
    $('#xsubscriptions-form').validate({
		submitHandler: function(form) {
			//if ($.cookie('cuvid') != null) $(form).children('input[name="cd_visitorkey"]').val($.cookie('cuvid'));
			$.post($(form).attr('action'), $(form).serialize(), function(data){
				if (data == 'success') {
					alert('success');
				} else {
					alert('error');
				}
	    	});
		}
	});
    
    // Subscriptions
    var apsisForm = $('form[name="SubscriberForm"]');
    var newsletters = '';
    apsisForm.find('.required-group').each(function() {
       if (newsletters.length > 0) newsletters += ' ';
       newsletters += $(this).attr('name');
    });
    apsisForm.validate({
	   groups: {
          newsletters: newsletters
       },
       errorPlacement: function(label, element) {
          if (element.prev('label').length) {
             label.insertBefore(element.prev('label'));
          } else {
             label.insertBefore(element);
          }
       }
    });
    
    // Validation
    $('form:not(.dont-validate)').each(function() {
		$(this).validate({
			ignoreTitle: true,
			errorPlacement: function(label, element) {
				if (element.parent('p').length) {
					label.insertBefore(element.parent('p'));
				} else if (element.prev('label').length) {
					label.insertBefore(element.prev('label'));
				} else {
					label.insertBefore(element);
				}
			}
		});
	});
});

function normalizeSpace(str) {
	return str.replace(/^\s*| (?= )|\s*$/g, "");
}

$.fn.equalHeight = function(){
	return this.height( Math.max.apply(this, $.map( this , function(e){ return $(e).height() }) ) );
}

// Preferences
function setPreference(action, key, value){
	$.post(action, key + '=' + value);
}

$(window).load(function(){
    // Equal height
    $('.equal-height').equalHeight();   
});