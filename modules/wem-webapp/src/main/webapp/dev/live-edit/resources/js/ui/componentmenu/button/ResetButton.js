(function () {
    // Class definition (constructor function)
    var resetButton = AdminLiveEdit.ui.componentmenu.button.ResetButton = function (componentMenu) {
        this.componentMenu = componentMenu;
        this.init();
    };

    // Inherits ui.Base
    resetButton.prototype = new AdminLiveEdit.ui.Button();

    // Fix constructor as it now is Base
    resetButton.constructor = resetButton;

    // Shorthand ref to the prototype
    var p = resetButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.init = function () {
        var self = this;

        var $button = self.create({
            text: 'Reset',
            id: 'live-edit-button-reset',
            iconCls: 'live-edit-icon-reset',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        self.appendTo(self.componentMenu.getEl());
        self.componentMenu.buttons.push(self);
    };

}());