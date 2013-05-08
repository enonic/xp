module LiveEdit.ui {
    var $ = $liveedit;

    export class ResetButton extends LiveEdit.ui.BaseButton {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;
            this.init();
        }

        init() {
            var me = this;

            var $button = me.createButton({
                text: 'Reset to Default',
                id: 'live-edit-button-reset',
                cls: 'live-edit-component-menu-button',
                handler: function (event) {
                    event.stopPropagation();
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
    var resetButton = AdminLiveEdit.view.menu.ResetButton = function (menu) {
        this.menu = menu;
        this.init();
    };

    // Inherits ui.Button
    resetButton.prototype = new AdminLiveEdit.view.menu.BaseButton();

    // Fix constructor as it now is Button
    // resetButton.constructor = resetButton;

    // Shorthand ref to the prototype
    var proto = resetButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.init = function () {
        var me = this;

        var $button = me.createButton({
            text: 'Reset to Default',
            id: 'live-edit-button-reset',
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