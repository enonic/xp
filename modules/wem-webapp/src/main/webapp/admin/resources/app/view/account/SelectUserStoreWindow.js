Ext.define('Admin.view.account.SelectUserStoreWindow', {
    extend: 'Admin.view.BaseDialogWindow',
    alias: 'widget.selectUserStoreWindow',

    dialogInfoTpl: undefined,

    /* Caller field defines is this window used for group wizard or user wizard*/
    caller: undefined,

    initComponent: function () {
        this.items = [
            {
                xtype: 'userStoreListPanel',
                caller: this.caller,
                height: 350
            }
        ];

        this.iconCls = this.caller === 'user' ? 'icon-user-24' : 'icon-group-24';
        this.dialogTitle = this.caller === 'user' ? 'New User' : 'New Group';

        this.callParent(arguments);
    }

});