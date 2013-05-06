AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.htmleditor');

(function ($) {
    'use strict';

    // Class definition (constructor function)
    var toolbar = AdminLiveEdit.view.htmleditor.Toolbar = function () {
        var me = this;
        me.$selectedComponent = null;

        me.addView();
        me.addEvents();
        me.registerGlobalListeners();
    };

    // Inherits ui.Base
    toolbar.prototype = new AdminLiveEdit.view.Base();

    // Fix constructor as it now is Base
    // toolbar.constructor = toolbar;

    // Shorthand ref to the prototype
    var proto = toolbar.prototype;

    // Uses
    var componentHelper = liveedit.ComponentHelper;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *


    proto.registerGlobalListeners = function () {
        $(window).on('component.onParagraphEdit', $.proxy(this.show, this));
        $(window).on('component.onParagraphEditLeave', $.proxy(this.hide, this));
        $(window).on('component.onRemove', $.proxy(this.hide, this));
        $(window).on('component.onSortStart', $.proxy(this.hide, this));
    };


    proto.addView = function () {
        var me = this;

        var html = '<div class="live-edit-editor-toolbar live-edit-arrow-bottom" style="display: none">' +
                   '    <button data-tag="paste" class="live-edit-editor-button"></button>' +
                   '    <button data-tag="insertUnorderedList" class="live-edit-editor-button"></button>' +
                   '    <button data-tag="insertOrderedList" class="live-edit-editor-button"></button>' +
                   '    <button data-tag="link" class="live-edit-editor-button"></button>' +
                   '    <button data-tag="cut" class="live-edit-editor-button"></button>' +
                   '    <button data-tag="strikeThrough" class="live-edit-editor-button"></button>' +
                   '    <button data-tag="bold" class="live-edit-editor-button"></button>' +
                   '    <button data-tag="underline" class="live-edit-editor-button"></button>' +
                   '    <button data-tag="italic" class="live-edit-editor-button"></button>' +
                   '    <button data-tag="superscript" class="live-edit-editor-button"></button>' +
                   '    <button data-tag="subscript" class="live-edit-editor-button"></button>' +
                   '    <button data-tag="justifyLeft" class="live-edit-editor-button"></button>' +
                   '    <button data-tag="justifyCenter" class="live-edit-editor-button"></button>' +
                   '    <button data-tag="justifyRight" class="live-edit-editor-button"></button>' +
                   '    <button data-tag="justifyFull" class="live-edit-editor-button"></button>' +
                   '</div>';

        me.createElement(html);
        me.appendTo($('body'));
    };


    proto.addEvents = function () {
        var me = this;
        me.getEl().on('click', function (event) {

            // Make sure component is not deselected when the toolbar is clicked.
            event.stopPropagation();

            // Simple editor command implementation ;)
            var tag = event.target.getAttribute('data-tag');
            if (tag) {
                $(window).trigger('editorToolbar.onButtonClick', [tag]);
            }
        });

        $(window).scroll(function () {
            if (me.$selectedComponent) {
                me.updatePosition();
            }
        });
    };


    proto.show = function (event, $component) {
        var me = this;
        me.$selectedComponent = $component;

        me.getEl().show();
        me.toggleArrowPosition(false);
        me.updatePosition();
    };


    proto.hide = function () {
        var me = this;
        me.$selectedComponent = null;
        me.getEl().hide();
    };


    proto.updatePosition = function () {
        var me = this;
        if (!me.$selectedComponent) {
            return;
        }

        var defaultPosition = me.getDefaultPosition();

        var stick = $(window).scrollTop() >= me.$selectedComponent.offset().top - 60;

        if (stick) {
            me.getEl().css({
                position: 'fixed',
                top: 10,
                left: defaultPosition.left
            });
        } else {
            me.getEl().css({
                position: 'absolute',
                top: defaultPosition.top,
                left: defaultPosition.left
            });
        }

        var placeArrowOnTop = $(window).scrollTop() >= defaultPosition.bottom - 10;
        me.toggleArrowPosition(placeArrowOnTop);
    };


    proto.toggleArrowPosition = function (showArrowAtTop) {
        var me = this;
        if (showArrowAtTop) {
            me.getEl().removeClass('live-edit-arrow-bottom').addClass('live-edit-arrow-top');
        } else {
            me.getEl().removeClass('live-edit-arrow-top').addClass('live-edit-arrow-bottom');
        }
    };


    // Rename
    proto.getDefaultPosition = function () {
        var me = this;
        var componentBox = componentHelper.getBoxModel(me.$selectedComponent),
            leftPos = componentBox.left + (componentBox.width / 2 - me.getEl().outerWidth() / 2),
            topPos = componentBox.top - me.getEl().height() - 25;

        return {
            left: leftPos,
            top: topPos,
            bottom: componentBox.top + componentBox.height
        };
    };

}($liveedit));