Ext.define('Admin.controller.schemaManager.ContentTypeWizardController', {
    extend: 'Admin.controller.schemaManager.WizardController',

    /*      Controller for handling Content Type Wizard UI events       */


    stores: [
    ],
    models: [
    ],
    views: [
        'Admin.view.schemaManager.wizard.ContentTypeWizardPanel'
    ],


    init: function () {
        var me = this;
        me.control({
            'schemaManagerContentTypeWizardPanel wizardHeader': {
                displaynamechange: this.onDisplayNameChanged,
                scope: this
            }
        });

        this.application.on({
            saveContentType: {
                fn: this.saveType,
                scope: this
            },
            deleteContentType: {
                fn: this.deleteType,
                scope: this
            }
        });
    },

    onDisplayNameChanged: function (newName, oldName) {
        this.getTopBar().setTitleButtonText(newName);
    },

    saveType: function (wizard, closeWizard) {
        var me = this;
        var data = wizard.getData();
        var contentType = data.configXML;
        var iconRef = data.iconRef;
        var contentTypeParams = {
            contentType: contentType,
            iconReference: iconRef
        };

        var onUpdateContentTypeSuccess = function (created, updated) {
            if (created || updated) {
                if (closeWizard) {
                    me.getWizardTab().close();
                }

                Admin.MessageBus.showFeedback({
                    title: '"' + data.displayName + '" was saved',
                    message: '"' + data.displayName + '" was saved',
                    opts: {}
                });

                me.getTreeGridPanel().refresh();
            }
        };
        this.remoteCreateOrUpdateContentType(contentTypeParams, onUpdateContentTypeSuccess);
    },

    deleteType: function (wizard) {

        var contentType = wizard.data;

        var onDeleteContentTypeSuccess = function (success, failures) {
            if (success) {
                wizard.close();
                Admin.MessageBus.showFeedback({
                    title: 'Content Type was deleted',
                    message: 'Content Type was deleted',
                    opts: {}
                });
            }
        };

        this.remoteDeleteContentType(contentType, onDeleteContentTypeSuccess);
    }
});
