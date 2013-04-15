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
                search: this.doSearch,
                reset: this.doReset
            }
        });
    },

    doSearch: function (values) {

        var params = this.getStoreParamsFromFilter(values);

        // set browse tab active
        this.getCmsTabPanel().setActiveTab(0);

        // save the search
        var filterPanel = this.getAccountFilter();
        filterPanel.lastQuery = params;

        // submit query
        var gridStore = this.getAccountGridPanel().getStore();
        gridStore.clearFilter();
        gridStore.getProxy().extraParams = params;
        gridStore.loadPage(1);

    },

    doReset: function (dirty) {
        if (!dirty) {
            // prevent reset if the filter is not dirty
            return false;
        }

        var store = this.getAccountGridPanel().getStore();
        delete store.getProxy().extraParams;
        store.loadPage(1);

        return true;
    },

    getStoreParamsFromFilter: function (values) {
        var query = {
            query: values.query || ""
        };
        if (!Ext.isEmpty(values.type)) {
            query.types = values.type;
        }
        if (!Ext.isEmpty(values.userstore)) {
            query.userstores = values.userstore;
        }
        return query;
    }

});
