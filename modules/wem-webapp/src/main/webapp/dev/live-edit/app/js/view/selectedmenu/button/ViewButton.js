(function ($) {
    'use strict';

    // Class definition (constructor function)
    var viewButton = AdminLiveEdit.view.selectedmenu.button.ViewButton = function (selectedMenu) {
        this.selectedMenu = selectedMenu;
        this.init();
    };

    // Inherits ui.Button
    viewButton.prototype = new AdminLiveEdit.view.Button();

    // Fix constructor as it now is Button
    viewButton.constructor = viewButton;

    // Shorthand ref to the prototype
    var p = viewButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.init = function () {
        var self = this;

        var $button = self.createButton({
            text: 'View',
            id: 'live-edit-button-view',
            iconCls: 'live-edit-icon-view',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        self.appendTo(self.selectedMenu.getEl());
        self.selectedMenu.buttons.push(self);
    };

}($liveedit));