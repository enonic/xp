(function ($) {
    'use strict';

    // Class definition (constructor function)
    var emptyButton = AdminLiveEdit.view.hovermenu.button.EmptyButton = function (hoverMenu) {
        this.hoverMenu = hoverMenu;
        this.init();
    };

    // Inherits ui.Button
    emptyButton.prototype = new AdminLiveEdit.view.Button();

    // Fix constructor as it now is Button
    emptyButton.constructor = emptyButton;

    // Shorthand ref to the prototype
    var p = emptyButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.init = function () {
        var self = this;

        var $button = self.createButton({
            text: 'Empty',
            id: 'live-edit-button-empty',
            iconCls: 'live-edit-icon-empty',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        self.appendTo(self.hoverMenu.getEl());
        self.hoverMenu.buttons.push(self);
    };

}($liveedit));