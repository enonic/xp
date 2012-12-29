(function ($) {
    'use strict';

    // Class definition (constructor function)
    var removeButton = AdminLiveEdit.view.componentmenu.button.RemoveButton = function (componentMenu) {
        this.componentMenu = componentMenu;
        this.init();
    };

    // Inherits ui.Button
    removeButton.prototype = new AdminLiveEdit.view.Button();

    // Fix constructor as it now is Button
    removeButton.constructor = removeButton;

    // Shorthand ref to the prototype
    var proto = removeButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.init = function () {
        var me = this;
        var $button = me.createButton({
            text: 'Remove',
            id: 'live-edit-button-remove',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        me.appendTo(me.componentMenu.getEl());
        me.componentMenu.buttons.push(me);
    };

}($liveedit));