Ext.define('Admin.controller.contentStudio.RelationshipTypeWizardController', {
    extend: 'Admin.controller.contentStudio.WizardController',

    /*      Controller for handling Content Type Wizard UI events       */


    stores: [
    ],
    models: [
    ],
    views: [],


    init: function () {
        this.application.on({
            saveRelationshipType: {
                fn: this.saveType,
                scope: this
            },
            deleteRelationshipType: {
                fn: this.saveType,
                scope: this
            }
        });
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
                    title: 'Relationship Type was saved',
                    message: 'Relationship Type was saved',
                    opts: {}
                });

                me.getTreeGridPanel().refresh();
            }
        };
        //this.remoteCreateOrUpdateContentType(contentTypeParams, onUpdateContentTypeSuccess);
    },

    deleteType: function (wizard, closeWizard) {
        console.log("TODO: delete relationshiptype");
    }

});
