Ext.define('Admin.controller.spaceAdmin.WizardController', {
    extend: 'Admin.controller.spaceAdmin.SpaceController',

    /*      Controller for handling Space Wizard UI events       */


    stores: [
    ],
    models: [
    ],
    views: [],


    init: function () {
        var me = this;
        me.control({
            'spaceAdminWizardPanel *[action=closeWizard]': {
                click: me.closeWizard
            },
            'spaceAdminWizardPanel *[action=saveSpace]': {
                click: function (el) {
                    me.saveSpace(el.up('spaceAdminWizardPanel'), false);
                }
            },
            'spaceAdminWizardPanel *[action=deleteSpace]': {
                click: function () {
                    this.deleteSpace(this.getWizardTab());
                }
            }
        });
    },

    closeWizard: function (el, e) {
        var tab = this.getWizardTab();
        var spaceWizard = this.getWizardPanel();
        if (spaceWizard.getWizardPanel().isWizardDirty) {
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

    saveSpace: function (spaceWizard, closeWizard) {
        var me = this;
        var spaceWizardData = spaceWizard.getData();
        var displayName = spaceWizardData.displayName;
        var spaceName = spaceWizardData.spaceName;
        var iconReference = spaceWizardData.iconRef;

        var spaceParams = {
            spaceName: spaceName,
            displayName: displayName,
            iconReference: iconReference
        };

        var onUpdateSpaceSuccess = function (created, updated) {
            if (created || updated) {
                if (closeWizard) {
                    me.getWizardTab().close();
                }

                Admin.MessageBus.showFeedback({
                    title: 'Space saved',
                    message: 'Space "' + spaceName + '" was saved',
                    opts: {}
                });

                me.getSpaceTreeGridPanel().refresh();
            }
        };
        this.remoteCreateOrUpdateSpace(spaceParams, onUpdateSpaceSuccess);
    },

    /*      Getters     */

    getWizardTab: function () {
        return this.getCmsTabPanel().getActiveTab();
    },

    getWizardPanel: function () {
        return this.getWizardTab();
    },

    deleteSpace: function (wizard) {
        var me = this;
        var space = wizard.data;

        var onDeleteSpaceSuccess = function (success, failures) {
            if (success) {
                wizard.close();
                Admin.MessageBus.showFeedback({
                    title: 'Space deleted',
                    message: 'Space was deleted',
                    opts: {}
                });
            }
        };

        this.remoteDeleteSpace(space, onDeleteSpaceSuccess);
    }
})
;
