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
        var me = this;

        var $button = me.createButton({
            text: 'Settings',
            id: 'live-edit-button-settings',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        me.appendTo(me.componentMenu.getEl());
        me.componentMenu.buttons.push(me);
    };

}($liveedit));