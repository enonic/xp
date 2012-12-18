(function ($) {
    'use strict';

    // Class definition (constructor function)
    var highlighter = AdminLiveEdit.view.Highlighter = function () {
        this.create();
        this.bindEvents();
    };

    // Inherits ui.Base
    highlighter.prototype = new AdminLiveEdit.view.Base();

    // Fix constructor as it now is Base
    highlighter.constructor = highlighter;

    // Shorthand ref to the prototype
    var p = highlighter.prototype;

    // Uses
    var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.bindEvents = function () {
        $(window).on('component:mouseover', $.proxy(this.highlight, this));
        $(window).on('component:select', $.proxy(this.highlight, this));
        $(window).on('component:drag:start', $.proxy(this.hide, this));
    };


    p.create = function () {
        var html = '<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="live-edit-highlighter" style="top:-5000px;left:-5000px">' +
                   '    <rect width="150" height="150"/>' +
                   '</svg>';
        this.createElement(html);
        this.appendTo($('body'));
    };


    p.highlight = function (event, $component) {
        var componentType = util.getComponentType($component);
        var componentTagName = util.getTagNameForComponent($component);
        var componentBoxModel = util.getBoxModel($component);
        var w       = Math.round(componentBoxModel.width);
        var h       = Math.round(componentBoxModel.height);
        var top     = Math.round(componentBoxModel.top);
        var left    = Math.round(componentBoxModel.left);

        // We need to get the full height of the page/document.
        if (componentType === 'page' && componentTagName === 'body') {
            h = AdminLiveEdit.Util.getDocumentSize().height;
        }

        var $highlighter = this.getEl();
        var $highlighterRect = $highlighter.find('rect');

        $highlighter.width(w);
        $highlighter.height(h);
        $highlighterRect[0].setAttribute('width', w);
        $highlighterRect[0].setAttribute('height', h);
        $highlighter.css({
            top : top,
            left: left
        });

        $highlighter.css('stroke', this.getBorderColor($component));
    };


    p.hide = function () {
        this.getEl().css({
            top: '-5000px',
            left: '-5000px'
        });
    };


    p.getBorderColor = function ($component) {
        var componentType = util.getComponentType($component);
        var color = '';
        switch (componentType) {
        case 'region':
            color = '#141414';
            break;
        case 'window':
            color = '#141414';
            break;
        case 'content':
            color = '#141414';
            break;
        case 'paragraph':
            color = '#141414';
            break;
        case 'page':
            color = '#141414';
            break;
        default:
            color = '#ff0000';
        }
        return color;
    };

}($liveedit));