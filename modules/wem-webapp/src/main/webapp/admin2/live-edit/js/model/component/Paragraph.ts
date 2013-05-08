module LiveEdit.model {
    var $ = $liveedit;

    export class Paragraph extends LiveEdit.model.Base {

        private selectedParagraph:JQuery = null;
        private modes:any = {};
        private currentMode:number;

        constructor() {
            super();

            var me = this;
            this.cssSelector = '[data-live-edit-type=paragraph]';
            this.modes = {
                UNSELECTED: 0,
                SELECTED: 1,
                EDIT: 2
            };
            this.currentMode = me.modes.UNSELECTED;
            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();
            this.registerGlobalListeners();

            console.log('Paragraph model instantiated. Using jQuery ' + $().jquery);
        }


        registerGlobalListeners() {
            $(window).on('shader.onClick', $.proxy(this.leaveEditMode, this));
            $(window).on('component.onDeselect', $.proxy(this.leaveEditMode, this));
        }


        // Override base attachClickEvent
        attachClickEvent() {
            var me = this;
            $(document).on('click contextmenu touchstart', me.cssSelector, function (event) {
                me.handleClick(event);
            });
        }


        handleClick(event) {
            var me = this;
            event.stopPropagation();
            event.preventDefault();

            // Remove the inlined css cursor when the mode is not EDIT.
            if (me.selectedParagraph && !(me.currentMode === me.modes.EDIT)) {
                me.selectedParagraph.css('cursor', '');
            }

            var $paragraph = $(event.currentTarget);

            // Reset mode in case another paragraph is selected.
            if (!$paragraph.is(me.selectedParagraph)) {
                me.currentMode = me.modes.UNSELECTED;
            }

            me.selectedParagraph = $paragraph;

            if (me.currentMode === me.modes.UNSELECTED) {
                me.setSelectMode(event);
            } else if (me.currentMode === me.modes.SELECTED) {
                me.setEditMode(event);
            } else {
            }

        }


        setSelectMode(event) {
            var me = this;
            me.selectedParagraph.css('cursor', 'url(../../../admin2/live-edit/images/pencil.png) 0 40, text');

            me.currentMode = me.modes.SELECTED;

            // Make sure Chrome does not selects the text on context click
            if (window.getSelection) {
                window.getSelection().removeAllRanges();
            }

            var pagePosition:any = {
                x: event.pageX,
                y: event.pageY
            };

            $(window).trigger('component.onSelect', [me.selectedParagraph, pagePosition]);
            $(window).trigger('component.onParagraphSelect', [me.selectedParagraph]);
        }


        setEditMode(event) {
            var me = this,
                $paragraph = me.selectedParagraph;

            $(window).trigger('component.onParagraphEdit', [me.selectedParagraph]);

            $paragraph.css('cursor', 'text');
            $paragraph.addClass('live-edit-edited-paragraph');

            me.currentMode = me.modes.EDIT;
        }


        leaveEditMode(event) {
            var me = this,
                $paragraph = me.selectedParagraph;
            if ($paragraph === null) {
                return;
            }
            $(window).trigger('component.onParagraphEditLeave', [me.selectedParagraph]);

            $paragraph.css('cursor', '');
            $paragraph.removeClass('live-edit-edited-paragraph');
            me.selectedParagraph = null;

            me.currentMode = me.modes.UNSELECTED;
        }
    }
}

/*
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.model.component');

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

        this.registerGlobalListeners();
    };
    // Inherit from Base prototype
    paragraphs.prototype = new AdminLiveEdit.model.component.Base();

    // Fix constructor as it now is Base
    // paragraphs.constructor = paragraphs;

    var proto = paragraphs.prototype;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *


    proto.registerGlobalListeners = function () {
        $(window).on('shader.onClick', $.proxy(this.leaveEditMode, this));
        $(window).on('component.onDeselect', $.proxy(this.leaveEditMode, this));
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
            me.setSelectMode(event);
        } else if (me.currentMode === me.modes.SELECTED) {
            me.setEditMode(event);
        } else {
        }

    };


    proto.setSelectMode = function (event) {
        var me = this;
        me.$selectedParagraph.css('cursor', 'url(../../../admin2/live-edit/images/pencil.png) 0 40, text');

        me.currentMode = me.modes.SELECTED;

        // Make sure Chrome does not selects the text on context click
        if (window.getSelection) {
            window.getSelection().removeAllRanges();
        }

        var pagePosition = {
            x: event.pageX,
            y: event.pageY
        };

        $(window).trigger('component.onSelect', [me.$selectedParagraph, pagePosition]);
        $(window).trigger('component.onParagraphSelect', [me.$selectedParagraph]);
    };


    proto.setEditMode = function (event) {
        var me = this,
            $paragraph = me.$selectedParagraph;

        $(window).trigger('component.onParagraphEdit', [me.$selectedParagraph]);

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
        $(window).trigger('component.onParagraphEditLeave', [me.$selectedParagraph]);

        $paragraph.css('cursor', '');
        $paragraph.removeClass('live-edit-edited-paragraph');
        me.$selectedParagraph = null;

        me.currentMode = me.modes.UNSELECTED;
    };

}($liveedit));
*/