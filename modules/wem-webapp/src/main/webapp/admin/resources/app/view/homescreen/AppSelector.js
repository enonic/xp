Ext.define('Admin.view.homescreen.AppSelector', {
    extend: 'Ext.view.View',
    alias: 'widget.appSelector',

    itemId: 'appSelectorList',
    renderTo: 'admin-home-app-tiles-placeholder',
    tpl: Templates.homescreen.appTile,
    itemSelector: 'div.admin-home-app-tile',

    trackOver: true,
    overItemCls: 'admin-home-app-tile-over',

    emptyText: 'No application found',

    searchTextField: undefined,

    initComponent: function () {
        var me = this;
        me.store = Ext.data.StoreManager.lookup('Admin.store.homescreen.Apps');

        me.addFilterTextField();

        me.callParent(arguments);
    },


    addFilterTextField: function () {
        var me = this;

        me.searchTextField = Ext.create('Ext.form.field.Text', {
            renderTo: 'admin-home-app-selector-search-input-container',
            id: 'admin-home-app-selector-search',
            emptyText: 'Application Filter',
            width: '470px'
        });
    },


    getSearchTextField: function () {
        return Ext.getCmp('admin-home-app-selector-search');

    }

});
