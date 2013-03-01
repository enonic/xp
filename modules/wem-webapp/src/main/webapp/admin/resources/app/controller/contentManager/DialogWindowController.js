Ext.define('Admin.controller.contentManager.DialogWindowController', {
    extend: 'Admin.controller.contentManager.ContentController',

    /*      Base controller for the content manager module      */

    stores: [],
    models: [],
    views: [
        'Admin.view.contentManager.DeleteContentWindow'
    ],


    init: function () {

        this.control({
            'deleteContentWindow *[action=deleteContent]': {
                click: this.doDelete
            },
            'newContentWindow': {
                contentTypeSelected: function (window, contentType) {
                    if (window) {
                        window.close();
                    }
                    if (contentType) {
                        this.createContent('contentType', contentType.get('qualifiedName'), contentType.get('name'));
                    }
                }
            }
        });

        this.application.on({});

    },

    doDelete: function (el, e) {
        var win = this.getDeleteContentWindow();
        console.log(win);
        var me = this;
        var content = win.data;

        var onContentDeleted = function (success, details) {
            win.close();
            if (success) {
                Admin.MessageBus.showFeedback({
                    title: 'Content was deleted',
                    message: Ext.isArray(content) && content.length > 1 ? content.length + ' contents were deleted' : '1 content was deleted',
                    opts: {}
                });
            } else {
                var message = '';
                var i;
                for (i = 0; i < details.length; i++) {
                    message += details[0].reason + "\n";
                }

                Admin.MessageBus.showFeedback({
                    title: 'Content was not deleted',
                    message: message,
                    opts: {}
                });

            }
            me.getContentTreeGridPanel().refresh();
        };

        this.remoteDeleteContent(content, onContentDeleted);
    }

});