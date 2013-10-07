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
        var user = this.getUserPreviewTab().data;
        if (user) {
            this.showDeleteAccountWindow(user);
        }
    },

    changePassword: function (el, e) {
        var user = this.getUserPreviewTab().data;
        if (user) {
            this.showChangePasswordWindow(user);
        }
    },

    editUser: function (el, e) {
        var user = this.getUserPreviewTab().data;
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

