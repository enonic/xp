(function ($) {
    'use strict';

    var paragraphs = AdminLiveEdit.model.component.Paragraph = function () {
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

        $(window).on('shader:click', $.proxy(this.destroyEditMode, this));
        $(window).on('component:click:deselect', $.proxy(this.destroyEditMode, this));
    };

    // Override base attachClickEvent
    proto.attachClickEvent = function () {
        var me = this;

        $(document).on('click touchstart', me.cssSelector, function (event) {
            me.handleClick(event);
        });

    };


    proto.handleClick = function (event) {
        var me = this;
        event.stopPropagation();
        event.preventDefault();

        var $paragraph = $(event.currentTarget);

        // In case another paragraph is clicked during any mode.
        if (!$paragraph.is(me.$selectedParagraph)) {
            me.mode = undefined;
        }

        me.$selectedParagraph = $paragraph;

        if (me.mode === undefined) {
            me.setSelectMode();
        } else if (me.mode === 'selected') {
            me.initEditMode();

        } else {
        }

        console.log('mode after: "' + me.mode + '"');
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
        $paragraph.addClass('live-edit-edited-paragraph');
        $paragraph.get(0).focus();

        me.mode = 'edit';
    };


    proto.destroyEditMode = function (event) {
        console.log('Paragraph: destroy');
        var me = this,
            $paragraph = me.$selectedParagraph;
        if ($paragraph === null) {
            return;
        }

        me.mode = undefined;

        $paragraph.get(0).contentEditable = false;
        $paragraph.css('cursor', '');
        $paragraph.removeClass('live-edit-edited-paragraph');
        $paragraph.get(0).blur();
        me.$selectedParagraph = null;

        $(window).trigger('component:paragraph:edit:destroy', [me.$selectedParagraph]);
    };

}($liveedit));