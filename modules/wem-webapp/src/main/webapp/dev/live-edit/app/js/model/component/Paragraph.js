(function ($) {
    'use strict';

    var paragraphs = AdminLiveEdit.model.component.Paragraph = function () {
        var me = this;
        me.cssSelector = '[data-live-edit-type=paragraph]';
        me.$selectedParagraph = null;

        me.modes = {
            UNSELECTED: 0,
            SELECTED: 1,
            EDIT: 2
        };

        me.currentMode = me.modes.UNSELECTED;

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

        // Remove the unlined cursor when mode is not edit.
        if (me.$selectedParagraph && !(me.currentMode === me.modes.EDIT)) {
            me.$selectedParagraph.css('cursor', '');
        }

        var $paragraph = $(event.currentTarget);

        // Reset mode in case another paragraph is selected.
        if (!$paragraph.is(me.$selectedParagraph)) {
            me.currentMode = me.modes.UNSELECTED;
        }

        me.$selectedParagraph = $paragraph;

        if (me.currentMode === me.modes.UNSELECTED) {
            me.setSelectMode();
        } else if (me.currentMode === me.modes.SELECTED) {
            me.initEditMode();
        } else {
        }
    };


    proto.setSelectMode = function () {
        var me = this;
        me.$selectedParagraph.css('cursor', 'url(../app/images/pencil.png) 0 40, text');

        me.currentMode = me.modes.SELECTED;

        $(window).trigger('component:click:select', [me.$selectedParagraph]);
        $(window).trigger('component:paragraph:select', [me.$selectedParagraph]);
    };


    proto.initEditMode = function () {
        var me = this,
            $paragraph = me.$selectedParagraph;

        $paragraph.get(0).contentEditable = true;
        $paragraph.css('cursor', 'text');
        $paragraph.addClass('live-edit-edited-paragraph');
        $paragraph.get(0).focus();

        me.currentMode = me.modes.EDIT;

        $(window).trigger('component:paragraph:edit:init', [me.$selectedParagraph]);
    };


    proto.destroyEditMode = function (event) {
        var me = this,
            $paragraph = me.$selectedParagraph;
        if ($paragraph === null) {
            return;
        }

        $paragraph.get(0).contentEditable = false;
        $paragraph.css('cursor', '');
        $paragraph.removeClass('live-edit-edited-paragraph');
        $paragraph.get(0).blur();
        me.$selectedParagraph = null;

        me.currentMode = me.modes.UNSELECTED;

        $(window).trigger('component:paragraph:edit:destroy', [me.$selectedParagraph]);
    };

}($liveedit));