(function ($) {
    'use strict';

    // Class definition (constructor function)
    var htmlElementReplacer = AdminLiveEdit.HtmlElementReplacer = function () {
        this.elements = ['iframe', 'object'];

        this.replaceElementsWithPlaceholders();
    };

    // Fix constructor
    htmlElementReplacer.constructor = htmlElementReplacer;

    // Shorthand ref to the prototype
    var p = htmlElementReplacer.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.bindGlobalEvents = function () {
    };


    p.replaceElementsWithPlaceholders = function () {
        var me = this;
        me.getElements().each(function () {
            me.replace($(this));
        });
    };


    p.replace = function ($element) {
        this.hideElement($element);
        this.addPlaceholder($element);
    };


    p.addPlaceholder = function ($element) {
        this.createPlaceholder($element).insertAfter($element);
    };


    p.createPlaceholder = function ($element) {
        var me = this;
        var $placeholder = $('<div></div>');
        $placeholder.addClass('live-edit-html-element-placeholder');
        $placeholder.width(me.getElementWidth($element));
        $placeholder.height(me.getElementHeight($element));

        var $icon = $('<div/>');
        $icon.addClass(me.resolveIconCssClass($element));
        $icon.append('<div>' + $element[0].tagName.toLowerCase() + '</div>');
        $placeholder.append($icon);

        return $placeholder;
    };


    p.getElements = function () {
        return $('[data-live-edit-type=window] > ' + this.elements.toString());
    };


    p.getElementWidth = function ($element) {
        var attrWidth = $element.attr('width');
        if (!attrWidth) {
            // Return computed style width (int/pixels);
            // -2 for placeholder border
            return $element.width() - 2;
        }
        return attrWidth;
    };


    p.getElementHeight = function ($element) {
        var attrHeight = $element.attr('height');
        if (!attrHeight) {
            // Return computed style height (int/pixels);
            // -2 for placeholder border
            return $element.height() - 2;
        }
        return attrHeight;
    };


    p.showElement = function ($element) {
        $element.show();
    };


    p.hideElement = function ($element) {
        $element.hide();
    };


    p.resolveIconCssClass = function ($element) {
        var tagName = $element[0].tagName.toLowerCase();
        var clsName = '';
        if (tagName === 'iframe') {
            clsName = 'live-edit-iframe';
        } else {
            clsName = 'live-edit-object';
        }
        return clsName;
    };

}($liveedit));