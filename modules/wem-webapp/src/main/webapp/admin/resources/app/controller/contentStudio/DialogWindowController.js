Ext.define('Admin.controller.contentStudio.DialogWindowController', {
    extend: 'Admin.controller.contentStudio.ContentTypeController',

    stores: [],
    models: [],
    views: [
        'Admin.view.contentStudio.DeleteContentTypeWindow'
    ],


    init: function () {

        this.control({
            'deleteContentTypeWindow *[action=deleteContentType]': {
                click: this.doDelete
            }
        });
    },

    doDelete: function (el, e) {
        var win = this.getDeleteContentTypeWindow();
        var me = this;
        this.remoteDeleteContentType(win.modelData, function (success, details) {
            win.close();
            var parentApp = parent.mainApp;
            if (parentApp) {
                if (success) {
                    parentApp.fireEvent('notifier.show', "Content Type was deleted",
                        win.modelData.path + " was deleted",
                        false);
                } else {
                    var message = '';
                    var i;
                    for (i = 0; i < details.length; i++) {
                        message += details[0].reason + "\n";
                    }
                    parentApp.fireEvent('notifier.show', "Content Type was not deleted", message, false);
                }
            }
            me.getTreeGridPanel().refresh();
        });
    }

});