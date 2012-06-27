Ext.define('Admin.controller.account.UserPreviewController', {
    extend: 'Admin.controller.account.UserController',

    /*      Controller for handling User Preview UI events       */

    stores: [],
    models: [],
    views: [],

    init: function () {

        this.control({
            'userPreviewPanel *[action=deleteUser]': {
                click: this.deleteUser
            },
            'userPreviewPanel *[action=editUser]': {
                click: this.editUser
            },
            'userPreviewPanel *[action=changePassword]': {
                click: this.changePassword
            },
            'userPreviewPanel *[action=closePreview]': {
                click: this.closePreview
            }
        });
    },

    deleteUser: function (el, e) {
        var previewPanel = el.up('userPreviewPanel');
        if (previewPanel && previewPanel.data) {
            this.showDeleteAccountWindow({data: previewPanel.data});
        }
    },

    changePassword: function (el, e) {
        var previewPanel = el.up('userPreviewPanel');
        if (previewPanel && previewPanel.data) {
            this.showChangePasswordWindow({data: previewPanel.data});
        }
    },

    editUser: function (el, e) {
        var tab = this.getCmsTabPanel().getActiveTab();
        var index = this.getCmsTabPanel().items.indexOf(tab);

        // check if we are inside the account detail view
        // beneath the grid or in separate tab
        var user;
        if (index >= 0) {
            var previewPanel = tab.down('userPreviewPanel');
            user = previewPanel ? previewPanel.user : tab.user;
        } else {
            user = this.getAccountDetailPanel().getCurrentAccount();
        }
        if (user) {
            this.showEditAccountPanel(user);
        }
    },

    closePreview: function (el, e) {
        this.getUserPreviewTab().close();
    },


    /*      Getters     */

    getUserPreviewTab: function () {
        return this.getCmsTabPanel().getActiveTab();
    }

});

