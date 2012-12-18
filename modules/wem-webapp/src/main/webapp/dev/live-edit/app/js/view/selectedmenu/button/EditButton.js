(function ($) {
    'use strict';

    // Class definition (constructor function)
    var editButton = AdminLiveEdit.view.selectedmenu.button.EditButton = function (selectedMenu) {
        this.selectedMenu = selectedMenu;
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
            iconCls: 'live-edit-icon-edit',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        self.appendTo(self.selectedMenu.getEl());
        self.selectedMenu.buttons.push(self);
    };

}($liveedit));