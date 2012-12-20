(function ($) {
    'use strict';

    // Class definition (constructor function)
    var resetButton = AdminLiveEdit.view.componentmenu.button.ResetButton = function (componentMenu) {
        this.componentMenu = componentMenu;
        this.init();
    };

    // Inherits ui.Button
    resetButton.prototype = new AdminLiveEdit.view.Button();

    // Fix constructor as it now is Button
    resetButton.constructor = resetButton;

    // Shorthand ref to the prototype
    var p = resetButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.init = function () {
        var self = this;

        var $button = self.createButton({
            text: 'Reset',
            id: 'live-edit-button-reset',
            iconCls: 'live-edit-icon-reset',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        self.appendTo(self.componentMenu.getEl());
        self.componentMenu.buttons.push(self);
    };

}($liveedit));