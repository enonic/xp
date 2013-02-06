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
