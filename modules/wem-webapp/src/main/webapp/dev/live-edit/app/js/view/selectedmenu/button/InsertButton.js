(function ($) {
    'use strict';

    // Class definition (constructor function)
    var insertButton = AdminLiveEdit.view.selectedmenu.button.InsertButton = function (selectedMenu) {
        this.selectedMenu = selectedMenu;
        this.init();
    };

    // Inherits ui.Button
    insertButton.prototype = new AdminLiveEdit.view.Button();

    // Fix constructor as it now is Button
    insertButton.constructor = insertButton;

    // Shorthand ref to the prototype
    var p = insertButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.init = function () {
        var self = this;

        var $button = self.createButton({
            text: 'Insert',
            id: 'live-edit-button-insert',
            iconCls: 'live-edit-icon-insert',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        self.appendTo(self.selectedMenu.getEl());
        self.selectedMenu.buttons.push(self);
    };

}($liveedit));