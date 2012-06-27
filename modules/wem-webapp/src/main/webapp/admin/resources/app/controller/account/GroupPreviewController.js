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
        var groupPreview = el.up('groupPreviewPanel');
        if (groupPreview && groupPreview.data) {
            this.showDeleteAccountWindow({ data: groupPreview.data });
        }
    },

    editGroup: function (el, e) {
        var tab = this.getCmsTabPanel().getActiveTab();
        var index = this.getCmsTabPanel().items.indexOf(tab);

        // check if we are inside the account detail view
        // beneath the grid or in separate tab
        var group;
        if (index >= 0) {
            var previewPanel = tab.down('groupPreviewPanel');
            group = previewPanel ? previewPanel.group : tab.group;
        } else {
            group = this.getAccountDetailPanel().getCurrentAccount();
        }
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

