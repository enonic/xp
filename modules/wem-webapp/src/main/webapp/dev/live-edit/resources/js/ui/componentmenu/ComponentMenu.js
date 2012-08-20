(function () {
    // Namespaces
    AdminLiveEdit.ui.componentmenu = {};
    AdminLiveEdit.ui.componentmenu.button = {};

    // Class definition (constructor)
    var componentMenu = AdminLiveEdit.ui.componentmenu.ComponentMenu = function () {
        this.buttons = [];
        this.buttonConfig = {
            'page': ['settings'],
            'region': ['parent', 'insert', 'reset', 'empty'],
            'window': ['parent', 'drag', 'settings', 'remove'],
            'content': ['parent', 'view', 'edit'],
            'paragraph': ['parent', 'edit']
        };

        this.$currentComponent = $liveedit([]);

        this.create();
        this.registerSubscribers();
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

    p.registerSubscribers = function () {
        var self = this;

        $liveedit.subscribe('/ui/componentselector/on-select', function ($component) {
            self.show.call(self, $component);
        });

        $liveedit.subscribe('/ui/highlighter/on-highlight', function ($component) {
            self.show.call(self, $component);
        });

        $liveedit.subscribe('/ui/componentselector/on-deselect', function () {
            self.hide.call(self);
        });

        $liveedit.subscribe('/ui/dragdrop/on-sortstart', function () {
            self.fadeOutAndHide.call(self);
        });

    };


    p.create = function () {
        this.createElement('<div class="live-edit-component-menu" style="top:-5000px; left:-5000px;">' +
                           '    <div class="live-edit-component-menu-inner"></div>' +
                           '</div>');
        this.appendTo($liveedit('body'));
        this.addButtons();
    };


    p.show = function ($component) {
        this.getMenuForComponent($component);
        this.moveToComponent($component);
        this.getEl().show();
    };


    p.hide = function () {
        this.getEl().css({ top: '-5000px', left: '-5000px', right: '' });
    };


    p.fadeOutAndHide = function () {
        this.getEl().fadeOut(500, function () {
            $liveedit.publish('/ui/componentselector/on-deselect');
        });
    };


    p.moveToComponent = function ($component) {
        this.$currentComponent = $component;
        var componentBoxModel = util.getBoxModel($component);
        var offsetLeft = 2,
            menuTopPos = Math.round(componentBoxModel.top),
            menuLeftPos = Math.round(componentBoxModel.left + componentBoxModel.width) - offsetLeft,
            documentSize = util.getDocumentSize();

        if (menuLeftPos >= (documentSize.width - offsetLeft)) {
            menuLeftPos = menuLeftPos - this.getEl().width();
        }

        this.getEl().css({
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