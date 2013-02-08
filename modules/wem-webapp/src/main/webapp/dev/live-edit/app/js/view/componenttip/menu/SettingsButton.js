(function ($) {
    'use strict';

    // Class definition (constructor function)
    var settingsButton = AdminLiveEdit.view.componenttip.menu.SettingsButton = function (menu) {
        this.menu = menu;
        this.init();
    };

    // Inherits ui.Button
    settingsButton.prototype = new AdminLiveEdit.view.Button();

    // Fix constructor as it now is Button
    settingsButton.constructor = settingsButton;

    // Shorthand ref to the prototype
    var proto = settingsButton.prototype;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.init = function () {
        var me = this;

        var $button = me.createButton({
            text: 'Settings',
            id: 'live-edit-button-settings',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        me.appendTo(me.menu.getEl());
        me.menu.buttons.push(me);
    };

}($liveedit));