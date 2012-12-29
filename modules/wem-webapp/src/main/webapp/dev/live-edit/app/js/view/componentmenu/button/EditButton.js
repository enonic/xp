(function ($) {
    'use strict';

    // Class definition (constructor function)
    var editButton = AdminLiveEdit.view.componentmenu.button.EditButton = function (componentMenu) {
        this.componentMenu = componentMenu;
        this.init();
    };

    // Inherits ui.Button
    editButton.prototype = new AdminLiveEdit.view.Button();

    // Fix constructor as it now is Button
    editButton.constructor = editButton;

    // Shorthand ref to the prototype
    var proto = editButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.init = function () {
        var me = this;

        var $button = me.createButton({
            id: 'live-edit-button-edit',
            text: 'Edit',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        me.appendTo(me.componentMenu.getEl());
        me.componentMenu.buttons.push(me);
    };

}($liveedit));