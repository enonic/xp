(function ($) {
    'use strict';

    // Class definition (constructor function)
    var parentButton = AdminLiveEdit.view.selectedmenu.button.ParentButton = function (selectedMenu) {
        this.selectedMenu = selectedMenu;
        this.init();
    };

    // Inherits ui.Button
    parentButton.prototype = new AdminLiveEdit.view.Button();

    // Fix constructor as it now is Button
    parentButton.constructor = parentButton;

    // Shorthand ref to the prototype
    var p = parentButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.init = function () {
        var self = this;
        var $button = self.createButton({
            id: 'live-edit-button-parent',
            text: 'Parent',
            iconCls: 'live-edit-icon-parent',
            handler: function (event) {
                event.stopPropagation();
                var $parent = self.selectedMenu.$currentComponent.parents('[data-live-edit-type]');
                if ($parent && $parent.length > 0) {
                    $(window).trigger('component:select', [$($parent[0])]);
                }
            }
        });

        self.appendTo(this.selectedMenu.getEl());
        self.selectedMenu.buttons.push(self);
    };

}($liveedit));