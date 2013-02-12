(function ($) {
    'use strict';

    // Class definition (constructor function)
    var detailsButton = AdminLiveEdit.view.componenttip.menu.DetailsButton = function (menu) {
        this.menu = menu;
        this.init();
    };

    // Inherits ui.Button
    detailsButton.prototype = new AdminLiveEdit.view.componenttip.menu.BaseButton();

    // Fix constructor as it now is Button
    detailsButton.constructor = detailsButton;

    // Shorthand ref to the prototype
    var proto = detailsButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.init = function () {
        var me = this;

        var $button = me.createButton({
            text: 'Show Details',
            id: 'live-edit-button-details',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        me.appendTo(me.menu.getEl());
        me.menu.buttons.push(me);
    };

}($liveedit));