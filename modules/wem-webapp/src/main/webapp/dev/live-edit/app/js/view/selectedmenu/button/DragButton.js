(function ($) {
    'use strict';

    // Class definition (constructor function)
    var dragButton = AdminLiveEdit.view.selectedmenu.button.DragButton = function (selectedMenu) {
        this.selectedMenu = selectedMenu;
        this.init();
    };

    // Inherits ui.Button
    dragButton.prototype = new AdminLiveEdit.view.Button();

    // Fix constructor as it now is Button
    dragButton.constructor = dragButton;

    // Shorthand ref to the prototype
    var p = dragButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.init = function () {
        var self = this;
        // var parentButton = new AdminLiveEdit.view.Button();
        var $button = self.createButton({
            text: 'Drag',
            id: 'live-edit-button-drag',
            iconCls: 'live-edit-icon-drag',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        self.getEl().on('mousedown', function () {
            this.le_mouseIsDown = true;
            // TODO: Use PubSub
            AdminLiveEdit.DragDrop.enable();
        });

        self.getEl().on('mousemove', function (event) {
            if (this.le_mouseIsDown) {
                this.le_mouseIsDown = false;
                self.selectedMenu.fadeOutAndHide();
                // TODO: Get the selected using PubSub
                var $selectedComponent = self.selectedMenu.$currentComponent;

                var evt = document.createEvent('MouseEvents');
                evt.initMouseEvent('mousedown', true, true, window, 0, event.screenX, event.screenY, event.clientX, event.clientY, false,
                    false, false, false, 0, null);

                $selectedComponent[0].dispatchEvent(evt);
            }
        });
        self.getEl().on('mouseup', function () {
            this.le_mouseIsDown = false;
            // TODO: remove reference to DragDrop, use PubSub.
            AdminLiveEdit.DragDrop.disable();
        });

        this.appendTo(this.selectedMenu.getEl());
        this.selectedMenu.buttons.push(this);
    };

}($liveedit));