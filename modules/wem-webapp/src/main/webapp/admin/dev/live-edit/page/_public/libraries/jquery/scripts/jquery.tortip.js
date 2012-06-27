/*
 * torTip jQuery Plugin
 * Tor Løkken
 * http://www.enonic.com
 */

(function ($) {
    $.fn.torTip = function (options) {
        var opts = $.extend({}, $.fn.torTip.defaults, options);

        return this.each(function () {
            //var  = $(this);
            $(this).hover(
                function () {
                    $.fn.torTip.show($(this));
                },
                function () {
                    $.fn.torTip.hide();
                }
            );
        });
    };

    $.fn.torTip.show = function (parent) {
        var tipContainer = $(document.createElement('div')).addClass('tortip-container');
        tipContainer.css('left', parent.offset().left);
        tipContainer.css('top', parent.offset().top + 30);
        tipContainer.text(parent.attr('title'));
        $('body').append(tipContainer);
    };

    $.fn.torTip.hide = function () {
        $('.tortip-container').remove();
    };

    $.fn.torTip.defaults = {
        effect: 'none',
        play: false,
        slideDuration: 6000,
        transitionTime: 2000
    };

})(jQuery);