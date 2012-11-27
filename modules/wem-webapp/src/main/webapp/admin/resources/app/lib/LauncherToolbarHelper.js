Ext.define('Admin.lib.LauncherToolbarHelper', {
    statics: {
        hideLauncherMenus: function () {

            var startMenu = Ext.ComponentQuery.query('startMenu')[0];
            if (startMenu) {
                startMenu.slideOut();
            }
        }
    },

    constructor: function () {
    }
});