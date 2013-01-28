Ext.define('Admin.controller.contentStudio.WizardController', {
    extend: 'Admin.controller.contentStudio.BaseTypeController',

    /*      Controller for handling Content Type Wizard UI events       */


    stores: [
    ],
    models: [
    ],
    views: [],


    init: function () {
        var me = this;
        me.control({
            'contentStudioWizardPanel *[action=closeWizard]': {
                click: me.closeWizard
            },
            'contentStudioWizardPanel *[action=saveType]': {
                click: function (button, event) {
                    me.saveBaseType(button.up('contentStudioWizardPanel'), false, button.baseType);
                }
            },
            'contentStudioWizardPanel wizardPanel': {
                finished: function (wizard, data) {
                    me.saveBaseType(wizard.up('contentStudioWizardPanel'), true);
                }
            },
            'contentStudioWizardPanel *[action=deleteType]': {
                click: function (button, event) {
                    this.deleteBaseType(button.up('contentStudioWizardPanel'), false, button.baseType);
                }
            }
        });
    },

    closeWizard: function (el, e) {
        var tab = this.getWizardTab();
        var baseTypeWizard = this.getWizardPanel();
        if (baseTypeWizard.getWizardPanel().isWizardDirty) {
            Ext.Msg.confirm('Close wizard', 'There are unsaved changes, do you want to close it anyway ?',
                function (answer) {
                    if ('yes' === answer) {
                        tab.close();
                    }
                });
        } else {
            tab.close();
        }
    },

    saveBaseType: function (wizard, closeWizard, baseType) {
        switch (baseType) {
        case 'contentType':
            this.application.fireEvent('saveContentType', wizard, closeWizard);
            break;
        case 'mixin':
            this.application.fireEvent('saveMixin', wizard, closeWizard);
            break;
        case 'relationshipType':
            this.application.fireEvent('saveRelationshipType', wizard, closeWizard);
            break;
        default:
            return;
        }

    },

    deleteBaseType: function (wizard, closeWizard, baseType) {
        switch (baseType) {
        case 'contentType':
            this.application.fireEvent('deleteContentType', wizard, closeWizard);
            break;
        case 'mixin':
            this.application.fireEvent('deleteMixin', wizard);
            break;
        case 'relationshipType':
            this.application.fireEvent('deleteRelationshipType', wizard, closeWizard);
            break;
        default:
            return;
        }

    },

    /*      Getters     */

    getWizardTab: function () {
        return this.getCmsTabPanel().getActiveTab();
    },

    getWizardPanel: function () {
        return this.getWizardTab();
    }

});
