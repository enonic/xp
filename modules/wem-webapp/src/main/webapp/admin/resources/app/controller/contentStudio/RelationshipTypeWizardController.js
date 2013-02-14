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
        var relationshipType = data.configXML;
        var iconRef = data.iconRef;
        var params = {
            relationshipType: relationshipType,
            iconReference: iconRef
        };

        var onUpdateRelationshipTypeSuccess = function (created, updated) {
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
        this.remoteCreateOrUpdateRelationshipType(params, onUpdateRelationshipTypeSuccess);
    },

    deleteType: function (wizard) {
        var me = this;
        var onDeleteRelationshipTypeSuccess = function (success, failures) {
            if (success) {
                me.getWizardTab().close();
                Admin.MessageBus.showFeedback({
                    title: 'Relationship Type was deleted',
                    message: 'Relationship Type was deleted',
                    opts: {}
                });
            }
        }

        this.remoteDeleteContentType(wizard.data, onDeleteRelationshipTypeSuccess());

    }

});
