Ext.define('Admin.controller.WizardController', {
    extend: 'Admin.controller.SpaceController',

    /*      Controller for handling Space Wizard UI events       */


    stores: [
    ],
    models: [
    ],
    views: [],


    init: function () {
        var me = this;

        /*app_event.NewSpaceEvent.on((event) => {
            this.showNewSpaceWindow();
        });*/

        me.control({
            '#spaceAdminWizardPanel *[action=closeWizard]': {
                click: me.closeWizard
            },
            '#spaceAdminWizardPanel *[action=deleteSpace]': {
                click: function () {
                    this.deleteSpace(this.getWizardTab());
                }
            },
            '#spaceAdminWizardPanel #wizardHeader': {
                displaynamechange: function (newVal, oldVal) {
                    this.getTopBar().setTitleButtonText(newVal);
                }
            },
            '#spaceAdminWizardPanel': {
                'validitychange': function (wizard, isValid) {
                    this.updateWizardToolbarButtons(wizard.isWizardDirty, isValid);
                },
                'dirtychange': function (wizard, isDirty) {
                    this.updateWizardToolbarButtons(isDirty, wizard.isWizardValid);
                }
            }
        });
    },

    updateWizardToolbarButtons: function (isDirty, isValid) {
        var toolbar = this.getWizardToolbar();
        var save = toolbar.down('button[action=saveSpace]');
        save.setDisabled(!isDirty || !isValid);
    },

    closeWizard: function (el, e) {
        var tab = this.getWizardTab();
        var spaceWizard = this.getWizardPanel();
        if (spaceWizard.isWizardDirty) {
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

    saveSpace: function () {
        var me = this;
        var spaceWizardPanel = me.getWizardPanel();
        var spaceWizardData = spaceWizardPanel.getData();
        var displayName = spaceWizardData.displayName;
        var spaceName = spaceWizardData.spaceName;
        var iconReference = spaceWizardData.iconRef;

        var spaceModel = spaceWizardPanel.data;
        var originalSpaceName = spaceModel && (spaceModel.get ? spaceModel.get('name') : spaceModel.name);

        var spaceParams = {
            spaceName: originalSpaceName || spaceName,
            displayName: displayName,
            iconReference: iconReference,
            newSpaceName: (originalSpaceName !== spaceName) ? spaceName : undefined
        };

        var onUpdateSpaceSuccess = function (created, updated) {
            if (created || updated) {
                api_notify.showFeedback('Space "' + spaceName + '" was saved');
                me.getSpaceTreeGridPanel().refresh();
                me.getWizardPanel().isWizardDirty = false;
            }
        };
        this.remoteCreateOrUpdateSpace(spaceParams, onUpdateSpaceSuccess);
    },

    deleteSpace: function (wizard) {
        var me = this;
        var space = wizard.data;

        var onDeleteSpaceSuccess = function (success, failures) {
            if (success) {
                wizard.close();
                api_notify.showFeedback('Space was deleted');
            }
        };

        this.remoteDeleteSpace(space, onDeleteSpaceSuccess);
    },


    /*      Getters     */

    getWizardTab: function () {
        return this.getCmsTabPanel().getActiveTab();
    },

    getWizardPanel: function () {
        return this.getWizardTab().wrapper;
    },

    getWizardToolbar: function () {
        return Ext.ComponentQuery.query('#spaceWizardToolbar', this.getWizardTab())[0];
    }

});
