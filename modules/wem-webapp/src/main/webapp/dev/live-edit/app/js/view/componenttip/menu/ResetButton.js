(function ($) {
    'use strict';

    // Class definition (constructor function)
    var resetButton = AdminLiveEdit.view.componenttip.menu.ResetButton = function (componentMenu) {
        this.componentMenu = componentMenu;
        this.init();
    };

    // Inherits ui.Button
    resetButton.prototype = new AdminLiveEdit.view.Button();

    // Fix constructor as it now is Button
    resetButton.constructor = resetButton;

    // Shorthand ref to the prototype
    var proto = resetButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.init = function () {
        var me = this;

        var $button = me.createButton({
            text: 'Reset',
            id: 'live-edit-button-reset',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        me.appendTo(me.componentMenu.getEl());
        me.componentMenu.buttons.push(me);
    };

}($liveedit));