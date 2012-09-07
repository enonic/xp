Ext.define('Admin.controller.account.FilterPanelController', {
    extend: 'Admin.controller.account.Controller',

    /*      Controller for handling Account Filter UI events       */

    stores: [
        'Admin.store.account.AccountStore',
        'Admin.store.account.UserstoreConfigStore'
    ],
    models: [
        'Admin.model.account.AccountModel'
    ],
    views: [],

    init: function () {

        this.control({
            'accountFilter': {
                search: this.doSearch
            }
        });
    },

    doSearch: function (values) {

        var lastQuery = this.buildAccountSearchQuery(values);

        // set browse tab active
        this.getCmsTabPanel().setActiveTab(0);

        // save the search
        var filterPanel = this.getAccountFilter();
        filterPanel.lastQuery = lastQuery;

        // submit query
        var gridStore = this.getAccountGridPanel().getStore();
        gridStore.clearFilter();
        gridStore.getProxy().extraParams = lastQuery;
        gridStore.loadPage(1);

    },

    buildAccountSearchQuery: function (values) {
        var query = {
            query: values.query
        };
        if (!Ext.isEmpty(values.type)) {
            query.types = Ext.isArray(values.type) ? values.type.join(',') : values.type;
        }
        if (!Ext.isEmpty(values.userstore)) {
            query.userstores = Ext.isArray(values.userstore) ? values.userstore.join(',') : values.userstore;
        }
        return query;
    }

});
