AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.menu');

(function ($) {
    'use strict';

    // Class definition (constructor function)
    var viewButton = AdminLiveEdit.view.menu.ViewButton = function (menu) {
        this.menu = menu;
        this.init();
    };

    // Inherits ui.Button
    viewButton.prototype = new AdminLiveEdit.view.menu.BaseButton();

    // Fix constructor as it now is Button
    viewButton.constructor = viewButton;

    // Shorthand ref to the prototype
    var proto = viewButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.init = function () {
        var me = this;

        var $button = me.createButton({
            text: 'View',
            id: 'live-edit-button-view',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        me.appendTo(me.menu.getEl());
        me.menu.buttons.push(me);
    };

}($liveedit));