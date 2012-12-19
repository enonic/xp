(function ($) {
    'use strict';

    // Class definition (constructor function)
    var removeButton = AdminLiveEdit.view.componentmenu.button.RemoveButton = function (componentMenu) {
        this.componentMenu = componentMenu;
        this.init();
    };

    // Inherits ui.Button
    removeButton.prototype = new AdminLiveEdit.view.Button();

    // Fix constructor as it now is Button
    removeButton.constructor = removeButton;

    // Shorthand ref to the prototype
    var p = removeButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.init = function () {
        var self = this;
        var $button = self.createButton({
            text: 'Remove',
            id: 'live-edit-button-remove',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        self.appendTo(self.componentMenu.getEl());
        self.componentMenu.buttons.push(self);
    };

}($liveedit));