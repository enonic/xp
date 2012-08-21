(function () {
    'use strict';

    // Class definition (constructor function)
    var parentButton = AdminLiveEdit.ui.componentmenu.button.ParentButton = function (componentMenu) {
        this.componentMenu = componentMenu;
        this.init();
    };

    // Inherits ui.Base
    parentButton.prototype = new AdminLiveEdit.ui.Button();

    // Fix constructor as it now is Base
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
                var $parent = self.componentMenu.$currentComponent.parents('[data-live-edit-type]');
                if ($parent && $parent.length > 0) {
                    $liveedit.publish('/component/on-select', [$liveedit($parent[0])]);
                }
            }
        });

        self.appendTo(this.componentMenu.getEl());
        self.componentMenu.buttons.push(self);
    };

}());