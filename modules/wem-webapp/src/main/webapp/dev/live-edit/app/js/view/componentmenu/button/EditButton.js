(function ($) {
    'use strict';

    // Class definition (constructor function)
    var editButton = AdminLiveEdit.view.componentmenu.button.EditButton = function (componentMenu) {
        this.componentMenu = componentMenu;
        this.init();
    };

    // Inherits ui.Button
    editButton.prototype = new AdminLiveEdit.view.Button();

    // Fix constructor as it now is Button
    editButton.constructor = editButton;

    // Shorthand ref to the prototype
    var p = editButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.init = function () {
        var self = this;

        var $button = self.createButton({
            id: 'live-edit-button-edit',
            text: 'Edit',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        self.appendTo(self.componentMenu.getEl());
        self.componentMenu.buttons.push(self);
    };

}($liveedit));