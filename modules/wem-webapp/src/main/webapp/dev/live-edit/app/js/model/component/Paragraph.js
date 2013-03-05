(function ($) {
    'use strict';

    var paragraphs =  AdminLiveEdit.model.component.Paragraph = function () {
        this.cssSelector = '[data-live-edit-type=paragraph]';
        this.$selectedParagraph = null;

        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();
        this.attachParagraphClickEvent();
        this.registerGlobalListeners();
    };
    // Inherit from Base prototype
    paragraphs.prototype = new AdminLiveEdit.model.component.Base();

    // Fix constructor as it now is Base
    paragraphs.constructor = paragraphs;

    var proto = paragraphs.prototype;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.registerGlobalListeners = function () {
        $(window).on('shader:click', $.proxy(this.closeEditable, this));
    };


    // Adding member to the prototype makes all inherit the method. We should use this.
    proto.attachParagraphClickEvent = function () {
        var me = this;

        $(document).on('click', me.cssSelector, function (event) {
            me.$selectedParagraph = $(this);

            var mode = me.$selectedParagraph.data('live-edit-paragraph-mode');

            if (mode === undefined) {
                me.$selectedParagraph.data('live-edit-paragraph-mode', 'select');
                me.$selectedParagraph.css('cursor', 'url(../app/images/pencil.png) 0 40, text');
            } else if (mode === 'select') {
                me.$selectedParagraph.data('live-edit-paragraph-mode', 'edit');
                me.makeSelectedParagraphEditable();
            } else if (mode === 'edit') {
                // Edit
            } else {
                me.closeEditable();
            }
        });
    };


    proto.makeSelectedParagraphEditable = function () {
        var me = this,
            $paragraph = me.$selectedParagraph;

        $(window).trigger('component:paragraph:edit', [me.$selectedParagraph]);

        $paragraph.get(0).contentEditable = true;
        $paragraph.css('cursor', 'text');
        $paragraph.get(0).focus();
    };


    proto.closeEditable = function (event) {
        var me = this,
            $paragraph = me.$selectedParagraph;

        $paragraph.get(0).contentEditable = false;
        $paragraph.css('cursor', '');
        $paragraph.get(0).blur();
        $paragraph.removeData('live-edit-paragraph-mode');

        $(window).trigger('component:paragraph:close', [me.$selectedParagraph]);
        me.$selectedParagraph = null;
    };

}($liveedit));