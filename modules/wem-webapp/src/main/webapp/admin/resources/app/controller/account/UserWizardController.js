Ext.define('Admin.controller.account.UserWizardController', {
    extend: 'Admin.controller.account.UserController',

    /*      Controller for handling User Wizard UI events       */

    requires: [
        'Admin.lib.Diff'
    ],

    stores: [
        'Admin.store.account.UserstoreConfigStore',
        'Admin.store.account.CountryStore',
        'Admin.store.account.TimezoneStore',
        'Admin.store.account.LocaleStore'
    ],
    models: [
        'Admin.model.account.UserstoreConfigModel',
        'Admin.model.account.UserFieldModel',
        'Admin.model.account.CountryModel',
        'Admin.model.account.RegionModel',
        'Admin.model.account.TimezoneModel',
        'Admin.model.account.LocaleModel'
    ],
    views: [],


    init: function () {
        var me = this;
        me.control({
            'userWizardPanel *[action=newGroup]': {
                click: me.createNewGroup
            },
            'userWizardPanel *[action=closeWizard]': {
                click: me.closeWizard
            },
            'userWizardPanel *[action=saveUser]': {
                click: function (el, e) {
                    me.saveUser(el.up('userWizardPanel'), false);
                }
            },
            'userWizardPanel *[action=changePassword]': {
                click: me.changePassword
            },
            'userWizardPanel *[action=deleteUser]': {
                click: me.deleteUser
            },
            'userWizardPanel wizardHeader': {
                displaynamechange: this.onDisplayNameChanged,
                displaynameoverride: this.onDisplayNameOverriden
            },
            'userWizardPanel *[displayNameSource]': {
                change: this.onDisplayNameSourceChanged
            },
            'userWizardPanel wizardPanel': {
                beforestepchanged: me.validateStep,
                stepchanged: me.stepChanged,
                finished: function (wizard, data) {
                    me.saveUser(wizard.up('userWizardPanel'), true);
                },
                validitychange: this.validityChanged,
                dirtychange: this.dirtyChanged
            },
            'userWizardPanel editUserFormPanel': {
                fieldsloaded: {
                    fn: me.onEditUserFormLoaded,
                    scope: me
                }
            }
        });

        me.application.on({
            userWizardNext: {
                fn: me.wizardNext,
                scope: me
            },
            userWizardPrev: {
                fn: me.wizardPrev,
                scope: me
            }
        });
    },

    saveUser: function (userWizard, closeWizard) {
        var me = this;
        var wizardPanel = userWizard.getWizardPanel();
        var data = userWizard.getData();
        var step = wizardPanel.getLayout().getActiveItem();
        if (Ext.isFunction(step.getData)) {
            Ext.merge(data, step.getData());
        }

        var onUpdateUserSuccess = function (key) {
            wizardPanel.addData({
                'key': key
            });
            if (closeWizard) {
                me.getUserWizardTab().close();
            }

            Admin.MessageBus.showFeedback({
                title: 'User was saved',
                message: 'Something just happened! Li Europan lingues es membres del sam familie. Lor separat existentie es un myth.',
                opts: {}
            });

            var current = me.getAccountGridPanel().store.currentPage;
            me.getAccountGridPanel().store.loadPage(current);
        };
        this.remoteCreateOrUpdateUser(data, onUpdateUserSuccess);
    },

    deleteUser: function (el, e) {
        var user = this.getUserWizardTab().data;
        if (user) {
            this.showDeleteAccountWindow(user);
        }
    },

    changePassword: function (el, e) {
        var user = this.getUserWizardTab().data;
        if (user) {
            this.showChangePasswordWindow(user);
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
        var userWizard = wizard.up('userWizardPanel');

        userWizard.addStickyNavigation(userWizard);

        if (newStep.getXType() === 'wizardStepProfilePanel') {
            // move to 1st step
            userWizard.setFileUploadDisabled(true);
        }

        if (newStep.getXType() === 'summaryTreePanel') {
            var treePanel = newStep;
            // Can not re-use data object each time the rootnode is set
            // This somewhat confuses the store. Clone for now.
            treePanel.setDiffData(userWizard.data, this.wizardDataToUserInfo(userWizard.getData()));
        }

        // oldStep can be null for first page
        if (oldStep && oldStep.getXType() === 'userStoreListPanel') {
            // move from 1st step
            userWizard.setFileUploadDisabled(false);
        }

        // auto-suggest username
        if ((oldStep && oldStep.itemId === 'profilePanel') && newStep.itemId === 'userPanel') {
            var formPanel = wizard.down('editUserFormPanel');
            var firstName = formPanel.down('#firstName');
            var firstNameValue = firstName ? Ext.String.trim(firstName.getValue()) : '';
            var lastName = formPanel.down('#lastName');
            var lastNameValue = lastName ? Ext.String.trim(lastName.getValue()) : '';
            var userStoreName = wizard.getData().userStore;
            var usernameField = wizard.down('#name');
            if (firstNameValue || lastNameValue) {
                this.autoSuggestUsername(firstNameValue, lastNameValue, userStoreName, usernameField);
            }
        }
    },

    validityChanged: function (wizard, valid) {
        // Need to go this way up the hierarchy in case there are multiple wizards
        var tb = wizard.up('userWizardPanel').down('userWizardToolbar');
        var pb = wizard.getProgressBar();
        var save = tb.down('#save');
        var finish = wizard.down('#controls #finish');
        var conditionsMet = valid && (wizard.isWizardDirty || wizard.isNew);
        if (save) {
            save.setDisabled(!conditionsMet);
        }
        if (finish) {
            finish.setVisible(conditionsMet);
        }
        pb.setDisabled(wizard.isNew ? !wizard.isStepValid() : !conditionsMet);
    },

    dirtyChanged: function (wizard, dirty) {
        var tb = wizard.up('userWizardPanel').down('userWizardToolbar');
        var pb = wizard.getProgressBar();
        var save = tb.down('#save');
        var finish = wizard.down('#controls #finish');
        var conditionsMet = (dirty || wizard.isNew) && wizard.isWizardValid;
        if (save) {
            save.setDisabled(!conditionsMet);
        }
        if (finish) {
            finish.setVisible(conditionsMet);
        }
        pb.setDisabled(wizard.isNew ? !wizard.isStepValid() : !conditionsMet);
    },

    wizardPrev: function (btn, evt) {
        var wizard = this.getUserWizardPanel().getWizardPanel();
        wizard.prev(btn);
    },

    wizardNext: function (btn, evt) {
        var wizard = this.getUserWizardPanel().getWizardPanel();
        if (wizard.isStepValid()) {
            wizard.next(btn);
        }
    },


    onDisplayNameChanged: function (newName, oldName) {
        this.getTopBar().setTitleButtonText(newName);
    },

    onDisplayNameOverriden: function (overriden) {
        var wizard = this.getUserWizardPanel();
        wizard.autogenerateDisplayName = !overriden;

        if (wizard.autogenerateDisplayName) {
            var displayName = this.autoGenerateDisplayName();
            wizard.getWizardHeader().setDisplayName(displayName);
        }
    },

    onDisplayNameSourceChanged: function (field, event, opts) {
        var wizard = this.getUserWizardPanel();

        if (wizard.autogenerateDisplayName) {
            var displayName = this.autoGenerateDisplayName();
            wizard.getWizardHeader().setDisplayName(displayName);
        }
    },

    onEditUserFormLoaded: function (form) {
        var wizard = this.getUserWizardPanel();
        var activeItem = wizard.getWizardPanel().getLayout().getActiveItem();

        // in case of edit check if the auto generated name
        // is not equal to the actual meaning it was user overriden
        if (!wizard.isNewUser() && activeItem == form) {
            var displayNameValue = (wizard.getWizardHeader().getDisplayName() || "").toLowerCase();
            var generatedDisplayName = this.autoGenerateDisplayName();
            wizard.autogenerateDisplayName = generatedDisplayName === displayNameValue;
        }
    },


    autoGenerateDisplayName: function () {
        var displayName = '';
        var activeItem = this.getUserWizardPanel().getWizardPanel().getLayout().getActiveItem();
        var fields = activeItem.query('*[displayNameSource]');
        if (fields.length > 0) {
            var eachFn = function (item, index, all) {
                displayName += item.getValue() + " ";
            };
            Ext.Array.forEach(fields, eachFn);
        }
        return Ext.String.trim(displayName).toLowerCase();
    },


    updateTabTitle: function (field, event) {
        var addressPanel = field.up('addressPanel');
        addressPanel.setTitle(field.getValue());
    },

    autoSuggestUsername: function (firstName, lastName, userStoreName, usernameField) {
        if (usernameField.getValue() !== '') {
            return;
        }

        Admin.lib.RemoteService.account_suggestUserName({ userStore: userStoreName, firstName: firstName, lastName: lastName },
            function (response) {
                if (response.success) {
                    if (usernameField.getValue() === '') {
                        usernameField.setValue(response.username);
                    }
                }
            });
    },

    createNewGroup: function (el, e) {
        this.showNewAccountWindow('group');
    },

    closeWizard: function (el, e) {
        var tab = this.getUserWizardTab();
        var wizard = tab.down('wizardPanel');
        if (wizard.isWizardDirty) {
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

    getUserWizardTab: function () {
        return this.getCmsTabPanel().getActiveTab();
    },

    getUserWizardPanel: function () {
        return this.getUserWizardTab().down('userWizardPanel');
    }

});
