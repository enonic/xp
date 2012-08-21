(function () {
    'use strict';

    // Class definition (constructor function)
    var insertButton = AdminLiveEdit.ui.componentmenu.button.InsertButton = function (componentMenu) {
        this.componentMenu = componentMenu;
        this.init();
    };

    // Inherits ui.Base
    insertButton.prototype = new AdminLiveEdit.ui.Button();

    // Fix constructor as it now is Base
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

        self.appendTo(self.componentMenu.getEl());
        self.componentMenu.buttons.push(self);
    };

}());