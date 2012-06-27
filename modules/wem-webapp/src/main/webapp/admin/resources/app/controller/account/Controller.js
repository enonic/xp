/**
 * Base controller for account
 */
Ext.define('Admin.controller.account.Controller', {
    extend: 'Admin.controller.Controller',

    /*      Base controller for the account module      */

    stores: [],
    models: [],
    views: [],


    init: function () {

        this.control({
            'viewport': {
                afterrender: this.initKeyMap
            },
            'cmsTabPanel': {
                afterrender: this.updateActionItems
            },
            'userStoreListPanel': {
                itemclick: this.openNewAccountTab
            }

        });

        this.application.on({
            showEditAccountPanel: {
                fn: this.showEditAccountPanel,
                scope: this
            },
            showPreviewAccountPanel: {
                fn: this.showPreviewAccountPanel,
                scope: this
            },
            showDeleteAccountWindow: {
                fn: this.showDeleteAccountWindow,
                scope: this
            },
            showChangePasswordWindow: {
                fn: this.showChangePasswordWindow,
                scope: this
            }
        });

    },


    /*      Public, should operate with accounts only      */

    showDeleteAccountWindow: function (accounts) {
        if (!accounts) {
            accounts = this.getPersistentGridSelectionPlugin().getSelection();
        } else {
            accounts = [].concat(accounts);
        }
        if (accounts && accounts.length > 0) {
            this.getDeleteAccountWindow().doShow(accounts);
        }
    },

    showChangePasswordWindow: function (account) {
        if (!account) {
            account = this.getPersistentGridSelectionPlugin().getSelection()[0];
        }
        if (account) {
            this.getUserChangePasswordWindow().doShow(account);
        }
    },

    showEditAccountPanel: function (accounts, callback) {
        if (!accounts) {
            accounts = this.getPersistentGridSelectionPlugin().getSelection();
        } else {
            accounts = [].concat(accounts);
        }

        if (accounts.length > 0 && accounts.length <= 5) {
            this.openEditAccountTabs(accounts, callback);
        } else if (accounts.length > 5 && accounts.length <= 50) {
            var confirmText = Ext.String.format("You have select {0} account(s) for editing/viewing. Are you sure you want to continue?",
                accounts.length);
            Ext.MessageBox.confirm("Conform multi-account action", confirmText,
                function (button) {
                    if (button === "yes") {
                        this.openEditAccountTabs(accounts, callback);
                    }
                }, this);
        } else if (accounts.length > 50) {
            var alertText = Ext.String.format("You have selected {0} account(s) for editing/viewing, however for performance reasons the maximum number of items you can bulk open has been limited to {1}, please limit your selection and try again",
                accounts.length, 50);
            Ext.MessageBox.alert("Too many items selected", alertText);
        }
    },

    showPreviewAccountPanel: function (accounts, callback) {
        if (!accounts) {
            accounts = this.getPersistentGridSelectionPlugin().getSelection();
        } else {
            accounts = [].concat(accounts);
        }

        if (accounts.length > 0 && accounts.length <= 5) {
            this.openPreviewAccountTabs(accounts, callback);
        } else if (accounts.length > 5 && accounts.length <= 50) {
            var confirmText = Ext.String.format("You have select {0} account(s) for editing/viewing. Are you sure you want to continue?",
                accounts.length);
            Ext.MessageBox.confirm("Conform multi-account action", confirmText,
                function (button) {
                    if (button === "yes") {
                        this.openPreviewAccountTabs(accounts, callback);
                    }
                }, this);
        } else if (accounts.length > 50) {
            var alertText = Ext.String.format("You have selected {0} account(s) for editing/viewing, however for performance reasons the maximum number of items you can bulk open has been limited to {1}, please limit your selection and try again",
                accounts.length, 50);
            Ext.MessageBox.alert("Too many items selected", alertText);
        }
    },

    showNewAccountWindow: function (type) {
        var window = Ext.create('widget.selectUserStoreWindow', {caller: type});
        window.show();
    },


    /*      Private     */

    openPreviewAccountTabs: function (selection, callback) {
        var me = this,
            tabPane = me.getCmsTabPanel(),
            i = 0,
            requestConfig,
            tabItem,
            index,
            createUserTabFn = function (response) {
                var jsonObj = Ext.JSON.decode(response.responseText);
                if (Ext.isFunction(callback)) {
                    callback();
                }
                return {
                    xtype: 'userPreviewPanel',
                    data: jsonObj,
                    user: jsonObj
                };
            },
            createGroupTabFn = function (response) {
                var jsonObj = Ext.JSON.decode(response.responseText);
                if (Ext.isFunction(callback)) {
                    callback();
                }
                return {
                    xtype: 'groupPreviewPanel',
                    data: jsonObj.group,
                    group: jsonObj.group
                };
            };
        for (i = 0; i < selection.length; i++) {
            var selected = selection[i].data || selection[i];
            if (selected.type === 'user') {
                requestConfig = {
                    url: 'data/user/userinfo',
                    method: 'GET',
                    params: {key: selected.key },
                    createTabFromResponse: createUserTabFn
                };
                tabItem = {
                    title: selected.displayName + ' (' +
                           selected.qualifiedName + ')',
                    id: 'tab-preview-user-' + selected.key,
                    closable: true,
                    layout: 'fit'
                };
                //check if preview tab is open and close it
                index = tabPane.items.indexOfKey('tab-edit-user-' + selected.key);
                if (index >= 0) {
                    tabPane.remove(index);
                }
                tabPane.addTab(tabItem, index >= 0 ? index : undefined, requestConfig);
            } else {
                requestConfig = {
                    url: 'data/account/groupinfo',
                    method: 'GET',
                    params: {key: selected.key },
                    createTabFromResponse: createGroupTabFn
                };
                tabItem = {
                    title: selected.displayName,
                    id: 'tab-preview-group-' + selected.key,
                    closable: true,
                    layout: 'fit'
                };
                //check if preview tab is open and close it
                index = tabPane.items.indexOfKey('tab-edit-group-' + selected.key);
                if (index >= 0) {
                    tabPane.remove(index);
                }
                tabPane.addTab(tabItem, index >= 0 ? index : undefined, requestConfig);
            }
        }
    },

    openEditAccountTabs: function (selection, callback) {
        var tabPane = this.getCmsTabPanel(),
            me = this,
            i,
            requestConfig,
            tabItem,
            index,
            createUserWizardFn = function (response) {
                var jsonObj = Ext.JSON.decode(response.responseText);
                var tab = {
                    xtype: 'userWizardPanel',
                    userstore: jsonObj.userStore,
                    qUserName: jsonObj.name,
                    userFields: jsonObj,
                    hasPhoto: jsonObj.hasPhoto,
                    autoScroll: true
                };
                var tabCmp = Ext.widget(tab.xtype, tab);
                var wizardPanel = tabCmp.down('wizardPanel');

                var data = me.userInfoToWizardData(jsonObj);
                wizardPanel.addData(data);
                tabCmp.updateHeader({value: jsonObj.displayName, edited: true});
                if (Ext.isFunction(callback)) {
                    callback();
                }
                return tabCmp;
            },
            createGroupWizardFn = function (response) {
                var jsonObj = Ext.JSON.decode(response.responseText);
                var tab = {
                    xtype: 'groupWizardPanel',
                    modelData: jsonObj.group,
                    userstore: jsonObj.group.userStore,
                    autoScroll: true
                };
                if (Ext.isFunction(callback)) {
                    callback();
                }
                return tab;
            };
        // Make sure it is array
        selection = [].concat(selection);
        for (i = 0; i < selection.length; i++) {
            var selected = selection[i].data || selection[i];
            if (selected.isEditable) {
                if (selected.type === 'user') {
                    requestConfig = {
                        url: 'data/user/userinfo',
                        method: 'GET',
                        params: {key: selected.key},
                        createTabFromResponse: createUserWizardFn
                    };
                    tabItem = {
                        id: 'tab-edit-user-' + selected.key,
                        title: selected.displayName + ' (' + selected.qualifiedName + ')',
                        iconCls: 'icon-user',
                        closable: true,
                        layout: 'fit'
                    };
                    //check if preview tab is open and close it
                    index = tabPane.items.indexOfKey('tab-preview-user-' + selected.key);
                    if (index >= 0) {
                        tabPane.remove(index);
                    }
                    tabPane.addTab(tabItem, index >= 0 ? index : undefined, requestConfig);
                } else {
                    requestConfig = {
                        url: 'data/account/groupinfo',
                        method: 'GET',
                        params: {key: selected.key},
                        createTabFromResponse: createGroupWizardFn
                    };

                    var tabIconCls = selected.type === 'group' ? 'icon-group' : 'icon-role';
                    tabItem = {
                        id: 'tab-edit-group-' + selected.key,
                        title: selected.displayName,
                        iconCls: tabIconCls,
                        closable: true,
                        layout: 'fit'
                    };
                    //check if preview tab is open and close it
                    index = tabPane.items.indexOfKey('tab-preview-group-' + selected.key);
                    if (index >= 0) {
                        tabPane.remove(index);
                    }
                    tabPane.addTab(tabItem, index >= 0 ? index : undefined, requestConfig);
                }
            }
        }
    },

    userInfoToWizardData: function (userData) {
        var data = {
            'userStore': userData.userStore,
            'key': userData.key,
            'email': userData.email,
            'username': userData.name,
            'displayName': userData.displayName,
            'userInfo': userData.userInfo
        };
        return data;
    },

    updateActionItems: function () {
        var actionItems2d = [];
        var editButtons = Ext.ComponentQuery.query('*[action=editAccount]');
        var changePasswordButtons = Ext.ComponentQuery.query('*[action=changePassword]');
        var deleteButtons = Ext.ComponentQuery.query('*[action=deleteAccount]');
        actionItems2d.push(editButtons);
        actionItems2d.push(changePasswordButtons);
        actionItems2d.push(deleteButtons);
        actionItems2d.push(Ext.ComponentQuery.query('*[action=viewAccount]'));

        var actionItems = [];
        var selectionCount = this.getPersistentGridSelectionPlugin().getSelectionCount();
        var multipleSelection = selectionCount > 1;
        var disable = selectionCount === 0;
        var i, j, k;
        for (i = 0; i < actionItems2d.length; i++) {
            actionItems = actionItems2d[i];
            for (j = 0; j < actionItems.length; j++) {
                actionItems[j].setDisabled(disable);
                if (multipleSelection && actionItems[j].disableOnMultipleSelection) {
                    actionItems[j].setDisabled(true);
                }
            }
        }


        if (selectionCount === 1) {
            var selection = this.getPersistentGridSelectionPlugin().getSelection()[0];
            var isEditable = selection.get('isEditable');
            var isUser = selection.get('type') === 'user';
            var isRole = selection.get('type') === 'role';

            for (j = 0; j < editButtons.length; j++) {
                editButtons[j].setDisabled(!isEditable);
            }
            for (j = 0; j < changePasswordButtons.length; j++) {
                changePasswordButtons[j].setDisabled(!isUser || !isEditable);
            }
            for (k = 0; k < deleteButtons.length; k++) {
                deleteButtons[k].setDisabled(isRole || !isEditable);
            }
        }
    },

    initKeyMap: function () {
        var me = this;
        var cmsTabPanel = this.getCmsTabPanel();
        var activeTab = cmsTabPanel.getActiveTab();
        var keyMap = Ext.create('Admin.view.account.AccountKeyMap', {
            newMegaMenu: function () {
                if (activeTab.getId() === "tab-browse") {
                    var menu = cmsTabPanel.down("#newItemMenu");
                    menu.showBy(cmsTabPanel.down("#newAccountButton"));
                }
            },
            openItem: function () {
                if (activeTab.getId() === "tab-browse") {
                    me.showPreviewAccountPanel();
                }
            },
            editItem: function () {
                if (activeTab.getId() === "tab-browse") {
                    me.showEditAccountPanel();
                }
            },
            saveItem: function () {
                var eventName;
                switch (activeTab.getXType()) {
                case 'groupWizardPanel':
                    eventName = 'saveNewUser';
                    break;
                case 'userWizardPanel':
                    eventName = 'saveGroup';
                    break;
                }
                this.application.fireEvent(eventName);
            },
            prevStep: function () {
                var eventName;
                switch (activeTab.getXType()) {
                case 'groupWizardPanel':
                    eventName = 'groupWizardPrev';
                    break;
                case 'userWizardPanel':
                    eventName = 'userWizardPrev';
                    break;
                }
                this.application.fireEvent(eventName);
            },
            nextStep: function () {
                var eventName;
                switch (activeTab.getXType()) {
                case 'groupWizardPanel':
                    eventName = 'groupWizardNext';
                    break;
                case 'userWizardPanel':
                    eventName = 'userWizardNext';
                    break;
                }
                this.application.fireEvent(eventName);
            },
            deleteItem: function (keyCode, event) {
                if (activeTab.getId() === "tab-browse") {
                    this.showDeleteAccountWindow();
                    event.stopEvent();
                }
            }
        });
    },

    openNewAccountTab: function (view, record, item) {
        view.setData(record);

        var selectedUserStoreElement = new Ext.Element(item);
        var userStoreElements = view.getNodes();
        var i;
        for (i = 0; i < userStoreElements.length; i++) {
            var userStoreElement = new Ext.Element(userStoreElements[i]);
            if (userStoreElement.id !== selectedUserStoreElement.id) {
                userStoreElement.removeCls('admin-userstore-active');
            }
        }

        selectedUserStoreElement.addCls('admin-userstore-active');

        var radioButton = selectedUserStoreElement.down('input');
        radioButton.dom.checked = true;

        var userStoreName = record.get('name');
        var itemType = view.caller;
        var tab;
        if (itemType === 'group') {
            tab = {
                id: Ext.id(null, 'new-group-'),
                title: 'New Group',
                iconCls: 'icon-group',
                closable: true,
                layout: 'fit',
                items: [
                    {
                        userstore: userStoreName,
                        xtype: 'groupWizardPanel'
                    }
                ]
            };
        } else {
            tab = {
                id: Ext.id(null, 'new-user-'),
                title: 'New User',
                iconCls: 'icon-user',
                closable: true,
                layout: 'fit',
                items: [
                    {
                        userstore: userStoreName,
                        xtype: 'userWizardPanel'
                    }
                ]
            };
        }
        var tabItem = this.getCmsTabPanel().addTab(tab);
        tabItem.down('wizardPanel').addData({'userStore': userStoreName});
        var window = view.up('window');
        window.close();
    },


    /*      Getters     */

    getAccountFilter: function () {
        return Ext.ComponentQuery.query('accountFilter')[0];
    },

    getAccountGridPanel: function () {
        return Ext.ComponentQuery.query('accountGrid')[0];
    },

    getAccountDetailPanel: function () {
        return Ext.ComponentQuery.query('accountDetail')[0];
    },

    getPersistentGridSelectionPlugin: function () {
        return this.getAccountGridPanel().getPlugin('persistentGridSelection');
    },

    getDeleteAccountWindow: function () {
        var win = Ext.ComponentQuery.query('deleteAccountWindow')[0];
        if (!win) {
            win = Ext.create('widget.deleteAccountWindow');
        }
        return win;
    },

    getUserChangePasswordWindow: function () {
        var win = Ext.ComponentQuery.query('userChangePasswordWindow')[0];
        if (!win) {
            win = Ext.create('widget.userChangePasswordWindow');
        }
        return win;
    }

});