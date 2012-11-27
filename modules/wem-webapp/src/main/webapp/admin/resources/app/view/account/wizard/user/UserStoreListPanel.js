Ext.define('Admin.view.account.wizard.user.UserStoreListPanel', {
    extend: 'Ext.view.View',
    alias: 'widget.userStoreListPanel',

    border: false,
    loadMask: true,
    store: 'Admin.store.account.UserstoreConfigStore',
    itemSelector: 'div.admin-data-view',
    trackOver: true,
    overItemCls: 'admin-data-view-over',
    emptyText: 'No items available',
    autoScroll: true,
    tpl: Templates.account.userstoreListItem,

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
