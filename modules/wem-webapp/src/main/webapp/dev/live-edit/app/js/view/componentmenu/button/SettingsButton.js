(function ($) {
    'use strict';

    // Class definition (constructor function)
    var settingsButton = AdminLiveEdit.view.componentmenu.button.SettingsButton = function (componentMenu) {
        this.componentMenu = componentMenu;
        this.init();
    };

    // Inherits ui.Button
    settingsButton.prototype = new AdminLiveEdit.view.Button();

    // Fix constructor as it now is Button
    settingsButton.constructor = settingsButton;

    // Shorthand ref to the prototype
    var p = settingsButton.prototype;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.init = function () {
        var self = this;

        var $button = self.createButton({
            text: 'Settings',
            id: 'live-edit-button-settings',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        self.appendTo(self.componentMenu.getEl());
        self.componentMenu.buttons.push(self);
    };

}($liveedit));