(function () {
    // Class definition (constructor function)
    var removeButton = AdminLiveEdit.ui.componentmenu.button.RemoveButton = function (componentMenu) {
        this.componentMenu = componentMenu;
        this.init();
    };

    // Inherits ui.Base
    removeButton.prototype = new AdminLiveEdit.ui.Button();

    // Fix constructor as it now is Base
    removeButton.constructor = removeButton;

    // Shorthand ref to the prototype
    var p = removeButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.init = function () {
        var self = this;
        var $button = self.create({
            text: 'Remove',
            id: 'live-edit-button-remove',
            iconCls: 'live-edit-icon-remove',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        self.appendTo(self.componentMenu.getEl());
        self.componentMenu.buttons.push(self);
    };

}());