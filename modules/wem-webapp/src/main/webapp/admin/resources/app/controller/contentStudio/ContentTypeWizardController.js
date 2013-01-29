Ext.define('Admin.controller.contentStudio.ContentTypeWizardController', {
    extend: 'Admin.controller.contentStudio.WizardController',

    /*      Controller for handling Content Type Wizard UI events       */


    stores: [
    ],
    models: [
    ],
    views: [],


    init: function () {
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

    saveType: function (wizard, closeWizard) {
        console.log("saving as contenttype");
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
                    title: 'Content Type was saved',
                    message: 'Content Type was saved',
                    opts: {}
                });

                me.getTreeGridPanel().refresh();
            }
        };
        this.remoteCreateOrUpdateContentType(contentTypeParams, onUpdateContentTypeSuccess);
    },

    deleteType: function (wizard) {
        var me = this;
        var onDeleteContentTypeSuccess = function (success, failures) {
            if (success) {
                me.getWizardTab().close();
                Admin.MessageBus.showFeedback({
                    title: 'Content Type was deleted',
                    message: 'Content Type was deleted',
                    opts: {}
                });
            }
        }

        this.remoteDeleteContentType(wizard.data, onDeleteContentTypeSuccess);

    }
});
