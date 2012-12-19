(function ($) {
    'use strict';

    // Class definition (constructor function)
    var emptyButton = AdminLiveEdit.view.componentmenu.button.EmptyButton = function (componentMenu) {
        this.componentMenu = componentMenu;
        this.init();
    };

    // Inherits ui.Button
    emptyButton.prototype = new AdminLiveEdit.view.Button();

    // Fix constructor as it now is Button
    emptyButton.constructor = emptyButton;

    // Shorthand ref to the prototype
    var p = emptyButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.init = function () {
        var self = this;

        var $button = self.createButton({
            text: 'Empty',
            id: 'live-edit-button-empty',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        self.appendTo(self.componentMenu.getEl());
        self.componentMenu.buttons.push(self);
    };

}($liveedit));