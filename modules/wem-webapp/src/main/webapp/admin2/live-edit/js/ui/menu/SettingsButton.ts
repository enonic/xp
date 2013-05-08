module liveedit.ui {
    var $ = $liveedit;

    export class SettingsButton extends liveedit.ui.BaseButton {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;
            this.init();
        }

        init() {
            var me = this;

            var $button = me.createButton({
                text: 'Settings',
                id: 'live-edit-button-settings',
                cls: 'live-edit-component-menu-button',
                handler: function (event) {
                    event.stopPropagation();

                    // Temporary workaround until we get a firm messaging system
                    var parentWindow = window['parent'];
                    if (parentWindow && parentWindow['Admin'].MessageBus) {
                        parentWindow['Admin'].MessageBus.showLiveEditTestSettingsWindow({});
                    }
                }
            });

            me.appendTo(me.menu.getEl());
            me.menu.buttons.push(me);
        }
    }
}
/*
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.menu');

(function ($) {
    'use strict';

    // Class definition (constructor function)
    var settingsButton = AdminLiveEdit.view.menu.SettingsButton = function (menu) {
        this.menu = menu;
        this.init();
    };

    // Inherits ui.Button
    settingsButton.prototype = new AdminLiveEdit.view.menu.BaseButton();

    // Fix constructor as it now is Button
    // settingsButton.constructor = settingsButton;

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
*/