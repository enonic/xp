Ext.define('Admin.controller.contentStudio.DialogWindowController', {
    extend: 'Admin.controller.contentStudio.WizardController',

    stores: [],
    models: [],
    views: [
        'Admin.view.contentStudio.DeleteSchemaWindow'
    ],


    init: function () {

        this.control({
            'deleteSchemaWindow *[action=deleteSchema]': {
                click: this.doDelete
            },
            'selectSchemaWindow': {
                createNewSchema: function (modalWindow, item) {
                    modalWindow.close();
                    this.createNewSchemaPanel(item);
                }
            }
        });
    },

    doDelete: function (el, e) {
        var win = this.getSchemaWindow();

        switch (win.modelData.type) {
        case 'ContentType':
            this.deleteContentType(win);
            break;
        case 'Mixin':
            this.deleteMixin(win);
            break;
        case 'RelationshipType':
            this.deleteRelationshipType(win);
            break;
        default:
            break;
        }

    },

    deleteContentType: function (win) {
        var me = this;
        var onDelete = function (success, details) {
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
        }

        this.remoteDeleteContentType(win.modelData, onDelete);
    },

    deleteMixin: function (win) {
        var me = this;
        var onDelete = function (success, details) {
            win.close();
            if (success) {

                Admin.MessageBus.showFeedback({
                    title: 'Mixin was deleted',
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
                    title: 'Mixin was not deleted',
                    message: message,
                    opts: {}
                });

            }
            me.getTreeGridPanel().refresh();
        }
        this.remoteDeleteMixin(win.modelData, onDelete);
    },

    deleteRelationshipType: function (win) {
        var me = this;
        var onDelete = function (success, details) {
            win.close();
            if (success) {

                Admin.MessageBus.showFeedback({
                    title: 'Relationship Type was deleted',
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
                    title: 'Relationship Type was not deleted',
                    message: message,
                    opts: {}
                });

            }
            me.getTreeGridPanel().refresh();
        }
        this.remoteDeleteRelationshipType(win.modelData, onDelete);
    }

});