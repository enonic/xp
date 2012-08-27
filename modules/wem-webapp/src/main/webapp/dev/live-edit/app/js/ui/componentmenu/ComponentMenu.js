(function () {
    'use strict';

    // Namespaces
    AdminLiveEdit.ui.componentmenu = {};
    AdminLiveEdit.ui.componentmenu.button = {};

    // Class definition (constructor)
    var componentMenu = AdminLiveEdit.ui.componentmenu.ComponentMenu = function () {
        var self = this;
        self.buttons = [];
        self.buttonConfig = {
            'page': ['settings'],
            'region': ['parent', 'insert', 'reset', 'empty'],
            'window': ['parent', 'drag', 'settings', 'remove'],
            'content': ['parent', 'view', 'edit'],
            'paragraph': ['parent', 'edit']
        };

        self.$currentComponent = $liveedit([]);
        self.create();
        self.bindEvents();
    };


    // Inherits ui.Base.js
    componentMenu.prototype = new AdminLiveEdit.ui.Base();

    // Fix constructor as it now is Base
    componentMenu.constructor = componentMenu;

    // Shorthand ref to the prototype
    var p = componentMenu.prototype;

    // Uses
    var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.bindEvents = function () {
        $liveedit(window).on('component:select', $liveedit.proxy(this.show, this));

        $liveedit(window).on('component:mouseover', $liveedit.proxy(this.show, this));

        $liveedit(window).on('component:deselect', $liveedit.proxy(this.hide, this));

        $liveedit(window).on('component:drag:start', $liveedit.proxy(this.fadeOutAndHide, this));
    };


    p.create = function () {
        var self = this;

        self.createElement('<div class="live-edit-component-menu" style="top:-5000px; left:-5000px;">' +
                           '    <div class="live-edit-component-menu-inner"></div>' +
                           '</div>');
        self.appendTo($liveedit('body'));
        self.addButtons();
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
            $liveedit(window).trigger('component:deselect');
        });
    };


    p.moveToComponent = function ($component) {
        var self = this;

        self.$currentComponent = $component;
        self.setCssPosition($component);

        var componentBoxModel = util.getBoxModel($component);
        var offsetLeft = 2,
            menuTopPos = Math.round(componentBoxModel.top),
            menuLeftPos = Math.round(componentBoxModel.left + componentBoxModel.width) - offsetLeft,
            documentSize = util.getDocumentSize();

        if (menuLeftPos >= (documentSize.width - offsetLeft)) {
            menuLeftPos = menuLeftPos - self.getEl().width();
        }

        self.getEl().css({
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
        var self = this;
        var parentButton = new AdminLiveEdit.ui.componentmenu.button.ParentButton(self);
        var insertButton = new AdminLiveEdit.ui.componentmenu.button.InsertButton(self);
        var resetButton = new AdminLiveEdit.ui.componentmenu.button.ResetButton(self);
        var emptyButton = new AdminLiveEdit.ui.componentmenu.button.EmptyButton(self);
        var viewButton = new AdminLiveEdit.ui.componentmenu.button.ViewButton(self);
        var editButton = new AdminLiveEdit.ui.componentmenu.button.EditButton(self);
        var dragButton = new AdminLiveEdit.ui.componentmenu.button.DragButton(self);
        var settingsButton = new AdminLiveEdit.ui.componentmenu.button.SettingsButton(self);
        var removeButton = new AdminLiveEdit.ui.componentmenu.button.RemoveButton(self);

        var i;
        for (i = 0; i < self.buttons.length; i++) {
            self.buttons[i].appendTo(self.getEl());
        }
    };

}());