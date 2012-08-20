(function () {
    // Class definition (constructor function)
    var viewButton = AdminLiveEdit.ui.componentmenu.button.ViewButton = function (componentMenu) {
        this.componentMenu = componentMenu;
        this.init();
    };

    // Inherits ui.Base
    viewButton.prototype = new AdminLiveEdit.ui.Button();

    // Fix constructor as it now is Base
    viewButton.constructor = viewButton;

    // Shorthand ref to the prototype
    var p = viewButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.init = function () {
        var self = this;

        var $button = self.createButton({
            text: 'View',
            id: 'live-edit-button-view',
            iconCls: 'live-edit-icon-view',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        self.appendTo(self.componentMenu.getEl());
        self.componentMenu.buttons.push(self);
    };

}());