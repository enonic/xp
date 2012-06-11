Ext.define('App.LauncherToolbarHelper', {
     statics: {
         hideLauncherMenus: function() {
             var componentQuery = Ext.ComponentQuery;
             var toolbarMenuButtons = componentQuery.query('launcherToolbar button[menu]');
             var loggedInUserButton = componentQuery.query('launcherToolbar loggedInUserButton')[0];

             var menu = null;
             for (var i = 0; i < toolbarMenuButtons.length; i++) {
                 menu = toolbarMenuButtons[i].menu;
                 if (menu.isVisible(true)) {
                    menu.hide();
                 }
             }

             loggedInUserButton.toggle(false);
         }

     },

     constructor: function() { }
});