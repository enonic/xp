(function () {
    // Class definition (constructor function)
    var emptyButton = AdminLiveEdit.ui.componentmenu.button.EmptyButton = function (componentMenu) {
        this.componentMenu = componentMenu;
        this.init();
    };

    // Inherits ui.Base
    emptyButton.prototype = new AdminLiveEdit.ui.Button();

    // Fix constructor as it now is Base
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

        self.appendTo(self.componentMenu.getEl());
        self.componentMenu.buttons.push(self);
    };

}());