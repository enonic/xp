(function () {
    // Class definition (constructor)
    var componentMenu = AdminLiveEdit.ui2.ComponentMenu = function () {
        this.buttons = [];
        this.create();
    };

    // Inherits ui.Base.js
    componentMenu.prototype = new AdminLiveEdit.ui2.Base();

    // Fix constructor as it now is Base
    componentMenu.constructor = componentMenu;

    // Shorthand ref to the prototype
    var p = componentMenu.prototype;

    // Uses
    var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *


    p.buttonConfig = {
        'page'      : ['settings'],
        'region'    : ['parent', 'insert', 'reset', 'empty'],
        'window'    : ['parent', 'drag', 'settings', 'remove'],
        'content'   : ['parent', 'view', 'edit'],
        'paragraph' : ['parent', 'edit']
    };


    p.initSubscribers = function() {
        $liveedit.subscribe('/page/component/select', this.show);
        $liveedit.subscribe('/page/component/deselect', this.hide);
        $liveedit.subscribe('/page/component/sortstart', this.fadeOutAndHide);
    };


    p.create = function () {
        this.createElement('<div class="live-edit-context-menu" style="top:-5000px; left:-5000px;">' +
                           '    <div class="live-edit-context-menu-inner"></div>' +
                           '</div>');
        this.appendTo($liveedit('body'));
        this.addButtons();
        this.initSubscribers();
    };


    p.show = function ($component) {
        var componentType = util.getTypeFromComponent($component);
        this.getMenu(componentType);
        this.moveTo($component);
    };


    p.hide = function () {
        this.getEl().css({ top: '-5000px', left: '-5000px', right: '' });
    };


    p.moveTo = function($component) {
        var componentBoxModel = util.getBoxModel($component);
        var menuTopPos = Math.round(componentBoxModel.top),
            menuLeftPos = Math.round(componentBoxModel.left + componentBoxModel.width),
            documentSize = util.getDocumentSize();

        if (menuLeftPos >= documentSize.width) {
            menuLeftPos = menuLeftPos - this.getEl().width();
        }

        this.getEl().css({
            top: menuTopPos,
            left: menuLeftPos
        });
    };


    p.getMenu = function (componentType) {

        if (p.buttonConfig.hasOwnProperty(componentType)) {
            var buttonArray = p.buttonConfig[componentType];
            var buttons = this.getButtons();

            for (var i = 0; i < buttons.length; i++) {
                var $button = buttons[i].getEl();
                var id = $button.attr('data-live-edit-cmp-id');
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
        var t = this;

        var parentButton = new AdminLiveEdit.ui2.Button();
        parentButton.create({
            id: 'live-edit-button-parent',
            text: 'Parent',
            iconCls: 'live-edit-icon-parent',
            handler: function (event) {
                event.stopPropagation();
                $liveedit.publish('/page/component/select-parent');
            }
        });
        t.buttons.push(parentButton);


        var insertButton = new AdminLiveEdit.ui2.Button();
        insertButton.create({
            text: 'Insert',
            id: 'live-edit-button-insert',
            iconCls: 'live-edit-icon-insert',
            handler: function (event) {
                event.stopPropagation();
            }
        });
        t.buttons.push(insertButton);

        var resetButton = new AdminLiveEdit.ui2.Button();
        resetButton.create({
            text: 'Reset',
            id: 'live-edit-button-reset',
            iconCls: 'live-edit-icon-reset',
            handler: function (event) {
                event.stopPropagation();
            }
        });
        t.buttons.push(resetButton);


        var emptyButton = new AdminLiveEdit.ui2.Button();
        emptyButton.create({
            text: 'Empty',
            id: 'live-edit-button-empty',
            iconCls: 'live-edit-icon-empty',
            handler: function (event) {
                event.stopPropagation();
            }
        });
        t.buttons.push(emptyButton);


        var viewButton = new AdminLiveEdit.ui2.Button();
        viewButton.create({
            text: 'View',
            id: 'live-edit-button-view',
            iconCls: 'live-edit-icon-view',
            handler: function (event) {
                event.stopPropagation();
            }
        });
        t.buttons.push(viewButton);


        var editButton = new AdminLiveEdit.ui2.Button();
        editButton.create({
            text: 'edit',
            id: 'live-edit-button-edit',
            iconCls: 'live-edit-icon-edit',
            handler: function (event) {
                event.stopPropagation();
            }
        });
        t.buttons.push(editButton);


        var settingsButton = new AdminLiveEdit.ui2.Button();
        settingsButton.create({
            text: 'Settings',
            id: 'live-edit-button-settings',
            iconCls: 'live-edit-icon-settings',
            handler: function (event) {
                event.stopPropagation();
            }
        });
        t.buttons.push(settingsButton);

        var dragButton = new AdminLiveEdit.ui2.Button();
        dragButton.create({
            text: 'Drag',
            id: 'live-edit-button-drag',
            iconCls: 'live-edit-icon-drag',
            handler: function (event) {
                event.stopPropagation();
            }
        });
        dragButton.getEl().on('mousemove', function (event) {
            if (this._mouseDown) {
                this._mouseDown = false;
                t.fadeOutAndHide();
                var highlighter = AdminLiveEdit.ui.Highlighter;
                var $selectedComponent = highlighter.getSelected();
                var evt = document.createEvent('MouseEvents');
                evt.initMouseEvent('mousedown', true, true, window, 0, event.screenX, event.screenY, event.clientX, event.clientY, false,
                    false, false, false, 0, null);

                $selectedComponent[0].dispatchEvent(evt);
            }
        });
        dragButton.getEl().on('mouseup', function (event) {
            this._mouseDown = false;
            AdminLiveEdit.ui.DragDrop.disable();
        });
        t.buttons.push(dragButton);


        var removeButton = new AdminLiveEdit.ui2.Button();
        removeButton.create({
            text: 'Remove',
            id: 'live-edit-button-remove',
            iconCls: 'live-edit-icon-remove',
            handler: function (event) {
                event.stopPropagation();
            }
        });
        t.buttons.push(removeButton);

        for (var i = 0; i < t.buttons.length; i++) {
            t.buttons[i].appendTo(t.getEl());
        }

    };


    p.fadeOutAndHide = function () {
        this.getEl().fadeOut(500, function () {
            $liveedit.publish('/page/component/deselect');
        });
    };


}());