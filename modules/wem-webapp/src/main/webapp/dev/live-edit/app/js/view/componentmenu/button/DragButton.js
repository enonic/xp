(function ($) {
    'use strict';

    // Class definition (constructor function)
    var dragButton = AdminLiveEdit.view.componentmenu.button.DragButton = function (componentMenu) {
        this.componentMenu = componentMenu;
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
        var me = this;
        // var parentButton = new AdminLiveEdit.view.Button();
        var $button = me.createButton({
            text: 'Drag',
            id: 'live-edit-button-drag',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        me.getEl().on('mousedown', function () {
            this.le_mouseIsDown = true;
            // TODO: Use PubSub
            AdminLiveEdit.DragDrop.enable();
        });

        me.getEl().on('mousemove', function (event) {
            if (this.le_mouseIsDown) {
                this.le_mouseIsDown = false;
                me.componentMenu.fadeOutAndHide();
                // TODO: Get the selected using PubSub
                var $selectedComponent = me.componentMenu.$currentComponent;

                var evt = document.createEvent('MouseEvents');
                evt.initMouseEvent('mousedown', true, true, window, 0, event.screenX, event.screenY, event.clientX, event.clientY, false,
                    false, false, false, 0, null);

                $selectedComponent[0].dispatchEvent(evt);
            }
        });
        me.getEl().on('mouseup', function () {
            this.le_mouseIsDown = false;
            // TODO: remove reference to DragDrop, use PubSub.
            AdminLiveEdit.DragDrop.disable();
        });

        this.appendTo(this.componentMenu.getEl());
        this.componentMenu.buttons.push(this);
    };

}($liveedit));