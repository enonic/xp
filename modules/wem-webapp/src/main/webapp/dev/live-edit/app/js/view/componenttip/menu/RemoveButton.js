(function ($) {
    'use strict';

    // Class definition (constructor function)
    var removeButton = AdminLiveEdit.view.componenttip.menu.RemoveButton = function (menu) {
        this.menu = menu;
        this.init();
    };

    // Inherits ui.Button
    removeButton.prototype = new AdminLiveEdit.view.componenttip.menu.BaseButton();

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
                // For demo purposes
                me.menu.trigger.$selectedComponent.remove();
                $(window).trigger('component:remove');
            }
        });

        me.appendTo(me.menu.getEl());
        me.menu.buttons.push(me);
    };

}($liveedit));