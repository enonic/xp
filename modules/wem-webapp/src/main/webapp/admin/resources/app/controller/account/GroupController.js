Ext.define('Admin.controller.account.GroupController', {
    extend: 'Admin.controller.account.Controller',

    /*      Base controller for group model     */

    stores: [],
    models: [],
    views: [],

    init: function () {
    },


    /*   Public, only CRUD model methods here please     */

    saveGroupToDB: function (group, callback) {
        var me = this;
        Ext.Ajax.request({
            url: 'data/group/update',
            method: 'POST',
            jsonData: group,
            success: function (response, opts) {
                var serverResponse = Ext.JSON.decode(response.responseText);
                if (!serverResponse.success) {
                    Ext.Msg.alert('Error', serverResponse.error);
                } else {
                    callback(serverResponse.groupkey);
                }
                var current = me.getAccountGridPanel().store.currentPage;
                me.getAccountGridPanel().store.loadPage(current);
            },
            failure: function (response, opts) {
                Ext.Msg.alert('Error', 'Unable to update group');
            }
        });
    },

    deleteGroupFromDB: function (group, callback) {
        //TODO
    }

});