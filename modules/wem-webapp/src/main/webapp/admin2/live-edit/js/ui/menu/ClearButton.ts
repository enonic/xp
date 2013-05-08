module LiveEdit.ui {
    var $ = $liveedit;

    export class ClearButton extends LiveEdit.ui.BaseButton {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;
            this.init();
        }

        init() {
            var me = this;

            var $button = me.createButton({
                text: 'Empty',
                id: 'live-edit-button-clear',
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
    var clearButton = AdminLiveEdit.view.menu.ClearButton = function (menu) {
        this.menu = menu;
        this.init();
    };

    // Inherits ui.Button
    clearButton.prototype = new AdminLiveEdit.view.menu.BaseButton();

    // Fix constructor as it now is Button
    // clearButton.constructor = clearButton;

    // Shorthand ref to the prototype
    var proto = clearButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.init = function () {
        var me = this;

        var $button = me.createButton({
            text: 'Empty',
            id: 'live-edit-button-clear',
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