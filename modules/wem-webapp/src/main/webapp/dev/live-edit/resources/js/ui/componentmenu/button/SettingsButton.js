(function () {
    'use strict';

    // Class definition (constructor function)
    var settingsButton = AdminLiveEdit.ui.componentmenu.button.SettingsButton = function (componentMenu) {
        this.componentMenu = componentMenu;
        this.init();
    };

    // Inherits ui.Base
    settingsButton.prototype = new AdminLiveEdit.ui.Button();

    // Fix constructor as it now is Base
    settingsButton.constructor = settingsButton;

    // Shorthand ref to the prototype
    var p = settingsButton.prototype;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.init = function () {
        var self = this;

        var $button = self.createButton({
            text: 'Settings',
            id: 'live-edit-button-settings',
            iconCls: 'live-edit-icon-settings',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        self.appendTo(self.componentMenu.getEl());
        self.componentMenu.buttons.push(self);
    };

}());