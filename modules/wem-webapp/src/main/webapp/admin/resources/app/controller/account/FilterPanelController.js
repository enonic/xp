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
                specialkey: this.filterHandleEnterKey,
                render: this.onFilterPanelRender
            },
            'accountFilter button[action=search]': {
                click: this.searchFilter
            }
        });

        this.getStore('Admin.store.account.UserstoreConfigStore').on('load', this.initFilterPanelUserStoreOptions,
            this);
        this.getStore('Admin.store.account.AccountStore').on('load', this.updateFilterFacets, this);
    },

    onFilterPanelRender: function () {
        var filterTextField = this.getFilterTextField();
        filterTextField.addListener('change', this.searchFilterKeyPress, this);
        this.getFilterUserStoreField().addListener('change', function (field, newValue, oldValue, eOpts) {
            this.getAccountFilter().updateTitle();
            this.searchFilter('userstore');
        }, this);

        this.getFilterAccountTypeField().addListener('change', function (field, newValue, oldValue, eOpts) {
            this.getAccountFilter().updateTitle();
            this.searchFilter('type');
        }, this);

        this.getFilterOrganizationField().addListener('change', function (field, newValue, oldValue, eOpts) {
            this.getAccountFilter().updateTitle();
            this.searchFilter('organization');
        }, this);

        filterTextField.focus(false, 10);
    },

    searchFilter: function (facetSelected) {
        this.setBrowseTabActive();

        var usersStore = this.getStore('Admin.store.account.AccountStore');
        var textField = this.getFilterTextField();
        var userStoreField = this.getFilterUserStoreField();
        var accountTypeField = this.getFilterAccountTypeField();
        var organizationsField = this.getFilterOrganizationField();

        var values = [];
        Ext.Object.each(organizationsField.getValue(), function (key, val) {
            values.push(val);
        });
        organizationsField = values.join(',');

        values = [];
        Ext.Object.each(accountTypeField.getValue(), function (key, val) {
            values.push(val);
        });
        accountTypeField = values.join(',');

        values = [];
        Ext.Object.each(userStoreField.getValue(), function (key, val) {
            values.push(val);
        });
        userStoreField = values.join(',');

        if (textField.getValue().length > 0) {
            this.getAccountFilter().updateTitle();
        }

        this.facetSelected = facetSelected || '';

        var filterQuery = {
            query: textField.getValue(),
            type: accountTypeField,
            userstores: userStoreField,
            organizations: organizationsField
        };

        // save the last filter query
        this.getAccountFilter().lastQuery = filterQuery;

        usersStore.clearFilter();
        usersStore.getProxy().extraParams = filterQuery;

        // move to page 1 when search filter updated
        var pagingToolbar = this.getAccountGridPanel().down('pagingtoolbar');
        // changing to first page triggers usersStore.load()
        pagingToolbar.moveFirst();
    },

    filterHandleEnterKey: function (field, event) {
        if (event.getKey() === event.ENTER) {
            this.searchFilter();
        }
    },

    initFilterPanelUserStoreOptions: function (store) {
        var items = store.data.items;
        var userstores = [];
        var i;
        for (i = 0; i < items.length; i++) {
            var userstoreName = items[i].data.name;
            userstores.push(userstoreName);
        }
        var filterPanel = this.getAccountFilter();
        filterPanel.setUserStores(userstores);

        // mark userstores inited
        this.isUserstoresInited = true;

        if (this.needUpdateAfterInit) {
            // executing scheduled update
            this.updateFilterFacets(this.needUpdateAfterInit);
        }
    },

    updateFilterFacets: function (store) {
        if (this.isUserstoresInited) {
            // executing update
            var data = store.proxy.reader.jsonData;
            var filterPanel = this.getAccountFilter();
            filterPanel.showFacets(data.results.facets, this.facetSelected);
        } else {
            // userstores has not been inited, schedule the update
            this.needUpdateAfterInit = store;
        }
    },

    setBrowseTabActive: function () {
        var browseTab = this.getCmsTabPanel().getTabById('tab-browse');
        this.getCmsTabPanel().setActiveTab(browseTab);
    },

    searchFilterKeyPress: function () {
        this.getAccountFilter().updateTitle();
        if (this.searchFilterTypingTimer !== null) {
            window.clearTimeout(this.searchFilterTypingTimer);
            this.searchFilterTypingTimer = null;
        }
        var controller = this;
        this.searchFilterTypingTimer = window.setTimeout(function () {
            controller.searchFilter();
        }, 500);
    },


    /*      Getters         */

    getFilterUserStoreField: function () {
        return Ext.ComponentQuery.query('accountFilter checkboxgroup[itemId=userstoreOptions]')[0];
    },

    getFilterAccountTypeField: function () {
        return Ext.ComponentQuery.query('accountFilter checkboxgroup[itemId=accountTypeOptions]')[0];
    },

    getFilterOrganizationField: function () {
        return Ext.ComponentQuery.query('accountFilter checkboxgroup[itemId=organizationOptions]')[0];
    },

    getFilterTextField: function () {
        return Ext.ComponentQuery.query('accountFilter textfield[name=filter]')[0];
    }

});
