(function ($) {
    'use strict';

    // Class definition (constructor function)
    var removeButton = AdminLiveEdit.view.selectedmenu.button.RemoveButton = function (selectedMenu) {
        this.selectedMenu = selectedMenu;
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
            iconCls: 'live-edit-icon-remove',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        self.appendTo(self.selectedMenu.getEl());
        self.selectedMenu.buttons.push(self);
    };

}($liveedit));