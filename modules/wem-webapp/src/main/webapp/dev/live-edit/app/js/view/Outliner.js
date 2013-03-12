(function ($) {
    'use strict';

    // Class definition (constructor function)
    var outliner = AdminLiveEdit.view.Outliner = function () {
        this.$selectedComponent = null;

        this.addView();
        this.registerGlobalListeners();
    };

    // Inherits ui.Base
    outliner.prototype = new AdminLiveEdit.view.Base();

    // Fix constructor as it now is Base
    outliner.constructor = outliner;

    // Shorthand ref to the prototype
    var proto = outliner.prototype;

    // Uses
    var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.registerGlobalListeners = function () {

        $(window).on('component:mouseover', $.proxy(this.componentMouseOver, this));
        $(window).on('component:mouseout', $.proxy(this.hide, this));
        $(window).on('component:contextclick:select', $.proxy(this.selectComponent, this));
        $(window).on('component:click:select', $.proxy(this.selectComponent, this));
        $(window).on('component:click:deselect', $.proxy(this.deselect, this));
        $(window).on('component:sort:start', $.proxy(this.hide, this));
        $(window).on('component:remove', $.proxy(this.hide, this));
        $(window).on('component:paragraph:edit:init', $.proxy(this.hide, this));

        $(window).on('component:sort:stop', function (event, uiEvent, ui, wasSelectedOnDragStart) {
            if (wasSelectedOnDragStart) {
                $(window).trigger('component:click:select', [ui.item]);
            }
        });
    };


    proto.addView = function () {
        var html = '<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="live-edit-outliner" style="top:-5000px;left:-5000px">' +
                   '    <rect width="150" height="150"/>' +
                   '</svg>';
        this.createElement(html);
        this.appendTo($('body'));
    };


    proto.componentMouseOver = function (event, $component) {
        var me = this;
        me.show();
        me.paintOutline($component);
    };


    proto.selectComponent = function (event, $component) {
        var me = this;
        me.$selectedComponent = $component;
        var componentType = util.getComponentType($component);

        // Outliner should not be shown when type page is selected
        if (componentType === 'page') {
            me.hide();
            return;
        }

        me.show();
        me.paintOutline($component);

        $('.live-edit-selected-component').removeClass('live-edit-selected-component');
        $component.addClass('live-edit-selected-component');


        var scrollComponentIntoView = componentType !== 'page' || event.type !== 'component:contextclick:select';
        if (scrollComponentIntoView) {
            me.scrollComponentIntoView($component);
        }
    };


    proto.deselect = function () {
        var me = this;

        me.$selectedComponent = null;
        me.hide();
        $('.live-edit-selected-component').removeClass('live-edit-selected-component');
    };


    proto.paintOutline = function ($component) {
        var me = this,
            $outline = me.getEl();

        me.resizeOutlineToComponent($component);

        var style = me.getOutlineStyleForComponent($component);

        $outline.css('stroke', style.strokeColor);
        $outline.css('fill', style.fillColor);
        $outline.css('stroke-dasharray', style.strokeDashArray);
    };


    proto.resizeOutlineToComponent = function ($component) {
        var me = this;
        var componentType = util.getComponentType($component);
        var componentTagName = util.getTagNameForComponent($component);
        var componentBoxModel = util.getBoxModel($component);
        var w       = Math.round(componentBoxModel.width);
        var h       = Math.round(componentBoxModel.height);
        var top     = Math.round(componentBoxModel.top);
        var left    = Math.round(componentBoxModel.left);

        var $outline = me.getEl();
        var $outlineRect = $outline.find('rect');

        $outline.width(w);
        $outline.height(h);
        $outlineRect[0].setAttribute('width', w);
        $outlineRect[0].setAttribute('height', h);
        $outline.css({
            top : top,
            left: left
        });
    };


    proto.show = function () {
        this.getEl().show();
    };


    proto.hide = function () {
        this.getEl().hide();
    };


    proto.getOutlineStyleForComponent = function ($component) {
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


    proto.scrollComponentIntoView = function ($selectedComponent) {
        var componentTopPosition = util.getPagePositionForComponent($selectedComponent).top;
        if (componentTopPosition <= window.pageYOffset) {
            $('html, body').animate({scrollTop: componentTopPosition - 10}, 200);
        }
    };


}($liveedit));