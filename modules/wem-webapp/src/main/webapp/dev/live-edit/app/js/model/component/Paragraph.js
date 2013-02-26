(function ($) {
    'use strict';

    var paragraphs =  AdminLiveEdit.model.component.Paragraph = function () {
        this.cssSelector = '[data-live-edit-type=paragraph]';
        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();
        this.attachParagraphClickEvent();
    };
    // Inherit from Base prototype
    paragraphs.prototype = new AdminLiveEdit.model.component.Base();

    // Fix constructor as it now is Base
    paragraphs.constructor = paragraphs;

    var proto = paragraphs.prototype;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    // Adding member to the prototype makes all inherit the method. We should use this.
    proto.attachParagraphClickEvent = function () {
        var me = this;

        $(document).on('click', me.cssSelector, function (event) {
            var $paragraph = $(this);

            // test

            if ($paragraph.data('live-edit-paragraph-mode') === undefined) {
                $paragraph.data('live-edit-paragraph-mode', 'select');

                $('body').css('cursor', 'text');

                $paragraph.get(0).contentEditable = false;
            } else if ($paragraph.data('live-edit-paragraph-mode') === 'select') {
                $paragraph.data('live-edit-paragraph-mode', 'edit');

                $('body').css('cursor', 'text');

                $paragraph.get(0).contentEditable = true;
                $paragraph.get(0).focus();
            } else {
                $paragraph.removeData('live-edit-paragraph-mode');
                $paragraph.get(0).contentEditable = false;
            }
        });
    };

}($liveedit));