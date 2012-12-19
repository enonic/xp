(function ($) {
    'use strict';

    // Namespaces
    AdminLiveEdit.view.hovermenu = {};
    AdminLiveEdit.view.hovermenu.button = {};

    // Class definition (constructor)
    var hoverMenu = AdminLiveEdit.view.hovermenu.HoverMenu = function () {
        var me = this;
        me.buttons = [];

        me.$currentComponent = $([]);
        me.create();
        me.bindEvents();
    };


    // Inherits ui.Base.js
    hoverMenu.prototype = new AdminLiveEdit.view.Base();

    // Fix constructor as it now is Base
    hoverMenu.constructor = hoverMenu;

    // Shorthand ref to the prototype
    var p = hoverMenu.prototype;

    // Uses
    var util = AdminLiveEdit.Util;


    var BUTTON_WIDTH = 74;
    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.bindEvents = function () {
        $(window).on('component:select', $.proxy(this.show, this));

        $(window).on('component:mouseover', $.proxy(this.show, this));

        $(window).on('component:deselect', $.proxy(this.hide, this));

        $(window).on('component:drag:start', $.proxy(this.fadeOutAndHide, this));
    };


    p.create = function () {
        var me = this;

        me.createElement('<div class="live-edit-hover-menu" style="top:-5000px; left:-5000px;"></div>');

        me.appendTo($('body'));
        me.addButtons();
    };


    p.show = function (event, $component) {
        var componentInfo = util.getComponentInfo($component);
        if (componentInfo.tagName === 'body' && componentInfo.type === 'page') {
            this.hide();
            return;
        }

        this.moveToComponent($component);
        this.getEl().show();
    };


    p.hide = function () {
        this.getEl().css({ top: '-5000px', left: '-5000px', right: '' });
    };


    p.fadeOutAndHide = function () {
        this.getEl().fadeOut(500, function () {
            $(window).trigger('component:deselect');
        });
    };


    p.moveToComponent = function ($component) {
        var me = this;

        me.$currentComponent = $component;
        me.setCssPosition($component);

        var componentBoxModel = util.getBoxModel($component);
        var menuTopPos = Math.round(componentBoxModel.top + 2),
            menuLeftPos = Math.round((componentBoxModel.left + componentBoxModel.width) - BUTTON_WIDTH);

        me.getEl().css({
            top: menuTopPos,
            left: menuLeftPos
        });
    };


    p.addButtons = function () {
        var me = this;
        var parentButton = new AdminLiveEdit.view.hovermenu.button.ParentButton(me);

        var i;
        for (i = 0; i < me.buttons.length; i++) {
            me.buttons[i].appendTo(me.getEl());
        }
    };

}($liveedit));