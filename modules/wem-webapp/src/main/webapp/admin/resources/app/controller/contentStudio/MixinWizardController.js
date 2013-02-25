Ext.define('Admin.controller.contentStudio.MixinWizardController', {
    extend: 'Admin.controller.contentStudio.WizardController',

    /*      Controller for handling Content Type Wizard UI events       */


    stores: [
    ],
    models: [
    ],
    views: [],


    init: function () {
        var me = this;
        me.control({
            'contentStudioMixinWizardPanel textfield#displayName': {
                keyup: function (field, event) {
                    var text = Ext.String.trim(field.getValue());
                    me.getTopBar().setTitleButtonText(text);
                }
            }
        });

        this.application.on({
            saveMixin: {
                fn: this.saveType,
                scope: this
            },
            deleteMixin: {
                fn: this.deleteType,
                scope: this
            }
        });
    },

    saveType: function (wizard, closeWizard) {
        var me = this;
        var data = wizard.getData();
        var xml = data.configXML;
        var iconRef = data.iconRef;
        var params = {
            mixin: xml,
            iconReference: iconRef
        };

        var onUpdateMixinSuccess = function (created, updated) {
            if (created || updated) {
                if (closeWizard) {
                    me.getWizardTab().close();
                }

                Admin.MessageBus.showFeedback({
                    title: 'Mixin was saved',
                    message: 'Mixin was saved',
                    opts: {}
                });

                me.getTreeGridPanel().refresh();
            }
        };
        this.remoteCreateOrUpdateMixin(params, onUpdateMixinSuccess);
    },

    deleteType: function (wizard) {
        var me = this;
        var onDeleteMixinSuccess = function (success, failures) {
            if (success) {
                me.getWizardTab().close();
                Admin.MessageBus.showFeedback({
                    title: 'Mixin was deleted',
                    message: 'Mixin was deleted',
                    opts: {}
                });
            }
        }

        this.remoteDeleteMixin(wizard.data, onDeleteMixinSuccess);

    }
});
