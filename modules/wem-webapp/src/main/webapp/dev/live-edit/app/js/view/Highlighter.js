(function ($) {
    'use strict';

    // Class definition (constructor function)
    var highlighter = AdminLiveEdit.view.Highlighter = function () {
        this.addView();
        this.registerGlobalListeners();
    };

    // Inherits ui.Base
    highlighter.prototype = new AdminLiveEdit.view.Base();

    // Fix constructor as it now is Base
    highlighter.constructor = highlighter;

    // Shorthand ref to the prototype
    var proto = highlighter.prototype;

    // Uses
    var util = AdminLiveEdit.Util;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.registerGlobalListeners = function () {
        $(window).on('component:mouseover', $.proxy(this.highlight, this));
        $(window).on('component:mouseout', $.proxy(this.hide, this));
        $(window).on('component:select', $.proxy(this.highlight, this));
        $(window).on('component:deselect', $.proxy(this.deselect, this));
        $(window).on('component:sort:start', $.proxy(this.hide, this));
        // $(window).on('componentBar:mouseover', $.proxy(this.hide, this));
    };


    proto.addView = function () {
        var html = '<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="live-edit-highlighter" style="top:-5000px;left:-5000px">' +
                   '    <rect width="150" height="150"/>' +
                   '</svg>';
        this.createElement(html);
        this.appendTo($('body'));
    };


    proto.highlight = function (event, $selectedComponent) {
        this.getEl().show();
        var me = this;
        me.resize($selectedComponent);

        var $highlighter = me.getEl();

        var style = me.getStyleForComponent($selectedComponent);

        $highlighter.css('stroke', style.strokeColor);
        $highlighter.css('fill', style.fillColor);
        $highlighter.css('stroke-dasharray', style.strokeDashArray);
    };


    proto.resize = function ($selectedComponent) {
        var me = this;
        var componentType = util.getComponentType($selectedComponent);
        var componentTagName = util.getTagNameForComponent($selectedComponent);
        var componentBoxModel = util.getBoxModel($selectedComponent);
        var w       = Math.round(componentBoxModel.width);
        var h       = Math.round(componentBoxModel.height);
        var top     = Math.round(componentBoxModel.top);
        var left    = Math.round(componentBoxModel.left);

        var $highlighter = me.getEl();
        var $highlighterRect = $highlighter.find('rect');

        $highlighter.width(w);
        $highlighter.height(h);
        $highlighterRect[0].setAttribute('width', w);
        $highlighterRect[0].setAttribute('height', h);
        $highlighter.css({
            top : top,
            left: left
        });
    };


    proto.deselect = function () {
        // this.getEl().css('opacity', '1');
    };


    proto.hide = function (event) {
        this.getEl().hide();
    };


    proto.getStyleForComponent = function ($component) {
        var componentType = util.getComponentType($component);

        var strokeColor,
            strokeDashArray,
            fillColor;

        switch (componentType) {
        case 'region':
            strokeColor = 'rgba(20,20,20,1)';
            strokeDashArray = '';
            fillColor = 'rgba(255,255,255,0)';
            break;

        case 'part':
            strokeColor = 'rgba(68,68,68,1)';
            strokeDashArray = '5 5';
            fillColor = 'rgba(255,255,255,0)';
            break;

        case 'content':
            strokeColor = '';
            strokeDashArray = '';
            fillColor = 'rgba(0,108,255,.25)';
            break;

        case 'paragraph':
            strokeColor = 'rgba(85,85,255,1)';
            strokeDashArray = '5 5';
            fillColor = 'rgba(255,255,255,0)';
            break;

        default:
            strokeColor = 'rgba(20,20,20,1)';
            strokeDashArray = '';
            fillColor = 'rgba(255,255,255,0)';
        }

        return {
            strokeColor: strokeColor,
            strokeDashArray: strokeDashArray,
            fillColor: fillColor
        };
    };

}($liveedit));