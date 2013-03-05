(function ($) {
    'use strict';

    var paragraphs =  AdminLiveEdit.model.component.Paragraph = function () {
        var me = this;
        me.cssSelector = '[data-live-edit-type=paragraph]';
        me.$selectedParagraph = null;
        me.mode = undefined;

        me.attachMouseOverEvent();
        me.attachMouseOutEvent();
        me.attachClickEvent();
        me.registerGlobalListeners();
    };
    // Inherit from Base prototype
    paragraphs.prototype = new AdminLiveEdit.model.component.Base();

    // Fix constructor as it now is Base
    paragraphs.constructor = paragraphs;

    var proto = paragraphs.prototype;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *


    proto.registerGlobalListeners = function () {
        var me = this;

        $(window).on('shader:click', $.proxy(this.closeEditable, this));
        $(window).on('component:click:deselect', $.proxy(this.destroyEditMode, this));
    };

    // Override base attachClickEvent
    proto.attachClickEvent = function () {
        var me = this;

        $(document).on('click touchstart', me.cssSelector, function (event) {
            event.stopPropagation();
            event.preventDefault();

            me.$selectedParagraph = $(event.currentTarget);

            if (me.mode === undefined) {
                me.setSelectMode();
            } else if (me.mode === 'selected') {
                me.initEditMode();

            } else { // Edit
            }

            console.log('mode after: "' + me.mode + '"');
        });

    };


    proto.setSelectMode = function () {
        var me = this;
        me.$selectedParagraph.css('cursor', 'url(../app/images/pencil.png) 0 40, text');

        // Make paragraph behave like other components when selected.
        $(window).trigger('component:click:select', [me.$selectedParagraph]);
        $(window).trigger('component:paragraph:select', [me.$selectedParagraph]);

        me.mode = 'selected';
    };

    proto.initEditMode = function () {
        var me = this,
            $paragraph = me.$selectedParagraph;

        $(window).trigger('component:paragraph:edit:init', [me.$selectedParagraph]);

        $paragraph.get(0).contentEditable = true;
        $paragraph.css('cursor', 'text');
        $paragraph.get(0).focus();

        me.mode = 'edit';
    };


    proto.destroyEditMode = function (event) {
        var me = this,
            $paragraph = me.$selectedParagraph;
        if ($paragraph === null) {
            return;
        }

        me.mode = undefined;

        $paragraph.get(0).contentEditable = false;
        $paragraph.css('cursor', '');
        $paragraph.get(0).blur();
        me.$selectedParagraph = null;

        $(window).trigger('component:paragraph:edit:destroy', [me.$selectedParagraph]);
    };

}($liveedit));