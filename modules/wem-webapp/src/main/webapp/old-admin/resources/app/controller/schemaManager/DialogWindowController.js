Ext.define('Admin.controller.schemaManager.DialogWindowController', {
    extend: 'Admin.controller.schemaManager.WizardController',

    stores: [],
    models: [],
    views: [
        'Admin.view.schemaManager.DeleteSchemaWindow'
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

        switch (win.data.get('type')) {
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
        var contentType = win.data;

        var onDelete = function (success, details) {
            win.close();
            if (success) {

                Admin.MessageBus.showFeedback({
                    title: 'Content Type was deleted',
                    message: Ext.isArray(contentType) && contentType.length > 1 ?
                        contentType.length + ' content types were deleted' : '1 content type was deleted',
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
        };

        this.remoteDeleteContentType(contentType, onDelete);
    },

    deleteMixin: function (win) {
        var me = this;
        var mixin = win.data;

        var onDelete = function (success, details) {
            win.close();
            if (success) {

                Admin.MessageBus.showFeedback({
                    title: 'Mixin was deleted',
                    message: '"' + mixin.get('displayName') + '" was deleted',
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
        };

        this.remoteDeleteMixin(mixin, onDelete);
    },

    deleteRelationshipType: function (win) {
        var me = this;
        var relationshipType = win.data;

        var onDelete = function (success, details) {
            win.close();
            if (success) {

                Admin.MessageBus.showFeedback({
                    title: 'Relationship Type was deleted',
                    message: '"' + relationshipType.get('displayName') + '" was deleted',
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
        };

        this.remoteDeleteRelationshipType(relationshipType, onDelete);
    }

});