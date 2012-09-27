Ext.define('Admin.controller.userstore.UserstoreWizardController', {
    extend: 'Admin.controller.userstore.UserstoreController',

    requires: [
        'Admin.lib.Diff'
    ],

    views: [
        'Admin.view.userstore.wizard.UserstoreWizardPanel',
        'Admin.view.account.EditUserFormPanel'
    ],

    init: function () {
        this.control({
            'userstoreWizardPanel *[action=saveUserstore]': {
                click: function (el, e) {
                    var userstoreWizard = el.up('userstoreWizardPanel');
                    this.saveUserstore(userstoreWizard, false);
                }
            },
            'userstoreWizardPanel textfield#displayName': {
                keyup: function (field, event) {
                    var value = field.getValue();
                    var displayNameLabel = field.up('userstoreWizardPanel').down('#wizardHeader');
                    if (value.trim() === '') {
                        value = 'Display Name';
                    }
                    displayNameLabel.update({
                        displayName: value
                    });
                }
            },
            'userstoreWizardPanel wizardPanel': {
                beforestepchanged: this.validateStep,
                stepchanged: this.stepChanged,
                finished: function (wizard, data) {
                    var userstoreWizard = wizard.up('userstoreWizardPanel');
                    this.saveUserstore(userstoreWizard, true);
                },
                validitychange: this.validityChanged,
                dirtychange: this.dirtyChanged
            },
            'userstoreWizardPanel *[action=closeWizard]': {
                click: this.closeWizard
            },

            'userstoreWizardPanel *[action=deleteUserstore]': {
                click: function (el, e) {
                    var userstoreWizard = el.up('userstoreWizardPanel');
                    if (userstoreWizard && !userstoreWizard.isNewUserstore()) {
                        this.showDeleteUserstoreWindow({data: userstoreWizard.getData()});
                    }
                }
            },

            'deleteUserstoreWindow *[action=deleteUserstore]': {
                click: this.deleteUserstore
            }
        });

        this.application.on({
            userstoreWizardNext: {
                fn: this.wizardNext,
                scope: this
            },
            userstoreWizardPrev: {
                fn: this.wizardPrev,
                scope: this
            }
        });
    },

    wizardPrev: function (btn, evt) {
        var wizard = this.getUserstoreWizardPanel().getWizardPanel();
        wizard.prev(btn);
    },

    wizardNext: function (btn, evt) {
        var wizard = this.getUserstoreWizardPanel().getWizardPanel();
        if (wizard.isStepValid()) {
            wizard.next(btn);
        }
    },

    getUserstoreWizardTab: function () {
        return this.getCmsTabPanel().getActiveTab();
    },

    getUserstoreWizardPanel: function () {
        return this.getUserstoreWizardTab();
    },

    validateStep: function (wizard, step) {
        var data;
        if (step.getData) {
            data = step.getData();
        }
        if (data) {
            wizard.addData(data);
        }
        return true;
    },

    stepChanged: function (wizard, oldStep, newStep) {
        var userstoreWizard = wizard.up('userstoreWizardPanel');
        if (newStep.getXType() === 'summaryTreePanel') {
            var treePanel = newStep;
            // Can not re-use data object each time the rootnode is set
            // This somewhat confuses the store. Clone for now.
            treePanel.setDiffData(userstoreWizard.modelData, userstoreWizard.getData());
        }
    },

    validityChanged: function (wizard, valid) {
        // Need to go this way up the hierarchy in case there are multiple wizards
        var tb = wizard.up('userstoreWizardPanel').down('userstoreWizardToolbar');
        var pb = wizard.getProgressBar();
        var save = tb.down('#save');
        var finish = wizard.down('#controls #finish');
        var conditionsMet = valid && (wizard.isWizardDirty || wizard.isNew);
        save.setDisabled(!conditionsMet);
        finish.setVisible(conditionsMet);
        pb.setDisabled(wizard.isNew ? !wizard.isStepValid() : !conditionsMet);
    },

    dirtyChanged: function (wizard, dirty) {
        var tb = wizard.up('userstoreWizardPanel').down('userstoreWizardToolbar');
        var pb = wizard.getProgressBar();
        var save = tb.down('#save');
        var finish = wizard.down('#controls #finish');
        var conditionsMet = (dirty || wizard.isNew) && wizard.isWizardValid;
        save.setDisabled(!conditionsMet);
        finish.setVisible(conditionsMet);
        pb.setDisabled(wizard.isNew ? !wizard.isStepValid() : !conditionsMet);
    },

    closeWizard: function (el, e) {
        var tab = this.getUserstoreWizardTab();
        var userstoreWizard = this.getUserstoreWizardPanel();
        if (userstoreWizard.getWizardPanel().isWizardDirty) {
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

    saveUserstore: function (userstoreWizard, closeWizard) {
        var me = this;
        var wizardPanel = userstoreWizard.getWizardPanel();
        var data = wizardPanel.getData();
        var step = wizardPanel.getLayout().getActiveItem();
        if (Ext.isFunction(step.getData)) {
            Ext.merge(data, step.getData());
        }

        var onUpdateGroupSuccess = function (key) {
            wizardPanel.addData({
                'key': key
            });
            if (closeWizard) {
                me.getUserstoreWizardTab().close();
            }
            var current = me.getUserstoreGridPanel().store.currentPage;
            me.getUserstoreGridPanel().store.loadPage(current);
        };
        me.saveUserstoreToDB(data, onUpdateGroupSuccess);
        var parentApp = parent.mainApp;
        if (parentApp) {
            parentApp.fireEvent('notifier.show', 'Userstore is saved',
                'Something just happened! Li Europan lingues es membres del sam familie. Lor separat existentie es un myth.',
                true);
        }
    },

    deleteUserstore: function (el, e) {
        var window = el.up('deleteUserstoreWindow');
        var editTab = this.getCmsTabPanel().down('#tab-userstore-' + window.modelData.key);
        if (editTab) {
            editTab.close();
        }
        window.close();
        var parentApp = parent.mainApp;
        if (parentApp) {
            parentApp.fireEvent('notifier.show', 'Userstore was deleted',
                'Something just happened! Li Europan lingues es membres del sam familie. Lor separat existentie es un myth.',
                true);
        }
    },

    getUserstoreGridPanel: function () {
        return Ext.ComponentQuery.query('userstoreGrid')[0];
    }


});
