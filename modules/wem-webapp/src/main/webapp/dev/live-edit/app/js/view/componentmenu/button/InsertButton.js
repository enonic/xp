(function ($) {
    'use strict';

    // Class definition (constructor function)
    var insertButton = AdminLiveEdit.view.componentmenu.button.InsertButton = function (componentMenu) {
        this.componentMenu = componentMenu;
        this.init();
    };

    // Inherits ui.Button
    insertButton.prototype = new AdminLiveEdit.view.Button();

    // Fix constructor as it now is Button
    insertButton.constructor = insertButton;

    // Shorthand ref to the prototype
    var p = insertButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.init = function () {
        var me = this;

        var $button = me.createButton({
            text: 'Insert',
            id: 'live-edit-button-insert',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        me.appendTo(me.componentMenu.getEl());
        me.componentMenu.buttons.push(me);
    };

}($liveedit));