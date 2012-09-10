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
                    itemdblclick: this.viewAccount,
                    afterrender: function (grid) {
                        var gridStore = grid.getStore();
                        gridStore.on('load', this.updateFilterPanel, this);
                        gridStore.load();
                    }
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
            var mask = new Ext.LoadMask(detailPanel, {msg: "Please wait..."});
            mask.show();
            var accountData = persistentSelection[0].raw;
            Admin.lib.RemoteService.account_get({ key: accountData.key }, function (response) {
                    if (response.success) {
                        detailPanel.setCurrentAccount(response);
                        detailPanel.showAccountPreview(response);
                        mask.hide();
                    }
                }
            );
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

    updateFilterPanel: function (store, records, success, opts) {
        var data = store.proxy.reader.jsonData;
        var facets = data ? data.facets : null;
        if (facets) {
            this.getAccountFilter().updateFacets(facets);
        }
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
