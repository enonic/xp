(function ($) {
    'use strict';

    // Class definition (constructor function)
    var parentButton = AdminLiveEdit.view.componentmenu.button.ParentButton = function (componentMenu) {
        this.componentMenu = componentMenu;
        this.init();
    };

    // Inherits ui.Button
    parentButton.prototype = new AdminLiveEdit.view.Button();

    // Fix constructor as it now is Button
    parentButton.constructor = parentButton;

    // Shorthand ref to the prototype
    var proto = parentButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.init = function () {
        var me = this;
        var $button = me.createButton({
            id: 'live-edit-button-parent',
            text: 'Parent',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
                var $parent = me.componentmenu.$currentComponent.parents('[data-live-edit-type]');
                if ($parent && $parent.length > 0) {
                    $(window).trigger('component:select', [$($parent[0])]);
                }
            }
        });

        me.appendTo(this.componentMenu.getEl());
        me.componentMenu.buttons.push(me);
    };

}($liveedit));