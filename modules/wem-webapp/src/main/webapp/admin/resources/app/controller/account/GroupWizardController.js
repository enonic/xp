Ext.define('Admin.controller.account.GroupWizardController', {
    extend: 'Admin.controller.account.GroupController',

    stores: [],
    models: [],
    views: [],

    init: function () {

        this.control({
            'groupWizardPanel *[action=saveGroup]': {
                click: function (el, e) {
                    var groupWizard = el.up('groupWizardPanel');
                    this.saveGroup(groupWizard, false);
                }
            },
            'groupWizardPanel *[action=deleteGroup]': {
                click: this.deleteGroup
            },
            'groupWizardPanel textfield#displayName': {
                keyup: function (field, event) {
                    var value = field.getValue();
                    var groupWizard = field.up('groupWizardPanel');
                    var displayNameLabel = groupWizard.down('#wizardHeader');
                    if (value.trim() === '') {
                        value = 'Display Name';
                    }
                    displayNameLabel.update({
                        displayName: value,
                        qualifiedName: groupWizard.userstore + '\\' + field.getValue()
                    });
                }
            },
            'groupWizardPanel wizardPanel': {
                beforestepchanged: this.validateStep,
                stepchanged: this.stepChanged,
                finished: function (wizard, data) {
                    var groupWizard = wizard.up('groupWizardPanel');
                    this.saveGroup(groupWizard, true);
                },
                validitychange: this.validityChanged,
                dirtychange: this.dirtyChanged
            },
            'groupWizardPanel *[action=newGroup]': {
                click: this.createNewGroup
            },
            'groupWizardPanel *[action=closeWizard]': {
                click: this.closeWizard
            }
        });

        this.application.on({
            groupWizardNext: {
                fn: this.wizardNext,
                scope: this
            },
            groupWizardPrev: {
                fn: this.wizardPrev,
                scope: this
            }
        });
    },

    saveGroup: function (groupWizard, closeWizard) {
        var me = this;
        var wizardPanel = groupWizard.getWizardPanel();
        var data = wizardPanel.getData();
        data.name = this.getDisplayNameValue();
        var step = wizardPanel.getLayout().getActiveItem();
        if (Ext.isFunction(step.getData)) {
            Ext.merge(data, step.getData());
        }

        var onUpdateGroupSuccess = function (key) {
            wizardPanel.addData({
                'key': key
            });
            if (closeWizard) {
                me.getGroupWizardTab().close();
            }
            var parentApp = parent.mainApp;
            if (parentApp) {
                var isNewGroup = groupWizard.isNewGroup();
                var notifyTitle = isNewGroup ? 'Group was created' : 'Group was updated';
                parentApp.fireEvent('notifier.show', notifyTitle,
                    'Something just happened! Li Europan lingues es membres del sam familie. Lor separat existentie es un myth.',
                    true);
            }
            var current = me.getAccountGridPanel().store.currentPage;
            me.getAccountGridPanel().store.loadPage(current);
        };
        this.saveGroupToDB(data, onUpdateGroupSuccess);
    },

    deleteGroup: function (el, evt) {
        var groupWizard = el.up('groupWizardPanel');
        if (groupWizard && groupWizard.modelData) {
            this.showDeleteAccountWindow({data: groupWizard.modelData});
        }
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

        if (newStep.getXType() === 'summaryTreePanel') {
            var groupWizard = wizard.up('groupWizardPanel');
            var treePanel = newStep;

            treePanel.setDiffData(groupWizard.modelData, groupWizard.getData());
        }
    },

    validityChanged: function (wizard, valid) {
        // Need to go this way up the hierarchy in case there are multiple wizards
        var tb = wizard.up('groupWizardPanel').down('groupWizardToolbar');
        var pb = wizard.getProgressBar();
        var save = tb.down('#save');
        var finish = wizard.down('#controls #finish');
        var conditionsMet = valid && (wizard.isWizardDirty || wizard.isNew);
        save.setDisabled(!conditionsMet);
        finish.setVisible(conditionsMet);
        pb.setDisabled(wizard.isNew ? !wizard.isStepValid() : !conditionsMet);
    },

    dirtyChanged: function (wizard, dirty) {
        var tb = wizard.up('groupWizardPanel').down('groupWizardToolbar');
        var pb = wizard.getProgressBar();
        var save = tb.down('#save');
        var finish = wizard.down('#controls #finish');
        var conditionsMet = (dirty || wizard.isNew) && wizard.isWizardValid;
        save.setDisabled(!conditionsMet);
        finish.setVisible(conditionsMet);
        pb.setDisabled(wizard.isNew ? !wizard.isStepValid() : !conditionsMet);
    },

    wizardPrev: function (btn, evt) {
        var wizard = this.getGroupWizardPanel().getWizardPanel();
        wizard.prev(btn);
    },

    wizardNext: function (btn, evt) {
        var wizard = this.getGroupWizardPanel().getWizardPanel();
        if (wizard.isStepValid()) {
            wizard.next(btn);
        }
    },

    getDisplayNameValue: function () {
        var groupWizard = this.getGroupWizardPanel();
        var generalStep = groupWizard.down('wizardStepGeneralPanel');
        return generalStep ? generalStep.query('#displayName')[0].value
            : groupWizard.getData().displayName;
    },

    createNewGroup: function (el, e) {
        this.showNewAccountWindow('group');
    },

    closeWizard: function (el, e) {
        var tab = this.getGroupWizardTab();
        var groupWizard = this.getGroupWizardPanel();
        if (groupWizard.getWizardPanel().isWizardDirty) {
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


    /*      Getters     */

    getGroupWizardTab: function () {
        return this.getCmsTabPanel().getActiveTab();
    },

    getGroupWizardPanel: function () {
        return this.getGroupWizardTab().items.get(0);
    }

});
