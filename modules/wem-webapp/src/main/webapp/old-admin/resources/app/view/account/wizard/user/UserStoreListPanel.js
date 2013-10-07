Ext.define('Admin.view.account.wizard.user.UserStoreListPanel', {
    extend: 'Ext.view.View',
    alias: 'widget.userStoreListPanel',

    border: false,
    loadMask: true,
    cls: 'admin-data-view',
    store: 'Admin.store.account.UserstoreConfigStore',
    itemSelector: '.admin-data-view-row',
    trackOver: true,
    overItemCls: 'x-item-over',
    emptyText: 'No items available',
    autoScroll: true,
    tpl:
    		'<tpl for=".">' +
    		    '<div class="admin-data-view-row">' +
    		        '<div class="admin-data-view-thumbnail">' +
    		            '<img src="{[values.icon || \'resources/images/icons/32x32/server_id_card.png\']}"/>' +
    		        '</div>' +
    		        '<div class="admin-data-view-description">' +
    		            '<h6>{name}</h6>' +
    		            '<p>userstores\\\\{name}</p>' +
    		        '</div>' +
    		        '<div class="x-clear"></div>' +
    		    '</div>' +
    		'</tpl>',

    initComponent: function () {
        this.callParent(arguments);
    },

    getData: function () {
        if (this.selectedUserStore) {
            return {
                userStore: this.selectedUserStore.get('name')
            };
        } else {
            return undefined;
        }
    },

    setData: function (record) {
        this.selectedUserStore = record;
    }

});
