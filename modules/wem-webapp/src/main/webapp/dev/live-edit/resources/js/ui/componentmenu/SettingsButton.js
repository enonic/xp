(function () {
    // Class definition (constructor function)
    var button = AdminLiveEdit.ui.componentmenu.SettingsButton = function (componentMenu) {
        this.componentMenu = componentMenu;
        this.init();
    };

    // Inherits ui.Base
    button.prototype = new AdminLiveEdit.ui.Button();

    // Fix constructor as it now is Base
    button.constructor = button;

    // Shorthand ref to the prototype
    var p = button.prototype;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.init = function () {
        // var parentButton = new AdminLiveEdit.ui.Button();
        var btn = this.create({
            text: 'Settings',
            id: 'live-edit-button-settings',
            iconCls: 'live-edit-icon-settings',
            handler: function (event) {
                event.stopPropagation();
            }
        });

        this.appendTo(this.componentMenu.getEl());
        this.componentMenu.buttons.push(this);
    };

}());