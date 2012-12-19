(function ($) {
    'use strict';

    // Namespaces
    AdminLiveEdit.view.componentmenu = {};
    AdminLiveEdit.view.componentmenu.button = {};

    // Class definition (constructor)
    var componentMenu = AdminLiveEdit.view.componentmenu.ComponentMenu = function () {
        var me = this;
        me.buttons = [];
        me.buttonConfig = {
            'page': ['settings'],
            'region': ['parent', 'insert', 'reset', 'empty'],
            'window': ['parent', 'settings', 'remove'],
            'content': ['parent', 'view', 'edit'],
            'paragraph': ['parent', 'edit']
        };

        me.$currentComponent = $([]);
        me.create();
        me.bindEvents();
    };


    // Inherits Base.js
    componentMenu.prototype = new AdminLiveEdit.view.Base();

    // Fix constructor as it now is Base
    componentMenu.constructor = componentMenu;

    // Shorthand ref to the prototype
    var p = componentMenu.prototype;

    // Uses
    var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.bindEvents = function () {
        $(window).on('component:select', $.proxy(this.show, this));

        // $(window).on('component:mouseover', $.proxy(this.show, this));

        $(window).on('component:deselect', $.proxy(this.hide, this));

        $(window).on('component:drag:start', $.proxy(this.fadeOutAndHide, this));
    };


    p.create = function () {
        var me = this;

        me.createElement('<div class="live-edit-component-menu" style="top:-5000px; left:-5000px;"></div>');
        me.appendTo($('body'));
        me.addButtons();
    };


    p.show = function (event, $component) {
        var componentInfo = util.getComponentInfo($component);
        if (componentInfo.tagName === 'body' && componentInfo.type === 'page') {
            this.hide();
            return;
        }

        this.getMenuForComponent($component);
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
        var offsetLeft = 0,
            menuTopPos = Math.round(componentBoxModel.top),
            menuLeftPos = Math.round(componentBoxModel.left + componentBoxModel.width) - offsetLeft,
            documentSize = util.getDocumentSize();

        if (menuLeftPos >= (documentSize.width - offsetLeft)) {
            menuLeftPos = menuLeftPos - me.getEl().width();
        }

        me.getEl().css({
            top: menuTopPos,
            left: menuLeftPos
        });
    };


    p.getMenuForComponent = function ($component) {
        var componentType = util.getComponentType($component);
        if (this.buttonConfig.hasOwnProperty(componentType)) {
            var buttonArray = this.buttonConfig[componentType];
            var buttons = this.getButtons();

            var i;
            for (i = 0; i < buttons.length; i++) {
                var $button = buttons[i].getEl();
                var id = $button.attr('data-live-edit-ui-cmp-id');
                var subStr = id.substring(id.lastIndexOf('-') + 1, id.length);
                if (buttonArray.indexOf(subStr) > -1) {
                    $button.show();
                } else {
                    $button.hide();
                }
            }
        }
    };


    p.getButtons = function () {
        return this.buttons;
    };


    p.addButtons = function () {
        var me = this;
        var insertButton = new AdminLiveEdit.view.componentmenu.button.InsertButton(me);
        var resetButton = new AdminLiveEdit.view.componentmenu.button.ResetButton(me);
        var emptyButton = new AdminLiveEdit.view.componentmenu.button.EmptyButton(me);
        var viewButton = new AdminLiveEdit.view.componentmenu.button.ViewButton(me);
        var editButton = new AdminLiveEdit.view.componentmenu.button.EditButton(me);
        // var dragButton = new AdminLiveEdit.view.componentmenu.button.DragButton(me);
        var settingsButton = new AdminLiveEdit.view.componentmenu.button.SettingsButton(me);
        var removeButton = new AdminLiveEdit.view.componentmenu.button.RemoveButton(me);

        var i;
        for (i = 0; i < me.buttons.length; i++) {
            me.buttons[i].appendTo(me.getEl());
        }
    };

}($liveedit));