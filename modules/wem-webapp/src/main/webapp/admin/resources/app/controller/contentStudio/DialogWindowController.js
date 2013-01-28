Ext.define('Admin.controller.contentStudio.DialogWindowController', {
    extend: 'Admin.controller.contentStudio.WizardController',

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
            if (success) {

                Admin.MessageBus.showFeedback({
                    title: 'Content Type was deleted',
                    message: win.modelData.path + ' was deleted',
                    opts: {}
                });

            } else {
                var message = '';
                var i;
                for (i = 0; i < details.length; i++) {
                    message += details[0].reason + "\n";
                }

                Admin.MessageBus.showFeedback({
                    title: 'Content Type was not deleted',
                    message: message,
                    opts: {}
                });

            }
            me.getTreeGridPanel().refresh();
        });
    }

});