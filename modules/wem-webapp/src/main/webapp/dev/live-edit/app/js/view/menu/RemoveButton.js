AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.menu');

(function ($) {
    'use strict';

    // Class definition (constructor function)
    var removeButton = AdminLiveEdit.view.menu.RemoveButton = function (menu) {
        this.menu = menu;
        this.init();
    };

    // Inherits ui.Button
    removeButton.prototype = new AdminLiveEdit.view.menu.BaseButton();

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
                me.menu.$selectedComponent.remove();
                $(window).trigger('component.onRemove');
            }
        });

        me.appendTo(me.menu.getEl());
        me.menu.buttons.push(me);
    };

}($liveedit));