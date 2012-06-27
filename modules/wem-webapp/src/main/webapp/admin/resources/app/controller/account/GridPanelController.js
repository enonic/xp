Ext.define('Admin.controller.account.GridPanelController', {
    extend: 'Admin.controller.account.Controller',

    /*      Controller for handling Grid & its Context Menu UI events       */

    requires: ['Admin.view.account.AccountKeyMap'],
    stores: [
        'Admin.store.account.AccountStore'
    ],
    models: [
        'Admin.model.account.AccountModel'
    ],
    views: [
        'Admin.view.account.BrowseToolbar',
        'Admin.view.account.FilterPanel',
        'Admin.view.account.ShowPanel',
        'Admin.view.account.ContextMenu'
    ],

    init: function () {

        this.control(
            {
                'accountGrid': {
                    selectionchange: function () {
                        this.updateDetailsPanel();
                        this.updateActionItems();
                    },
                    itemcontextmenu: this.popupMenu,
                    itemdblclick: this.viewAccount
                },
                'accountContextMenu *[action=deleteAccount]': {
                    click: this.deleteAccount
                },
                'accountContextMenu *[action=editAccount]': {
                    click: this.editAccount
                },
                'accountContextMenu *[action=changePassword]': {
                    click: this.changePassword
                },
                'accountContextMenu *[action=viewAccount]': {
                    click: this.viewAccount
                }
            }
        );
    },

    deleteAccount: function (el, e) {
        this.showDeleteAccountWindow();
    },

    editAccount: function (el, e) {
        this.showEditAccountPanel();
    },

    changePassword: function (el, e) {
        this.showChangePasswordWindow();
    },

    viewAccount: function (el, e) {
        this.showPreviewAccountPanel();
    },

    updateDetailsPanel: function () {
        var detailPanel = this.getAccountDetailPanel();
        var persistentGridSelectionPlugin = this.getPersistentGridSelectionPlugin();
        var persistentSelection = persistentGridSelectionPlugin.getSelection();
        var persistentSelectionCount = persistentGridSelectionPlugin.getSelectionCount();
        var showAccountPreviewOnly = persistentSelectionCount === 1;

        if (persistentSelectionCount === 0) {
            detailPanel.showNoneSelection();
        } else if (showAccountPreviewOnly) {
            // need raw to include fields like memberships, not defined in model
            var accountData = persistentSelection[0].raw;
            if (accountData) {
                detailPanel.setCurrentAccount(accountData);
                detailPanel.showAccountPreview(accountData);
            }
        } else {
            var detailed = true;
            if (persistentSelectionCount > 10) {
                detailed = false;
            }
            var selectedUsers = [];
            Ext.Array.each(persistentSelection, function (user) {
                Ext.Array.include(selectedUsers, user.data);
            });
            detailPanel.showMultipleSelection(selectedUsers, detailed);
        }

        detailPanel.updateTitle(persistentGridSelectionPlugin);
    },

    popupMenu: function (view, rec, node, index, e) {
        e.stopEvent();
        this.getUserContextMenu().showAt(e.getXY());
        return false;
    },


    /*      Getters     */

    getUserContextMenu: function () {
        var menu = Ext.ComponentQuery.query('accountContextMenu')[0];
        if (!menu) {
            menu = Ext.create('widget.accountContextMenu');
        }
        return menu;
    }

});
