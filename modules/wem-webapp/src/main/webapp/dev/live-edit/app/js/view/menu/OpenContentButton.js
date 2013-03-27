AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.menu');

(function ($) {
    'use strict';

    // Class definition (constructor function)
    var openContentButton = AdminLiveEdit.view.menu.OpenContentButton = function (menu) {
        this.menu = menu;
        this.init();
    };

    // Inherits ui.Button
    openContentButton.prototype = new AdminLiveEdit.view.menu.BaseButton();

    // Fix constructor as it now is Button
    openContentButton.constructor = openContentButton;

    // Shorthand ref to the prototype
    var proto = openContentButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.init = function () {
        var me = this;

        var $button = me.createButton({
            text: 'Open in new tab',
            id: 'live-edit-button-opencontent',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
                if (window.parent.Admin && window.parent.Admin.MessageBus) {
                    window.parent.Admin.MessageBus.liveEditOpenContent({});
                }
            }
        });

        me.appendTo(me.menu.getEl());
        me.menu.buttons.push(me);
    };

}($liveedit));