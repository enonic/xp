Ext.define('Admin.controller.account.BrowseToolbarController', {
    extend: 'Admin.controller.account.Controller',

    /*      Controller for handling Toolbar UI events       */

    stores: [
        'Admin.store.account.GroupStore'
    ],
    models: [
        'Admin.model.account.GroupModel',
        'Admin.model.account.CallingCodeModel'
    ],
    views: [
        'Admin.view.account.ChangePasswordWindow',
        'Admin.view.account.DeleteAccountWindow',
        'Admin.view.account.wizard.user.UserWizardPanel',
        'Admin.view.account.wizard.group.GroupWizardPanel',
        'Admin.view.account.preview.user.UserPreviewPanel',
        'Admin.view.account.preview.group.GroupPreviewPanel',
        'Admin.view.account.ExportAccountsWindow'
    ],

    init: function () {
        this.control({
            'browseToolbar *[action=newUser]': {
                click: this.createNewUser
            },
            'browseToolbar *[action=newGroup]': {
                click: this.createNewGroup
            },
            'browseToolbar *[action=deleteAccount]': {
                click: this.deleteAccount
            },
            'browseToolbar *[action=editAccount]': {
                click: this.editAccount
            },
            'browseToolbar *[action=changePassword]': {
                click: this.changePassword
            },
            'browseToolbar *[action=viewAccount]': {
                click: this.viewAccount
            },
            'browseToolbar *[action=exportAccounts]': {
                click: this.exportAccounts
            }
        });
    },

    createNewUser: function (el, e) {
        this.showNewAccountWindow('user');
    },

    createNewGroup: function (el, e) {
        this.showNewAccountWindow('group');
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

    exportAccounts: function (el, e) {
        var grid = this.getAccountGridPanel(),
            lastQuery = this.getAccountFilter().lastQuery,
            selected = this.getPersistentGridSelectionPlugin().getSelection(),
            data = {
                selected: selected,
                searched: {
                    count: grid.getStore().getTotalCount(),
                    lastQuery: lastQuery
                }
            };
        this.getExportAccountsWindow().doShow({ data: data });
    },


    /*      Getters     */

    getExportAccountsWindow: function () {
        var win = Ext.ComponentQuery.query('exportAccountsWindow')[0];
        if (!win) {
            win = Ext.create('widget.exportAccountsWindow');
        }
        return win;
    }

});
