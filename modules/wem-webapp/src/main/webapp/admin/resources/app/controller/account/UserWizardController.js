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

    EMPTY_DISPLAY_NAME_TEXT: 'Display Name',

    init: function () {
        var me = this;
        me.control({
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
            'userWizardPanel': {
                afterrender: me.bindDisplayNameEvents
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
            // For unknown reason selector 'userWizardPanel editUserFormPanel' doesn't work
            'userWizardPanel editUserFormPanel': {
                fieldsloaded: {
                    fn: me.userStoreFieldsLoaded,
                    scope: me
                }
            },
            'userWizardPanel *[action=newGroup]': {
                click: me.createNewGroup
            },
            'userWizardPanel *[action=closeWizard]': {
                click: me.closeWizard
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
        var userWizard = el.up('userWizardPanel');
        if (userWizard && userWizard.userFields) {
            this.showDeleteAccountWindow({data: userWizard.userFields});
        }
    },

    changePassword: function (el, e) {
        var userWizard = el.up('userWizardPanel');
        if (userWizard && userWizard.userFields) {
            this.showChangePasswordWindow({data: userWizard.userFields});
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
            treePanel.setDiffData(userWizard.userFields, this.wizardDataToUserInfo(userWizard.getData()));
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

    bindDisplayNameEvents: function (wizard) {
        var displayName = wizard.down('#displayName');
        if (displayName) {
            displayName.on('blur', this.displayNameBlur, this);
            displayName.on('focus', this.displayNameFocus, this);
            displayName.on('keydown', this.displayNameChanged, this, {wizard: wizard});
            displayName.on('keyup', this.onChangeDisplayName, this);
        }
    },

    hasDefaultDisplayName: function (displayNameInputField) {
        var text = Ext.String.trim(displayNameInputField.getValue());
        return (text === this.EMPTY_DISPLAY_NAME_TEXT);
    },

    displayNameFocus: function (element) {
        if (this.hasDefaultDisplayName(element)) {
            element.setValue('');
        }
    },

    displayNameBlur: function (element) {
        var text = Ext.String.trim(element.getValue());
        if (text === '') {
            var sourceFields = this.getDisplayNameSourceFields(element.up('userWizardPanel'));
            var autogeneratedDispName = this.autoGenerateDisplayName(sourceFields);
            element.up('userWizardPanel').displayNameAutoGenerate = true;
            if (autogeneratedDispName !== '') {
                element.setValue(autogeneratedDispName);
            }
        }
    },

    displayNameChanged: function (element, event, opts) {
        if ((event.isSpecialKey() ||
             event.isNavKeyPress()) &&
            ((event.getKey() !== event.BACKSPACE) &&
             (event.getKey() !== event.DELETE) &&
             (element.getValue() !== ""))) {
            return;
        }
        if (element.getValue() === '') {
            opts.wizard.displayNameAutoGenerate = true;
        } else {
            opts.wizard.displayNameAutoGenerate = false;
        }
    },

    onChangeDisplayName: function (element) {
        var text = Ext.String.trim(element.getValue());
        this.getTopBar().setTitleButtonText(text);
    },

    userStoreFieldsLoaded: function (target) {
        var userWizard = target.up('userWizardPanel');
        if (!userWizard.isNewUser()) {
            var fields = this.getDisplayNameSourceFields(userWizard);
            var displayNameValue = userWizard.getData().displayName;
            var generatedDisplayName = this.autoGenerateDisplayName(fields);
            if (generatedDisplayName !== displayNameValue) {
                userWizard.displayNameAutoGenerate = false;
            }
        }

        userWizard.getWizardPanel().focusFirstField();
        this.bindFormEvents(target);
    },

    getDisplayNameSourceFields: function (wizard) {
        var fields = [];
        var firstStep = wizard.getSteps().get(0).itemId;
        if (firstStep === 'userPanel') {
            fields = wizard.query('#name');
        } else if (firstStep === 'profilePanel') {
            fields = wizard.query('#prefix , #firstName , #middleName , #lastName , #suffix');
        }
        return fields;
    },

    bindFormEvents: function (form) {
        var me = this;
        var fields = me.getDisplayNameSourceFields(form.up('userWizardPanel'));
        Ext.Array.each(fields, function (item) {
            item.on('change', me.profileNameFieldChanged, me);
        });
    },

    profileNameFieldChanged: function (field) {
        var userWizard = field.up('userWizardPanel');
        var fields = this.getDisplayNameSourceFields(userWizard);
        if (!userWizard.displayNameAutoGenerate) {
            return;
        }

        var displayNameValue = this.autoGenerateDisplayName(fields);
        var displayNameField = userWizard.down('#displayName');
        displayNameField.setValue(displayNameValue);
    },

    autoGenerateDisplayName: function (fields) {
        var displayNameValue = Ext.Array.pluck(fields, 'value').join(' ');
        return Ext.String.trim(displayNameValue.replace(/ {2}/g, ' '));
    },

    usernameFieldChanged: function (field) {
        var userWizard = field.up('userWizardPanel');
        this.updateWizardHeader(userWizard, {qUserName: field.value});
    },

    updateWizardHeader: function (wizard, data) {
        wizard.updateHeader(data);
        this.bindDisplayNameEvents(wizard);
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
        var userWizard = this.getUserWizardPanel();
        if (userWizard.getWizardPanel().isWizardDirty) {
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
        return this.getUserWizardTab().items.get(0);
    }

});
