(function ($) {
    'use strict';

    // Class definition (constructor function)
    var editButton = AdminLiveEdit.view.menu.EditButton = function (menu) {
        this.menu = menu;
        this.init();
    };

    // Inherits ui.Button
    editButton.prototype = new AdminLiveEdit.view.menu.BaseButton();

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

                var $paragraph = me.menu.$selectedComponent;
                if ($paragraph && $paragraph.length > 0) {
                    $(window).trigger('component:paragraph:edit:init', [$paragraph]);
                }
            }
        });

        me.appendTo(me.menu.getEl());
        me.menu.buttons.push(me);
    };

}($liveedit));