(function ($) {
    'use strict';

    var paragraphs = AdminLiveEdit.model.component.Paragraph = function () {
        var me = this;
        this.cssSelector = '[data-live-edit-type=paragraph]';
        this.$selectedParagraph = null;

        this.modes = {
            UNSELECTED: 0,
            SELECTED: 1,
            EDIT: 2
        };

        this.currentMode = me.modes.UNSELECTED;

        this.attachMouseOverEvent();
        this.attachMouseOutEvent();
        this.attachClickEvent();
        this.attachContextClickEvent();

        this.registerGlobalListeners();
    };
    // Inherit from Base prototype
    paragraphs.prototype = new AdminLiveEdit.model.component.Base();

    // Fix constructor as it now is Base
    paragraphs.constructor = paragraphs;

    var proto = paragraphs.prototype;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *


    proto.registerGlobalListeners = function () {
        $(window).on('shader:click', $.proxy(this.leaveEditMode, this));
        $(window).on('component:click:deselect', $.proxy(this.leaveEditMode, this));
    };


    // Override base attachClickEvent
    proto.attachClickEvent = function () {
        var me = this;

        $(document).on('click contextmenu touchstart', me.cssSelector, function (event) {
            me.handleClick(event);
        });

    };


    proto.handleClick = function (event) {
        var me = this;
        event.stopPropagation();
        event.preventDefault();


        // Remove the inlined css cursor when the mode is not EDIT.
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
            me.setEditMode();
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


    proto.setEditMode = function () {
        var me = this,
            $paragraph = me.$selectedParagraph;

        $(window).trigger('component:paragraph:edit:init', [me.$selectedParagraph]);

        $paragraph.css('cursor', 'text');
        $paragraph.addClass('live-edit-edited-paragraph');

        me.currentMode = me.modes.EDIT;
    };


    proto.leaveEditMode = function (event) {
        var me = this,
            $paragraph = me.$selectedParagraph;
        if ($paragraph === null) {
            return;
        }
        $(window).trigger('component:paragraph:edit:destroy', [me.$selectedParagraph]);

        $paragraph.css('cursor', '');
        $paragraph.removeClass('live-edit-edited-paragraph');
        me.$selectedParagraph = null;

        me.currentMode = me.modes.UNSELECTED;
    };

}($liveedit));