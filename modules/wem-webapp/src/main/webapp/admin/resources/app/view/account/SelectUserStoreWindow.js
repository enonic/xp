Ext.define('Admin.view.account.SelectUserStoreWindow', {
    extend: 'Admin.view.BaseDialogWindow',
    alias: 'widget.selectUserStoreWindow',

    requires: ['Admin.view.account.wizard.user.UserStoreListPanel'],

    dialogInfoTpl: undefined,

    /* Caller field defines is this window used for group wizard or user wizard*/
    caller: undefined,
    items: [
        {
            xtype: 'userStoreListPanel'
        }
    ],

    initComponent: function () {
        // TODO: Why is caller and items set twice?
        this.items = [
            {
                xtype: 'userStoreListPanel',
                caller: this.caller
            }
        ];

        this.dialogTitle = this.createHtmlTitle();
        this.dialogSubTitle = 'Select Userstore';

        this.callParent(arguments);
    },

    createHtmlTitle: function () {
        var icon = this.caller;

        var title = '<div><img src="resources/images/icons/16x16/' + icon +
                    '.png" alt="" style="vertical-align: text-top; margin:0 5px 5px 0"/>';
        title += this.caller === 'user' ? 'New User' : 'New Group';

        return title;
    }

});