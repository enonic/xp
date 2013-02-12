(function ($) {
    'use strict';

    // Class definition (constructor function)
    var emptyButton = AdminLiveEdit.view.componenttip.menu.EmptyButton = function (menu) {
        this.menu = menu;
        this.init();
    };

    // Inherits ui.Button
    emptyButton.prototype = new AdminLiveEdit.view.componenttip.menu.BaseButton();

    // Fix constructor as it now is Button
    emptyButton.constructor = emptyButton;

    // Shorthand ref to the prototype
    var proto = emptyButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.init = function () {
        var me = this;

        var $button = me.createButton({
            text: 'Empty',
            id: 'live-edit-button-empty',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        me.appendTo(me.menu.getEl());
        me.menu.buttons.push(me);
    };

}($liveedit));