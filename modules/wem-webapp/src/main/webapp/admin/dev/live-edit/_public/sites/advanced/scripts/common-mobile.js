$(function() {

    positionBullets($('#menu'));
    
    // Mobile menu: Toggle submenus, get submenus with ajax call, follow links            
    $('a.nav').live("click", function(){
        var selItem = $(this);
        if ($(this).hasClass('open') || $(this).hasClass('closed')) {
            $(this).toggleClass('open');
            $(this).toggleClass('closed');
        }
        if ($(this).siblings('ul:first').length) {
            $(this).siblings('ul:first').slideToggle('fast');
        } else {
            var attrSeparator = (selItem.attr('href').split('?')[1]) ? '&' : '?';
            $.ajax({
                type: 'GET',
                url: selItem.attr('href') + attrSeparator + 'page-mode=submenu',
                success: function(response){
                    if (response != '') {
                        selItem.next('a').after($(response).find('div').html());
                        positionBullets($('#menu')); // Burde sendt med innsatt ul som 'element'
                        selItem.next('ul').hide().slideToggle('fast');
                    } else {
                        window.location = selItem.attr('href');
                    }
                },
                error: function() {
                    window.location = selItem.attr('href');
                }
            });
        }
        return false;
    });
    
    // This is required to load the pages inside the web app
    $('a:not(.nav)').click(function (event) {
        if ($(this).attr('rel') != 'external') {
            event.preventDefault();
            window.location = $(this).attr('href');
        }
    });

});

// Mobile menu: Center bullets vertically 
function positionBullets(element) {
    element.find('.bullet.arrow:visible').each(function() {
        $(this).height(Math.ceil($(this).prev('a').height()));
        var newBGPosY = Math.ceil($(this).prev('a').outerHeight() / 2) - 13;
        $(this).css('background-position', $(this).css('background-position').split(' ')[0] + ' ' + newBGPosY + 'px');
    });
}