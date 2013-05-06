AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view');

(function ($) {
    'use strict';

    // Class definition (constructor function)
    var highlighter = AdminLiveEdit.view.Highlighter = function () {
        this.$selectedComponent = null;

        this.addView();
        this.registerGlobalListeners();
    };

    // Inherits ui.Base
    highlighter.prototype = new AdminLiveEdit.view.Base();

    // Fix constructor as it now is Base
    // highlighter.constructor = highlighter;

    // Shorthand ref to the prototype
    var proto = highlighter.prototype;

    // Uses
    var util = liveedit.Helper;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.registerGlobalListeners = function () {
        $(window).on('component.mouseOver', $.proxy(this.componentMouseOver, this));
        $(window).on('component.mouseOut', $.proxy(this.hide, this));
        $(window).on('component.onSelect', $.proxy(this.selectComponent, this));
        $(window).on('component.onDeselect', $.proxy(this.deselect, this));
        $(window).on('component.onSortStart', $.proxy(this.hide, this));
        $(window).on('component.onRemove', $.proxy(this.hide, this));
        $(window).on('component.onParagraphEdit', $.proxy(this.hide, this));
        $(window).on('liveEdit.onWindowResize', $.proxy(this.handleWindowResize, this));

        $(window).on('component.onSortStop', function (event, uiEvent, ui, wasSelectedOnDragStart) {
            if (wasSelectedOnDragStart) {
                $(window).trigger('component.onSelect', [ui.item]);
            }
        });
    };


    proto.addView = function () {
        var html = '<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="live-edit-highlight-border" style="top:-5000px;left:-5000px">' +
                   '    <rect width="150" height="150"/>' +
                   '</svg>';
        this.createElement(html);
        this.appendTo($('body'));
    };


    proto.componentMouseOver = function (event, $component) {
        var me = this;
        me.show();
        me.paintBorder($component);
    };


    proto.selectComponent = function (event, $component) {
        var me = this;
        me.$selectedComponent = $component;
        var componentType = util.getComponentType($component);

        // Move CSS class manipulation to model base
        $('.live-edit-selected-component').removeClass('live-edit-selected-component');

        $component.addClass('live-edit-selected-component');

        // jQuery.addClass does not work for SVG elements.
        me.getEl().attr('class', me.getEl().attr('class') + ' live-edit-animatable');

        // Highlighter should not be shown when type page is selected
        if (componentType === 'page') {
            me.hide();
            return;
        }

        me.paintBorder($component);
        me.show();
    };


    proto.deselect = function () {
        var me = this;

        me.getEl().attr('class', me.getEl().attr('class').replace(/ live-edit-animatable/g, ''));

        $('.live-edit-selected-component').removeClass('live-edit-selected-component');

        me.$selectedComponent = null;
    };


    proto.paintBorder = function ($component) {
        var me = this,
            $border = me.getEl();

        me.resizeBorderToComponent($component);

        var style = me.getStyleForComponent($component);
        $border.css('stroke', style.strokeColor);
        $border.css('fill', style.fillColor);
        $border.css('stroke-dasharray', style.strokeDashArray);
    };


    proto.resizeBorderToComponent = function ($component) {
        var me = this;
        var componentType = util.getComponentType($component);
        var componentTagName = util.getTagNameForComponent($component);
        var componentBoxModel = util.getBoxModel($component);
        var w       = Math.round(componentBoxModel.width);
        var h       = Math.round(componentBoxModel.height);
        var top     = Math.round(componentBoxModel.top);
        var left    = Math.round(componentBoxModel.left);

        var $highlighter = me.getEl();
        var $HighlighterRect = $highlighter.find('rect');

        $highlighter.width(w);
        $highlighter.height(h);
        $HighlighterRect[0].setAttribute('width', w);
        $HighlighterRect[0].setAttribute('height', h);
        $highlighter.css({
            top : top,
            left: left
        });
    };


    proto.show = function () {
        this.getEl().show();
    };


    proto.hide = function () {
        this.getEl().hide();

        var $el = this.getEl();
        $el.attr('class', $el.attr('class').replace(/ live-edit-animatable/g, ''));
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

        case 'layout':
            strokeColor = 'rgba(255,165,0,1)';
            strokeDashArray = '5 5';
            fillColor = 'rgba(100,12,36,0)';
            break;

        case 'part':
            strokeColor = 'rgba(68,68,68,1)';
            strokeDashArray = '5 5';
            fillColor = 'rgba(255,255,255,0)';
            break;

        case 'paragraph':
            strokeColor = 'rgba(85,85,255,1)';
            strokeDashArray = '5 5';
            fillColor = 'rgba(255,255,255,0)';
            break;

        case 'content':
            strokeColor = '';
            strokeDashArray = '';
            fillColor = 'rgba(0,108,255,.25)';
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


    proto.handleWindowResize = function (event) {
        if (this.$selectedComponent) {
            this.paintBorder(this.$selectedComponent);
        }
    };

}($liveedit));