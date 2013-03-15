(function ($) {
    'use strict';

    // Class definition (constructor function)
    var parentButton = AdminLiveEdit.view.menu.ParentButton = function (menu) {
        this.menu = menu;
        this.init();
    };

    // Inherits ui.Button
    parentButton.prototype = new AdminLiveEdit.view.menu.BaseButton();

    // Fix constructor as it now is Button
    parentButton.constructor = parentButton;

    // Shorthand ref to the prototype
    var proto = parentButton.prototype;

    // Uses
    var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.init = function () {
        var me = this;
        var $button = me.createButton({
            id: 'live-edit-button-parent',
            text: 'Select Parent',
            cls: 'live-edit-component-menu-button',
            handler: function (event) {
                event.stopPropagation();
                var $parent = me.menu.$selectedComponent.parents('[data-live-edit-type]');
                if ($parent && $parent.length > 0) {
                    $parent = $($parent[0]);
                    // The menu should stay at the same position when selecting parent
                    var offset = me.menu.getEl().offset(),
                        coordinates = {x: offset.left, y: offset.top};
                    $(window).trigger('component:click:select', [$parent, coordinates]);
                }
            }
        });

        me.appendTo(this.menu.getEl());
        me.menu.buttons.push(me);
    };

}($liveedit));