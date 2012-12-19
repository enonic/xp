(function ($) {
    'use strict';

    // Class definition (constructor function)
    var viewButton = AdminLiveEdit.view.componentmenu.button.ViewButton = function (componentMenu) {
        this.componentMenu = componentMenu;
        this.init();
    };

    // Inherits ui.Button
    viewButton.prototype = new AdminLiveEdit.view.Button();

    // Fix constructor as it now is Button
    viewButton.constructor = viewButton;

    // Shorthand ref to the prototype
    var p = viewButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.init = function () {
        var me = this;

        var $button = me.createButton({
            text: 'View',
            id: 'live-edit-button-view',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        me.appendTo(me.componentMenu.getEl());
        me.componentMenu.buttons.push(me);
    };

}($liveedit));