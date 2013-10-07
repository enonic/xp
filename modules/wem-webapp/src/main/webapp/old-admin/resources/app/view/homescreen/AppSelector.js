Ext.define('Admin.view.homescreen.AppSelector', {
    extend: 'Ext.view.View',
    alias: 'widget.appSelector',

    itemId: 'appSelectorListView',
    renderTo: 'admin-home-app-tiles-placeholder',
    tpl: '<tpl for=".">' +
         '  <div data-tile-id="{id}" class="admin-home-app-tile">' +
         '      <div class="img-container">' +
         '          <img src="{icon}" alt=""/>' +
         '      </div>' +
         '      <div class="name-container">{name}</div>' +
         '      <div class="tab-count-container" style="display:none">0</div>' +
         '  </div>' +
         '</tpl>',

    itemSelector: 'div.admin-home-app-tile',
    emptyText: 'No application found',
    trackOver: true,
    overItemCls: 'admin-home-app-tile-over',
    searchTextField: undefined,

    initComponent: function () {
        var me = this;
        me.store = Ext.data.StoreManager.lookup('Admin.store.homescreen.Apps');
        me.addFilterTextField();

        me.callParent(arguments);
    },


    addFilterTextField: function () {
        this.searchTextField = Ext.create('Ext.form.field.Text', {
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
