Ext.define('Admin.controller.account.GroupPreviewController', {
    extend: 'Admin.controller.account.GroupController',

    stores: [],
    models: [],
    views: [],

    init: function () {

        this.control({
            'groupPreviewPanel *[action=deleteGroup]': {
                click: this.deleteGroup
            },
            'groupPreviewPanel *[action=editGroup]': {
                click: this.editGroup
            },
            'groupPreviewPanel *[action=closePreview]': {
                click: this.closePreview
            }
        });
    },

    deleteGroup: function (el, e) {
        var group = this.getGroupPreviewTab().data;
        if (group) {
            this.showDeleteAccountWindow(group);
        }
    },

    editGroup: function (el, e) {
        var group = this.getGroupPreviewTab().data;
        if (group) {
            this.showEditAccountPanel(group);
        }
    },

    closePreview: function (el, e) {
        this.getGroupPreviewTab().close();
    },


    /*      Getters     */

    getGroupPreviewTab: function () {
        return this.getCmsTabPanel().getActiveTab();
    }

});

