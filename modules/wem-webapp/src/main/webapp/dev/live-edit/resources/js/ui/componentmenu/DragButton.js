(function () {
    // Class definition (constructor function)
    var dragButton = AdminLiveEdit.ui.componentmenu.DragButton = function (componentMenu) {
        this.componentMenu = componentMenu;
        this.init();
    };

    // Inherits ui.Base
    dragButton.prototype = new AdminLiveEdit.ui.Button();

    // Fix constructor as it now is Base
    dragButton.constructor = dragButton;

    // Shorthand ref to the prototype
    var p = dragButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.init = function () {
        var self = this;
        // var parentButton = new AdminLiveEdit.ui.Button();
        var $button = this.create({
            text: 'Drag',
            id: 'live-edit-button-drag',
            iconCls: 'live-edit-icon-drag',
            handler: function (event) {
                event.stopPropagation();
            }
        });
        console.log($button);

        this.getEl().on('mousedown', function () {
            this.le_mouseIsDown = true;
            // TODO: Use PubSub
            AdminLiveEdit.ui.DragDrop.enable();
        });


        this.getEl().on('mousemove', function (event) {
            if (this.le_mouseIsDown) {
                this.le_mouseIsDown = false;
                self.componentMenu.fadeOutAndHide();
                // TODO: Get the selected using PubSub
                var $selectedComponent = self.componentMenu.$currentComponent;

                var evt = document.createEvent('MouseEvents');
                evt.initMouseEvent('mousedown', true, true, window, 0, event.screenX, event.screenY, event.clientX, event.clientY, false,
                    false, false, false, 0, null);

                $selectedComponent[0].dispatchEvent(evt);
            }
        });
        this.getEl().on('mouseup', function () {
            this.le_mouseIsDown = false;
            // TODO: remove reference to DragDrop, use PubSub.
            AdminLiveEdit.ui.DragDrop.disable();
        });

        this.appendTo(this.componentMenu.getEl());
        this.componentMenu.buttons.push(this);
    };

}());